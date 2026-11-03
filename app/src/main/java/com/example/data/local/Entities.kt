package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pregnancy_logs")
data class PregnancyLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis(),
    val weightKg: Double = 0.0,
    val waterGlasses: Int = 0,
    val mood: String = "Calm",
    val symptoms: String = "",
    val bloodPressure: String = "",
    val notes: String = "",
    val syncedHealthConnect: Boolean = false
)

@Entity(tableName = "kick_sessions")
data class KickSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Long = 0,
    val kickCount: Int = 10,
    val targetReached: Boolean = true,
    val notes: String = ""
)

@Entity(tableName = "baby_logs")
data class BabyLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val logType: String, // NURSING, BOTTLE, DIAPER, SLEEP
    val detail: String, // LEFT / RIGHT / BOTH / WET / DIRTY / FORMULA
    val durationSeconds: Long = 0,
    val amountMl: Int = 0,
    val notes: String = "",
    val syncedHealthConnect: Boolean = false
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weekNumber: Int,
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String,
    val reflection: String,
    val photoUri: String? = null,
    val category: String = "Belly Bump",
    val audioUri: String? = null,
    val videoUri: String? = null
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String, // "Équipement Bébé", "Santé & Frais Médicaux", "Vêtements & Soins", "Maternité & Séjour", "Autre"
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis(),
    val receiptUri: String? = null,
    val notes: String = ""
)

@Entity(tableName = "wallet_documents")
data class WalletDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val documentType: String, // "INSURANCE", "MUTUAL", "ID_MOM", "ID_DAD", "VITAL_CARD", "BLOOD_TYPE", "MEDICAL_REPORT"
    val holderName: String = "Fatou",
    val documentNumber: String = "",
    val expirationDate: String = "",
    val photoUri: String? = null,
    val photoBackUri: String? = null,
    val googleDriveLink: String? = null,
    val notes: String = ""
)

@Entity(tableName = "glycemia_records")
data class GlycemiaRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String,
    val period: String, // "À jeun (matin)", "Post-Petit-Déjeuner", "Avant Déjeuner", "Post-Déjeuner", "Avant Dîner", "Post-Dîner", "Au Coucher"
    val glucoseValueMgDl: Double, // in mg/dL (ex: 88.0)
    val targetMaxMgDl: Double = 95.0,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val weekSuggested: Int = 0,
    val dateTimestamp: Long = System.currentTimeMillis(),
    val doctorName: String = "",
    val location: String = "",
    val notes: String = "",
    val isCompleted: Boolean = false
)

@Entity(tableName = "checklist_items")
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val title: String,
    val isChecked: Boolean = false,
    val isCustom: Boolean = false
)

@Entity(tableName = "medical_contacts")
data class MedicalContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "DOCTOR", "PHARMACY", "CLINIC", "HOSPITAL"
    val name: String,
    val phone: String = "",
    val address: String = "",
    val notes: String = ""
)

@Entity(tableName = "contractions")
data class ContractionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val durationSeconds: Long,
    val intervalSeconds: Long = 0,
    val intensity: String = "MODEREE", // "DOUCE", "MODEREE", "INTENSE"
    val notes: String = ""
)

@Entity(tableName = "baby_names")
data class BabyNameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val gender: String = "SURPRISE", // "FILLE", "GARCON", "SURPRISE"
    val originOrMeaning: String = "",
    val fatouRating: Int = 5, // 1 to 5 stars
    val balaRating: Int = 5, // 1 to 5 stars
    val isFavorite: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "couple_notes")
data class CoupleNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val author: String, // "Fatou" or "Bala"
    val recipient: String, // "Bala", "Fatou", or "Bébé"
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)


