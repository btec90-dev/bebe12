package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.ripple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.SageContainer
import com.example.ui.theme.SageTertiary
import com.example.ui.theme.SoftPinkCard
import com.example.ui.theme.SoftSageCard
import com.example.ui.viewmodel.PregnancyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun KickCounterScreen(
    viewModel: PregnancyViewModel
) {
    val count by viewModel.kickCount.collectAsState()
    val timerSeconds by viewModel.kickTimerSeconds.collectAsState()
    val isRunning by viewModel.isKickTimerRunning.collectAsState()
    val kickSessions by viewModel.kickSessions.collectAsState()

    var sessionNotes by remember { mutableStateOf("") }
    var buttonPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.94f else 1f,
        label = "kickButtonScale"
    )

    val progress = (count / 10f).coerceIn(0f, 1f)
    val minutes = timerSeconds / 60
    val seconds = timerSeconds % 60
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Description
        item {
            Column {
                Text(
                    text = "Fetal Kick Counter",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = RosePrimary
                    ),
                    modifier = Modifier.testTag("kick_counter_title")
                )
                Text(
                    text = "Target: 10 movements within 2 hours (ACOG standard)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Active Kick Session Main Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("kick_session_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Timer & Controls Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏱️ $timeFormatted",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.testTag("kick_timer_display")
                        )

                        Row {
                            IconButton(
                                onClick = {
                                    if (isRunning) viewModel.pauseKickTimer() else viewModel.startKickTimer()
                                },
                                modifier = Modifier.testTag("btn_kick_timer_toggle")
                            ) {
                                Icon(
                                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Toggle Timer",
                                    tint = RosePrimary
                                )
                            }
                            IconButton(
                                onClick = { viewModel.resetKickCounter() },
                                modifier = Modifier.testTag("btn_kick_reset")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reset Kick Counter",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Circular Kick Gauge & Tactile Tap Button
                    Box(
                        modifier = Modifier.size(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 12.dp,
                            color = if (count >= 10) SageTertiary else RosePrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        // Big interactive Kick Tap Button with ripple
                        Box(
                            modifier = Modifier
                                .size(175.dp)
                                .scale(scale)
                                .clip(CircleShape)
                                .background(
                                    if (count >= 10) SageContainer else RoseContainer
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true, radius = 90.dp)
                                ) {
                                    viewModel.registerKick()
                                }
                                .testTag("btn_tap_kick"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.TouchApp,
                                    contentDescription = null,
                                    tint = if (count >= 10) SageTertiary else RosePrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$count",
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (count >= 10) SageTertiary else RosePrimary
                                    )
                                )
                                Text(
                                    text = if (count >= 10) "Goal Reached!" else "TAP ON KICK",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = if (count >= 10) SageTertiary else RosePrimary
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Celebration banner when 10 kicks reached
                    AnimatedVisibility(visible = count >= 10) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = SoftSageCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = SageTertiary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Wonderful! Baby reached 10 kicks in $timeFormatted. Great reassurance!",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SageTertiary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    // Optional Notes & Save Button
                    OutlinedTextField(
                        value = sessionNotes,
                        onValueChange = { sessionNotes = it },
                        label = { Text("Session notes (e.g. after lunch, active rolls)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_kick_notes"),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.saveKickSession(sessionNotes)
                            sessionNotes = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_save_kick_session"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (count >= 10) SageTertiary else RosePrimary
                        ),
                        enabled = count > 0
                    ) {
                        Text("Save Kick Session Log")
                    }
                }
            }
        }

        // Clinical Guideline Information Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftPinkCard)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = RosePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "When to do kick counts?",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Starting around Week 28, count daily when baby is typically active (often in the evening or after eating). Lie on your left side to maximize blood flow. If you do not feel 10 movements in 2 hours, contact your care provider.",
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
                        )
                    }
                }
            }
        }

        // Past Sessions Log Section
        item {
            Text(
                text = "Recent Kick Sessions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (kickSessions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "No saved kick sessions yet. Tap the button above to begin counting baby's first session!",
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
            items(kickSessions) { session ->
                val dateStr = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
                    .format(Date(session.timestamp))
                val sessMinutes = session.durationSeconds / 60
                val sessSecs = session.durationSeconds % 60

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("kick_session_item_${session.id}"),
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
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (session.targetReached) SageContainer else RoseContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${session.kickCount}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (session.targetReached) SageTertiary else RosePrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (session.targetReached) "Goal Reached (10 kicks)" else "${session.kickCount} kicks recorded",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Duration: ${sessMinutes}m ${sessSecs}s • $dateStr",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            if (session.notes.isNotBlank()) {
                                Text(
                                    text = "Notes: ${session.notes}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
