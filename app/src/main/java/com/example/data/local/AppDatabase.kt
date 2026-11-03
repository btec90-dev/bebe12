package com.example.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PregnancyLogEntity::class,
        KickSessionEntity::class,
        BabyLogEntity::class,
        JournalEntryEntity::class,
        AppointmentEntity::class,
        ChecklistItemEntity::class,
        MedicalContactEntity::class,
        com.example.data.CachedContact::class,
        ExpenseEntity::class,
        WalletDocumentEntity::class,
        GlycemiaRecordEntity::class,
        ContractionEntity::class,
        BabyNameEntity::class,
        CoupleNoteEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pregnancyDao(): PregnancyDao
    abstract fun contactDao(): com.example.data.ContactDao

    companion object {
        private const val TAG = "AppDatabase"
        private const val DB_NAME = "maman_bebe_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val existing = INSTANCE
                if (existing != null) return@synchronized existing

                val appContext = context.applicationContext
                val db = try {
                    val created = buildDatabase(appContext)
                    // Trigger database opening immediately inside try-catch to catch schema verification errors
                    created.openHelper.writableDatabase
                    created
                } catch (e: Exception) {
                    Log.e(TAG, "Legacy database schema mismatch or corruption detected: ${e.message}. Rebuilding cleanly.", e)
                    try {
                        appContext.deleteDatabase(DB_NAME)
                    } catch (delEx: Exception) {
                        Log.e(TAG, "Failed to delete corrupted database: ${delEx.message}")
                    }
                    val freshDb = buildDatabase(appContext)
                    try {
                        freshDb.openHelper.writableDatabase
                    } catch (_: Exception) {}
                    freshDb
                }
                INSTANCE = db
                db
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DB_NAME
            )
                .fallbackToDestructiveMigration(true)
                .fallbackToDestructiveMigrationOnDowngrade(true)
                .build()
        }
    }
}
