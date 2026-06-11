package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sunlight_logs")
data class SunlightLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val timestamp: Long,
    val directHours: Float,
    val indirectHours: Float,
    val notes: String = ""
)
