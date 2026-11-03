package com.example.repository

import com.example.data.CachedContact
import com.example.data.ContactDao
import com.example.network.MamanNetworkService
import com.example.network.NetworkContactPayload
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class CareNetworkRepository(
    private val contactDao: ContactDao,
    private val networkService: MamanNetworkService
) {
    // Exposes real-time streams straight from the local Room database cache
    fun getCachedContacts(type: String): Flow<List<CachedContact>> = 
        contactDao.getContactsByType(type)

    fun getAllCachedContacts(): Flow<List<CachedContact>> =
        contactDao.getAllCachedContacts()

    // Synchronizes remote database tables into Room local caching storage
    suspend fun refreshDirectoryCache() {
        try {
            val response = networkService.getFullCareDirectory()
            
            val networkItems = mutableListOf<CachedContact>()
            
            // Map remote payloads to storage arrays
            response.doctors.forEach { networkItems.add(CachedContact(it.id, "DOCTOR", it.name, it.phone)) }
            response.pharmacies.forEach { networkItems.add(CachedContact(it.id, "PHARMACY", it.name, it.phone)) }
            response.clinics.forEach { networkItems.add(CachedContact(it.id, "CLINIC", it.name, it.phone)) }

            if (networkItems.isNotEmpty()) {
                contactDao.insertContacts(networkItems)
            }
        } catch (e: IOException) {
            // Network loss failover: drops request execution but preserves current Room data safely
        }
    }

    // Handles new contact creations locally first, uploading to backend if connection is active
    suspend fun addNewContact(payload: NetworkContactPayload) {
        try {
            // Upload to API first
            val remoteSaved = networkService.uploadNewContact(payload)
            val cacheItem = CachedContact(remoteSaved.id, payload.type, remoteSaved.name, remoteSaved.phone)
            contactDao.insertSingleContact(cacheItem)
        } catch (e: IOException) {
            // Offline fallback: Save with a temporary ID locally so it appears in the app immediately
            val tempId = "temp_${System.currentTimeMillis()}"
            val localFallbackItem = CachedContact(tempId, payload.type, payload.name, payload.phone)
            contactDao.insertSingleContact(localFallbackItem)
        }
    }
}
