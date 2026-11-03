package com.example.repository

import android.content.Context
import android.util.Log
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object PregnancySyncManager {
    private const val TAG = "PregnancySyncManager"

    /**
     * Reconciles local Room database records for symptoms and milestones with Firestore,
     * ensuring seamless offline-first data persistence.
     */
    fun startBackgroundSync(context: Context) {
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(appContext)
                val dao = db.pregnancyDao()
                val syncRepo = PregnancySyncRepository(appContext, dao)

                // 1. Pull remote records from Firestore (offline-first merge)
                try {
                    syncRepo.restoreFromCloud()
                } catch (e: Exception) {
                    Log.w(TAG, "Offline or cloud restore skipped: ${e.localizedMessage}")
                }

                // 2. Push local unsynced symptom logs to Firestore
                val localLogs = dao.getAllPregnancyLogs().first()
                for (log in localLogs) {
                    try {
                        syncRepo.backupPregnancyLog(log)
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to sync log ${log.dateString}: ${e.localizedMessage}")
                    }
                }

                // 3. Push local milestone / journal entries to Firestore
                val localEntries = dao.getAllJournalEntries().first()
                for (entry in localEntries) {
                    try {
                        syncRepo.backupJournalEntry(entry)
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to sync journal entry ${entry.title}: ${e.localizedMessage}")
                    }
                }

                Log.d(TAG, "Pregnancy sync reconciliation completed successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "Pregnancy sync reconciliation failed", e)
            }
        }
    }
}
