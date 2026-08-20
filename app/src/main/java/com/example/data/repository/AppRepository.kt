package com.example.data.repository

import com.example.data.dao.ContactDao
import com.example.data.dao.GreetingDao
import com.example.data.dao.VoicemailDao
import com.example.data.model.ContactEntity
import com.example.data.model.GreetingEntity
import com.example.data.model.VoicemailEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val voicemailDao: VoicemailDao,
    private val contactDao: ContactDao,
    private val greetingDao: GreetingDao
) {
    val allVoicemails: Flow<List<VoicemailEntity>> = voicemailDao.getAllVoicemails()
    val allContacts: Flow<List<ContactEntity>> = contactDao.getAllContacts()
    val allGreetings: Flow<List<GreetingEntity>> = greetingDao.getAllGreetings()
    val activeGreeting: Flow<GreetingEntity?> = greetingDao.getActiveGreetingFlow()
    val unreadCount: Flow<Int> = voicemailDao.getUnreadCount()
    val trackerDispatches: Flow<List<VoicemailEntity>> = voicemailDao.getTrackerDispatches()

    suspend fun getActiveGreetingSync(): GreetingEntity? = greetingDao.getActiveGreeting()

    suspend fun getContactByPhone(phoneNumber: String): ContactEntity? = contactDao.getContactByPhone(phoneNumber)

    suspend fun saveVoicemail(voicemail: VoicemailEntity): Long = voicemailDao.insertVoicemail(voicemail)

    suspend fun markAsRead(id: Long, isRead: Boolean = true) = voicemailDao.markAsRead(id, isRead)

    suspend fun toggleStarred(id: Long, isStarred: Boolean) = voicemailDao.toggleStarred(id, isStarred)

    suspend fun deleteVoicemail(id: Long) = voicemailDao.deleteById(id)

    suspend fun addContact(contact: ContactEntity): Long = contactDao.insertContact(contact)

    suspend fun updateContact(contact: ContactEntity) = contactDao.updateContact(contact)

    suspend fun deleteContact(id: Long) = contactDao.deleteById(id)

    suspend fun addGreeting(greeting: GreetingEntity): Long = greetingDao.insertGreeting(greeting)

    suspend fun updateGreeting(greeting: GreetingEntity) = greetingDao.updateGreeting(greeting)

    suspend fun setActiveGreeting(id: Long) {
        greetingDao.resetAllActive()
        greetingDao.setActive(id)
    }

    suspend fun setActiveGreetingForCallerType(id: Long, callerType: String) {
        if (callerType == "ALL") {
            greetingDao.resetAllActive()
        } else {
            greetingDao.resetActiveForCallerType(callerType)
        }
        greetingDao.setActive(id)
    }

    suspend fun setActiveGreetingForContact(id: Long, contactId: Long) {
        greetingDao.resetActiveForContact(contactId)
        greetingDao.setActive(id)
    }

    suspend fun getGreetingForCaller(
        contactId: Long?,
        phoneNumber: String?,
        category: String?
    ): GreetingEntity? {
        // 1. Try specific contact ID
        if (contactId != null) {
            val contactGreeting = greetingDao.getActiveGreetingForContact(contactId)
            if (contactGreeting != null) return contactGreeting
        }
        // 2. Try phone number
        if (!phoneNumber.isNullOrBlank()) {
            val phoneGreeting = greetingDao.getActiveGreetingForPhone(phoneNumber)
            if (phoneGreeting != null) return phoneGreeting
        }
        // 3. Try category (e.g. RECRUITER, FRIEND_FAMILY, VIP, SPAM)
        if (!category.isNullOrBlank()) {
            val categoryGreeting = greetingDao.getActiveGreetingForCallerType(category)
            if (categoryGreeting != null) return categoryGreeting
        }
        // 4. Fallback to global active greeting
        return greetingDao.getActiveGreeting()
    }

    suspend fun deleteGreeting(greeting: GreetingEntity) = greetingDao.deleteGreeting(greeting)

    suspend fun deleteGreetingById(id: Long) = greetingDao.deleteById(id)
}
