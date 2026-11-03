package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PregnancyDao {

    // Pregnancy daily logs
    @Query("SELECT * FROM pregnancy_logs ORDER BY timestamp DESC")
    fun getAllPregnancyLogs(): Flow<List<PregnancyLogEntity>>

    @Query("SELECT * FROM pregnancy_logs WHERE dateString = :date LIMIT 1")
    suspend fun getLogByDate(date: String): PregnancyLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePregnancyLog(log: PregnancyLogEntity): Long

    // Kick counter sessions
    @Query("SELECT * FROM kick_sessions ORDER BY timestamp DESC")
    fun getAllKickSessions(): Flow<List<KickSessionEntity>>

    @Insert
    suspend fun insertKickSession(session: KickSessionEntity): Long

    @Delete
    suspend fun deleteKickSession(session: KickSessionEntity)

    // Baby postpartum logs
    @Query("SELECT * FROM baby_logs ORDER BY timestamp DESC")
    fun getAllBabyLogs(): Flow<List<BabyLogEntity>>

    @Insert
    suspend fun insertBabyLog(log: BabyLogEntity): Long

    @Delete
    suspend fun deleteBabyLog(log: BabyLogEntity)

    // Bump Journal entries
    @Query("SELECT * FROM journal_entries ORDER BY weekNumber ASC, timestamp DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntryEntity): Long

    @Update
    suspend fun updateJournalEntry(entry: JournalEntryEntity)

    @Delete
    suspend fun deleteJournalEntry(entry: JournalEntryEntity)

    // Appointments
    @Query("SELECT * FROM appointments ORDER BY dateTimestamp ASC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity): Long

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Delete
    suspend fun deleteAppointment(appointment: AppointmentEntity)

    // Checklist items
    @Query("SELECT * FROM checklist_items ORDER BY id ASC")
    fun getAllChecklistItems(): Flow<List<ChecklistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(item: ChecklistItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItems(items: List<ChecklistItemEntity>)

    @Update
    suspend fun updateChecklistItem(item: ChecklistItemEntity)

    @Delete
    suspend fun deleteChecklistItem(item: ChecklistItemEntity)

    @Query("SELECT COUNT(*) FROM checklist_items")
    suspend fun getChecklistCount(): Int

    // Medical Contacts (Doctor, Pharmacy, Clinic, etc.)
    @Query("SELECT * FROM medical_contacts ORDER BY name ASC")
    fun getAllMedicalContacts(): Flow<List<MedicalContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalContact(contact: MedicalContactEntity): Long

    @Update
    suspend fun updateMedicalContact(contact: MedicalContactEntity)

    @Delete
    suspend fun deleteMedicalContact(contact: MedicalContactEntity)

    // Pregnancy & Baby Expenses Tracker
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    // Medical Wallet & Insurance Cards
    @Query("SELECT * FROM wallet_documents ORDER BY id ASC")
    fun getAllWalletDocuments(): Flow<List<WalletDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalletDocument(doc: WalletDocumentEntity): Long

    @Update
    suspend fun updateWalletDocument(doc: WalletDocumentEntity)

    @Delete
    suspend fun deleteWalletDocument(doc: WalletDocumentEntity)

    // Gestational Diabetes & Glycemia Log
    @Query("SELECT * FROM glycemia_records ORDER BY timestamp DESC")
    fun getAllGlycemiaRecords(): Flow<List<GlycemiaRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlycemiaRecord(record: GlycemiaRecordEntity): Long

    @Delete
    suspend fun deleteGlycemiaRecord(record: GlycemiaRecordEntity)

    // Contractions Tracker (Labor Timing & 5-1-1 Rule)
    @Query("SELECT * FROM contractions ORDER BY startTimestamp DESC")
    fun getAllContractions(): Flow<List<ContractionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContraction(contraction: ContractionEntity): Long

    @Delete
    suspend fun deleteContraction(contraction: ContractionEntity)

    @Query("DELETE FROM contractions")
    suspend fun clearAllContractions()

    // Baby Names Shortlist & Ratings (Fatou & Bala)
    @Query("SELECT * FROM baby_names ORDER BY isFavorite DESC, (fatouRating + balaRating) DESC")
    fun getAllBabyNames(): Flow<List<BabyNameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBabyName(babyName: BabyNameEntity): Long

    @Update
    suspend fun updateBabyName(babyName: BabyNameEntity)

    @Delete
    suspend fun deleteBabyName(babyName: BabyNameEntity)

    // Couple Notes & Letters
    @Query("SELECT * FROM couple_notes ORDER BY timestamp DESC")
    fun getAllCoupleNotes(): Flow<List<CoupleNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupleNote(note: CoupleNoteEntity): Long

    @Delete
    suspend fun deleteCoupleNote(note: CoupleNoteEntity)
}
