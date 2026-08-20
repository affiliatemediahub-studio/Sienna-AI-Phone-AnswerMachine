package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VoicemailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VoicemailDao {
    @Query("SELECT * FROM voicemails ORDER BY timestamp DESC")
    fun getAllVoicemails(): Flow<List<VoicemailEntity>>

    @Query("SELECT * FROM voicemails WHERE category = :category ORDER BY timestamp DESC")
    fun getVoicemailsByCategory(category: String): Flow<List<VoicemailEntity>>

    @Query("SELECT * FROM voicemails WHERE sentiment = :sentiment ORDER BY timestamp DESC")
    fun getVoicemailsBySentiment(sentiment: String): Flow<List<VoicemailEntity>>

    @Query("SELECT * FROM voicemails WHERE isStarred = 1 ORDER BY timestamp DESC")
    fun getStarredVoicemails(): Flow<List<VoicemailEntity>>

    @Query("SELECT * FROM voicemails WHERE id = :id LIMIT 1")
    suspend fun getVoicemailById(id: Long): VoicemailEntity?

    @Query("SELECT COUNT(*) FROM voicemails WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Query("SELECT * FROM voicemails WHERE sentToCallTracker = 1 ORDER BY trackerSentTimestamp DESC")
    fun getTrackerDispatches(): Flow<List<VoicemailEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoicemail(voicemail: VoicemailEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(voicemails: List<VoicemailEntity>)

    @Update
    suspend fun updateVoicemail(voicemail: VoicemailEntity)

    @Delete
    suspend fun deleteVoicemail(voicemail: VoicemailEntity)

    @Query("DELETE FROM voicemails WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE voicemails SET isRead = :isRead WHERE id = :id")
    suspend fun markAsRead(id: Long, isRead: Boolean)

    @Query("UPDATE voicemails SET isStarred = :isStarred WHERE id = :id")
    suspend fun toggleStarred(id: Long, isStarred: Boolean)
}
