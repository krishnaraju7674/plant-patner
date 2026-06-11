package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM captured_plants ORDER BY dateIdentified DESC")
    fun getAllCapturedPlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM captured_plants WHERE id = :id LIMIT 1")
    fun getPlantById(id: Long): Flow<PlantEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity): Long

    @Update
    suspend fun updatePlant(plant: PlantEntity)

    @Delete
    suspend fun deletePlant(plant: PlantEntity)

    @Query("UPDATE captured_plants SET lastWateredTime = :timestamp WHERE id = :id")
    suspend fun updateLastWatered(id: Long, timestamp: Long)

    @Query("UPDATE captured_plants SET lastFertilizedTime = :timestamp WHERE id = :id")
    suspend fun updateLastFertilized(id: Long, timestamp: Long)

    @Query("UPDATE captured_plants SET customFertilizingIntervalDays = :interval WHERE id = :id")
    suspend fun updateCustomFertilizingInterval(id: Long, interval: Int)

    @Query("UPDATE captured_plants SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFav: Boolean)

    @Query("SELECT * FROM sunlight_logs WHERE plantId = :plantId ORDER BY timestamp DESC")
    fun getSunlightLogsForPlant(plantId: Long): Flow<List<SunlightLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSunlightLog(log: SunlightLogEntity): Long

    @Delete
    suspend fun deleteSunlightLog(log: SunlightLogEntity)

    @Query("SELECT * FROM plant_photos WHERE plantId = :plantId ORDER BY timestamp DESC")
    fun getPhotosForPlant(plantId: Long): Flow<List<PlantPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlantPhoto(photo: PlantPhotoEntity): Long

    @Delete
    suspend fun deletePlantPhoto(photo: PlantPhotoEntity)

    // --- PHOTO ANNOTATIONS DAO ---
    @Query("SELECT * FROM photo_annotations WHERE photoId = :photoId ORDER BY timestamp ASC")
    fun getAnnotationsForPhoto(photoId: Long): Flow<List<PhotoAnnotationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoAnnotation(annotation: PhotoAnnotationEntity): Long

    @Delete
    suspend fun deletePhotoAnnotation(annotation: PhotoAnnotationEntity)

    // --- RECENT SEARCHES DAO ---
    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT 15")
    fun getRecentSearches(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(search: RecentSearchEntity): Long

    @Query("DELETE FROM recent_searches WHERE query = :query")
    suspend fun deleteRecentSearchByQuery(query: String)

    @Query("DELETE FROM recent_searches")
    suspend fun clearRecentSearches()

    // --- PLANT HUMIDITY LOGS DAO ---
    @Query("SELECT * FROM plant_humidity_logs WHERE plantId = :plantId ORDER BY timestamp DESC")
    fun getHumidityLogsForPlant(plantId: Long): Flow<List<PlantHumidityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHumidityLog(log: PlantHumidityLogEntity): Long

    @Delete
    suspend fun deleteHumidityLog(log: PlantHumidityLogEntity)

    // --- PLANT JOURNALS DAO ---
    @Query("SELECT * FROM plant_journals WHERE plantId = :plantId ORDER BY timestamp DESC")
    fun getJournalsForPlant(plantId: Long): Flow<List<PlantJournalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: PlantJournalEntity): Long

    @Delete
    suspend fun deleteJournal(journal: PlantJournalEntity)
}
