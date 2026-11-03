package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cached_contacts")
data class CachedContact(
    @PrimaryKey val id: String,
    val type: String, // "DOCTOR", "PHARMACY", or "CLINIC"
    val name: String,
    val phone: String?,
    val address: String? = "",
    val notes: String? = ""
)

@Dao
interface ContactDao {
    @Query("SELECT * FROM cached_contacts WHERE type = :contactType ORDER BY name ASC")
    fun getContactsByType(contactType: String): Flow<List<CachedContact>>

    @Query("SELECT * FROM cached_contacts ORDER BY name ASC")
    fun getAllCachedContacts(): Flow<List<CachedContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<CachedContact>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleContact(contact: CachedContact)

    @Update
    suspend fun updateContact(contact: CachedContact)

    @Delete
    suspend fun deleteContact(contact: CachedContact)

    @Query("DELETE FROM cached_contacts WHERE type = :contactType")
    suspend fun clearContactsByType(contactType: String)
}
