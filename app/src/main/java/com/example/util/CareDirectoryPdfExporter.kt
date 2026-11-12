package com.example.util

import android.content.Context
import android.content.Intent
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
     * Génère un document PDF complet contenant l'annuaire médical et les rendez-vous prénataux.
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
            // Bannière supérieure
            paint.color = Color.rgb(217, 90, 101)
            paint.style = Paint.Style.FILL
            val headerRect = RectF(MARGIN, currentY, MARGIN + CONTENT_WIDTH, currentY + 70f)
            canvas.drawRoundRect(headerRect, 8f, 8f, paint)

            // Titre d'en-tête
            textPaint.color = Color.WHITE
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textPaint.textSize = 15f
            canvas.drawText("MAMAN & BÉBÉ — ANNUAIRE MÉDICAL & SOINS", MARGIN + 16f, currentY + 28f, textPaint)

            textPaint.textSize = 10f
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Contacts médicaux, suivi clinique et récapitulatif des rendez-vous prénataux", MARGIN + 16f, currentY + 46f, textPaint)

            currentY += 82f

            // Encadré résumé patient
            paint.color = Color.rgb(255, 245, 246)
            val summaryBox = RectF(MARGIN, currentY, MARGIN + CONTENT_WIDTH, currentY + 52f)
            canvas.drawRoundRect(summaryBox, 6f, 6f, paint)

            paint.color = Color.rgb(245, 204, 210)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            canvas.drawRoundRect(summaryBox, 6f, 6f, paint)
            paint.style = Paint.Style.FILL

            val dueDateFormatted = SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH).format(Date(dueDateMillis))
            val exportedDate = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.FRENCH).format(Date())

            textPaint.color = Color.rgb(30, 20, 12)
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textPaint.textSize = 10f
            canvas.drawText("Âge gestationnel : Semaine $currentWeek sur 40", MARGIN + 12f, currentY + 20f, textPaint)
            canvas.drawText("Date d'accouchement prévue : $dueDateFormatted", MARGIN + 12f, currentY + 38f, textPaint)

            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textPaint.color = Color.rgb(110, 86, 71)
            canvas.drawText("Généré le : $exportedDate", MARGIN + CONTENT_WIDTH - 190f, currentY + 20f, textPaint)
            canvas.drawText("Objet : Consultations & Suivi médical", MARGIN + CONTENT_WIDTH - 210f, currentY + 38f, textPaint)

            currentY += 66f
        }

        fun drawFooter(pageNum: Int) {
            linePaint.color = Color.rgb(230, 220, 215)
            linePaint.strokeWidth = 1f
            canvas.drawLine(MARGIN, PAGE_HEIGHT - 32f, MARGIN + CONTENT_WIDTH, PAGE_HEIGHT - 32f, linePaint)

            textPaint.color = Color.rgb(130, 115, 105)
            textPaint.textSize = 8.5f
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Maman & Bébé • Résumé médical prénatal confidentiel", MARGIN, PAGE_HEIGHT - 20f, textPaint)

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

        drawHeader()

        // 1. SECTION : CONTACTS MÉDICAUX
        checkNewPage(40f)
        paint.color = Color.rgb(217, 90, 101)
        canvas.drawRect(MARGIN, currentY, MARGIN + 4f, currentY + 16f, paint)

        textPaint.color = Color.rgb(30, 20, 12)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 12f
        canvas.drawText("1. ANNUAIRE MÉDICAL & CONTACTS DE SOINS", MARGIN + 10f, currentY + 13f, textPaint)
        currentY += 24f

        if (contacts.isEmpty()) {
            checkNewPage(24f)
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            textPaint.color = Color.GRAY
            textPaint.textSize = 9.5f
            canvas.drawText("Aucun contact enregistré. Ajoutez vos praticiens, maternité et pharmacie dans Maman & Bébé.", MARGIN + 8f, currentY + 12f, textPaint)
            currentY += 24f
        } else {
            for (contact in contacts) {
                checkNewPage(56f)

                paint.color = Color.rgb(250, 250, 252)
                val cardRect = RectF(MARGIN, currentY, MARGIN + CONTENT_WIDTH, currentY + 50f)
                canvas.drawRoundRect(cardRect, 4f, 4f, paint)

                paint.color = Color.rgb(230, 230, 235)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 0.8f
                canvas.drawRoundRect(cardRect, 4f, 4f, paint)
                paint.style = Paint.Style.FILL

                val badgeLabel = when (contact.type.uppercase()) {
                    "DOCTOR" -> "MÉDECIN"
                    "PHARMACY" -> "PHARMACIE"
                    "CLINIC", "HOSPITAL" -> "MATERNITÉ"
                    else -> contact.type.uppercase()
                }
                paint.color = when (contact.type.uppercase()) {
                    "DOCTOR", "MÉDECIN" -> Color.rgb(226, 109, 119)
                    "PHARMACY", "PHARMACIE" -> Color.rgb(77, 124, 102)
                    "CLINIC", "HOSPITAL", "MATERNITÉ", "CLINIQUE" -> Color.rgb(138, 104, 176)
                    else -> Color.rgb(110, 86, 71)
                }
                val badgeRect = RectF(MARGIN + 8f, currentY + 8f, MARGIN + 76f, currentY + 22f)
                canvas.drawRoundRect(badgeRect, 3f, 3f, paint)

                textPaint.color = Color.WHITE
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textPaint.textSize = 7.5f
                canvas.drawText(badgeLabel.take(10), MARGIN + 12f, currentY + 18f, textPaint)

                textPaint.color = Color.rgb(30, 20, 12)
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textPaint.textSize = 10.5f
                canvas.drawText(contact.name, MARGIN + 82f, currentY + 18f, textPaint)

                if (contact.phone.isNotBlank()) {
                    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    textPaint.color = Color.rgb(217, 90, 101)
                    textPaint.textSize = 9.5f
                    canvas.drawText("Tél. : ${contact.phone}", MARGIN + 8f, currentY + 36f, textPaint)
                }

                var infoLine = ""
                if (contact.address.isNotBlank()) infoLine += "Adresse : ${contact.address}  "
                if (contact.notes.isNotBlank()) infoLine += "• Notes : ${contact.notes}"

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

        // 2. SECTION : RENDEZ-VOUS PRÉNATAUX
        checkNewPage(40f)
        paint.color = Color.rgb(217, 90, 101)
        canvas.drawRect(MARGIN, currentY, MARGIN + 4f, currentY + 16f, paint)

        textPaint.color = Color.rgb(30, 20, 12)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 12f
        canvas.drawText("2. SUIVI PRÉNATAL & RENDEZ-VOUS PROGRAMMÉS", MARGIN + 10f, currentY + 13f, textPaint)
        currentY += 24f

        if (appointments.isEmpty()) {
            checkNewPage(24f)
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            textPaint.color = Color.GRAY
            textPaint.textSize = 9.5f
            canvas.drawText("Aucun rendez-vous clinique enregistré.", MARGIN + 8f, currentY + 12f, textPaint)
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

                textPaint.color = Color.rgb(30, 20, 12)
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textPaint.textSize = 10.5f
                canvas.drawText(app.title, MARGIN + 10f, currentY + 18f, textPaint)

                val statusText = if (app.isCompleted) "[EFFECTUÉ]" else "[PRÉVU]"
                textPaint.color = if (app.isCompleted) Color.rgb(77, 124, 102) else Color.rgb(217, 90, 101)
                textPaint.textSize = 8.5f
                val statusWidth = textPaint.measureText(statusText)
                canvas.drawText(statusText, MARGIN + CONTENT_WIDTH - statusWidth - 10f, currentY + 18f, textPaint)

                val dateStr = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRENCH).format(Date(app.dateTimestamp))
                textPaint.color = Color.rgb(90, 75, 65)
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textPaint.textSize = 9f
                canvas.drawText("Date : $dateStr", MARGIN + 10f, currentY + 34f, textPaint)

                val doctorDetails = "Praticien : ${app.doctorName.ifBlank { "Spécialiste assigné" }} • Lieu : ${app.location.ifBlank { "Maternité / Clinique" }}"
                canvas.drawText(doctorDetails, MARGIN + 10f, currentY + 47f, textPaint)

                currentY += 60f
            }
        }

        currentY += 12f

        // 3. SECTION : OBSERVATIONS DU PRATICIEN
        checkNewPage(120f)
        paint.color = Color.rgb(217, 90, 101)
        canvas.drawRect(MARGIN, currentY, MARGIN + 4f, currentY + 16f, paint)

        textPaint.color = Color.rgb(30, 20, 12)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 12f
        canvas.drawText("3. NOTES DE CONSULTATION & OBSERVATIONS MÉDICALES", MARGIN + 10f, currentY + 13f, textPaint)
        currentY += 24f

        paint.color = Color.rgb(255, 255, 255)
        val notesRect = RectF(MARGIN, currentY, MARGIN + CONTENT_WIDTH, currentY + 100f)
        canvas.drawRoundRect(notesRect, 4f, 4f, paint)

        paint.color = Color.rgb(220, 215, 210)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 0.8f
        canvas.drawRoundRect(notesRect, 4f, 4f, paint)

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
        canvas.drawText("(Observations manuscrites, tension artérielle, hauteur utérine, remarques d'échographie)", MARGIN + 12f, currentY + 16f, textPaint)

        currentY += 110f

        drawFooter(pageNumber)
        document.finishPage(page)

        return try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()
            val pdfFile = File(exportDir, "Maman_Annuaire_Soins_Semaine_${currentWeek}.pdf")
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
                putExtra(Intent.EXTRA_SUBJECT, "Maman & Bébé — Annuaire Médical & Rendez-vous")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Voici mon annuaire de soins prénataux, ma liste de contacts médicaux et mes rendez-vous exportés depuis Maman & Bébé."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Partager le PDF via"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Échec du partage PDF : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

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

            context.startActivity(Intent.createChooser(intent, "Ouvrir le PDF médical avec"))
        } catch (e: Exception) {
            e.printStackTrace()
            sharePdf(context, pdfFile)
        }
    }
}
