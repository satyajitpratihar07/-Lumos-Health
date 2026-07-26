package com.example.data.api

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

object ElevenLabsManager {
    private const val TAG = "ElevenLabsManager"
    private var mediaPlayer: MediaPlayer? = null

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun speakText(context: Context, text: String) = withContext(Dispatchers.IO) {
        // Use ElevenLabs API key from BuildConfig or environment
        val apiKey = try { com.example.BuildConfig.ELEVENLABS_API_KEY } catch (e: Exception) { "" }

        try {
            stopSpeaking()

            // ElevenLabs API Stream URL - Rachel voice ID: 21m00Tcm4TlvDq8ikWAM
            val voiceId = "21m00Tcm4TlvDq8ikWAM" 
            val url = "https://api.elevenlabs.io/v1/text-to-speech/$voiceId"

            val jsonPayload = JSONObject().apply {
                put("text", text)
                put("model_id", "eleven_monolingual_v1")
                val voiceSettings = JSONObject().apply {
                    put("stability", 0.5)
                    put("similarity_boost", 0.75)
                }
                put("voice_settings", voiceSettings)
            }.toString()

            val request = Request.Builder()
                .url(url)
                .addHeader("xi-api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(jsonPayload.toRequestBody("application/json".toMediaType()))
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bytes = response.body?.bytes() ?: return@withContext
                    val tempFile = File.createTempFile("tts_", ".mp3", context.cacheDir)
                    FileOutputStream(tempFile).use { fos ->
                        fos.write(bytes)
                    }

                    withContext(Dispatchers.Main) {
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(tempFile.absolutePath)
                            prepare()
                            start()
                            setOnCompletionListener {
                                tempFile.delete()
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "ElevenLabs TTS request failed: ${response.code}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing ElevenLabs TTS", e)
        }
    }

    fun stopSpeaking() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            // Ignored
        }
    }
}
