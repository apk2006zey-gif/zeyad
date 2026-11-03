package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class MemoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uri: String,
    val mediaType: String = "IMAGE", // "IMAGE" or "VIDEO"
    val title: String,
    val description: String = "",
    val dateString: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val moodTag: String = "حب وسعادة" // Tag: ذكرى غالية, أول مرة, رحلة سوا, فرحة قلبنا
)

@Entity(tableName = "milestones")
data class StoryMilestone(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis(),
    val storyText: String,
    val photoUri: String? = null,
    val iconName: String = "favorite"
)

@Entity(tableName = "love_letters")
data class LoveLetter(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderName: String,
    val recipientName: String,
    val title: String,
    val letterBody: String,
    val timestamp: Long = System.currentTimeMillis(),
    val dateFormatted: String = "",
    val isRead: Boolean = false
)

data class RomanticQuote(
    val id: Int,
    val text: String,
    val author: String = "من القلب",
    val category: String = "غزل"
)
