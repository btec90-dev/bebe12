package com.example.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.local.JournalEntryEntity
import com.example.data.local.PregnancyDao
import com.example.data.local.PregnancyLogEntity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PregnancySyncRepository(
    private val context: Context,
    private val pregnancyDao: PregnancyDao,
    private val firestore: FirebaseFirestore?
) {
    constructor(context: Context, pregnancyDao: PregnancyDao) : this(
        context = context,
        pregnancyDao = pregnancyDao,
        firestore = try {
            val dbId = context.applicationContext.getString(R.string.firestore_database_id)
            if (dbId.isNotBlank() && dbId != "(default)") {
                try {
                    FirebaseFirestore.getInstance(dbId)
                } catch (_: Throwable) {
                    FirebaseFirestore.getInstance()
                }
            } else {
                FirebaseFirestore.getInstance()
            }
        } catch (e: Throwable) {
            Log.w("PregnancySyncRepo", "Firestore not available: ${e.message}")
            null
        }
    )

    private fun requireUserId(): String {
        return try {
            Firebase.auth.currentUser?.uid
                ?: com.example.util.UserSessionManager.getUserId(context)
        } catch (_: Throwable) {
            com.example.util.UserSessionManager.getUserId(context)
        }
    }

    /**
     * Uploads local pregnancy symptom log to Firestore for cloud backup.
     */
    suspend fun backupPregnancyLog(log: PregnancyLogEntity) {
        val fs = firestore ?: return
        try {
            val uid = requireUserId()
            val logData = mapOf(
                "id" to log.id.toString(),
                "dateString" to log.dateString,
                "timestamp" to log.timestamp,
                "weightKg" to log.weightKg,
                "waterGlasses" to log.waterGlasses,
                "mood" to log.mood,
                "symptoms" to log.symptoms,
                "bloodPressure" to log.bloodPressure,
                "notes" to log.notes,
                "userId" to uid,
                "updatedAt" to FieldValue.serverTimestamp()
            )

            fs.collection("users")
                .document(uid)
                .collection("pregnancyLogs")
                .document(log.dateString)
                .set(logData)
                .await()

            Log.d("PregnancySync", "Successfully backed up symptom log for ${log.dateString}")
        } catch (e: Exception) {
            Log.e("PregnancySync", "Failed to backup pregnancy log", e)
        }
    }

    /**
     * Uploads local journal/milestone entry to Firestore for cloud backup.
     */
    suspend fun backupJournalEntry(entry: JournalEntryEntity) {
        val fs = firestore ?: return
        try {
            val uid = requireUserId()
            val entryData = mapOf(
                "id" to entry.id.toString(),
                "weekNumber" to entry.weekNumber,
                "dateString" to entry.dateString,
                "timestamp" to entry.timestamp,
                "title" to entry.title,
                "reflection" to entry.reflection,
                "photoUri" to (entry.photoUri ?: ""),
                "category" to entry.category,
                "userId" to uid,
                "updatedAt" to FieldValue.serverTimestamp()
            )

            fs.collection("users")
                .document(uid)
                .collection("journalEntries")
                .document(entry.id.toString())
                .set(entryData)
                .await()

            Log.d("PregnancySync", "Successfully backed up journal entry: ${entry.title}")
        } catch (e: Exception) {
            Log.e("PregnancySync", "Failed to backup journal entry", e)
        }
    }

    /**
     * Pulls remote backups from Firestore and restores/merges them into local Room database.
     */
    suspend fun restoreFromCloud() {
        val fs = firestore ?: return
        try {
            val uid = requireUserId()

            // Restore Pregnancy Logs (Symptoms)
            val logsSnapshot = fs.collection("users")
                .document(uid)
                .collection("pregnancyLogs")
                .get()
                .await()

            for (doc in logsSnapshot.documents) {
                val dateStr = doc.getString("dateString") ?: continue
                val weight = doc.getDouble("weightKg") ?: 0.0
                val water = (doc.getLong("waterGlasses") ?: 0L).toInt()
                val mood = doc.getString("mood") ?: "Calm"
                val symptoms = doc.getString("symptoms") ?: ""
                val bp = doc.getString("bloodPressure") ?: ""
                val notes = doc.getString("notes") ?: ""
                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()

                val entity = PregnancyLogEntity(
                    dateString = dateStr,
                    timestamp = timestamp,
                    weightKg = weight,
                    waterGlasses = water,
                    mood = mood,
                    symptoms = symptoms,
                    bloodPressure = bp,
                    notes = notes,
                    syncedHealthConnect = true
                )
                pregnancyDao.insertOrUpdatePregnancyLog(entity)
            }

            // Restore Journal & Milestone Entries
            val journalSnapshot = fs.collection("users")
                .document(uid)
                .collection("journalEntries")
                .get()
                .await()

            for (doc in journalSnapshot.documents) {
                val week = (doc.getLong("weekNumber") ?: 1L).toInt()
                val dateStr = doc.getString("dateString") ?: ""
                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                val title = doc.getString("title") ?: ""
                val reflection = doc.getString("reflection") ?: ""
                val photoUri = doc.getString("photoUri").takeIf { !it.isNullOrBlank() }
                val category = doc.getString("category") ?: "Belly Bump"

                val entryEntity = JournalEntryEntity(
                    weekNumber = week,
                    dateString = dateStr,
                    timestamp = timestamp,
                    title = title,
                    reflection = reflection,
                    photoUri = photoUri,
                    category = category
                )
                pregnancyDao.insertJournalEntry(entryEntity)
            }

            Log.d("PregnancySync", "Successfully restored data from Firestore cloud backup.")
        } catch (e: Exception) {
            Log.e("PregnancySync", "Failed to restore from cloud backup", e)
        }
    }
}
