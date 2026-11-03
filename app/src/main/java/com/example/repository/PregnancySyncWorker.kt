package com.example.repository

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class PregnancySyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val WORK_NAME = "PregnancySyncWork"

        fun scheduleSync(context: Context) {
            try {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val syncRequest = PeriodicWorkRequestBuilder<PregnancySyncWorker>(
                    repeatInterval = 6,
                    repeatIntervalTimeUnit = TimeUnit.HOURS
                )
                    .setConstraints(constraints)
                    .build()

                WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncRequest
                )
            } catch (e: Throwable) {
                Log.w("PregnancySyncWorker", "WorkManager scheduleSync skipped or not initialized: ${e.message}")
            }
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getInstance(applicationContext)
            val dao = database.pregnancyDao()
            val syncRepository = PregnancySyncRepository(applicationContext, dao)

            // 1. Restore/fetch latest cloud records
            try {
                syncRepository.restoreFromCloud()
            } catch (e: Exception) {
                Log.w("PregnancySyncWorker", "Cloud restore skipped or offline: ${e.localizedMessage}")
            }

            // 2. Backup local symptom logs
            val localLogs = dao.getAllPregnancyLogs().first()
            for (log in localLogs) {
                try {
                    syncRepository.backupPregnancyLog(log)
                } catch (e: Exception) {
                    Log.w("PregnancySyncWorker", "Failed to backup log ${log.dateString}: ${e.localizedMessage}")
                }
            }

            // 3. Backup local journal & milestone entries
            val localEntries = dao.getAllJournalEntries().first()
            for (entry in localEntries) {
                try {
                    syncRepository.backupJournalEntry(entry)
                } catch (e: Exception) {
                    Log.w("PregnancySyncWorker", "Failed to backup journal entry ${entry.title}: ${e.localizedMessage}")
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("PregnancySyncWorker", "Sync worker execution failed", e)
            Result.retry()
        }
    }
}
