package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.local.AppointmentEntity
import com.example.data.local.BabyLogEntity
import com.example.data.local.ChecklistItemEntity
import com.example.data.local.JournalEntryEntity
import com.example.data.local.KickSessionEntity
import com.example.data.local.MedicalContactEntity
import com.example.data.local.PregnancyLogEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MamanDataTextExporter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val dateOnlyFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

    fun buildCompleteArchiveText(
        profileName: String,
        profileAge: Int,
        profilePhotoUri: String,
        currentWeek: Int,
        dueDateMillis: Long,
        isTwinMode: Boolean,
        userEmail: String,
        pregnancyLogs: List<PregnancyLogEntity>,
        kickSessions: List<KickSessionEntity>,
        babyLogs: List<BabyLogEntity>,
        journalEntries: List<JournalEntryEntity>,
        appointments: List<AppointmentEntity>,
        checklistItems: List<ChecklistItemEntity>,
        medicalContacts: List<MedicalContactEntity>
    ): String {
        val sb = StringBuilder()
        val border = "================================================================================"
        val subBorder = "--------------------------------------------------------------------------------"

        val nowFormatted = dateFormat.format(Date())
        val dueDateFormatted = dateOnlyFormat.format(Date(dueDateMillis))
        val daysRemaining = ((dueDateMillis - System.currentTimeMillis()) / (1000L * 3600 * 24)).coerceAtLeast(0)

        sb.appendLine(border)
        sb.appendLine("                      MAMAN & BÉBÉ - COMPLETE DATA ARCHIVE")
        sb.appendLine("           Personal Pregnancy, Postpartum, & Clinical Care Records")
        sb.appendLine(border)
        sb.appendLine("Generated: $nowFormatted")
        sb.appendLine("Application: Maman & Bébé (Android)")
        sb.appendLine()

        // 1. User Profile & Clinical Overview
        sb.appendLine(subBorder)
        sb.appendLine("1. PATIENT & MATERNAL PROFILE")
        sb.appendLine(subBorder)
        sb.appendLine("Name:                $profileName")
        sb.appendLine("Age:                 $profileAge years old")
        sb.appendLine("Registered Account:  $userEmail")
        sb.appendLine("Current Gestation:   Week $currentWeek")
        sb.appendLine("Estimated Due Date:  $dueDateFormatted")
        sb.appendLine("Countdown:           $daysRemaining days until expected arrival")
        sb.appendLine("Pregnancy Type:      ${if (isTwinMode) "Twin Pregnancy (Multiples)" else "Single Pregnancy"}")
        if (profilePhotoUri.isNotBlank()) {
            sb.appendLine("Profile Photo URI:   $profilePhotoUri")
        }
        sb.appendLine()

        // 2. Scheduled Prenatal Appointments & Clinical Care Visits
        sb.appendLine(subBorder)
        sb.appendLine("2. PRENATAL APPOINTMENTS & MEDICAL CLINICAL AGENDA")
        sb.appendLine(subBorder)
        if (appointments.isEmpty()) {
            sb.appendLine("No clinical appointments recorded.")
        } else {
            appointments.sortedBy { it.dateTimestamp }.forEachIndexed { idx, appt ->
                val apptDate = dateFormat.format(Date(appt.dateTimestamp))
                val statusStr = if (appt.isCompleted) "[COMPLETED]" else "[UPCOMING / SCHEDULED]"
                sb.appendLine("#${idx + 1} $statusStr: ${appt.title}")
                sb.appendLine("   Date & Time:      $apptDate")
                if (appt.doctorName.isNotBlank()) sb.appendLine("   Doctor/Provider:  ${appt.doctorName}")
                if (appt.location.isNotBlank())   sb.appendLine("   Location/Clinic:  ${appt.location}")
                if (appt.notes.isNotBlank())      sb.appendLine("   Clinical Notes:   ${appt.notes}")
                sb.appendLine()
            }
        }

        // 3. Care Directory & Emergency Contacts
        sb.appendLine(subBorder)
        sb.appendLine("3. PRENATAL CARE DIRECTORY & CONTACTS")
        sb.appendLine(subBorder)
        if (medicalContacts.isEmpty()) {
            sb.appendLine("No contacts recorded.")
        } else {
            medicalContacts.forEachIndexed { idx, contact ->
                sb.appendLine("#${idx + 1} [${contact.type}] ${contact.name}")
                if (contact.phone.isNotBlank())   sb.appendLine("   Phone:   ${contact.phone}")
                if (contact.address.isNotBlank()) sb.appendLine("   Address: ${contact.address}")
                if (contact.notes.isNotBlank())   sb.appendLine("   Notes:   ${contact.notes}")
                sb.appendLine()
            }
        }

        // 4. Daily Health Logs (Weight, Hydration, Symptoms, Blood Pressure, Mood)
        sb.appendLine(subBorder)
        sb.appendLine("4. DAILY HEALTH, SYMPTOM & HYDRATION LOGS")
        sb.appendLine(subBorder)
        if (pregnancyLogs.isEmpty()) {
            sb.appendLine("No daily health logs recorded.")
        } else {
            pregnancyLogs.sortedByDescending { it.timestamp }.forEachIndexed { idx, log ->
                val logDate = dateFormat.format(Date(log.timestamp))
                sb.appendLine("#${idx + 1} Date: ${log.dateString} ($logDate)")
                sb.appendLine("   Mood:            ${log.mood}")
                sb.appendLine("   Weight:          ${log.weightKg} kg")
                sb.appendLine("   Water Intake:    ${log.waterGlasses} glasses (approx. ${log.waterGlasses * 250} ml)")
                if (log.bloodPressure.isNotBlank()) sb.appendLine("   Blood Pressure:  ${log.bloodPressure}")
                if (log.symptoms.isNotBlank())      sb.appendLine("   Symptoms:        ${log.symptoms}")
                if (log.notes.isNotBlank())         sb.appendLine("   Personal Notes:  ${log.notes}")
                sb.appendLine()
            }
        }

        // 5. Fetal Movement & Kick Counter Sessions
        sb.appendLine(subBorder)
        sb.appendLine("5. FETAL MOVEMENT & KICK COUNTER SESSIONS")
        sb.appendLine(subBorder)
        if (kickSessions.isEmpty()) {
            sb.appendLine("No kick counter sessions recorded.")
        } else {
            kickSessions.sortedByDescending { it.timestamp }.forEachIndexed { idx, kick ->
                val kickDate = dateFormat.format(Date(kick.timestamp))
                val mins = kick.durationSeconds / 60
                val secs = kick.durationSeconds % 60
                val status = if (kick.targetReached) "Goal Reached (10 kicks)" else "Session Incomplete"
                sb.appendLine("#${idx + 1} Session Date: $kickDate")
                sb.appendLine("   Total Kicks:    ${kick.kickCount} kicks")
                sb.appendLine("   Duration:       ${mins}m ${secs}s")
                sb.appendLine("   Outcome:        $status")
                if (kick.notes.isNotBlank()) sb.appendLine("   Notes:          ${kick.notes}")
                sb.appendLine()
            }
        }

        // 6. Milestone Journal & Memory Reflections
        sb.appendLine(subBorder)
        sb.appendLine("6. MILESTONE JOURNAL & PREGNANCY REFLECTIONS")
        sb.appendLine(subBorder)
        if (journalEntries.isEmpty()) {
            sb.appendLine("No journal entries recorded.")
        } else {
            journalEntries.sortedBy { it.weekNumber }.forEachIndexed { idx, entry ->
                sb.appendLine("#${idx + 1} Week ${entry.weekNumber}: ${entry.title}")
                sb.appendLine("   Category:   ${entry.category}")
                sb.appendLine("   Date:       ${entry.dateString}")
                if (!entry.photoUri.isNullOrBlank()) sb.appendLine("   Photo URI:  ${entry.photoUri}")
                sb.appendLine("   Reflection: ${entry.reflection}")
                sb.appendLine()
            }
        }

        // 7. Postpartum Baby Care Logs
        sb.appendLine(subBorder)
        sb.appendLine("7. POSTPARTUM BABY CARE LOGS")
        sb.appendLine(subBorder)
        if (babyLogs.isEmpty()) {
            sb.appendLine("No baby care logs recorded.")
        } else {
            babyLogs.sortedByDescending { it.timestamp }.forEachIndexed { idx, baby ->
                val babyDate = dateFormat.format(Date(baby.timestamp))
                sb.appendLine("#${idx + 1} [${baby.logType}] $babyDate")
                sb.appendLine("   Details:   ${baby.detail}")
                if (baby.amountMl > 0) sb.appendLine("   Volume:    ${baby.amountMl} ml")
                if (baby.durationSeconds > 0) {
                    val mins = baby.durationSeconds / 60
                    sb.appendLine("   Duration:  ${mins} minutes")
                }
                if (baby.notes.isNotBlank()) sb.appendLine("   Notes:     ${baby.notes}")
                sb.appendLine()
            }
        }

        // 8. Preparation & Hospital Bag Checklists
        sb.appendLine(subBorder)
        sb.appendLine("8. PREPARATION & HOSPITAL BAG CHECKLISTS")
        sb.appendLine(subBorder)
        if (checklistItems.isEmpty()) {
            sb.appendLine("No checklist items recorded.")
        } else {
            val grouped = checklistItems.groupBy { it.category }
            grouped.forEach { (cat, items) ->
                sb.appendLine("[$cat]")
                items.forEach { item ->
                    val checkMark = if (item.isChecked) "[X]" else "[ ]"
                    sb.appendLine("  $checkMark ${item.title}")
                }
                sb.appendLine()
            }
        }

        sb.appendLine(border)
        sb.appendLine("END OF MAMAN & BÉBÉ ARCHIVE EXPORT")
        sb.appendLine(border)

        return sb.toString()
    }

    /**
     * Saves the formatted text content into the app's cache exports directory as a .txt file.
     */
    fun saveArchiveToFile(context: Context, textContent: String, currentWeek: Int): File? {
        return try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()
            val txtFile = File(exportDir, "Maman_Bebe_Complete_Archive_Week_${currentWeek}.txt")
            val outputStream = FileOutputStream(txtFile)
            outputStream.write(textContent.toByteArray(Charsets.UTF_8))
            outputStream.flush()
            outputStream.close()
            txtFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Shares the exported text file using Android's standard share intent sheet.
     */
    fun shareTextFile(context: Context, txtFile: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                txtFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Maman & Bébé Complete Pregnancy & Postpartum Archive")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Here is my complete pregnancy and postpartum archive exported from Maman & Bébé."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Share Complete Archive (.txt) via"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to share archive: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Copies the full plain text export to clipboard.
     */
    fun copyToClipboard(context: Context, textContent: String) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Maman & Bébé Complete Archive", textContent)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Complete archive copied to clipboard!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Could not copy to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
}
