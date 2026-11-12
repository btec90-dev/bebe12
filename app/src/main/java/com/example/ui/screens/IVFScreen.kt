package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.CaramelSecondary
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.LatteBorderSoft
import com.example.ui.theme.SoftToffeeContainer
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.theme.WarmCanvasBackground
import com.example.ui.viewmodel.IVFViewModel
import com.example.ui.viewmodel.IvfMedicationInstruction
import com.example.ui.viewmodel.IvfPhase
import com.example.ui.viewmodel.IvfPhaseStatus
import com.example.ui.viewmodel.IvfPhaseTask

@Composable
fun IVFScreen(
    viewModel: IVFViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allExpanded = uiState.phases.isNotEmpty() && uiState.phases.all { it.isExpanded }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCanvasBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
                .testTag("ivf_screen_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Navigation & Protocol Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(CircleShape)
                                .background(CrispWhiteSurface)
                                .border(1.dp, LatteBorderSoft, CircleShape)
                                .testTag("btn_ivf_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour",
                                tint = DeepEspressoPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Parcours FIV • Module PMA",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextDarkEspresso,
                                    fontSize = 20.sp
                                )
                            )
                            Text(
                                text = "${uiState.protocolTitle} • Jour ${uiState.cycleDay}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextWarmTaupe,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            if (allExpanded) {
                                viewModel.collapseAllPhases()
                            } else {
                                viewModel.expandAllPhases()
                            }
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftToffeeContainer)
                            .testTag("btn_ivf_toggle_all")
                    ) {
                        Icon(
                            imageVector = if (allExpanded) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                            contentDescription = if (allExpanded) "Tout réduire" else "Tout déplier",
                            tint = DeepEspressoPrimary
                        )
                    }
                }
            }

            // Header Banner with Current Medication Instructions
            item {
                IvfMedicationHeaderBanner(
                    currentPhaseNumber = uiState.currentPhaseNumber,
                    cycleDay = uiState.cycleDay,
                    instruction = uiState.medicationInstruction,
                    onConfirmDoseToggle = { viewModel.toggleDailyDoseTaken() }
                )
            }

            // Section Title & Phase Step Progress Strip
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Les 4 Phases du Protocole FIV",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso,
                                fontSize = 16.sp
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SoftToffeeContainer
                        ) {
                            Text(
                                text = "Phase ${uiState.currentPhaseNumber} / 4 active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DeepEspressoPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    // 4-step horizontal phase progress indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        uiState.phases.forEach { phase ->
                            val barColor = when (phase.status) {
                                IvfPhaseStatus.COMPLETED -> Color(0xFF4A5D4E)
                                IvfPhaseStatus.ACTIVE -> DeepEspressoPrimary
                                IvfPhaseStatus.UPCOMING -> LatteBorderSoft
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(barColor)
                            )
                        }
                    }
                }
            }

            // Four Expandable Phase Cards
            items(uiState.phases, key = { it.id }) { phase ->
                IvfExpandablePhaseCard(
                    phase = phase,
                    onToggleExpand = { viewModel.togglePhaseExpanded(phase.id) },
                    onToggleTask = { taskId -> viewModel.toggleTaskCompleted(taskId) },
                    onSetActivePhase = { viewModel.setActivePhase(phase.phaseNumber) }
                )
            }
        }
    }
}

