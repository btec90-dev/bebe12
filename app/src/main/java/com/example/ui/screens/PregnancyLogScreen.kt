package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.PeachHeaderBackground
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.SquircleItemBg
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.theme.WarmCanvasBackground
import com.example.ui.viewmodel.PregnancyViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PregnancyLogScreen(
    viewModel: PregnancyViewModel,
    onOpenHealthConnectDialog: () -> Unit,
    onOpenLanguageDialog: () -> Unit = {}
) {
    val logs by viewModel.pregnancyLogs.collectAsState()
    val hasHealthPerms by viewModel.hasHealthPermissions.collectAsState()
    val todayWater by viewModel.todayWaterGlasses.collectAsState()
    val todayWeight by viewModel.todayWeightKg.collectAsState()
    val todayMood by viewModel.todayMood.collectAsState()
    val todaySymptoms by viewModel.todaySymptoms.collectAsState()
    val currentWeek by viewModel.currentWeekNumber.collectAsState()
    val dueDateMillis by viewModel.dueDateMillis.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val context = LocalContext.current

    var bpInput by remember { mutableStateOf("118/76") }
    var notesInput by remember { mutableStateOf("") }

    val dueDateFormatted = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(dueDateMillis))
    val diffDays = ((dueDateMillis - System.currentTimeMillis()) / (1000L * 3600 * 24)).toInt().coerceAtLeast(0)

    val flagEmoji = when (currentLanguage) {
        "fr" -> "🇫🇷 FR"
        "es" -> "🇪🇸 ES"
        "ar" -> "🇸🇦 AR"
        else -> "🇺🇸 EN"
    }

    val moodOptions = listOf(
        Pair("Radiant", "🌟"),
        Pair("Calm", "🌸"),
        Pair("Tired", "🥱"),
        Pair("Emotional", "🥹"),
        Pair("Nauseous", "🤢"),
        Pair("Anxious", "💭")
    )

    val symptomOptions = listOf(
        "Morning sickness",
        "Heartburn",
        "Braxton Hicks",
        "Back pain",
        "Swollen feet",
        "Insomnia",
        "Fatigue",
        "Frequent urination",
        "Headache",
        "Leg cramps",
        "Food cravings"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCanvasBackground)
    ) {


        // Scrollable Log Cards
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Editable Pregnancy Countdown Card on Top
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(22.dp), spotColor = Color.Black.copy(alpha = 0.04f))
                        .clickable {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = dueDateMillis
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val pickedCal = Calendar.getInstance()
                                    pickedCal.set(year, month, day)
                                    viewModel.updateDueDate(pickedCal.timeInMillis)
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .testTag("log_screen_countdown_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SquircleItemBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🗓️", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stringResource(id = R.string.pregnancy_countdown),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextWarmTaupe,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = stringResource(id = R.string.days_left, diffDays),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepEspressoPrimary,
                                        fontSize = 15.sp
                                    )
                                )
                                Text(
                                    text = "Week $currentWeek • Due: $dueDateFormatted",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextWarmTaupe,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(SoftToffeeSecondary)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = DeepEspressoPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(id = R.string.edit_due_date),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepEspressoPrimary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 1. Hydration Water Tracker Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
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
                                    .background(SquircleItemBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = DeepEspressoPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = stringResource(id = R.string.hydration_water),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextDarkEspresso)
                                    )
                                    Text(
                                        text = "${todayWater * 250} ml of 2,000 ml goal",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextWarmTaupe)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { if (todayWater > 0) viewModel.setTodayWater(todayWater - 1) },
                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(SquircleItemBg)
                                ) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = DeepEspressoPrimary, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "$todayWater",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = DeepEspressoPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { viewModel.setTodayWater(todayWater + 1) },
                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(DeepEspressoPrimary)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            // 2. Maternal Weight Logging Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SquircleItemBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Scale,
                                        contentDescription = null,
                                        tint = DeepEspressoPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = stringResource(id = R.string.weight_kg),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextDarkEspresso)
                                    )
                                    Text(
                                        text = if (hasHealthPerms) "Synced with Health Connect" else "Manual logging",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextWarmTaupe)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.setTodayWeight(todayWeight - 0.1) },
                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(SquircleItemBg)
                                ) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Minus", tint = DeepEspressoPrimary, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = String.format(Locale.US, "%.1f", todayWeight),
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = DeepEspressoPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { viewModel.setTodayWeight(todayWeight + 0.1) },
                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(DeepEspressoPrimary)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Plus", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // 3. Mood Tracking Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = stringResource(id = R.string.mood),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextDarkEspresso)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(moodOptions) { (moodName, emoji) ->
                                val isSelected = todayMood == moodName
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) SoftToffeeSecondary else SquircleItemBg)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 0.dp,
                                            color = if (isSelected) DeepEspressoPrimary else Color.Transparent,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable { viewModel.setTodayMood(moodName) }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                        .testTag("mood_option_$moodName")
                                ) {
                                    Text(text = emoji, fontSize = 26.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = moodName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = TextDarkEspresso,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Symptoms Tracker Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = stringResource(id = R.string.symptoms),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextDarkEspresso)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            symptomOptions.forEach { symptom ->
                                val isSelected = todaySymptoms.contains(symptom)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) DeepEspressoPrimary else SquircleItemBg)
                                        .clickable { viewModel.toggleSymptom(symptom) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .testTag("symptom_chip_${symptom.take(6)}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = symptom,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Color.White else TextDarkEspresso,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Blood Pressure & Notes Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Blood Pressure & Journal Notes",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextDarkEspresso)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = bpInput,
                            onValueChange = { bpInput = it },
                            label = { Text("Blood Pressure (mmHg)", fontSize = 12.sp) },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextDarkEspresso, fontWeight = FontWeight.Medium),
                            modifier = Modifier.fillMaxWidth().testTag("input_blood_pressure"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = WarmCanvasBackground,
                                unfocusedContainerColor = WarmCanvasBackground
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            label = { Text("Daily thoughts or symptoms notes…", fontSize = 12.sp) },
                            maxLines = 3,
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextDarkEspresso, fontWeight = FontWeight.Medium),
                            modifier = Modifier.fillMaxWidth().testTag("input_health_notes"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = WarmCanvasBackground,
                                unfocusedContainerColor = WarmCanvasBackground
                            )
                        )
                    }
                }
            }

            // Save Daily Log Action Button
            item {
                Button(
                    onClick = {
                        viewModel.saveDailyHealthLog(bpInput, notesInput)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_save_daily_log"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepEspressoPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.save_daily_log),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}
