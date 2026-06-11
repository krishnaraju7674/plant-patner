package com.example.data

import kotlinx.coroutines.flow.Flow

class PlantRepository(private val plantDao: PlantDao) {
    val allPlants: Flow<List<PlantEntity>> = plantDao.getAllCapturedPlants()

    fun getPlantById(id: Long): Flow<PlantEntity?> = plantDao.getPlantById(id)

    suspend fun insertPlant(plant: PlantEntity): Long = plantDao.insertPlant(plant)

    suspend fun updatePlant(plant: PlantEntity) = plantDao.updatePlant(plant)

    suspend fun deletePlant(plant: PlantEntity) = plantDao.deletePlant(plant)

    suspend fun updateLastWatered(id: Long, timestamp: Long) = plantDao.updateLastWatered(id, timestamp)

    suspend fun updateLastFertilized(id: Long, timestamp: Long) = plantDao.updateLastFertilized(id, timestamp)

    suspend fun updateCustomFertilizingInterval(id: Long, interval: Int) = plantDao.updateCustomFertilizingInterval(id, interval)

    suspend fun updateFavorite(id: Long, isFav: Boolean) = plantDao.updateFavorite(id, isFav)

    fun getSunlightLogsForPlant(plantId: Long): Flow<List<SunlightLogEntity>> = plantDao.getSunlightLogsForPlant(plantId)

    suspend fun insertSunlightLog(log: SunlightLogEntity) = plantDao.insertSunlightLog(log)

    suspend fun deleteSunlightLog(log: SunlightLogEntity) = plantDao.deleteSunlightLog(log)

    fun getPhotosForPlant(plantId: Long): Flow<List<PlantPhotoEntity>> = plantDao.getPhotosForPlant(plantId)

    suspend fun insertPlantPhoto(photo: PlantPhotoEntity) = plantDao.insertPlantPhoto(photo)

    suspend fun deletePlantPhoto(photo: PlantPhotoEntity) = plantDao.deletePlantPhoto(photo)

    // --- PHOTO ANNOTATIONS ---
    fun getAnnotationsForPhoto(photoId: Long): Flow<List<PhotoAnnotationEntity>> = plantDao.getAnnotationsForPhoto(photoId)

    suspend fun insertPhotoAnnotation(annotation: PhotoAnnotationEntity) = plantDao.insertPhotoAnnotation(annotation)

    suspend fun deletePhotoAnnotation(annotation: PhotoAnnotationEntity) = plantDao.deletePhotoAnnotation(annotation)

    // --- RECENT SEARCHES ---
    val recentSearches: Flow<List<RecentSearchEntity>> = plantDao.getRecentSearches()

    suspend fun insertRecentSearch(search: RecentSearchEntity) = plantDao.insertRecentSearch(search)

    suspend fun deleteRecentSearchByQuery(query: String) = plantDao.deleteRecentSearchByQuery(query)

    suspend fun clearRecentSearches() = plantDao.clearRecentSearches()

    // --- PLANT HUMIDITY LOGS ---
    fun getHumidityLogsForPlant(plantId: Long): Flow<List<PlantHumidityLogEntity>> = plantDao.getHumidityLogsForPlant(plantId)

    suspend fun insertHumidityLog(log: PlantHumidityLogEntity) = plantDao.insertHumidityLog(log)

    suspend fun deleteHumidityLog(log: PlantHumidityLogEntity) = plantDao.deleteHumidityLog(log)

    // --- PLANT JOURNALS ---
    fun getJournalsForPlant(plantId: Long): Flow<List<PlantJournalEntity>> = plantDao.getJournalsForPlant(plantId)

    suspend fun insertJournal(journal: PlantJournalEntity) = plantDao.insertJournal(journal)

    suspend fun deleteJournal(journal: PlantJournalEntity) = plantDao.deleteJournal(journal)
}
