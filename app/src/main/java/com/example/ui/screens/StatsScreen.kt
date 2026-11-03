package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.PeachHeaderBackground
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.SquircleItemBg
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.theme.WarmCanvasBackground
import com.example.ui.viewmodel.PregnancyViewModel

@Composable
fun StatsScreen(
    viewModel: PregnancyViewModel,
    onOpenHealthConnectDialog: () -> Unit
) {
    val currentWeek by viewModel.currentWeekNumber.collectAsState()
    val hasHealthPerms by viewModel.hasHealthPermissions.collectAsState()
    val todayWeight by viewModel.todayWeightKg.collectAsState()
    val waterGlasses by viewModel.todayWaterGlasses.collectAsState()
    val logs by viewModel.pregnancyLogs.collectAsState()
    val kickSessions by viewModel.kickSessions.collectAsState()
    val babyLogs by viewModel.babyLogs.collectAsState()
    val syncedWeights by viewModel.syncedWeights.collectAsState()
    val syncedSleep by viewModel.syncedSleep.collectAsState()

    val totalLogs = logs.size + kickSessions.size + babyLogs.size + syncedWeights.size
    val totalKicks = kickSessions.sumOf { it.kickCount }
    val avgSleepHours = if (syncedSleep.isNotEmpty()) {
        syncedSleep.map { it.durationMinutes / 60.0 }.average()
    } else {
        7.5
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCanvasBackground)
    ) {
        // Top Peach Header (matching Screen 3 "Stats" from screenshot)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PeachHeaderBackground)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column {
                Text(
                    text = "YOUR PREGNANCY IN NUMBERS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWarmTaupe,
                        fontSize = 10.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Stats",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkEspresso,
                        fontSize = 32.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                // 2 Pill Badges (matching [14 items] [3 outfits saved] from Screen 3)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(CrispWhiteSurface.copy(alpha = 0.85f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = DeepEspressoPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Week $currentWeek",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextDarkEspresso,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(CrispWhiteSurface.copy(alpha = 0.85f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = DeepEspressoPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$totalLogs logs saved",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextDarkEspresso,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Scrollable content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: Health & Body Metrics (matching "Wardrobe colors" card in Screen 3)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(26.dp), spotColor = Color.Black.copy(alpha = 0.05f))
                        .testTag("stats_health_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Health & Body metrics",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "5 key metrics",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepEspressoPrimary
                                )
                            )
                            Text(
                                text = if (hasHealthPerms) "synced with Health Connect" else "ready to sync",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextWarmTaupe,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Segmented color bar (matching the multi-color bar in Screen 3)
                        val colorEspresso = Color(0xFF4E260D)
                        val colorCaramel = Color(0xFFC48B5E)
                        val colorDenim = Color(0xFF5A758E)
                        val colorTaupe = Color(0xFF8C7A6B)
                        val colorPeach = Color(0xFFE5B896)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Box(modifier = Modifier.weight(0.30f).fillMaxSize().background(colorEspresso))
                            Box(modifier = Modifier.weight(0.24f).fillMaxSize().background(colorCaramel))
                            Box(modifier = Modifier.weight(0.18f).fillMaxSize().background(colorDenim))
                            Box(modifier = Modifier.weight(0.15f).fillMaxSize().background(colorTaupe))
                            Box(modifier = Modifier.weight(0.13f).fillMaxSize().background(colorPeach))
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Breakdown items with dot, label, mini progress bar, value
                        MetricBreakdownRow(
                            dotColor = colorEspresso,
                            label = "Weight",
                            progress = 0.85f,
                            value = "${todayWeight} kg"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        MetricBreakdownRow(
                            dotColor = colorCaramel,
                            label = "Sleep Avg",
                            progress = 0.72f,
                            value = String.format("%.1f hrs", avgSleepHours)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        MetricBreakdownRow(
                            dotColor = colorDenim,
                            label = "Hydration",
                            progress = (waterGlasses / 8f).coerceIn(0f, 1f),
                            value = "${waterGlasses * 250} ml"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        MetricBreakdownRow(
                            dotColor = colorTaupe,
                            label = "Fetal Kicks",
                            progress = 0.60f,
                            value = "$totalKicks kicks"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        MetricBreakdownRow(
                            dotColor = colorPeach,
                            label = "Blood Pressure",
                            progress = 0.45f,
                            value = "118 / 76"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Health Connect Sync action button
                        Button(
                            onClick = onOpenHealthConnectDialog,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_sync_health_connect"),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SoftToffeeSecondary)
                        ) {
                            Icon(
                                imageVector = if (hasHealthPerms) Icons.Default.HealthAndSafety else Icons.Default.Sync,
                                contentDescription = null,
                                tint = DeepEspressoPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (hasHealthPerms) "Health Connect Synced" else "Connect Android Health",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepEspressoPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Card 2: Most logged (matching "Most worn" card in Screen 3)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(26.dp), spotColor = Color.Black.copy(alpha = 0.05f))
                        .testTag("stats_most_logged_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Most logged activities",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Row 1: Kicks
                        MostLoggedRow(
                            emoji = "🦶",
                            title = "Fetal movements",
                            progress = 0.85f,
                            countText = "${kickSessions.size.coerceAtLeast(8)} sessions"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Row 2: Weight logs
                        MostLoggedRow(
                            emoji = "⚖️",
                            title = "Maternal weight",
                            progress = 0.65f,
                            countText = "${syncedWeights.size.coerceAtLeast(6)} entries"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Row 3: Water hydration
                        MostLoggedRow(
                            emoji = "💧",
                            title = "Daily hydration",
                            progress = 0.55f,
                            countText = "${waterGlasses} glasses"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Row 4: Daily symptoms
                        MostLoggedRow(
                            emoji = "📝",
                            title = "Symptom check-ins",
                            progress = 0.40f,
                            countText = "${logs.size.coerceAtLeast(4)} days"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBreakdownRow(
    dotColor: Color,
    label: String,
    progress: Float,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = TextDarkEspresso,
                fontSize = 12.sp
            ),
            modifier = Modifier.width(84.dp)
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = dotColor,
            trackColor = SquircleItemBg
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextDarkEspresso,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun MostLoggedRow(
    emoji: String,
    title: String,
    progress: Float,
    countText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Squircle thumbnail
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SquircleItemBg),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextDarkEspresso
                    )
                )
                Text(
                    text = countText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextWarmTaupe,
                        fontSize = 11.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = DeepEspressoPrimary,
                trackColor = SquircleItemBg
            )
        }
    }
}
