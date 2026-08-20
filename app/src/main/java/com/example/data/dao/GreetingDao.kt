package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GreetingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GreetingDao {
    @Query("SELECT * FROM greetings ORDER BY id DESC")
    fun getAllGreetings(): Flow<List<GreetingEntity>>

    @Query("SELECT * FROM greetings WHERE targetCallerType = :callerType ORDER BY id DESC")
    fun getGreetingsByCallerType(callerType: String): Flow<List<GreetingEntity>>

    @Query("SELECT * FROM greetings WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveGreeting(): GreetingEntity?

    @Query("SELECT * FROM greetings WHERE isActive = 1 LIMIT 1")
    fun getActiveGreetingFlow(): Flow<GreetingEntity?>

    @Query("SELECT * FROM greetings WHERE id = :id LIMIT 1")
    suspend fun getGreetingById(id: Long): GreetingEntity?

    @Query("SELECT * FROM greetings WHERE assignedContactId = :contactId AND isActive = 1 LIMIT 1")
    suspend fun getActiveGreetingForContact(contactId: Long): GreetingEntity?

    @Query("SELECT * FROM greetings WHERE assignedPhoneNumber = :phoneNumber AND isActive = 1 LIMIT 1")
    suspend fun getActiveGreetingForPhone(phoneNumber: String): GreetingEntity?

    @Query("SELECT * FROM greetings WHERE targetCallerType = :callerType AND isActive = 1 LIMIT 1")
    suspend fun getActiveGreetingForCallerType(callerType: String): GreetingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGreeting(greeting: GreetingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(greetings: List<GreetingEntity>)

    @Update
    suspend fun updateGreeting(greeting: GreetingEntity)

    @Query("UPDATE greetings SET isActive = 0")
    suspend fun resetAllActive()

    @Query("UPDATE greetings SET isActive = 0 WHERE targetCallerType = :callerType")
    suspend fun resetActiveForCallerType(callerType: String)

    @Query("UPDATE greetings SET isActive = 0 WHERE assignedContactId = :contactId")
    suspend fun resetActiveForContact(contactId: Long)

    @Query("UPDATE greetings SET isActive = 1 WHERE id = :id")
    suspend fun setActive(id: Long)

    @Query("DELETE FROM greetings WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Delete
    suspend fun deleteGreeting(greeting: GreetingEntity)
}

