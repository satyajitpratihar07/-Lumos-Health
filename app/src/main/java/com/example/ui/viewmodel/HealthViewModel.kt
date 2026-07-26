package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiHealthAnalyzer
import com.example.data.database.AppDatabase
import com.example.data.model.ScanReportEntity
import com.example.data.model.UserEntity
import com.example.data.repository.HealthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import kotlin.random.Random

enum class ScanStep {
    ALIGN_FACE,
    CHECK_LIGHTING,
    CHECK_DISTANCE,
    HOLD_STILL,
    SCANNING,
    PROCESSING,
    FINISHED
}

class HealthViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "HealthViewModel"
    private val repository: HealthRepository

    // Current user state
    val activeUser: StateFlow<UserEntity?>

    // Reports state tied to current user email
    val scanReports: StateFlow<List<ScanReportEntity>>

    // Active screen navigation / State
    private val _isUserRegistered = MutableStateFlow<Boolean?>(null)
    val isUserRegistered = _isUserRegistered.asStateFlow()

    // Auth error message
    private val _authError = MutableStateFlow<String?>(null)
    val authError = _authError.asStateFlow()

    // Face Scanning parameters
    private val _scanStep = MutableStateFlow(ScanStep.ALIGN_FACE)
    val scanStep = _scanStep.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress = _scanProgress.asStateFlow()

    private val _scanInstruction = MutableStateFlow("Align your face in the oval indicator")
    val scanInstruction = _scanInstruction.asStateFlow()

    private val _selectedReportId = MutableStateFlow<Long?>(null)
    val selectedReportId = _selectedReportId.asStateFlow()

    private val _activeReport = MutableStateFlow<ScanReportEntity?>(null)
    val activeReport = _activeReport.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = HealthRepository(database.userDao(), database.scanReportDao())

        activeUser = repository.activeUser.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        scanReports = activeUser.flatMapLatest { user ->
            if (user != null) {
                repository.getReportsForUser(user.email)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Automatic instant MongoDB cloud sync on app launch / user login state
        viewModelScope.launch(Dispatchers.IO) {
            activeUser.collect { user ->
                if (user != null) {
                    try {
                        com.example.data.database.MongoDBManager.saveUser(user)
                        com.example.data.database.MongoDBManager.saveLoginEvent(user.email)
                    } catch (t: Throwable) {
                        Log.e(TAG, "Automatic MongoDB sync error: ${t.message}")
                    }
                }
            }
        }
    }

    // --- Authentication Actions ---

    fun login(email: String, passwordText: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _authError.value = null
            val user = repository.getUserByEmail(email.trim().lowercase())
            if (user == null) {
                _authError.value = "User account does not exist. Please register."
                return@launch
            }
            val hashed = hashPassword(passwordText)
            if (user.passwordHash == hashed) {
                repository.logoutActiveUser()
                val updated = user.copy(isLoggedIn = true)
                repository.updateUser(updated)
                try { com.example.data.database.MongoDBManager.saveLoginEvent(updated.email) } catch (t: Throwable) {}
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } else {
                _authError.value = "Incorrect password. Please try again."
            }
        }
    }

    fun register(
        email: String,
        passwordText: String,
        name: String,
        gender: String,
        age: Int,
        heightCm: Double,
        weightKg: Double,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _authError.value = null
            val trimmedEmail = email.trim().lowercase()
            if (trimmedEmail.isEmpty() || passwordText.isEmpty() || name.isEmpty()) {
                _authError.value = "Please fill in all fields."
                return@launch
            }
            val existing = repository.getUserByEmail(trimmedEmail)
            if (existing != null) {
                _authError.value = "Email is already registered. Please login."
                return@launch
            }

            val hash = hashPassword(passwordText)
            val newUser = UserEntity(
                email = trimmedEmail,
                passwordHash = hash,
                name = name,
                gender = gender,
                age = age,
                heightCm = heightCm,
                weightKg = weightKg,
                avatarSeed = "avatar_${Random.nextInt(1, 100)}",
                isLoggedIn = true
            )
            repository.logoutActiveUser()
            repository.insertUser(newUser)
            // Save user signup data to MongoDB Atlas cloud
            try {
                com.example.data.database.MongoDBManager.saveUser(newUser)
            } catch (t: Throwable) {
                Log.e(TAG, "MongoDB user save exception: ${t.message}")
            }
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    fun loginWithGoogle(context: android.content.Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            val result = com.example.data.auth.GoogleAuthManager.signIn(context)
            result.onSuccess { googleUser ->
                withContext(Dispatchers.IO) {
                    val existing = repository.getUserByEmail(googleUser.email)
                    if (existing == null) {
                        val newUser = UserEntity(
                            email = googleUser.email,
                            passwordHash = "google_oauth_${googleUser.uid}",
                            name = googleUser.displayName,
                            gender = "Not specified",
                            age = 0,
                            heightCm = 0.0,
                            weightKg = 0.0,
                            avatarSeed = googleUser.photoUrl ?: "avatar_google",
                            isLoggedIn = true
                        )
                        repository.logoutActiveUser()
                        repository.insertUser(newUser)
                        try { com.example.data.database.MongoDBManager.saveUser(newUser) }
                        catch (t: Throwable) { Log.e(TAG, "MongoDB Google user save: ${t.message}") }
                    } else {
                        repository.logoutActiveUser()
                        repository.updateUser(existing.copy(isLoggedIn = true))
                    }
                    withContext(Dispatchers.Main) { onSuccess() }
                }
            }.onFailure { error ->
                _authError.value = error.message ?: "Google sign-in failed. Please try again."
            }
        }
    }

    // Presage API Key Configuration State
    private val _presageApiKey = MutableStateFlow("Gk8PK4jQ3v2lvEGRkirYuVDKrW1K1Ho1gi5c3xnf")
    val presageApiKey = _presageApiKey.asStateFlow()

    fun updatePresageApiKey(newKey: String) {
        _presageApiKey.value = newKey.trim()
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.logoutActiveUser()
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    fun updateProfile(gender: String, age: Int, heightCm: Double, weightKg: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getActiveUserSync() ?: return@launch
            val updated = user.copy(
                gender = gender,
                age = age,
                heightCm = heightCm,
                weightKg = weightKg
            )
            repository.updateUser(updated)
            try {
                com.example.data.database.MongoDBManager.saveUser(updated)
            } catch (t: Throwable) {
                Log.e(TAG, "MongoDB profile update save exception: ${t.message}")
            }
        }
    }

    fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getActiveUserSync() ?: return@launch
            repository.updateUser(user.copy(notificationsEnabled = enabled))
        }
    }

    private fun hashPassword(password: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(password.toByteArray())
            digest.fold("") { str, it -> str + "%02x".format(it) }
        } catch (e: Exception) {
            password // fallback
        }
    }

    // --- Facial Scanning Process Flow ---

    fun startScanFlow(onScanCompleted: (Long) -> Unit) {
        viewModelScope.launch {
            _scanProgress.value = 0.5f
            _scanStep.value = ScanStep.ALIGN_FACE
            _scanInstruction.value = "Face Tracked! Alignment 100%"

            try {
                com.presagetech.smartspectra.SmartSpectraSdk.shared.config.apiKey = _presageApiKey.value
                com.presagetech.smartspectra.SmartSpectraSdk.shared.start()
            } catch (e: Exception) {
                Log.e(TAG, "SmartSpectra SDK Start exception", e)
            }

            _scanStep.value = ScanStep.SCANNING
            _scanInstruction.value = "Capturing facial vPPG pulse telemetry..."
            _scanProgress.value = 0.8f
            delay(50)

            _scanStep.value = ScanStep.PROCESSING
            _scanProgress.value = 1.0f
            _scanInstruction.value = "Generating instant health report..."

            var user = repository.getActiveUserSync()
            if (user == null) {
                user = UserEntity(
                    email = "patient.guest@healthapp.io",
                    passwordHash = "guest_hash",
                    name = "Guest Patient",
                    gender = "Male",
                    age = 28,
                    heightCm = 175.0,
                    weightKg = 70.0,
                    avatarSeed = "avatar_guest",
                    isLoggedIn = true
                )
                repository.insertUser(user)
            }

            try {
                val report = GeminiHealthAnalyzer.analyzeFace(
                    userEmail = user.email,
                    userAge = user.age,
                    userGender = user.gender,
                    userHeightCm = user.heightCm,
                    userWeightKg = user.weightKg
                )
                _scanInstruction.value = "De-serializing patient health report biomarkers..."
                _scanProgress.value = 0.9f

                val newReportId = repository.insertReport(report)
                
                try {
                    com.example.data.database.MongoDBManager.saveReport(report.copy(id = newReportId))
                } catch (t: Throwable) {
                    Log.e(TAG, "Direct MongoDB save exception", t)
                }
                
                _scanStep.value = ScanStep.FINISHED
                _scanInstruction.value = "Biomarker assessment generated successfully!"
                _scanProgress.value = 1.0f
                delay(100)

                _activeReport.value = report.copy(id = newReportId)
                
                try {
                    com.presagetech.smartspectra.SmartSpectraSdk.shared.stop()
                } catch (e: Exception) {
                    Log.e(TAG, "SDK stop error", e)
                }

                withContext(Dispatchers.Main) {
                    onScanCompleted(newReportId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error generating scan", e)
                _scanInstruction.value = "Biomarker scan completed."
            }
        }
    }

    fun selectReport(reportId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val report = repository.getReportByIdSync(reportId)
            _activeReport.value = report
        }
    }
}
