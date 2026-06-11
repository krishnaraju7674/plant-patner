package com.example.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    val mimeType: String,
    val data: String // Base64 encoded image string
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Float? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

// --- Local Structured Plant Identification Response Model ---

@JsonClass(generateAdapter = true)
data class PlantCareResponse(
    val commonName: String,
    val scientificName: String,
    val family: String,
    val description: String,
    val wateringIntervalDays: Int,
    val wateringInstructions: String,
    val sunlightRequirements: String,
    val optimalTemperature: String,
    val toxicity: String,
    val soilPreference: String,
    val quickTips: List<String>
)

// --- Local Structured Plant troubleshooting Response Models ---

@JsonClass(generateAdapter = true)
data class SymptomCause(
    val cause: String,
    val explanation: String,
    val severity: String // e.g. "Low", "Medium", "High"
)

@JsonClass(generateAdapter = true)
data class TroubleshootingResponse(
    val plantCommonName: String,
    val identifiedSymptom: String,
    val confidenceRating: String,
    val diagnosisSummary: String,
    val possibleCauses: List<SymptomCause>,
    val immediateActions: List<String>,
    val preventativeCareList: List<String>,
    val wateringAdjustment: String,
    val lightAdjustment: String
)

// --- Local Structured Seasonal Climate Tips Response Model ---

@JsonClass(generateAdapter = true)
data class SeasonalTipsResponse(
    val regionInput: String,
    val climateZone: String,
    val currentSeason: String,
    val summary: String,
    val wateringAdjustments: String,
    val humidityAndTempTips: String,
    val pestPreventionTips: String,
    val fertilizationSeasonalTips: String,
    val recommendedPlants: List<String>,
    val quickActionBulletedTips: List<String>
)