@Composable
private fun IvfMedicationHeaderBanner(
    currentPhaseNumber: Int,
    cycleDay: Int,
    instruction: IvfMedicationInstruction,
    onConfirmDoseToggle: () -> Unit
) {
    val bannerGradient = Brush.linearGradient(
        colors = listOf(
            DeepEspressoPrimary,
            Color(0xFF7A3E15)
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = DeepEspressoPrimary.copy(alpha = 0.25f)
            )
            .testTag("ivf_medication_header_banner"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bannerGradient)
                .drawBehind {
                    drawCircle(
                        color = CaramelSecondary.copy(alpha = 0.16f),
                        radius = size.minDimension * 0.45f,
                        center = Offset(size.width * 0.92f, size.height * 0.15f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = size.minDimension * 0.28f,
                        center = Offset(size.width * 0.12f, size.height * 0.90f)
                    )
                }
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Header Badge Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = "Instructions Médication FIV",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TRAITEMENT DU JOUR • PHASE $currentPhaseNumber",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SoftToffeeSecondary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.7.sp,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "Consignes Médication Actuelles",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "J$cycleDay",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Active Medication Details Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.14f))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = instruction.medicationName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp
                        ),
                        modifier = Modifier.testTag("ivf_banner_med_name")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MedicationMetaChip(
                            icon = Icons.Default.Vaccines,
                            text = instruction.dosage
                        )
                        MedicationMetaChip(
                            icon = Icons.Default.AccessTime,
                            text = instruction.scheduleTime
                        )
                    }

                    Text(
                        text = "Voie : ${instruction.route}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SoftToffeeSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )

                    Text(
                        text = instruction.instructionSummary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.18f))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SoftToffeeSecondary,
                            modifier = Modifier
                                .size(15.dp)
                                .padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = instruction.cautionNote,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.88f),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }

                // Interactive Daily Dose Confirmation Button
                Button(
                    onClick = onConfirmDoseToggle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_ivf_confirm_dose"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (instruction.isDoseTakenToday) Color(0xFF4A5D4E) else Color.White,
                        contentColor = if (instruction.isDoseTakenToday) Color.White else DeepEspressoPrimary
                    )
                ) {
                    Icon(
                        imageVector = if (instruction.isDoseTakenToday) Icons.Default.CheckCircle else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (instruction.isDoseTakenToday) {
                            "Dose d'aujourd'hui confirmée ✓"
                        } else {
                            "Confirmer la prise du jour"
                        },
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun MedicationMetaChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.16f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SoftToffeeSecondary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IvfExpandablePhaseCard(
    phase: IvfPhase,
    onToggleExpand: () -> Unit,
    onToggleTask: (String) -> Unit,
    onSetActivePhase: () -> Unit
) {
    val completedCount = phase.tasks.count { it.isCompleted }
    val totalCount = phase.tasks.size.coerceAtLeast(1)
    val progressRatio = completedCount.toFloat() / totalCount.toFloat()

    val isActive = phase.status == IvfPhaseStatus.ACTIVE
    val isCompleted = phase.status == IvfPhaseStatus.COMPLETED

    val badgeBg = when (phase.status) {
        IvfPhaseStatus.ACTIVE -> DeepEspressoPrimary
        IvfPhaseStatus.COMPLETED -> Color(0xFF4A5D4E)
        IvfPhaseStatus.UPCOMING -> SoftToffeeContainer
    }
    val badgeFg = when (phase.status) {
        IvfPhaseStatus.ACTIVE, IvfPhaseStatus.COMPLETED -> Color.White
        IvfPhaseStatus.UPCOMING -> DeepEspressoPrimary
    }
    val statusLabel = when (phase.status) {
        IvfPhaseStatus.ACTIVE -> "Phase Active"
        IvfPhaseStatus.COMPLETED -> "Terminée"
        IvfPhaseStatus.UPCOMING -> "À venir"
    }

    val chevronRotation by animateFloatAsState(
        targetValue = if (phase.isExpanded) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "ivfChevronRotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isActive) 6.dp else 2.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = DeepEspressoPrimary.copy(alpha = 0.08f)
            )
            .testTag("ivf_phase_card_${phase.phaseNumber}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
        border = BorderStroke(
            width = if (isActive) 1.5.dp else 1.dp,
            color = if (isActive) DeepEspressoPrimary else LatteBorderSoft
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Card Header (Clickable to expand/collapse)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .clickable { onToggleExpand() }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("ivf_phase_header_${phase.phaseNumber}"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Phase Number / Status Badge
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Phase terminée",
                            tint = badgeFg,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text(
                            text = "0${phase.phaseNumber}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = badgeFg,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = badgeBg.copy(alpha = if (phase.status == IvfPhaseStatus.UPCOMING) 1f else 0.14f)
                        ) {
                            Text(
                                text = statusLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (phase.status == IvfPhaseStatus.UPCOMING) DeepEspressoPrimary else badgeBg,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = phase.durationLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextWarmTaupe,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = phase.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = TextDarkEspresso,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = phase.subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWarmTaupe,
                            fontSize = 12.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = badgeBg,
                            trackColor = SoftToffeeContainer
                        )
                        Text(
                            text = "$completedCount/$totalCount",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextWarmTaupe,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (phase.isExpanded) "Réduire la phase" else "Déplier la phase",
                    tint = DeepEspressoPrimary,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(chevronRotation)
                )
            }

            // Expandable Body
            AnimatedVisibility(
                visible = phase.isExpanded,
                enter = expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
                exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(color = LatteBorderSoft.copy(alpha = 0.7f))

                    // Phase Description
                    Text(
                        text = phase.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextDarkEspresso,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    )

                    // Prescribed Medications Chips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = DeepEspressoPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Médicaments associés",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = DeepEspressoPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            phase.medications.forEach { med ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SoftToffeeContainer,
                                    border = BorderStroke(1.dp, LatteBorderSoft)
                                ) {
                                    Text(
                                        text = med,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextDarkEspresso,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Key Phase Milestones Checklist
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Étapes clés de la phase",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = TextDarkEspresso,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        phase.tasks.forEach { task ->
                            IvfTaskRow(
                                task = task,
                                onToggle = { onToggleTask(task.id) }
                            )
                        }
                    }

                    // Clinical Tip Callout
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SoftToffeeContainer.copy(alpha = 0.65f))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = DeepEspressoPrimary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = phase.clinicalTip,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextDarkEspresso,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            )
                        }
                    }

                    // Set Active Phase Action (Updates Header Banner Medication Instructions)
                    if (!isActive) {
                        OutlinedButton(
                            onClick = onSetActivePhase,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_ivf_activate_phase_${phase.phaseNumber}"),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.2.dp, DeepEspressoPrimary),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepEspressoPrimary)
                        ) {
                            Text(
                                text = "Activer la Phase ${phase.phaseNumber} & afficher ses consignes",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IvfTaskRow(
    task: IvfPhaseTask,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(12.dp))
            .background(WarmCanvasBackground)
            .clickable { onToggle() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("ivf_task_${task.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = DeepEspressoPrimary,
                uncheckedColor = TextWarmTaupe,
                checkmarkColor = Color.White
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (task.isCompleted) TextWarmTaupe else TextDarkEspresso,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
            )
            Text(
                text = task.detail,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextWarmTaupe,
                    fontSize = 11.sp
                )
            )
        }
    }
}
