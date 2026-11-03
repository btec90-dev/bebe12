package com.example.ui.components

import android.media.MediaPlayer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import kotlinx.coroutines.delay

@Composable
fun VoiceNotePlayerComponent(
    title: String = "Note Vocale — Battements de cœur & Pensées de Fatou",
    recordedDate: String = "Aujourd'hui",
    durationSeconds: Int = 45,
    audioUri: String? = null,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentProgressSeconds by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying && currentProgressSeconds < durationSeconds) {
                delay(200)
                currentProgressSeconds += 0.2f
            }
            if (currentProgressSeconds >= durationSeconds) {
                isPlaying = false
                currentProgressSeconds = 0f
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("voice_note_player_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SoftToffeeSecondary.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = DeepEspressoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso,
                                fontSize = 13.sp
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = "Enregistré le $recordedDate • Audio HD",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextWarmTaupe,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = DeepEspressoPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Audio Waveform Visualization
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF9F6F0))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                val heights = listOf(8, 14, 22, 10, 18, 24, 16, 20, 12, 18, 24, 15, 20, 11, 16, 22, 9, 14, 19, 12, 22, 16, 10, 18, 24, 12, 8, 16, 20)
                val progressFraction = (currentProgressSeconds / durationSeconds.toFloat()).coerceIn(0f, 1f)
                val activeBars = (heights.size * progressFraction).toInt()

                heights.forEachIndexed { index, barHeight ->
                    val isActive = index <= activeBars
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(if (isPlaying && isActive) (barHeight * 0.9f).dp else (barHeight * 0.6f).dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isActive) DeepEspressoPrimary else SoftToffeeSecondary.copy(alpha = 0.4f))
                    )
                }
            }

            // Controls & Time Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DeepEspressoPrimary)
                            .testTag("btn_toggle_voice_note_play")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    val currentSec = currentProgressSeconds.toInt()
                    val totalSec = durationSeconds
                    Text(
                        text = String.format("%02d:%02d / %02d:%02d", currentSec / 60, currentSec % 60, totalSec / 60, totalSec % 60),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextDarkEspresso,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }

                Text(
                    text = if (isPlaying) "Lecture en cours..." else "Appuyer pour écouter",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextWarmTaupe,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
