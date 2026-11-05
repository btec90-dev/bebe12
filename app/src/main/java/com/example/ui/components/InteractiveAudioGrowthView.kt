package com.example.ui.components

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PregnancyWeek
import com.example.data.model.SizeTheme
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import java.util.Locale

@Composable
fun InteractiveAudioGrowthView(
    currentWeek: Int,
    weekData: PregnancyWeek,
    selectedTheme: SizeTheme,
    onThemeChanged: (SizeTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSpeaking by remember { mutableStateOf(false) }
    var ttsInstance by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = Locale.getDefault()
            }
        }
        ttsInstance = tts
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    val currentComparison = weekData.getComparison(selectedTheme)

    // Pulse animation for interactive visual model
    val infiniteTransition = rememberInfiniteTransition(label = "growth_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val audioDescriptionText = "Week $currentWeek pregnancy update. Your baby is about the size of a ${currentComparison.title} ${currentComparison.emoji}, measuring approximately ${weekData.lengthCm} centimeters and weighing ${weekData.weightGrams} grams. Development highlight: ${weekData.babyDevelopment}. Maternal notes: ${weekData.momChanges}"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 5.dp, shape = RoundedCornerShape(26.dp), spotColor = Color.Black.copy(alpha = 0.06f))
            .testTag("interactive_audio_growth_card"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with Audio Description Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SoftToffeeSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Audiotrack,
                            contentDescription = "Audio Guide",
                            tint = DeepEspressoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "3D VISUAL & AUDIO GUIDE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.1.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWarmTaupe,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = "Week $currentWeek Development",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso,
                                fontSize = 16.sp
                            )
                        )
                    }
                }

                // Audio Play/Stop Button
                IconButton(
                    onClick = {
                        if (isSpeaking) {
                            ttsInstance?.stop()
                            isSpeaking = false
                        } else {
                            ttsInstance?.speak(audioDescriptionText, TextToSpeech.QUEUE_FLUSH, null, null)
                            isSpeaking = true
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DeepEspressoPrimary)
                        .testTag("btn_audio_description")
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Play/Stop Audio",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Visual Model Canvas Representation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFFF6F0), Color(0xFFFCEFEA))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(130.dp * pulseScale)) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.minDimension / 2.2f

                    // Soft aura glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFE89372).copy(alpha = 0.35f), Color.Transparent),
                            center = center,
                            radius = radius * 1.3f
                        ),
                        radius = radius * 1.3f,
                        center = center
                    )

                    // Outer amniotic ring
                    drawCircle(
                        color = Color(0xFFE89372).copy(alpha = 0.5f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 3f)
                    )

                    // Inner embryonic/fetal core circle
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFF7C5B0), Color(0xFFD67352)),
                            center = center,
                            radius = radius * 0.7f
                        ),
                        radius = radius * 0.7f,
                        center = center
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = currentComparison.emoji,
                        fontSize = 42.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentComparison.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepEspressoPrimary,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = "Approx. ${weekData.lengthCm} cm | ${weekData.weightGrams} g",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWarmTaupe,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Size Guide Theme Selector (Fruits, Animals, Sweets, Sports)
            Text(
                text = "Baby Size Guide Categories",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextWarmTaupe,
                    fontSize = 11.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            val themes = listOf(
                SizeTheme.FRUITS to "🍓 Fruits",
                SizeTheme.ANIMALS to "🐾 Animals",
                SizeTheme.SNACKS to "🧁 Sweets",
                SizeTheme.SPORTS to "⚽ Sports"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(themes) { (theme, label) ->
                    val isSelected = selectedTheme == theme
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) DeepEspressoPrimary else CrispWhiteSurface)
                            .border(1.dp, if (isSelected) DeepEspressoPrimary else Color(0xFFE5D5CD), RoundedCornerShape(14.dp))
                            .clickable { onThemeChanged(theme) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("size_theme_${theme.name.lowercase()}")
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextDarkEspresso,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Week-by-Week Developmental Expectation Summary
            Text(
                text = "What to Expect in Week $currentWeek",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextWarmTaupe,
                    fontSize = 11.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = weekData.babyDevelopment,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextDarkEspresso,
                    fontSize = 13.sp
                )
            )
        }
    }
}
