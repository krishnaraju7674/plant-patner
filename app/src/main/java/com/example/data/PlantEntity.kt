package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "captured_plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
    val joinedTips: String, // Double pipeline || separated tips
    val dateIdentified: Long,
    val lastWateredTime: Long = 0L,
    val isFavorite: Boolean = false,
    val customImageUri: String? = null, // Stored local image file path or mock index if mock
    val isReminderEnabled: Boolean = true,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val customWateringIntervalDays: Int = 0, // if > 0, overrides default wateringIntervalDays
    val lastFertilizedTime: Long = 0L,
    val fertilizingIntervalDays: Int = 30, // Default to 30 days
    val customFertilizingIntervalDays: Int = 0, // if > 0, overrides default fertilizingIntervalDays
    val tags: String = "", // Comma-separated tags (e.g. "Living Room, Succulent")
    val isWishlist: Boolean = false // If true, this plant is in the user's wishlist
) {
    val quickTipsList: List<String>
        get() = if (joinedTips.trim().isEmpty()) emptyList() else joinedTips.split("||")
}
