package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.BabyLogEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.PregnancyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BabyLogScreen(
    viewModel: PregnancyViewModel
) {
    val babyLogs by viewModel.babyLogs.collectAsState()
    val isNursingRunning by viewModel.isNursingTimerRunning.collectAsState()
    val nursingDuration by viewModel.nursingDurationSeconds.collectAsState()
    val nursingSide by viewModel.nursingSide.collectAsState()
    val hasHealthPerms by viewModel.hasHealthPermissions.collectAsState()
    val isCompact by viewModel.isBabyLogCompact.collectAsState()
    val motherName by viewModel.profileName.collectAsState()
    val fatherName by viewModel.fatherName.collectAsState()
    val context = LocalContext.current

    var bottleAmountMl by remember { mutableIntStateOf(90) }
    var isFormula by remember { mutableStateOf(false) }
    var sleepMinutesSlider by remember { mutableFloatStateOf(60f) }

    val nurseMins = nursingDuration / 60
    val nurseSecs = nursingDuration % 60
    val nursingTimerFormatted = String.format(Locale.getDefault(), "%02d:%02d", nurseMins, nurseSecs)

    // Compute compact aggregates
    val nursingLogs = babyLogs.filter { it.logType == "NURSING" }
    val bottleLogs = babyLogs.filter { it.logType == "BOTTLE" }
    val diaperLogs = babyLogs.filter { it.logType == "DIAPER" }
    val sleepLogs = babyLogs.filter { it.logType == "SLEEP" }

    val totalNursingSeconds = nursingLogs.sumOf { it.durationSeconds }
    val totalBottleMl = bottleLogs.sumOf { it.amountMl }
    val wetDiaperCount = diaperLogs.count { it.detail.contains("Wet", ignoreCase = true) || it.detail.contains("Both", ignoreCase = true) }
    val dirtyDiaperCount = diaperLogs.count { it.detail.contains("Dirty", ignoreCase = true) || it.detail.contains("Both", ignoreCase = true) }
    val totalSleepMinutes = sleepLogs.sumOf { it.durationSeconds / 60 }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftPinkCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.illustration_baby_peaceful),
                            contentDescription = "Newborn baby",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Carnet de Santé Bébé",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = RosePrimary
                            ),
                            modifier = Modifier.testTag("baby_logger_title")
                        )
                        Text(
                            text = "Parents : ${motherName.ifBlank { "Fatou" }} & ${fatherName.ifBlank { "Bala" }}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DeepEspressoPrimary
                            )
                        )
                        Text(
                            text = "Alimentation, couches et suivi du rythme de bébé",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        }

        // Toggle Version Compacte / Complète
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_baby_log_toggle"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCompact) SageContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isCompact) Icons.Default.ViewHeadline else Icons.Default.ViewAgenda,
                            contentDescription = null,
                            tint = if (isCompact) SageTertiary else RosePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isCompact) "Version Compacte (Pédiatre)" else "Version Standard (Outils)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isCompact) "Synthèse journalière pour la visite médicale" else "Chronomètres et curseurs d'allaitement",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    FilterChip(
                        selected = isCompact,
                        onClick = { viewModel.toggleBabyLogCompact() },
                        label = {
                            Text(
                                text = if (isCompact) "Compact ✓" else "Détaillé",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SageTertiary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("btn_toggle_compact_mode")
                    )
                }
            }
        }

        if (isCompact) {
            // ==================== COMPACT VERSION (PEDIATRICIAN VIEW) ====================
            item {
                Text(
                    text = "Bilan du Jour pour Consultation",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepEspressoPrimary
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Nursing card
                    CompactStatCard(
                        title = "Tétées",
                        value = "${nursingLogs.size} fois",
                        detail = "${totalNursingSeconds / 60} min au total",
                        emoji = "🤱",
                        bgColor = SoftPinkCard,
                        modifier = Modifier.weight(1f)
                    )

                    // Bottle card
                    CompactStatCard(
                        title = "Biberons",
                        value = "${totalBottleMl} ml",
                        detail = "${bottleLogs.size} prises",
                        emoji = "🍼",
                        bgColor = SoftPeachCard,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Diapers card
                    CompactStatCard(
                        title = "Couches",
                        value = "${diaperLogs.size} changes",
                        detail = "$wetDiaperCount 💧 / $dirtyDiaperCount 💩",
                        emoji = "🧷",
                        bgColor = Color(0xFFE8F5E9),
                        modifier = Modifier.weight(1f)
                    )

                    // Sleep card
                    CompactStatCard(
                        title = "Sommeil",
                        value = "${String.format(Locale.getDefault(), "%.1f", totalSleepMinutes / 60f)} h",
                        detail = "${sleepLogs.size} siestes",
                        emoji = "🌙",
                        bgColor = SoftSageCard,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Actions Bar in Compact Mode
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = DeepEspressoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ajout Rapide 1-Clic",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.logDiaper("Wet 💧") },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("💧 Mouillée", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.logDiaper("Dirty 💩") },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("💩 Selle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.logBottleFeeding(90, false) },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("🍼 90ml", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Compact Chronological List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Événements Chronologiques (${babyLogs.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepEspressoPrimary
                        )
                    )

                    IconButton(onClick = {
                        val shareText = buildString {
                            appendLine("📊 Carnet de Santé Bébé — Synthèse du Jour")
                            appendLine("Bébé de ${motherName.ifBlank { "Fatou" }} & ${fatherName.ifBlank { "Bala" }}")
                            appendLine("-----------------------------")
                            appendLine("• Allaitement : ${nursingLogs.size} tétées (${totalNursingSeconds / 60} min)")
                            appendLine("• Biberons : ${totalBottleMl} ml (${bottleLogs.size} prises)")
                            appendLine("• Couches : ${diaperLogs.size} changes (💧 $wetDiaperCount mouillées, 💩 $dirtyDiaperCount selles)")
                            appendLine("• Sommeil : ${String.format(Locale.getDefault(), "%.1f", totalSleepMinutes / 60f)} heures")
                            appendLine("-----------------------------")
                            appendLine("Événements :")
                            babyLogs.take(10).forEach { log ->
                                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(log.timestamp))
                                appendLine("[$time] ${log.logType} : ${log.detail}")
                            }
                        }
                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(android.content.Intent.createChooser(sendIntent, "Partager le carnet pédiatre"))
                    }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Partager bilan", tint = DeepEspressoPrimary)
                    }
                }
            }

            if (babyLogs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "Aucune activité enregistrée aujourd'hui. Utilisez les boutons d'ajout rapide ci-dessus.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        )
                    }
                }
            } else {
                items(babyLogs) { log ->
                    CompactLogRow(log = log)
                }
            }

        } else {
            // ==================== FULL DETAILED VERSION ====================
            // Section 1: Nursing Timer
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_nursing_timer"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Spa, contentDescription = null, tint = RosePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Breastfeeding Timer",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Text(
                                text = nursingTimerFormatted,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isNursingRunning) RosePrimary else MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.testTag("nursing_timer_display")
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.toggleNursingTimer("LEFT") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_nurse_left"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isNursingRunning && nursingSide == "LEFT") RosePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isNursingRunning && nursingSide == "LEFT") Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Icon(
                                    imageVector = if (isNursingRunning && nursingSide == "LEFT") Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Left Breast")
                            }

                            Button(
                                onClick = { viewModel.toggleNursingTimer("RIGHT") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_nurse_right"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isNursingRunning && nursingSide == "RIGHT") RosePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isNursingRunning && nursingSide == "RIGHT") Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Icon(
                                    imageVector = if (isNursingRunning && nursingSide == "RIGHT") Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Right Breast")
                            }
                        }

                        if (nursingDuration > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.saveNursingSession() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_save_nursing"),
                                colors = ButtonDefaults.buttonColors(containerColor = SageTertiary)
                            ) {
                                Text("Save Nursing Session ($nursingTimerFormatted)")
                            }
                        }
                    }
                }
            }

            // Section 2: Bottle Feeding
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_bottle_feeding"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftPeachCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.LocalDrink, contentDescription = null, tint = Color(0xFFD97724))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Bottle Feeding",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = "$bottleAmountMl ml (${String.format(Locale.getDefault(), "%.1f", bottleAmountMl / 29.57)} oz)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = !isFormula,
                                onClick = { isFormula = false },
                                label = { Text("Breastmilk") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RoseContainer)
                            )
                            FilterChip(
                                selected = isFormula,
                                onClick = { isFormula = true },
                                label = { Text("Formula") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RoseContainer)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(60, 90, 120, 150, 180).forEach { ml ->
                                OutlinedButton(
                                    onClick = { bottleAmountMl = ml },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("${ml}ml", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.logBottleFeeding(bottleAmountMl, isFormula) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_save_bottle"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97724))
                        ) {
                            Text("Log Bottle Feeding ($bottleAmountMl ml)")
                        }
                    }
                }
            }

            // Section 3: Diaper Tracker
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_diaper_tracker"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Wc, contentDescription = null, tint = RosePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Diaper Change",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.logDiaper("Wet 💧") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_diaper_wet"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1F5FE), contentColor = Color(0xFF0288D1))
                            ) {
                                Text("Wet 💧")
                            }

                            Button(
                                onClick = { viewModel.logDiaper("Dirty 💩") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_diaper_dirty"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF3E0), contentColor = Color(0xFFE65100))
                            ) {
                                Text("Dirty 💩")
                            }

                            Button(
                                onClick = { viewModel.logDiaper("Both 💧💩") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_diaper_both"),
                                colors = ButtonDefaults.buttonColors(containerColor = RoseContainer, contentColor = RosePrimary)
                            ) {
                                Text("Both")
                            }
                        }
                    }
                }
            }

            // Section 4: Sleep Session
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_sleep_tracker"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftSageCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Bedtime, contentDescription = null, tint = SageTertiary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sleep & Nap Tracker",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            if (hasHealthPerms) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.HealthAndSafety,
                                        contentDescription = null,
                                        tint = SageTertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Health Connect",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SageTertiary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Duration: ${sleepMinutesSlider.toInt()} minutes (${String.format(Locale.getDefault(), "%.1f", sleepMinutesSlider / 60)} hours)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )

                        Slider(
                            value = sleepMinutesSlider,
                            onValueChange = { sleepMinutesSlider = it },
                            valueRange = 15f..360f,
                            steps = 22,
                            colors = SliderDefaults.colors(thumbColor = SageTertiary, activeTrackColor = SageTertiary),
                            modifier = Modifier.testTag("slider_sleep_duration")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.logSleepSession(sleepMinutesSlider.toLong(), isBaby = true) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_log_baby_sleep"),
                                colors = ButtonDefaults.buttonColors(containerColor = SageTertiary)
                            ) {
                                Text("Log Baby Sleep")
                            }

                            Button(
                                onClick = { viewModel.logSleepSession(sleepMinutesSlider.toLong(), isBaby = false) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_log_mom_sleep"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text("Log Mom Sleep")
                            }
                        }
                    }
                }
            }

            // Section 5: Recent Baby Logs List
            item {
                Text(
                    text = "Today's Baby Activity Log",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (babyLogs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "No baby events logged yet. Tap any tool above to record nursing, bottle, or diaper!",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        )
                    }
                }
            } else {
                items(babyLogs) { log ->
                    val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("baby_log_item_${log.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val icon = when (log.logType) {
                                "NURSING" -> "🤱"
                                "BOTTLE" -> "🍼"
                                "DIAPER" -> "🧷"
                                "SLEEP" -> "🌙"
                                else -> "👶"
                            }
                            Text(text = icon, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${log.logType.lowercase().replaceFirstChar { it.uppercase() }}: ${log.detail}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                if (log.durationSeconds > 0) {
                                    val m = log.durationSeconds / 60
                                    val s = log.durationSeconds % 60
                                    Text(
                                        text = "Duration: ${m}m ${s}s",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                                    )
                                }
                                if (log.amountMl > 0) {
                                    Text(
                                        text = "Amount: ${log.amountMl} ml",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                                    )
                                }
                            }

                            Text(
                                text = timeStr,
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactStatCard(
    title: String,
    value: String,
    detail: String,
    emoji: String,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkEspresso
                    )
                )
                Text(text = emoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepEspressoPrimary
                )
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = TextWarmTaupe
                )
            )
        }
    }
}

@Composable
private fun CompactLogRow(log: BabyLogEntity) {
    val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(log.timestamp))
    val icon = when (log.logType) {
        "NURSING" -> "🤱"
        "BOTTLE" -> "🍼"
        "DIAPER" -> "🧷"
        "SLEEP" -> "🌙"
        else -> "👶"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("compact_log_row_${log.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = timeStr,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DeepEspressoPrimary
                ),
                modifier = Modifier.width(48.dp)
            )

            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${log.logType} — ${log.detail}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                )
                if (log.amountMl > 0 || log.durationSeconds > 0) {
                    val sub = buildString {
                        if (log.amountMl > 0) append("${log.amountMl} ml ")
                        if (log.durationSeconds > 0) append("${log.durationSeconds / 60} min")
                    }
                    Text(
                        text = sub,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SageTertiary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
