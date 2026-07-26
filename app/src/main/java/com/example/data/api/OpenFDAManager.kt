package com.example.data.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object OpenFDAManager {
    private const val TAG = "OpenFDAManager"
    private const val BASE_URL = "https://api.fda.gov/drug"
    private const val DAILYMED_URL = "https://dailymed.nlm.nih.gov/dailymed/services/v2/drugnames.json"
    private const val RXNORM_URL = "https://rxnav.nlm.nih.gov/REST/drugs.json"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(4, TimeUnit.SECONDS)
        .build()

    data class FdaDrugInfo(
        val medicineName: String,
        val brandName: String?,
        val genericName: String?,
        val purpose: String?,
        val warnings: String?,
        val adverseReactions: String?,
        val recallNotice: String?,
        val rxNormConcept: String?,
        val dailyMedName: String?
    )

    suspend fun queryDrugInformation(medicineName: String): FdaDrugInfo? = withContext(Dispatchers.IO) {
        val cleanQuery = medicineName.trim().lowercase()
        if (cleanQuery.isBlank()) return@withContext null

        try {
            var brandName: String? = null
            var genericName: String? = null
            var purpose: String? = null
            var warnings: String? = null
            var adverseReactions: String? = null
            var recallNotice: String? = null
            var rxNormConcept: String? = null
            var dailyMedName: String? = null

            // 1. Query Official OpenFDA Drug Label Endpoint
            try {
                val labelUrl = "$BASE_URL/label.json?search=openfda.brand_name:\"$cleanQuery\"+OR+openfda.generic_name:\"$cleanQuery\"+OR+\"$cleanQuery\"&limit=1"
                val labelRequest = Request.Builder().url(labelUrl).build()

                okHttpClient.newCall(labelRequest).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string() ?: ""
                        val json = JSONObject(responseBody)
                        val results = json.optJSONArray("results")
                        if (results != null && results.length() > 0) {
                            val item = results.getJSONObject(0)

                            val openfda = item.optJSONObject("openfda")
                            if (openfda != null) {
                                brandName = openfda.optJSONArray("brand_name")?.optString(0)
                                genericName = openfda.optJSONArray("generic_name")?.optString(0)
                            }

                            purpose = item.optJSONArray("purpose")?.optString(0)
                                ?: item.optJSONArray("indications_and_usage")?.optString(0)

                            warnings = item.optJSONArray("warnings")?.optString(0)
                                ?: item.optJSONArray("warnings_and_cautions")?.optString(0)

                            adverseReactions = item.optJSONArray("adverse_reactions")?.optString(0)
                                ?: item.optJSONArray("side_effects")?.optString(0)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "OpenFDA query exception for $cleanQuery: ${e.message}")
            }

            // 2. Query RxNorm (NIH / NLM) Endpoint
            try {
                val rxUrl = "$RXNORM_URL?name=$cleanQuery"
                val rxRequest = Request.Builder().url(rxUrl).build()
                okHttpClient.newCall(rxRequest).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: ""
                        val json = JSONObject(body)
                        val drugGroup = json.optJSONObject("drugGroup")
                        val conceptGroup = drugGroup?.optJSONArray("conceptGroup")
                        if (conceptGroup != null) {
                            for (i in 0 until conceptGroup.length()) {
                                val group = conceptGroup.getJSONObject(i)
                                val conceptProperties = group.optJSONArray("conceptProperties")
                                if (conceptProperties != null && conceptProperties.length() > 0) {
                                    rxNormConcept = conceptProperties.getJSONObject(0).optString("name", null)
                                    if (rxNormConcept != null) break
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "RxNorm query exception: ${e.message}")
            }

            // 3. Query DailyMed (NIH / NLM) Endpoint
            try {
                val dailyUrl = "$DAILYMED_URL?drug_name=$cleanQuery"
                val dailyRequest = Request.Builder().url(dailyUrl).build()
                okHttpClient.newCall(dailyRequest).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: ""
                        val json = JSONObject(body)
                        val data = json.optJSONArray("data")
                        if (data != null && data.length() > 0) {
                            dailyMedName = data.getJSONObject(0).optString("drug_name", null)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "DailyMed query exception: ${e.message}")
            }

            // 4. Query OpenFDA Drug Recalls
            try {
                val recallUrl = "$BASE_URL/enforcement.json?search=product_description:\"$cleanQuery\"&limit=1"
                val recallRequest = Request.Builder().url(recallUrl).build()

                okHttpClient.newCall(recallRequest).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string() ?: ""
                        val json = JSONObject(responseBody)
                        val results = json.optJSONArray("results")
                        if (results != null && results.length() > 0) {
                            val recallItem = results.getJSONObject(0)
                            val reason = recallItem.optString("reason_for_recall", "")
                            val classification = recallItem.optString("classification", "")
                            if (reason.isNotBlank()) {
                                recallNotice = "[$classification] $reason"
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Recall query exception: ${e.message}")
            }

            if (brandName != null || genericName != null || purpose != null || warnings != null || adverseReactions != null || recallNotice != null || rxNormConcept != null || dailyMedName != null) {
                FdaDrugInfo(
                    medicineName = medicineName,
                    brandName = brandName ?: dailyMedName,
                    genericName = genericName ?: rxNormConcept,
                    purpose = purpose?.take(300),
                    warnings = warnings?.take(400),
                    adverseReactions = adverseReactions?.take(400),
                    recallNotice = recallNotice?.take(300),
                    rxNormConcept = rxNormConcept,
                    dailyMedName = dailyMedName
                )
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching medical database info for $medicineName", e)
            null
        }
    }
}
