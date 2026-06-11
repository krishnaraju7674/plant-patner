package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant_journals")
data class PlantJournalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val timestamp: Long,
    val note: String,
    val category: String // "General", "Pest Sightings", "Growth Observations", etc.
)
