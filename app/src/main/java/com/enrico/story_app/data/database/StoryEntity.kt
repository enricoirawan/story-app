package com.enrico.story_app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story")
data class StoryEntity(
    @PrimaryKey
    val userId: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double,
    val lon: Double
)