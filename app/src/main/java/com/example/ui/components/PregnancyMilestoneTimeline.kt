package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.LatteBorderSoft
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.SquircleItemBg
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextMutedCoffee
import com.example.ui.theme.TextWarmTaupe

data class PregnancyTimelineMilestone(
    val week: Int,
    val title: String,
    val description: String,
    val emoji: String
)

@Composable
fun PregnancyMilestoneTimeline(
    currentWeek: Int,
    modifier: Modifier = Modifier
) {
    val milestones = remember {
        listOf(
            PregnancyTimelineMilestone(4, "Implantation", "Nidation & hausse hCG", "🌱"),
            PregnancyTimelineMilestone(8, "1er Battement", "Cœur fœtal détecté", "💓"),
            PregnancyTimelineMilestone(12, "Écho T1", "Dépistage 1er trimestre", "🩺"),
            PregnancyTimelineMilestone(16, "Mouvements", "Premiers frémissements", "🦋"),
            PregnancyTimelineMilestone(20, "Écho T2", "Morphologie détaillée", "🔍"),
            PregnancyTimelineMilestone(24, "Viabilité Fœtale", "Poumons & surfactant actifs", "🎈"),
            PregnancyTimelineMilestone(28, "3e Trimestre", "Sommeil & coups vifs", "🌟"),
            PregnancyTimelineMilestone(36, "Nidification", "Prise de poids rapide", "🧺"),
            PregnancyTimelineMilestone(40, "Jour J", "Prêt pour la naissance", "👶")
        )
    }

    // Default to the milestone closest to currentWeek (e.g. index 5 for Week 24)
    val defaultIndex = remember(currentWeek) {
        val found = milestones.indexOfLast { it.week <= currentWeek }
        if (found >= 0) found else 5
    }
    var selectedIndex by remember(currentWeek) { mutableIntStateOf(defaultIndex) }
    val activeMilestone = milestones.getOrElse(selectedIndex) { milestones[5] }

    val listState = rememberLazyListState()

    LaunchedEffect(defaultIndex) {
        listState.animateScrollToItem((defaultIndex - 1).coerceAtLeast(0))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(14.dp), spotColor = Color.Black.copy(alpha = 0.05f))
            .testTag("pregnancy_milestone_timeline_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: Compact & Rectangular
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(SoftToffeeSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = "Milestone Timeline",
                            tint = DeepEspressoPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "CHRONOLOGIE RECTANGULAIRE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 0.8.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWarmTaupe,
                                fontSize = 9.sp
                            )
                        )
                        Text(
                            text = "Étape Semaine $currentWeek",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepEspressoPrimary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Semaine $currentWeek • 60%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Spotlight Rectangular Banner for Week 24 / active milestone
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SquircleItemBg)
                    .border(1.dp, LatteBorderSoft, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = activeMilestone.emoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Semaine ${activeMilestone.week} : ${activeMilestone.title}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkEspresso,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = activeMilestone.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextMutedCoffee,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        if (currentWeek >= activeMilestone.week) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(DeepEspressoPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Passed",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Rectangular Progress Bar
                    val progressFraction = (currentWeek.coerceIn(1, 40)) / 40f
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = DeepEspressoPrimary,
                        trackColor = SoftToffeeSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Compact Rectangular Milestone Tabs (fitted horizontally)
            LazyRow(
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(milestones) { idx, milestone ->
                    val isSelected = idx == selectedIndex
                    val isCurrent = milestone.week == currentWeek || (currentWeek in milestone.week..(milestone.week + 3))
                    val isPassed = currentWeek >= milestone.week

                    Box(
                        modifier = Modifier
                            .width(88.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    isSelected -> SoftToffeeSecondary
                                    isCurrent -> SoftToffeeSecondary.copy(alpha = 0.5f)
                                    else -> CrispWhiteSurface
                                }
                            )
                            .border(
                                width = if (isSelected || (milestone.week == 24 && currentWeek == 24)) 1.5.dp else 1.dp,
                                color = if (isSelected || (milestone.week == 24 && currentWeek == 24)) DeepEspressoPrimary else LatteBorderSoft,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedIndex = idx }
                            .padding(horizontal = 6.dp, vertical = 6.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = milestone.emoji, fontSize = 13.sp)
                                Text(
                                    text = "S${milestone.week}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPassed) DeepEspressoPrimary else TextWarmTaupe,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = milestone.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = TextDarkEspresso,
                                    fontSize = 9.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
