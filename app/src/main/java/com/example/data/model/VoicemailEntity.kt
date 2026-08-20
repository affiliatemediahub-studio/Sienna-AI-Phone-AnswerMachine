package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voicemails")
data class VoicemailEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val callerName: String,
    val callerNumber: String,
    val callerRelationship: String, // "Recruiter", "Friend & Family", "Spam", "Business", "Unknown"
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 30,
    val transcript: String,
    val summary: String,
    val category: String, // RECRUITER, FRIEND_FAMILY, SPAM, BUSINESS, GENERAL
    val sentiment: String, // POSITIVE, NEUTRAL, NEGATIVE
    val sentimentScore: Float = 0.85f,
    val emotionalTone: String = "Neutral",
    val keyEmotionalPhrases: String = "", // comma-separated
    val urgencyLevel: String = "MEDIUM", // LOW, MEDIUM, HIGH, CRITICAL
    val recruiterCompany: String? = null,
    val recruiterRole: String? = null,
    val recruiterCallback: String? = null,
    val actionItems: String = "", // comma or newline separated
    val sentToCallTracker: Boolean = true,
    val trackerSentTimestamp: Long = System.currentTimeMillis(),
    val audioWaveform: String = "20,45,60,35,80,95,70,55,85,60,40,75,90,65,30,50,85,60,45,70,50,30,20",
    val audioFilePath: String = "file:///storage/emulated/0/SiennaAI/Audio/vm_rec_2026_001.m4a",
    val audioFileUrl: String = "https://vault.sienna.ai/audio/recordings/john_lanter_vm_001.m4a",
    val isRead: Boolean = false,
    val isStarred: Boolean = false
)
