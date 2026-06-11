package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant_photos")
data class PlantPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val timestamp: Long,
    val imageUriOrBase64: String, // Holds compressed Base64 format of the growth photo
    val note: String = "" // E.g., "First sprout!", "Looks beautiful in full bloom!"
)
