package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "greetings")
data class GreetingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val messageText: String,
    val voiceType: String = "Sienna Casual", // "Sienna Natural", "Sienna Executive", "Casual Friendly", "Custom Voice"
    val isActive: Boolean = false,
    val routingRule: String = "DEFAULT", // "DEFAULT", "BUSINESS_HOURS", "AFTER_HOURS", "WEEKEND", "RECRUITER_SCREEN"
    val audioFilePath: String? = null, // Local storage path for recorded or uploaded audio
    val audioFileName: String? = null, // Display name for audio file
    val isCustomAudio: Boolean = false, // True if recorded by user or uploaded audio file
    val audioSourceType: String = "TTS", // "RECORDED", "UPLOADED", "TTS"
    val audioDurationSeconds: Int = 0,
    val targetCallerType: String = "ALL", // "ALL", "RECRUITER", "FRIEND_FAMILY", "VIP", "SPAM", "UNKNOWN", "SPECIFIC_CONTACT"
    val assignedContactId: Long? = null,
    val assignedContactName: String? = null,
    val assignedPhoneNumber: String? = null,
    val waveformCsv: String = "20,35,50,75,90,65,45,70,85,60,35,20",
    val createdAt: Long = System.currentTimeMillis()
)
