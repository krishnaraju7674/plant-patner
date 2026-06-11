package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant_humidity_logs")
data class PlantHumidityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val timestamp: Long,
    val humidityPercent: Double,
    val optimalRangeMin: Double,
    val optimalRangeMax: Double
)
