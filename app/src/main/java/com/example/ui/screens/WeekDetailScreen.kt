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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SizeTheme
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.SquircleItemBg
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.theme.WarmCanvasBackground
import com.example.ui.viewmodel.PregnancyViewModel
import com.example.ui.viewmodel.ScreenTab

@Composable
fun WeekDetailScreen(
    viewModel: PregnancyViewModel,
    onBack: () -> Unit
) {
    val currentWeek by viewModel.currentWeekNumber.collectAsState()
    val weekData by viewModel.currentWeekData.collectAsState()
    val sizeTheme by viewModel.selectedSizeTheme.collectAsState()
    val dueDateMillis by viewModel.dueDateMillis.collectAsState()

    var isFavorited by remember { mutableStateOf(false) }

    val comparison = weekData.getComparison(sizeTheme)
    val diffDays = ((dueDateMillis - System.currentTimeMillis()) / (1000L * 3600 * 24)).toInt().coerceAtLeast(0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCanvasBackground)
    ) {
        // Top Navigation Bar (white top header)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("btn_week_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DeepEspressoPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Week $currentWeek",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkEspresso
                    )
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { isFavorited = !isFavorited },
                    modifier = Modifier.testTag("btn_favorite_milestone")
                ) {
                    Icon(
                        imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = DeepEspressoPrimary
                    )
                }
                IconButton(
                    onClick = { /* share */ },
                    modifier = Modifier.testTag("btn_share_milestone")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = DeepEspressoPrimary
                    )
                }
            }
        }
    }

        // Scrollable content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Big Squircle Hero Card (matching huge photo card in Screen 2)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(26.dp), spotColor = Color.Black.copy(alpha = 0.06f))
                        .testTag("hero_squircle_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = SquircleItemBg)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = comparison.emoji,
                                fontSize = 84.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = comparison.description,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextDarkEspresso
                                )
                            )
                        }
                    }
                }
            }

            // Category Pill Badges (matching [Winter] [Autumn] from Screen 2)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftToffeeSecondary)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Trimester ${weekData.trimester}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepEspressoPrimary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftToffeeSecondary)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Week $currentWeek of 40",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepEspressoPrimary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftToffeeSecondary)
                            .clickable {
                                val nextTheme = when (sizeTheme) {
                                    SizeTheme.FRUITS -> SizeTheme.ANIMALS
                                    SizeTheme.ANIMALS -> SizeTheme.SNACKS
                                    SizeTheme.SNACKS -> SizeTheme.SPORTS
                                    SizeTheme.SPORTS -> SizeTheme.FRUITS
                                }
                                viewModel.setSizeTheme(nextTheme)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = sizeTheme.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepEspressoPrimary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Big Title
            item {
                Column {
                    Text(
                        text = "BABY'S SIZE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWarmTaupe,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = comparison.title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkEspresso,
                            fontSize = 32.sp
                        )
                    )
                }
            }

            // 3 White Stat Pills in a row (matching [WORN 14+] [PER WEAR $2.07] [LAST WORN 2d ago] from Screen 2)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatPillBox(
                        label = "WEIGHT",
                        value = "${weekData.weightGrams} g",
                        modifier = Modifier.weight(1f)
                    )
                    StatPillBox(
                        label = "LENGTH",
                        value = "${weekData.lengthCm} cm",
                        modifier = Modifier.weight(1f)
                    )
                    StatPillBox(
                        label = "HEARTBEAT",
                        value = "140 bpm",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Info line below stats
            item {
                Text(
                    text = "Size of a ${comparison.title.lowercase()} · $diffDays days remaining until delivery",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextWarmTaupe,
                        fontSize = 12.sp
                    )
                )
            }

            // Developmental & Clinical Insights in Crisp White Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Fetal Development",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = weekData.babyDevelopment,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextDarkEspresso,
                                lineHeight = 20.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Mom's Guidance & Symptoms",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = weekData.clinicalAdvice,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextDarkEspresso,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
            }

            // Week Slider Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Browse Weeks (1–42)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkEspresso
                                )
                            )
                            Text(
                                text = "Week $currentWeek",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepEspressoPrimary
                                )
                            )
                        }
                        Slider(
                            value = currentWeek.toFloat(),
                            onValueChange = { viewModel.setWeek(it.toInt()) },
                            valueRange = 1f..42f,
                            steps = 40,
                            colors = SliderDefaults.colors(
                                thumbColor = DeepEspressoPrimary,
                                activeTrackColor = DeepEspressoPrimary
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("week_detail_slider")
                        )
                    }
                }
            }
        }

        // Sticky Bottom Bar with 2 Pill Buttons (matching [Style it] [Wear today] from Screen 2)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = WarmCanvasBackground,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Secondary Pill Button ("Style it" -> "Ask AI Doctor")
                Button(
                    onClick = {
                        onBack()
                        viewModel.selectTab(ScreenTab.ASSISTANT)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_ask_ai_milestone"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftToffeeSecondary,
                        contentColor = DeepEspressoPrimary
                    )
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ask AI Doctor",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Primary Pill Button ("Wear today" -> "Log today")
                Button(
                    onClick = {
                        onBack()
                        viewModel.selectTab(ScreenTab.HEALTH_LOG)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_log_today_milestone"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepEspressoPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Log today",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatPillBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.04f)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWarmTaupe,
                    fontSize = 9.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextDarkEspresso,
                    fontSize = 15.sp
                )
            )
        }
    }
}
