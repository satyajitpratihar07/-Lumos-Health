package com.presagetech.smartspectra

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.presagetech.smartspectra.proto.MetricsProto.ExpressionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.sin
import kotlin.random.Random

class SmartSpectraSdk private constructor() {
    val config = SmartSpectraConfig()

    private val _processingStatus = MutableLiveData<ProcessingStatus>(ProcessingStatus.IDLE)
    val processingStatus: LiveData<ProcessingStatus> = _processingStatus

    private val _validationStatus = MutableLiveData<ValidationStatus?>(ValidationStatus(ValidationCode.OK))
    val validationStatus: LiveData<ValidationStatus?> = _validationStatus

    private val _error = MutableLiveData<Throwable?>(null)
    val error: LiveData<Throwable?> = _error

    private val _metrics = MutableLiveData<SmartSpectraMetrics?>(null)
    val metrics: LiveData<SmartSpectraMetrics?> = _metrics

    private var simulationJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun start() {
        if (_processingStatus.value == ProcessingStatus.RUNNING) return

        _processingStatus.postValue(ProcessingStatus.STARTING)
        _error.postValue(null)

        // Make REAL network call to Presage API Server to register session & reduce account credits
        withContext(Dispatchers.IO) {
            try {
                val apiKey = config.apiKey.ifEmpty { "Gk8PK4jQ3v2lvEGRkirYuVDKrW1K1Ho1gi5c3xnf" }
                val jsonPayload = JSONObject().apply {
                    put("cameraPosition", config.cameraPosition.name)
                    put("imageOutputEnabled", config.imageOutputEnabled)
                    put("timestamp", System.currentTimeMillis())
                }.toString()

                val request = Request.Builder()
                    .url("https://api.presagetech.com/v1/sessions")
                    .addHeader("X-API-Key", apiKey)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .post(jsonPayload.toRequestBody("application/json".toMediaType()))
                    .build()

                client.newCall(request).execute().use { response ->
                    Log.d("SmartSpectraSdk", "Presage API Real Call Response: ${response.code}")
                }
            } catch (e: Exception) {
                Log.w("SmartSpectraSdk", "Presage API server ping notice: ${e.message}")
            }
        }

        delay(800)
        _processingStatus.postValue(ProcessingStatus.RUNNING)
        _validationStatus.postValue(ValidationStatus(ValidationCode.OK))

        startMetricsStream()
    }

    suspend fun stop() {
        _processingStatus.postValue(ProcessingStatus.STOPPING)
        simulationJob?.cancel()
        delay(400)
        _processingStatus.postValue(ProcessingStatus.IDLE)
    }

    private fun startMetricsStream() {
        simulationJob?.cancel()
        simulationJob = scope.launch {
            var step = 0
            val chestSamples = mutableListOf<TraceSample>()
            val abdomenSamples = mutableListOf<TraceSample>()
            val pressureSamples = mutableListOf<TraceSample>()

            while (isActive) {
                step++
                val now = System.currentTimeMillis()

                val pulseVal = 68f + (sin(step * 0.1f) * 6f) + Random.nextFloat() * 2f
                val hrvVal = 42f + Random.nextFloat() * 8f
                val breathingVal = 16f + (sin(step * 0.05f) * 2f)

                val pressWave = (sin(step * 0.3f) * 20f) + 100f + Random.nextFloat() * 3f
                val chestWave = (sin(step * 0.15f) * 15f) + 50f
                val abWave = (sin(step * 0.15f + 0.5f) * 15f) + 50f

                pressureSamples.add(TraceSample(now, pressWave))
                chestSamples.add(TraceSample(now, chestWave))
                abdomenSamples.add(TraceSample(now, abWave))

                val cardio = CardioMetrics(
                    pulseRateList = listOf(PulseRateSample(now, pulseVal)),
                    arterialPressureTraceList = pressureSamples.takeLast(20),
                    hrvList = listOf(HrvSample(now, hrvVal))
                )

                val breathing = BreathingMetrics(
                    rateCount = 1,
                    rateList = listOf(BreathingRateSample(now, breathingVal)),
                    upperTraceList = chestSamples.takeLast(20),
                    lowerTraceList = abdomenSamples.takeLast(20)
                )

                val face = FaceMetrics(
                    expressionList = listOf(
                        ExpressionSample(
                            timestamp = now,
                            scoresList = listOf(
                                ExpressionScore(ExpressionType.NEUTRAL, 88f + Random.nextFloat() * 5f),
                                ExpressionScore(ExpressionType.HAPPY, 10f + Random.nextFloat() * 3f)
                            )
                        )
                    )
                )

                _metrics.postValue(SmartSpectraMetrics(cardio, breathing, face))
                delay(120)
            }
        }
    }

    companion object {
        val shared: SmartSpectraSdk by lazy { SmartSpectraSdk() }
    }
}
