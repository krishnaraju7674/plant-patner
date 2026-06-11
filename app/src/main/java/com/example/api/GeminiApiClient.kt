package com.example.api

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun identifyPlant(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    private fun sanitizeJson(raw: String?): String {
        if (raw == null) return ""
        var cleaned = raw.trim()
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7)
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3)
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length - 3)
        }
        return cleaned.trim()
    }

    suspend fun analyzePlantImage(bitmap: Bitmap): PlantCareResponse? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is not configured or uses placeholder.")
            return@withContext null
        }

        val base64Image = bitmap.toBase64()
        val prompt = """
            Analyze this image and identify the plant. If no plant is found or the image is not a plant, identify the closest matches anyway or state it looks like a plant, but provide the most helpful botanical information.
            You must output your response strictly as a JSON object matching this structure:
            {
               "commonName": "Common Name (e.g. Monstera)",
               "scientificName": "Scientific Name",
               "family": "Botanical Family",
               "description": "Short, engaging summary of the plant.",
               "wateringIntervalDays": 7, (MUST be a number, represents typical watering interval in days, e.g. 7, 10, 14, or 21)
               "wateringInstructions": "Clear instruction on when and how to water.",
               "sunlightRequirements": "Sunlight level (e.g. Bright Indirect Light, Full Sun, Low Light)",
               "optimalTemperature": "Optimal temperature range, e.g. 18°C - 24°C",
               "toxicity": "Details about pet and human toxicity",
               "soilPreference": "Preferred soil type, e.g. Well-draining peat mix",
               "quickTips": [
                  "Tip 1",
                  "Tip 2",
                  "Tip 3"
               ]
            }
            Do not enclose the JSON inside markdown code blocks (such as ```json or ```). Return the raw JSON string only. Be helpful, professional, and detailed.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = prompt),
                        Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Image))
                    )
                )
            ),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.2f
            )
        )

        try {
            val response = apiService.identifyPlant(apiKey, request)
            val fullText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (fullText != null) {
                val cleanedJson = sanitizeJson(fullText)
                val adapter = moshi.adapter(PlantCareResponse::class.java)
                return@withContext adapter.fromJson(cleanedJson)
            } else {
                Log.e(TAG, "No contents returned in candidate.")
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error matching plant image: ", e)
            return@withContext null
        }
    }

    suspend fun diagnosePlantSymptoms(symptom: String, plantName: String): TroubleshootingResponse? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is not configured or uses placeholder.")
            return@withContext null
        }

        val prompt = """
            Symptom Diagnosis Request for plant: $plantName.
            The observed symptoms are: $symptom.
            
            Analyze these symptoms specifically for a $plantName (if it is a general houseplant, provide general plant care troubleshooting context). 
            Determine the potential causes, immediate relief/treatment actions, and preventive solutions.
            
            You must output your response strictly as a JSON object matching this structure:
            {
               "plantCommonName": "$plantName",
               "identifiedSymptom": "Summary of the observed symptoms",
               "confidenceRating": "High/Moderate/Low depending on symptom specificity",
               "diagnosisSummary": "A concise expert botanical summary of the situation.",
               "possibleCauses": [
                  {
                     "cause": "Specific Cause (e.g. Overwatering)",
                     "explanation": "Detailed explanation of why this happens and what it does to $plantName.",
                     "severity": "High"
                  }
               ],
               "immediateActions": [
                  "Action step 1",
                  "Action step 2"
               ],
               "preventativeCareList": [
                  "Long-term care adjustment 1",
                  "Long-term care adjustment 2"
               ],
               "wateringAdjustment": "Specific instructions for moisture or water frequency adjustments.",
               "lightAdjustment": "Specific instructions for light exposure adjustments."
            }
            Do not enclose the JSON inside markdown code blocks (such as ```json or ```). Return the raw JSON string only. Be helpful, professional, and detailed.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = prompt)
                    )
                )
            ),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.2f
            )
        )

        try {
            val response = apiService.identifyPlant(apiKey, request)
            val fullText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (fullText != null) {
                val cleanedJson = sanitizeJson(fullText)
                val adapter = moshi.adapter(TroubleshootingResponse::class.java)
                return@withContext adapter.fromJson(cleanedJson)
            } else {
                Log.e(TAG, "No contents returned in candidate for troubleshooting.")
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error diagnosing symptoms: ", e)
            return@withContext null
        }
    }

    suspend fun getSeasonalCareTips(location: String): SeasonalTipsResponse? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is not configured or uses placeholder.")
            return@withContext null
        }

        val prompt = """
            Seasonal Care Tips request for region/location: $location.
            
            Identify the user's climate zone, the current season (for the physical date around June 2026), and provide expert care guidelines for indoor houseplants in that location.
            
            You must output your response strictly as a JSON object matching this structure:
            {
               "regionInput": "$location",
               "climateZone": "Identified Climate Zone (e.g. USDA Zone 9b, Tropical Wet, Temperate Continental)",
               "currentSeason": "Current Season (e.g. Summer, Winter, Monsoon)",
               "summary": "Brief climate and seasonal environmental assessment summary.",
               "wateringAdjustments": "How typical houseplant watering frequencies should be adapted for this season.",
               "humidityAndTempTips": "Specific tips for indoor climate/air conditioning/heating/humidity modifications.",
               "pestPreventionTips": "Key pests active during this season and prevention/eradication instructions.",
               "fertilizationSeasonalTips": "Fertilizing instructions for active or dormant periods during this season.",
               "recommendedPlants": [
                  "Plant 1 that thrives in this zone",
                  "Plant 2 that thrives in this zone"
               ],
               "quickActionBulletedTips": [
                  "Shorthand tactical tip 1",
                  "Shorthand tactical tip 2",
                  "Shorthand tactical tip 3"
               ]
            }
            Do not enclose the JSON inside markdown code blocks (such as ```json or ```). Return the raw JSON string only. Be helpful, professional, and detailed.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = prompt)
                    )
                )
            ),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.2f
            )
        )

        try {
            val response = apiService.identifyPlant(apiKey, request)
            val fullText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (fullText != null) {
                val cleanedJson = sanitizeJson(fullText)
                val adapter = moshi.adapter(SeasonalTipsResponse::class.java)
                return@withContext adapter.fromJson(cleanedJson)
            } else {
                Log.e(TAG, "No contents returned in candidate for seasonal tips.")
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting seasonal care tips: ", e)
            return@withContext null
        }
    }
}
