package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

/**
 * GoogleAuthManager — handles the full Google One Tap Sign-In flow.
 *
 * SETUP REQUIRED:
 * 1. Go to https://console.firebase.google.com → your project
 * 2. Authentication → Sign-in method → Enable Google
 * 3. Project Settings → Download google-services.json → place in health app/app/
 * 4. Project Settings → Your apps → Web Client ID → paste as WEB_CLIENT_ID below
 */
object GoogleAuthManager {
    private const val TAG = "GoogleAuthManager"

    // ── IMPORTANT: Replace with your real Web Client ID from Firebase Console ──
    // Firebase Console → Project Settings → Your apps → Web client (auto created) → Client ID
    const val WEB_CLIENT_ID = "192042747667-g0un80f96kfp8phf8ga9mqrl2qmsd9fl.apps.googleusercontent.com"

    data class GoogleUser(
        val uid: String,
        val email: String,
        val displayName: String,
        val photoUrl: String?
    )

    suspend fun signIn(context: Context): Result<GoogleUser> {
        if (WEB_CLIENT_ID.startsWith("YOUR_WEB")) {
            return Result.failure(
                IllegalStateException(
                    "❌ Web Client ID not configured!\n" +
                    "Open GoogleAuthManager.kt and set WEB_CLIENT_ID from Firebase Console → " +
                    "Project Settings → Your Apps → Web Client ID"
                )
            )
        }

        return try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context
            )

            handleSignInResult(response)
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "Google sign-in cancelled by user")
            Result.failure(Exception("Sign-in cancelled"))
        } catch (e: NoCredentialException) {
            Log.e(TAG, "No Google account found on device")
            Result.failure(Exception("No Google account found. Please add a Google account in your device settings."))
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Google sign-in error: ${e.message}")
            Result.failure(Exception("Google sign-in failed: ${e.message}"))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during Google sign-in", e)
            Result.failure(e)
        }
    }

    private suspend fun handleSignInResult(response: GetCredentialResponse): Result<GoogleUser> {
        val credential = response.credential

        if (credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return Result.failure(Exception("Unexpected credential type received"))
        }

        return try {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken

            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = FirebaseAuth.getInstance()
                .signInWithCredential(firebaseCredential)
                .await()

            val firebaseUser: FirebaseUser = authResult.user
                ?: return Result.failure(Exception("Firebase sign-in returned null user"))

            Log.d(TAG, "✅ Google Sign-In successful: ${firebaseUser.email}")

            Result.success(
                GoogleUser(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: googleIdTokenCredential.id,
                    displayName = firebaseUser.displayName ?: googleIdTokenCredential.displayName ?: "User",
                    photoUrl = firebaseUser.photoUrl?.toString()
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Firebase credential sign-in failed", e)
            Result.failure(Exception("Authentication failed: ${e.message}"))
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        Log.d(TAG, "User signed out from Google/Firebase")
    }

    fun getCurrentUser(): FirebaseUser? = FirebaseAuth.getInstance().currentUser
}
