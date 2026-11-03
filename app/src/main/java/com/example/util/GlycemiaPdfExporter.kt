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
import com.example.data.local.GlycemiaRecordEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GlycemiaPdfExporter {

    private const val PAGE_WIDTH = 595 // Standard A4 width in points
    private const val PAGE_HEIGHT = 842 // Standard A4 height in points
    private const val MARGIN = 36f
    private const val CONTENT_WIDTH = PAGE_WIDTH - (MARGIN * 2)

    /**
     * Generates an official French maternity logbook format PDF for gestational diabetes self-monitoring.
     */
    fun generateGlycemiaLogPdf(
        context: Context,
        motherName: String = "Fatou",
        fatherName: String = "Bala",
        currentWeek: Int,
        records: List<GlycemiaRecordEntity>
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
            // Main Top Header Banner (Medical Espresso)
            paint.color = Color.rgb(45, 27, 16) // Deep Espresso Primary
            paint.style = Paint.Style.FILL
            val headerRect = RectF(MARGIN, currentY, MARGIN + CONTENT_WIDTH, currentY + 68f)
            canvas.drawRoundRect(headerRect, 8f, 8f, paint)

            // Header Title
            textPaint.color = Color.WHITE
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textPaint.textSize = 15f
            canvas.drawText("MAMAN & BÉBÉ — CARNET DE MATERNITÉ", MARGIN + 14f, currentY + 26f, textPaint)

            textPaint.textSize = 10f
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Autosurveillance Glycémique du Diabète Gestationnel & Suivi Obstétrique", MARGIN + 14f, currentY + 44f, textPaint)

            currentY += 80f

            // Patient & Family Information Card
            paint.color = Color.rgb(253, 247, 240) // Soft Cream
            canvas.drawRoundRect(RectF(MARGIN, currentY, MARGIN + CONTENT_WIDTH, currentY + 48f), 6f, 6f, paint)

            textPaint.color = Color.rgb(45, 27, 16)
            textPaint.textSize = 10.5f
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Patiente (Mère) : $motherName", MARGIN + 12f, currentY + 18f, textPaint)
            canvas.drawText("Accompagnant (Père) : $fatherName", MARGIN + 12f, currentY + 36f, textPaint)

            val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())
            textPaint.textSize = 9.5f
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Terme actuel : Semaine $currentWeek SA", MARGIN + 320f, currentY + 18f, textPaint)
            canvas.drawText("Édité le : $dateStr", MARGIN + 320f, currentY + 36f, textPaint)

            currentY += 58f

            // Targets Reminder Card (Normes Médicales)
            paint.color = Color.rgb(243, 229, 215) // Soft Toffee
            canvas.drawRoundRect(RectF(MARGIN, currentY, MARGIN + CONTENT_WIDTH, currentY + 38f), 6f, 6f, paint)

            textPaint.color = Color.rgb(158, 87, 39) // Caramel Primary
            textPaint.textSize = 9.5f
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("OBJECTIFS CIBLES MÉDICAUX :", MARGIN + 12f, currentY + 15f, textPaint)

            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textPaint.textSize = 8.5f
            textPaint.color = Color.rgb(45, 27, 16)
            canvas.drawText("• À jeun (matin) : < 0.95 g/L (< 95 mg/dL)", MARGIN + 12f, currentY + 28f, textPaint)
            canvas.drawText("• 2h post-prandial : < 1.20 g/L (< 120 mg/dL)", MARGIN + 260f, currentY + 28f, textPaint)

            currentY += 48f
        }

        drawHeader()

        // Table Header
        paint.color = Color.rgb(235, 222, 210)
        canvas.drawRect(MARGIN, currentY, MARGIN + CONTENT_WIDTH, currentY + 22f, paint)

        textPaint.color = Color.rgb(45, 27, 16)
        textPaint.textSize = 9f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        canvas.drawText("Date", MARGIN + 8f, currentY + 15f, textPaint)
        canvas.drawText("Moment / Repas", MARGIN + 80f, currentY + 15f, textPaint)
        canvas.drawText("Glycémie (mg/dL)", MARGIN + 215f, currentY + 15f, textPaint)
        canvas.drawText("Valeur (g/L)", MARGIN + 305f, currentY + 15f, textPaint)
        canvas.drawText("Statut", MARGIN + 380f, currentY + 15f, textPaint)
        canvas.drawText("Notes / Insuline", MARGIN + 445f, currentY + 15f, textPaint)

        currentY += 24f

        linePaint.color = Color.rgb(220, 210, 200)
        linePaint.strokeWidth = 1f

        val displayRecords = if (records.isNotEmpty()) records else listOf(
            GlycemiaRecordEntity(dateString = "Exemple", period = "À jeun (matin)", glucoseValueMgDl = 86.0, targetMaxMgDl = 95.0, notes = "Conforme"),
            GlycemiaRecordEntity(dateString = "Exemple", period = "Post-Petit-Déjeuner", glucoseValueMgDl = 112.0, targetMaxMgDl = 120.0, notes = "Pain complet + œuf"),
            GlycemiaRecordEntity(dateString = "Exemple", period = "Post-Déjeuner", glucoseValueMgDl = 118.0, targetMaxMgDl = 120.0, notes = "Légumes vapeur + quinoa")
        )

        for (rec in displayRecords) {
            if (currentY > PAGE_HEIGHT - 120f) {
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                currentY = MARGIN
                drawHeader()
            }

            val gL = rec.glucoseValueMgDl / 100.0
            val isHigh = rec.glucoseValueMgDl > rec.targetMaxMgDl

            // Row background for high values
            if (isHigh) {
                paint.color = Color.rgb(255, 235, 235)
                canvas.drawRect(MARGIN, currentY, MARGIN + CONTENT_WIDTH, currentY + 20f, paint)
            }

            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textPaint.textSize = 8.5f
            textPaint.color = Color.rgb(45, 27, 16)

            canvas.drawText(rec.dateString, MARGIN + 8f, currentY + 14f, textPaint)
            canvas.drawText(rec.period.take(22), MARGIN + 80f, currentY + 14f, textPaint)
            canvas.drawText("${rec.glucoseValueMgDl.toInt()} mg/dL", MARGIN + 215f, currentY + 14f, textPaint)
            canvas.drawText(String.format(Locale.US, "%.2f g/L", gL), MARGIN + 305f, currentY + 14f, textPaint)

            // Status label
            if (isHigh) {
                textPaint.color = Color.rgb(180, 40, 40)
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("Élevé (> ${rec.targetMaxMgDl.toInt()})", MARGIN + 380f, currentY + 14f, textPaint)
            } else {
                textPaint.color = Color.rgb(40, 130, 60)
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText("Normal", MARGIN + 380f, currentY + 14f, textPaint)
            }

            textPaint.color = Color.rgb(80, 70, 60)
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(rec.notes.take(18).ifBlank { "-" }, MARGIN + 445f, currentY + 14f, textPaint)

            canvas.drawLine(MARGIN, currentY + 20f, MARGIN + CONTENT_WIDTH, currentY + 20f, linePaint)
            currentY += 21f
        }

        // Doctor / Sage-Femme Signature & Remarks Box
        currentY = (currentY + 20f).coerceAtLeast(PAGE_HEIGHT - 100f)
        paint.color = Color.rgb(248, 244, 238)
        canvas.drawRoundRect(RectF(MARGIN, currentY, MARGIN + CONTENT_WIDTH, currentY + 54f), 6f, 6f, paint)

        textPaint.color = Color.rgb(45, 27, 16)
        textPaint.textSize = 9f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Visas & Observations du Diabétologue / Sage-Femme :", MARGIN + 12f, currentY + 16f, textPaint)

        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        textPaint.color = Color.rgb(120, 110, 100)
        canvas.drawText("Signature & Cachet du praticien :", MARGIN + 330f, currentY + 16f, textPaint)

        document.finishPage(page)

        // Save PDF to cache
        return try {
            val exportDir = File(context.cacheDir, "maternity_exports").apply { mkdirs() }
            val file = File(exportDir, "carnet_glycemie_maternite_${motherName.lowercase()}_w$currentWeek.pdf")
            val outputStream = FileOutputStream(file)
            document.writeTo(outputStream)
            outputStream.close()
            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
            null
        }
    }

    fun sharePdf(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Carnet d'Autosurveillance Glycémique — Maman & Bébé")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Partager le carnet glycémique PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "Erreur de partage: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
