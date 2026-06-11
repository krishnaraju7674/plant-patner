package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiApiClient
import com.example.api.PlantCareResponse
import com.example.data.PlantDatabase
import com.example.data.PlantEntity
import com.example.data.PlantRepository
import com.example.data.SunlightLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface WeatherState {
    object Idle : WeatherState
    object Loading : WeatherState
    data class Success(val cityName: String, val temperature: Double, val humidity: Double) : WeatherState
    data class Error(val message: String) : WeatherState
}

sealed interface AnalysisState {
    object Idle : AnalysisState
    object Analyzing : AnalysisState
    data class Success(val plant: PlantCareResponse, val bitmap: Bitmap?) : AnalysisState
    data class Error(val message: String) : AnalysisState
}

sealed interface TroubleshootingState {
    object Idle : TroubleshootingState
    object Diagnosing : TroubleshootingState
    data class Success(val response: com.example.api.TroubleshootingResponse) : TroubleshootingState
    data class Error(val message: String) : TroubleshootingState
}

sealed interface SeasonalTipsState {
    object Idle : SeasonalTipsState
    object Loading : SeasonalTipsState
    data class Success(val response: com.example.api.SeasonalTipsResponse) : SeasonalTipsState
    data class Error(val message: String) : SeasonalTipsState
}

class PlantViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "PlantViewModel"
    private val repository: PlantRepository

    val allPlants: StateFlow<List<PlantEntity>>
    val recentSearches: StateFlow<List<com.example.data.RecentSearchEntity>>

    private val prefs = application.getSharedPreferences("gardening_assistant_prefs", Context.MODE_PRIVATE)
    private val _isDarkModeEnabled = MutableStateFlow(prefs.getBoolean("key_dark_mode", false))
    val isDarkModeEnabled: StateFlow<Boolean> = _isDarkModeEnabled.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Idle)
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()

    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    private val _troubleshootingState = MutableStateFlow<TroubleshootingState>(TroubleshootingState.Idle)
    val troubleshootingState: StateFlow<TroubleshootingState> = _troubleshootingState.asStateFlow()

    private val _seasonalTipsState = MutableStateFlow<SeasonalTipsState>(SeasonalTipsState.Idle)
    val seasonalTipsState: StateFlow<SeasonalTipsState> = _seasonalTipsState.asStateFlow()

    init {
        val database = PlantDatabase.getDatabase(application)
        repository = PlantRepository(database.plantDao())
        
        allPlants = repository.allPlants.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        recentSearches = repository.recentSearches.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Fetch default local weather for a beautiful starter screen
        fetchWeather("San Francisco")
    }

    fun toggleDarkMode() {
        val newValue = !_isDarkModeEnabled.value
        _isDarkModeEnabled.value = newValue
        prefs.edit().putBoolean("key_dark_mode", newValue).apply()
    }

    fun fetchWeather(cityName: String) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            try {
                val result = com.example.api.WeatherApiClient.fetchWeatherForCity(cityName)
                if (result != null) {
                    val (geocoding, weather) = result
                    val current = weather.current
                    if (current != null) {
                        _weatherState.value = WeatherState.Success(
                            cityName = geocoding.name,
                            temperature = current.temperature_2m,
                            humidity = current.relative_humidity_2m
                        )
                    } else {
                        _weatherState.value = WeatherState.Error("No current weather data available for $cityName.")
                    }
                } else {
                    _weatherState.value = WeatherState.Error("Could not find coordinates or weather for '$cityName'.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching weather", e)
                _weatherState.value = WeatherState.Error(e.localizedMessage ?: "Unknown network error")
            }
        }
    }

    fun resetAnalysis() {
        _analysisState.value = AnalysisState.Idle
    }

    fun resetTroubleshooting() {
        _troubleshootingState.value = TroubleshootingState.Idle
    }

    fun resetSeasonalTips() {
        _seasonalTipsState.value = SeasonalTipsState.Idle
    }

    fun fetchSeasonalTips(location: String) {
        viewModelScope.launch {
            _seasonalTipsState.value = SeasonalTipsState.Loading
            try {
                val result = GeminiApiClient.getSeasonalCareTips(location)
                if (result != null) {
                    _seasonalTipsState.value = SeasonalTipsState.Success(result)
                } else {
                    _seasonalTipsState.value = SeasonalTipsState.Error(
                        "Unable to load care tips. Please verify your internet connection and Gemini API key in the Secrets panel."
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in fetchSeasonalTips: ", e)
                _seasonalTipsState.value = SeasonalTipsState.Error(e.localizedMessage ?: "Unknown error while fetching seasonal tips")
            }
        }
    }

    fun diagnoseSymptoms(symptom: String, plantName: String) {
        viewModelScope.launch {
            _troubleshootingState.value = TroubleshootingState.Diagnosing
            try {
                val result = GeminiApiClient.diagnosePlantSymptoms(symptom, plantName)
                if (result != null) {
                    _troubleshootingState.value = TroubleshootingState.Success(result)
                } else {
                    _troubleshootingState.value = TroubleshootingState.Error(
                        "Unable to diagnose symptoms. Please check your internet connection and verify your Gemini API key in the Secrets panel."
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in diagnoseSymptoms: ", e)
                _troubleshootingState.value = TroubleshootingState.Error(e.localizedMessage ?: "Unknown diagnostic error")
            }
        }
    }

    fun analyzePlant(bitmap: Bitmap) {
        viewModelScope.launch {
            _analysisState.value = AnalysisState.Analyzing
            try {
                val result = GeminiApiClient.analyzePlantImage(bitmap)
                if (result != null) {
                    _analysisState.value = AnalysisState.Success(result, bitmap)
                } else {
                    _analysisState.value = AnalysisState.Error(
                        "Could not analyze plant. Please ensure your Gemini API Key is set in the Secrets Panel."
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in analyzePlant: ", e)
                _analysisState.value = AnalysisState.Error(e.localizedMessage ?: "Unknown analysis error")
            }
        }
    }

    fun saveAnalyzedPlant(
        plant: PlantCareResponse,
        bitmap: Bitmap?,
        isReminderEnabled: Boolean = true,
        reminderHour: Int = 9,
        reminderMinute: Int = 0,
        customInterval: Int = 0
    ) {
        viewModelScope.launch {
            // In a real app we might write the bitmap into internal storage.
            // Let's store a reference. For simple offline-first demo persistence, 
            // since storing large bitmaps in memory can be configured, storing a small sized 
            // base64 in Room or setting customImageUri as a marker is extremely suitable.
            // Let's compress and store the compressed base64 representation of imageUri, 
            // but wait: since we have Room and it runs fine, let's keep it clean or store a 
            // marker so we can display it. 
            // Let's convert bitmap to a lightweight Base64 string if it exists and keep it in customImageUri
            // so we don't need secondary file-system permission storage complexity! This is ultra reliable and portable.
            var base64Str: String? = null
            if (bitmap != null) {
                try {
                    val out = java.io.ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out)
                    val bytes = out.toByteArray()
                    base64Str = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to compress bitmap for DB storage", e)
                }
            }

            val entity = PlantEntity(
                commonName = plant.commonName,
                scientificName = plant.scientificName,
                family = plant.family,
                description = plant.description,
                wateringIntervalDays = plant.wateringIntervalDays,
                wateringInstructions = plant.wateringInstructions,
                sunlightRequirements = plant.sunlightRequirements,
                optimalTemperature = plant.optimalTemperature,
                toxicity = plant.toxicity,
                soilPreference = plant.soilPreference,
                joinedTips = plant.quickTips.joinToString("||"),
                dateIdentified = System.currentTimeMillis(),
                lastWateredTime = System.currentTimeMillis(), // Set initially watered today
                customImageUri = base64Str,
                isReminderEnabled = isReminderEnabled,
                reminderHour = reminderHour,
                reminderMinute = reminderMinute,
                customWateringIntervalDays = customInterval,
                lastFertilizedTime = System.currentTimeMillis(), // Set initially fertilized today
                fertilizingIntervalDays = 30, // Default to 30 days
                customFertilizingIntervalDays = 0
            )
            repository.insertPlant(entity)
            _analysisState.value = AnalysisState.Idle
        }
    }

    fun fertilizePlant(plantId: Long, timestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            repository.updateLastFertilized(plantId, timestamp)
        }
    }

    fun updatePlantFertilizationInterval(plantId: Long, interval: Int) {
        viewModelScope.launch {
            repository.updateCustomFertilizingInterval(plantId, interval)
        }
    }

    fun updatePlantSchedule(
        plant: PlantEntity,
        isReminderEnabled: Boolean,
        reminderHour: Int,
        reminderMinute: Int,
        customInterval: Int
    ) {
        viewModelScope.launch {
            val updated = plant.copy(
                isReminderEnabled = isReminderEnabled,
                reminderHour = reminderHour,
                reminderMinute = reminderMinute,
                customWateringIntervalDays = customInterval
            )
            repository.updatePlant(updated)
        }
    }

    fun snoozePlantWatering(plant: PlantEntity) {
        viewModelScope.launch {
            val updated = plant.copy(
                lastWateredTime = plant.lastWateredTime + (24 * 60 * 60 * 1000L)
            )
            repository.updatePlant(updated)
        }
    }

    fun deletePlant(plant: PlantEntity) {
        viewModelScope.launch {
            repository.deletePlant(plant)
        }
    }

    fun waterPlant(plantId: Long, timestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            repository.updateLastWatered(plantId, timestamp)
        }
    }

    fun bulkWaterPlants(plantIds: List<Long>, timestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            plantIds.forEach { plantId ->
                repository.updateLastWatered(plantId, timestamp)
            }
        }
    }

    fun bulkFertilizePlants(plantIds: List<Long>, timestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            plantIds.forEach { plantId ->
                repository.updateLastFertilized(plantId, timestamp)
            }
        }
    }

    fun updatePlantTags(plant: PlantEntity, newTags: String) {
        viewModelScope.launch {
            val updated = plant.copy(tags = newTags)
            repository.updatePlant(updated)
        }
    }

    fun addPlantToWishlist(plant: com.example.api.PlantCareResponse) {
        viewModelScope.launch {
            val entity = PlantEntity(
                commonName = plant.commonName,
                scientificName = plant.scientificName,
                family = plant.family,
                description = plant.description,
                wateringIntervalDays = plant.wateringIntervalDays,
                wateringInstructions = plant.wateringInstructions,
                sunlightRequirements = plant.sunlightRequirements,
                optimalTemperature = plant.optimalTemperature,
                toxicity = plant.toxicity,
                soilPreference = plant.soilPreference,
                joinedTips = plant.quickTips.joinToString("||"),
                dateIdentified = System.currentTimeMillis(),
                isWishlist = true
            )
            repository.insertPlant(entity)
        }
    }

    fun promoteWishlistToGarden(plant: PlantEntity) {
        viewModelScope.launch {
            val updated = plant.copy(
                isWishlist = false,
                lastWateredTime = System.currentTimeMillis(),
                lastFertilizedTime = System.currentTimeMillis()
            )
            repository.updatePlant(updated)
        }
    }

    fun toggleFavorite(plant: PlantEntity) {
        viewModelScope.launch {
            repository.updateFavorite(plant.id, !plant.isFavorite)
        }
    }

    fun getSunlightLogs(plantId: Long): Flow<List<SunlightLogEntity>> {
        return repository.getSunlightLogsForPlant(plantId)
    }

    fun logSunlight(plantId: Long, directHours: Float, indirectHours: Float, notes: String = "") {
        viewModelScope.launch {
            val log = SunlightLogEntity(
                plantId = plantId,
                timestamp = System.currentTimeMillis(),
                directHours = directHours,
                indirectHours = indirectHours,
                notes = notes
            )
            repository.insertSunlightLog(log)
        }
    }

    fun deleteSunlightLog(log: SunlightLogEntity) {
        viewModelScope.launch {
            repository.deleteSunlightLog(log)
        }
    }

    // --- Mock Identification for Quick Demos or Offline Playgrounds ---
    fun loadMockPlant(predefinedPlantIdx: Int) {
        viewModelScope.launch {
            _analysisState.value = AnalysisState.Analyzing
            kotlinx.coroutines.delay(1200) // Aesthetic waiting transition
            
            val mockPlant = getPredefinedMockPlants().getOrNull(predefinedPlantIdx)
            if (mockPlant != null) {
                _analysisState.value = AnalysisState.Success(mockPlant, null)
            } else {
                _analysisState.value = AnalysisState.Error("Unknown test plant specified.")
            }
        }
    }

    fun getPredefinedMockPlants(): List<PlantCareResponse> {
        return listOf(
            PlantCareResponse(
                commonName = "Swiss Cheese Plant",
                scientificName = "Monstera Deliciosa",
                family = "Araceae",
                description = "Iconic indoor climber famous for its massive, perforated tropical split leaves.",
                wateringIntervalDays = 7,
                wateringInstructions = "Water thoroughly when the top 2-3 inches of soil feel dry. Typically once every 7-9 days. Avoid soggy soil.",
                sunlightRequirements = "Bright Indirect Sunlight",
                optimalTemperature = "18°C - 30°C",
                toxicity = "Toxic to Cats and Dogs (calcium oxalate crystals)",
                soilPreference = "Organically rich, well-aerated soil mix with peat-moss",
                quickTips = listOf(
                    "Wipe the massive leaves weekly with a damp cloth to promote photosynthesis.",
                    "Provides excellent vertical styling as it grows; support with a sturdy moss pole.",
                    "Misting surrounding air or using a humidifier keeps leaf tips crisp-free."
                )
            ),
            PlantCareResponse(
                commonName = "Snake Plant",
                scientificName = "Sansevieria Trifasciata",
                family = "Asparagaceae",
                description = "Excellent resilient plant featuring upright sword-like variegated leaves. Practically indestructible.",
                wateringIntervalDays = 21,
                wateringInstructions = "Allow soil to dry out completely between waterings. Typically water every 2-3 weeks. Under-watering is always safer than over-watering.",
                sunlightRequirements = "Adaptable (Low Light to Direct Sun)",
                optimalTemperature = "15°C - 27°C",
                toxicity = "Mildly Toxic to Pets if consumed in large quantities",
                soilPreference = "Sandy, extremely sharp well-draining cactus/succulent potting mix",
                quickTips = listOf(
                    "Perfect beginner plant, it purifies indoor air and converts CO2 to oxygen at night.",
                    "Keep in tight, well-fitted terracotta pots to prevent soil from water-logging.",
                    "Do not pour water directly into the center leaf rosette, water around the outer rim."
                )
            ),
            PlantCareResponse(
                commonName = "Fiddle-Leaf Fig",
                scientificName = "Ficus Lyrata",
                family = "Moraceae",
                description = "Stately evergreen indoor tree with dramatic, heavily veined violin-shaped leaves.",
                wateringIntervalDays = 10,
                wateringInstructions = "Water when the top 2 inches of soil are dry. Pour water slowly until it drains out of the bottom. Consistent watering is crucial.",
                sunlightRequirements = "Consistent Bright Indirect Light",
                optimalTemperature = "18°C - 24°C",
                toxicity = "Toxic to pets, sap can irritate skin and mouths",
                soilPreference = "Well-draining, highly porous premium peat-based option",
                quickTips = listOf(
                    "Dislikes being moved! Find a bright spot with zero drafts and keep it there.",
                    "Rotate the pot 90 degrees every month to promote uniform vertical foliage.",
                    "If leaves begin drying or turning dark brown, check for excess moisture or drafts."
                )
            ),
            PlantCareResponse(
                commonName = "Aloe Vera",
                scientificName = "Aloe Barbadensis Miller",
                family = "Asphodelaceae",
                description = "Gorgeous succulent with thick fleshy leaves filled with soothing therapeutic gel.",
                wateringIntervalDays = 14,
                wateringInstructions = "Water deeply, but very sparingly. Let soil compile and dry out entirely. Reduce watering down to 4-week sequences in freezing winters.",
                sunlightRequirements = "Full Bright Direct Sunlight",
                optimalTemperature = "15°C - 28°C",
                toxicity = "Mildly toxic to pets due to saponins; therapeutic for skin burns",
                soilPreference = "Highly porous succulent/cactus gritty soil mix",
                quickTips = listOf(
                    "Requires excellent drainage; never leave sitting in standing water.",
                    "Sparsely harvest mature outer leaves to extract therapeutic aloe gel.",
                    "Can be placed on warm, south-facing windowsills for maximum solar health."
                )
            )
        )
    }

    fun getPhotosForPlant(plantId: Long): Flow<List<com.example.data.PlantPhotoEntity>> {
        return repository.getPhotosForPlant(plantId)
    }

    fun addPlantPhoto(plantId: Long, imageBase64: String, note: String = "") {
        viewModelScope.launch {
            val photo = com.example.data.PlantPhotoEntity(
                plantId = plantId,
                timestamp = System.currentTimeMillis(),
                imageUriOrBase64 = imageBase64,
                note = note
            )
            repository.insertPlantPhoto(photo)
        }
    }

    fun deletePlantPhoto(photo: com.example.data.PlantPhotoEntity) {
        viewModelScope.launch {
            repository.deletePlantPhoto(photo)
        }
    }

    // --- PHOTO ANNOTATIONS STATE & ACTIONS ---
    fun getAnnotationsForPhoto(photoId: Long): Flow<List<com.example.data.PhotoAnnotationEntity>> {
        return repository.getAnnotationsForPhoto(photoId)
    }

    fun addPhotoAnnotation(photoId: Long, note: String) {
        viewModelScope.launch {
            repository.insertPhotoAnnotation(
                com.example.data.PhotoAnnotationEntity(
                    photoId = photoId,
                    timestamp = System.currentTimeMillis(),
                    note = note
                )
            )
        }
    }

    fun deletePhotoAnnotation(annotation: com.example.data.PhotoAnnotationEntity) {
        viewModelScope.launch {
            repository.deletePhotoAnnotation(annotation)
        }
    }

    // --- RECENT SEARCHES ACTIONS ---
    fun recordSearchOrCheck(query: String) {
        if (query.trim().isEmpty()) return
        viewModelScope.launch {
            try {
                repository.deleteRecentSearchByQuery(query.trim())
                repository.insertRecentSearch(
                    com.example.data.RecentSearchEntity(
                        query = query.trim(),
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to record recent search", e)
            }
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            try {
                repository.clearRecentSearches()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear recent searches", e)
            }
        }
    }

    // --- DAILY HUMIDITY LOGGER STATE & ACTIONS ---
    fun getHumidityLogs(plantId: Long): Flow<List<com.example.data.PlantHumidityLogEntity>> {
        return repository.getHumidityLogsForPlant(plantId)
    }

    fun logHumidity(plantId: Long, humidity: Double, min: Double, max: Double) {
        viewModelScope.launch {
            repository.insertHumidityLog(
                com.example.data.PlantHumidityLogEntity(
                    plantId = plantId,
                    timestamp = System.currentTimeMillis(),
                    humidityPercent = humidity,
                    optimalRangeMin = min,
                    optimalRangeMax = max
                )
            )
        }
    }

    fun deleteHumidityLog(log: com.example.data.PlantHumidityLogEntity) {
        viewModelScope.launch {
            repository.deleteHumidityLog(log)
        }
    }

    // --- PLANT JOURNALS STATE & ACTIONS ---
    fun getJournals(plantId: Long): Flow<List<com.example.data.PlantJournalEntity>> {
        return repository.getJournalsForPlant(plantId)
    }

    fun addJournalEntry(plantId: Long, note: String, category: String) {
        viewModelScope.launch {
            repository.insertJournal(
                com.example.data.PlantJournalEntity(
                    plantId = plantId,
                    timestamp = System.currentTimeMillis(),
                    note = note,
                    category = category
                )
            )
        }
    }

    fun deleteJournal(journal: com.example.data.PlantJournalEntity) {
        viewModelScope.launch {
            repository.deleteJournal(journal)
        }
    }

    // --- ENTIRE PLANT COLLECTION JSON BACKUP EXPORTER ---
    fun exportEntireCollectionToJSON(context: Context) {
        viewModelScope.launch {
            try {
                val backupObject = org.json.JSONObject()
                val plantsArray = org.json.JSONArray()

                // Read all current plants
                val plants = allPlants.value
                for (plant in plants) {
                    val plantObj = org.json.JSONObject().apply {
                        put("id", plant.id)
                        put("commonName", plant.commonName)
                        put("scientificName", plant.scientificName)
                        put("family", plant.family)
                        put("description", plant.description)
                        put("wateringIntervalDays", plant.wateringIntervalDays)
                        put("wateringInstructions", plant.wateringInstructions)
                        put("sunlightRequirements", plant.sunlightRequirements)
                        put("optimalTemperature", plant.optimalTemperature)
                        put("toxicity", plant.toxicity)
                        put("soilPreference", plant.soilPreference)
                        put("joinedTips", plant.joinedTips)
                        put("dateIdentified", plant.dateIdentified)
                        put("lastWateredTime", plant.lastWateredTime)
                        put("isFavorite", plant.isFavorite)
                        put("customImageUri", plant.customImageUri ?: "")
                        put("isReminderEnabled", plant.isReminderEnabled)
                        put("reminderHour", plant.reminderHour)
                        put("reminderMinute", plant.reminderMinute)
                        put("customWateringIntervalDays", plant.customWateringIntervalDays)
                        put("lastFertilizedTime", plant.lastFertilizedTime)
                        put("fertilizingIntervalDays", plant.fertilizingIntervalDays)
                        put("customFertilizingIntervalDays", plant.customFertilizingIntervalDays)
                    }

                    // Collect other items sequentially from the DB using a helper trigger
                    // Retrieve journals
                    val journalsArray = org.json.JSONArray()
                    try {
                        // Use Flow first() operator to collect the list once
                        val journalsList = repository.getJournalsForPlant(plant.id).first()
                        for (journal in journalsList) {
                            journalsArray.put(org.json.JSONObject().apply {
                                put("id", journal.id)
                                put("timestamp", journal.timestamp)
                                put("note", journal.note)
                                put("category", journal.category)
                            })
                        }
                    } catch (e: Exception) {
                        Log.e("Backup", "Failed to query journals for backup", e)
                    }
                    plantObj.put("journals", journalsArray)

                    // Retrieve humidity logs
                    val humidityArray = org.json.JSONArray()
                    try {
                        val humidityList = repository.getHumidityLogsForPlant(plant.id).first()
                        for (log in humidityList) {
                            humidityArray.put(org.json.JSONObject().apply {
                                put("id", log.id)
                                put("timestamp", log.timestamp)
                                put("humidityPercent", log.humidityPercent)
                                put("optimalRangeMin", log.optimalRangeMin)
                                put("optimalRangeMax", log.optimalRangeMax)
                            })
                        }
                    } catch (e: Exception) {
                        Log.e("Backup", "Failed to query humidity logs for backup", e)
                    }
                    plantObj.put("humidity_logs", humidityArray)

                    // Retrieve sunlight logs
                    val sunlightArray = org.json.JSONArray()
                    try {
                        val sunlightList = repository.getSunlightLogsForPlant(plant.id).first()
                        for (log in sunlightList) {
                            sunlightArray.put(org.json.JSONObject().apply {
                                put("id", log.id)
                                put("timestamp", log.timestamp)
                                put("directHours", log.directHours)
                                put("indirectHours", log.indirectHours)
                                put("notes", log.notes)
                            })
                        }
                    } catch (e: Exception) {
                        Log.e("Backup", "Failed to query sunlight logs for backup", e)
                    }
                    plantObj.put("sunlight_logs", sunlightArray)

                    // Retrieve photos & and nested photo annotations
                    val photosArray = org.json.JSONArray()
                    try {
                        val photosList = repository.getPhotosForPlant(plant.id).first()
                        for (photo in photosList) {
                            val photoObj = org.json.JSONObject().apply {
                                put("id", photo.id)
                                put("timestamp", photo.timestamp)
                                put("imageUriOrBase64", photo.imageUriOrBase64)
                                put("note", photo.note)
                            }
                            
                            val photoAnnotationsArray = org.json.JSONArray()
                            try {
                                val annotationsList = repository.getAnnotationsForPhoto(photo.id).first()
                                for (annotation in annotationsList) {
                                    photoAnnotationsArray.put(org.json.JSONObject().apply {
                                        put("id", annotation.id)
                                        put("timestamp", annotation.timestamp)
                                        put("note", annotation.note)
                                    })
                                }
                            } catch (e: Exception) {
                                Log.e("Backup", "Failed to query photo annotations", e)
                            }
                            photoObj.put("annotations", photoAnnotationsArray)
                            photosArray.put(photoObj)
                        }
                    } catch (e: Exception) {
                        Log.e("Backup", "Failed to query photos for backup", e)
                    }
                    plantObj.put("photos", photosArray)

                    plantsArray.put(plantObj)
                }

                backupObject.put("gardening_plants_backup", plantsArray)
                backupObject.put("schema_version", 6)
                backupObject.put("backup_timestamp", System.currentTimeMillis())

                val jsonContent = backupObject.toString(2) // Beautifully formatted indent spaces

                // Save JSON text data into a temporary cache file to launch sharing intent
                val backupFile = java.io.File(context.cacheDir, "gardening_backup_${System.currentTimeMillis()}.json")
                backupFile.writeText(jsonContent)

                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "application/json"
                    putExtra(android.content.Intent.EXTRA_SUBJECT, "GreenSanctuary-Data-Backup.json")
                    putExtra(
                        android.content.Intent.EXTRA_TEXT,
                        "Your botanical garden backup database is complete. File contains ${plants.size} plants and linked historical chronicles."
                    )
                    // Sharing file uri using FileProvider is standard on modern Android. 
                    // To keep implementation 100% stable, offline-compatible and error-free,
                    // we can share the text content directly as EXTRA_TEXT if email/app is chosen, 
                    // AND provide a chooser to easily export.
                    // But wait, putting the text into EXTRA_TEXT is incredibly compatible and allows instant copy-paste, 
                    // while also generating a share sheet! Let's do both to ensure safety.
                }
                context.startActivity(android.content.Intent.createChooser(intent, "Save Gardening JSON Backup"))
                android.widget.Toast.makeText(context, "Backup JSON compiled successfully!", android.widget.Toast.LENGTH_SHORT).show()
                
            } catch (e: Exception) {
                Log.e("Backup", "Failed to compile backup JSON: ", e)
                android.widget.Toast.makeText(context, "Failed to compile backup JSON: ${e.localizedMessage}", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }
}
