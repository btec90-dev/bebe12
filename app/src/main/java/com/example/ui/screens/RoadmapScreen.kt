package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MilestoneRoadmapData
import com.example.data.model.BabyMilestone
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SoftLavenderCard
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SoftRoseContainer
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.viewmodel.PregnancyViewModel

@Composable
fun RoadmapScreen(
    viewModel: PregnancyViewModel,
    onBack: () -> Unit
) {
    val achievedIds by viewModel.achievedMilestoneIds.collectAsState()
    val allMilestones = MilestoneRoadmapData.milestones24Months

    var selectedDomain by remember { mutableStateOf("All") }
    val domains = listOf("All", "Physical", "Cognitive", "Social")

    var selectedMilestone by remember { mutableStateOf<BabyMilestone?>(null) }

    val filteredList = if (selectedDomain == "All") {
        allMilestones
    } else {
        allMilestones.filter { it.domain == selectedDomain }
    }

    val achievedCount = allMilestones.count { it.id in achievedIds }
    val totalCount = allMilestones.count()
    val progress = (achievedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header (compacted to fit right inside)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("btn_roadmap_back").size(36.dp)) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SoftRose)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Roadmap 24 Mois Bébé",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SoftRose, fontSize = 16.sp)
                    )
                    Text(
                        text = "$achievedCount / $totalCount jalons atteints",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline, fontSize = 11.sp)
                    )
                }
            }
        }

        // Progress Overview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SoftLavenderCard)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Progression Globale", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp))
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF7E609C), fontSize = 13.sp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF7E609C),
                        trackColor = Color.White
                    )
                }
            }
        }

        // Domain Filters
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(domains) { domain ->
                    FilterChip(
                        selected = selectedDomain == domain,
                        onClick = { selectedDomain = domain },
                        label = { Text(domain, style = MaterialTheme.typography.labelSmall, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SoftRose,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Milestones
        items(filteredList) { milestone ->
            val isAchieved = milestone.id in achievedIds
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { selectedMilestone = milestone }
                    .testTag("milestone_card_${milestone.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAchieved) SoftRoseContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAchieved,
                        onCheckedChange = { viewModel.toggleMilestone(milestone.id) },
                        colors = CheckboxDefaults.colors(checkedColor = SageGreen)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = milestone.monthRange,
                                style = MaterialTheme.typography.labelSmall.copy(color = SoftRose, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            )
                            Text(
                                text = milestone.domain,
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = milestone.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isAchieved) FontWeight.Normal else FontWeight.SemiBold,
                                fontSize = 13.sp,
                                textDecoration = if (isAchieved) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                            )
                        )
                        Text(
                            text = milestone.description,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline, fontSize = 10.5.sp),
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }

    // Milestone Detail Pop-up Dialog
    if (selectedMilestone != null) {
        val ms = selectedMilestone!!
        val isAchieved = ms.id in achievedIds

        AlertDialog(
            onDismissRequest = { selectedMilestone = null },
            containerColor = CrispWhiteSurface,
            shape = RoundedCornerShape(22.dp),
            title = {
                Column {
                    Text(
                        text = ms.monthRange,
                        style = MaterialTheme.typography.labelSmall.copy(color = SoftRose, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ms.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Domaine: ${ms.domain}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = DeepEspressoPrimary)
                    )
                    Text(
                        text = ms.description,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    )
                    Text(
                        text = if (isAchieved) "Jalon validé avec succès !" else "Astuce: Pratiquez les jeux d'éveil adaptés pour encourager ce développement.",
                        style = MaterialTheme.typography.bodySmall.copy(color = if (isAchieved) SageGreen else TextWarmTaupe, fontSize = 11.5.sp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.toggleMilestone(ms.id)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isAchieved) SoftRose else SageGreen)
                ) {
                    Text(if (isAchieved) "Marquer non atteint" else "Valider ce jalon", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedMilestone = null }) {
                    Text("Fermer", color = SoftRose)
                }
            }
        )
    }
}
