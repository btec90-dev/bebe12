package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.GlycemiaRecordEntity
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.viewmodel.PregnancyViewModel
import com.example.util.GlycemiaPdfExporter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GlycemiaTrackerDialog(
    viewModel: PregnancyViewModel,
    onDismiss: () -> Unit
) {
    val records by viewModel.glycemiaRecords.collectAsState()
    val motherName by viewModel.profileName.collectAsState()
    val fatherName by viewModel.fatherName.collectAsState()
    val currentWeek by viewModel.currentWeekNumber.collectAsState()
    val context = LocalContext.current

    var showAddModal by remember { mutableStateOf(false) }

    // Seed default sample readings if empty
    LaunchedEffect(records.isEmpty()) {
        if (records.isEmpty()) {
            val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())
            viewModel.addGlycemiaRecord(
                dateString = todayStr,
                period = "À jeun (matin)",
                glucoseValueMgDl = 86.0,
                targetMaxMgDl = 95.0,
                notes = "Au réveil, Conforme"
            )
            viewModel.addGlycemiaRecord(
                dateString = todayStr,
                period = "Post-Petit-Déjeuner",
                glucoseValueMgDl = 114.0,
                targetMaxMgDl = 120.0,
                notes = "2h après toast complet"
            )
            viewModel.addGlycemiaRecord(
                dateString = todayStr,
                period = "Post-Déjeuner",
                glucoseValueMgDl = 118.0,
                targetMaxMgDl = 120.0,
                notes = "Légumes vapeur et poisson"
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CrispWhiteSurface,
        shape = RoundedCornerShape(26.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEBEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bloodtype,
                        contentDescription = null,
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Autosurveillance Glycémique",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkEspresso,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "Carnet de maternité — Diabète gestationnel",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextWarmTaupe,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // PDF Download Action Banner
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("btn_export_glycemia_pdf"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Télécharger le Carnet PDF",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100),
                                    fontSize = 13.sp
                                )
                            )
                        }
                        Text(
                            text = "Format officiel carnet de maternité avec cibles médicales & visas praticien.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF795548),
                                fontSize = 11.sp
                            )
                        )
                        Button(
                            onClick = {
                                val pdfFile = GlycemiaPdfExporter.generateGlycemiaLogPdf(
                                    context = context,
                                    motherName = motherName,
                                    fatherName = fatherName,
                                    currentWeek = currentWeek,
                                    records = records
                                )
                                if (pdfFile != null) {
                                    Toast.makeText(context, "PDF généré : ${pdfFile.name}", Toast.LENGTH_SHORT).show()
                                    GlycemiaPdfExporter.sharePdf(context, pdfFile)
                                } else {
                                    Toast.makeText(context, "Erreur génération PDF", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Télécharger & Partager le PDF", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // Add measurement button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Relevés récents (${records.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkEspresso
                        )
                    )

                    OutlinedButton(
                        onClick = { showAddModal = true },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ajouter", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // List of Records
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(records, key = { it.id }) { rec ->
                        val isHigh = rec.glucoseValueMgDl > rec.targetMaxMgDl
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isHigh) Color(0xFFFFEBEE) else Color(0xFFF1F8E9)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${rec.period} • ${rec.dateString}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkEspresso,
                                            fontSize = 11.sp
                                        )
                                    )
                                    if (rec.notes.isNotBlank()) {
                                        Text(
                                            text = rec.notes,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = TextWarmTaupe,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${rec.glucoseValueMgDl.toInt()} mg/dL",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isHigh) Color(0xFFC62828) else Color(0xFF2E7D32),
                                                fontSize = 13.sp
                                            )
                                        )
                                        Text(
                                            text = if (isHigh) "Élevé (> ${rec.targetMaxMgDl.toInt()})" else "Objectif atteint",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isHigh) Color(0xFFC62828) else Color(0xFF2E7D32),
                                                fontSize = 9.sp
                                            )
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteGlycemiaRecord(rec) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Supprimer",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
            ) {
                Text("Fermer", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )

    // Add Measurement Modal
    if (showAddModal) {
        var valueInput by remember { mutableStateOf("") }
        var periodInput by remember { mutableStateOf("À jeun (matin)") }
        var notesInput by remember { mutableStateOf("") }

        val periods = listOf(
            "À jeun (matin)",
            "Post-Petit-Déjeuner",
            "Avant Déjeuner",
            "Post-Déjeuner",
            "Avant Dîner",
            "Post-Dîner",
            "Au Coucher"
        )

        AlertDialog(
            onDismissRequest = { showAddModal = false },
            containerColor = CrispWhiteSurface,
            shape = RoundedCornerShape(22.dp),
            title = { Text("Nouveau relevé de glycémie") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = valueInput,
                        onValueChange = { valueInput = it },
                        label = { Text("Valeur (mg/dL ou g/L)") },
                        placeholder = { Text("Ex: 92 ou 0.92") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text("Moment :", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        periods.take(4).forEach { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { periodInput = p },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = periodInput == p, onClick = { periodInput = p })
                                Text(text = p, fontSize = 12.sp)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Repas ou remarque") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rawVal = valueInput.replace(',', '.').toDoubleOrNull() ?: 0.0
                        val mgDl = if (rawVal in 0.1..3.0) rawVal * 100.0 else rawVal
                        if (mgDl > 0) {
                            val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())
                            viewModel.addGlycemiaRecord(
                                dateString = todayStr,
                                period = periodInput,
                                glucoseValueMgDl = mgDl,
                                notes = notesInput.trim()
                            )
                            showAddModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
                ) {
                    Text("Enregistrer", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddModal = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
