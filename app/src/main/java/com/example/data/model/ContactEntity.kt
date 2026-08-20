package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val category: String, // RECRUITER, FRIEND_FAMILY, VIP, SPAM, UNKNOWN
    val company: String? = null,
    val relationship: String = "Acquaintance",
    val voiceProfileNotes: String = "Clear vocal pattern, known caller",
    val handlingRule: String = "STANDARD", // VIP_PRIORITY, WARM_FRIEND, RECRUITER_SCREEN, SPAM_BLOCK
    val avatarColor: Long = 0xFF6366F1
)
