package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.local.AppointmentEntity
import com.example.data.local.MedicalContactEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CareDirectoryPdfExporter {

    private const val PAGE_WIDTH = 595 // Standard A4 width in points
    private const val PAGE_HEIGHT = 842 // Standard A4 height in points
    private const val MARGIN = 40f
    private const val CONTENT_WIDTH = PAGE_WIDTH - (MARGIN * 2)

    /**
     * Generates a polished PDF document containing the patient's Care Directory contacts
     * and scheduled prenatal clinical visits.
     */
    fun generateCareDirectoryPdf(
        context: Context,
        currentWeek: Int,
        dueDateMillis: Long,
        appointments: List<AppointmentEntity>,
        contacts: List<MedicalContactEntity>
    ): File? {
        val document = PdfDocument()

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        var currentY = MARGIN

        fun drawHeader() {
            // Top Accent Banner
            paint.color = Color.rgb(217, 90, 101) // Warm Rose Primary
            paint.style = Paint.Style.FILL
            val headerRect = RectF(MARGIN, currentY, MARGIN + CONTENT_WIDTH, currentY + 70f)
            canvas.drawRoundRect(headerRect, 8f, 8f, paint)

            // Header Title
            textPaint.color = Color.WHITE
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textPaint.textSize = 16f
            canvas.drawText("MAMAN & BÉBÉ PREGNANCY CARE DIRECTORY", MARGIN + 16f, currentY + 28f, textPaint)

            textPaint.textSize = 10f
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Medical Contacts, Clinical Visits & Prenatal Appointment Summary", MARGIN + 16f, currentY + 46f, textPaint)

            currentY += 82f

            // Patient Summary Info Box
            paint.color = Color.rgb(255, 245, 246) // Soft Rose Tint
            val summaryBox = RectF(MARGIN, currentY, MARGIN + CONTENT_WIDTH, currentY + 52f)
            canvas.drawRoundRect(summaryBox, 6f, 6f, paint)

            paint.color = Color.rgb(245, 204, 210)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            canvas.drawRoundRect(summaryBox, 6f, 6f, paint)
            paint.style = Paint.Style.FILL

            val dueDateFormatted = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(dueDateMillis))
            val exportedDate = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date())

            textPaint.color = Color.rgb(30, 20, 12)
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textPaint.textSize = 10f
            canvas.drawText("Gestational Age: Week $currentWeek of 40", MARGIN + 12f, currentY + 20f, textPaint)
            canvas.drawText("Estimated Due Date: $dueDateFormatted", MARGIN + 12f, currentY + 38f, textPaint)

            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textPaint.color = Color.rgb(110, 86, 71)
            canvas.drawText("Generated: $exportedDate", MARGIN + CONTENT_WIDTH - 180f, currentY + 20f, textPaint)
            canvas.drawText("Purpose: Clinical & Medical Consultations", MARGIN + CONTENT_WIDTH - 210f, currentY + 38f, textPaint)

            currentY += 66f
        }

        fun drawFooter(pageNum: Int) {
            linePaint.color = Color.rgb(230, 220, 215)
            linePaint.strokeWidth = 1f
            canvas.drawLine(MARGIN, PAGE_HEIGHT - 32f, MARGIN + CONTENT_WIDTH, PAGE_HEIGHT - 32f, linePaint)

            textPaint.color = Color.rgb(130, 115, 105)
            textPaint.textSize = 8.5f
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Maman & Bébé • Confidential Prenatal Medical Summary", MARGIN, PAGE_HEIGHT - 20f, textPaint)

            val pageStr = "Page $pageNum"
            val textWidth = textPaint.measureText(pageStr)
            canvas.drawText(pageStr, MARGIN + CONTENT_WIDTH - textWidth, PAGE_HEIGHT - 20f, textPaint)
        }

        fun checkNewPage(neededHeight: Float) {
            if (currentY + neededHeight > PAGE_HEIGHT - 50f) {
                drawFooter(pageNumber)
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                currentY = MARGIN + 20f
            }
        }

        // Draw initial page header
        drawHeader()

        // 1. SECTION: MEDICAL CONTACTS DIRECTORY
        checkNewPage(40f)
        paint.color = Color.rgb(217, 90, 101)
        canvas.drawRect(MARGIN, currentY, MARGIN + 4f, currentY + 16f, paint)

        textPaint.color = Color.rgb(30, 20, 12)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 12f
        canvas.drawText("1. MEDICAL DIRECTORY & CARE CONTACTS", MARGIN + 10f, currentY + 13f, textPaint)
        currentY += 24f

        if (contacts.isEmpty()) {
            checkNewPage(24f)
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            textPaint.color = Color.GRAY
            textPaint.textSize = 9.5f
            canvas.drawText("No contacts registered. Add your preferred doctors, clinic, and pharmacy in Maman & Bébé.", MARGIN + 8f, currentY + 12f, textPaint)
            currentY += 24f
        } else {
            for (contact in contacts) {
                checkNewPage(56f)

                // Contact card background
                paint.color = Color.rgb(250, 250, 252)
                val cardRect = RectF(MARGIN, currentY, MARGIN + CONTENT_WIDTH, currentY + 50f)
                canvas.drawRoundRect(cardRect, 4f, 4f, paint)

                paint.color = Color.rgb(230, 230, 235)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 0.8f
                canvas.drawRoundRect(cardRect, 4f, 4f, paint)
                paint.style = Paint.Style.FILL

                // Contact Type Badge
                paint.color = when (contact.type.uppercase()) {
                    "DOCTOR" -> Color.rgb(226, 109, 119)
                    "PHARMACY" -> Color.rgb(77, 124, 102)
                    "CLINIC", "HOSPITAL" -> Color.rgb(138, 104, 176)
                    else -> Color.rgb(110, 86, 71)
                }
                val badgeRect = RectF(MARGIN + 8f, currentY + 8f, MARGIN + 72f, currentY + 22f)
                canvas.drawRoundRect(badgeRect, 3f, 3f, paint)

                textPaint.color = Color.WHITE
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textPaint.textSize = 8f
                canvas.drawText(contact.type.uppercase(), MARGIN + 12f, currentY + 18f, textPaint)

                // Contact Name
                textPaint.color = Color.rgb(30, 20, 12)
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textPaint.textSize = 10.5f
                canvas.drawText(contact.name, MARGIN + 78f, currentY + 18f, textPaint)

                // Phone
                if (contact.phone.isNotBlank()) {
                    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    textPaint.color = Color.rgb(217, 90, 101)
                    textPaint.textSize = 9.5f
                    canvas.drawText("Tel: ${contact.phone}", MARGIN + 8f, currentY + 36f, textPaint)
                }

                // Address & Notes
                var infoLine = ""
                if (contact.address.isNotBlank()) infoLine += "Address: ${contact.address}  "
                if (contact.notes.isNotBlank()) infoLine += "• Notes: ${contact.notes}"

                if (infoLine.isNotBlank()) {
                    textPaint.color = Color.rgb(90, 80, 75)
                    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    textPaint.textSize = 8.5f
                    val truncated = if (infoLine.length > 75) infoLine.take(72) + "..." else infoLine
                    canvas.drawText(truncated, MARGIN + 130f, currentY + 36f, textPaint)
                }

                currentY += 56f
            }
        }

        currentY += 10f

        // 2. SECTION: CLINICAL VISITS & APPOINTMENTS
        checkNewPage(40f)
        paint.color = Color.rgb(217, 90, 101)
        canvas.drawRect(MARGIN, currentY, MARGIN + 4f, currentY + 16f, paint)

        textPaint.color = Color.rgb(30, 20, 12)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 12f
        canvas.drawText("2. PRENATAL CARE & SCHEDULED CLINICAL VISITS", MARGIN + 10f, currentY + 13f, textPaint)
        currentY += 24f

        if (appointments.isEmpty()) {
            checkNewPage(24f)
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            textPaint.color = Color.GRAY
            textPaint.textSize = 9.5f
            canvas.drawText("No upcoming clinical appointments logged.", MARGIN + 8f, currentY + 12f, textPaint)
            currentY += 24f
        } else {
            for (app in appointments) {
                checkNewPage(60f)

                paint.color = if (app.isCompleted) Color.rgb(246, 252, 248) else Color.rgb(255, 255, 255)
                val appRect = RectF(MARGIN, currentY, MARGIN + CONTENT_WIDTH, currentY + 54f)
                canvas.drawRoundRect(appRect, 4f, 4f, paint)

                paint.color = Color.rgb(230, 225, 220)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 0.8f
                canvas.drawRoundRect(appRect, 4f, 4f, paint)
                paint.style = Paint.Style.FILL

                // Title & Status
                textPaint.color = Color.rgb(30, 20, 12)
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textPaint.textSize = 10.5f
                canvas.drawText(app.title, MARGIN + 10f, currentY + 18f, textPaint)

                val statusText = if (app.isCompleted) "[COMPLETED]" else "[SCHEDULED]"
                textPaint.color = if (app.isCompleted) Color.rgb(77, 124, 102) else Color.rgb(217, 90, 101)
                textPaint.textSize = 8.5f
                val statusWidth = textPaint.measureText(statusText)
                canvas.drawText(statusText, MARGIN + CONTENT_WIDTH - statusWidth - 10f, currentY + 18f, textPaint)

                // Date & Doctor Info
                val dateStr = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()).format(Date(app.dateTimestamp))
                textPaint.color = Color.rgb(90, 75, 65)
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textPaint.textSize = 9f
                canvas.drawText("Date: $dateStr", MARGIN + 10f, currentY + 34f, textPaint)

                val doctorDetails = "Physician: Dr. ${app.doctorName.ifBlank { "Assigned Specialist" }} • Location: ${app.location.ifBlank { "Maternity Clinic" }}"
                canvas.drawText(doctorDetails, MARGIN + 10f, currentY + 47f, textPaint)

                currentY += 60f
            }
        }

        currentY += 12f

        // 3. SECTION: PHYSICIAN CLINICAL CONSULTATION NOTES
        checkNewPage(120f)
        paint.color = Color.rgb(217, 90, 101)
        canvas.drawRect(MARGIN, currentY, MARGIN + 4f, currentY + 16f, paint)

        textPaint.color = Color.rgb(30, 20, 12)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 12f
        canvas.drawText("3. CLINICAL CONSULTATION NOTES & PHYSICIAN OBSERVATIONS", MARGIN + 10f, currentY + 13f, textPaint)
        currentY += 24f

        // Lined notes box for handwritten doctor notes during appointment
        paint.color = Color.rgb(255, 255, 255)
        val notesRect = RectF(MARGIN, currentY, MARGIN + CONTENT_WIDTH, currentY + 100f)
        canvas.drawRoundRect(notesRect, 4f, 4f, paint)

        paint.color = Color.rgb(220, 215, 210)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 0.8f
        canvas.drawRoundRect(notesRect, 4f, 4f, paint)

        // Draw note lines for Doctor to handwrite Blood Pressure, Weight, Recommendations
        linePaint.color = Color.rgb(235, 230, 225)
        linePaint.strokeWidth = 0.7f
        var lineY = currentY + 25f
        while (lineY < currentY + 95f) {
            canvas.drawLine(MARGIN + 10f, lineY, MARGIN + CONTENT_WIDTH - 10f, lineY, linePaint)
            lineY += 24f
        }

        textPaint.color = Color.rgb(160, 150, 140)
        textPaint.textSize = 8.5f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        canvas.drawText("(Handwritten clinical observations, blood pressure, fundal height, ultrasound notes & instructions)", MARGIN + 12f, currentY + 16f, textPaint)

        currentY += 110f

        // Finish last page footer
        drawFooter(pageNumber)
        document.finishPage(page)

        // Write to cache file
        return try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()
            val pdfFile = File(exportDir, "Maman_Care_Directory_Week_${currentWeek}.pdf")
            val outputStream = FileOutputStream(pdfFile)
            document.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            document.close()
            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
            null
        }
    }

    /**
     * Opens the standard Android Share sheet with the generated medical PDF document.
     */
    fun sharePdf(context: Context, pdfFile: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Maman & Bébé Care Directory & Medical Appointment Summary")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Here is my prenatal care directory, contact list, and scheduled medical appointments exported from Maman & Bébé."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Share Care Directory PDF via"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to share PDF: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens the PDF directly in a PDF viewer app on the device.
     */
    fun viewPdf(context: Context, pdfFile: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(Intent.createChooser(intent, "Open Medical PDF with"))
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to sharing if no dedicated viewer is installed
            sharePdf(context, pdfFile)
        }
    }
}
