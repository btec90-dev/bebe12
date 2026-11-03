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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.SquircleItemBg
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MonthlyPregnancyTracker(
    selectedDateString: String,
    onDateSelected: (String) -> Unit,
    currentGestationalWeek: Int,
    onAddAppointmentForDate: ((String) -> Unit)? = null
) {
    var monthOffset by remember { mutableIntStateOf(0) }
    var clickedDateForPopup by remember { mutableStateOf<String?>(null) }

    val calendar = remember(monthOffset) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, monthOffset)
        cal
    }

    val monthYearTitle = remember(calendar) {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
    }

    val todayCalendar = Calendar.getInstance()
    val isCurrentMonth = todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
            todayCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
    val todayDayOfMonth = if (isCurrentMonth) todayCalendar.get(Calendar.DAY_OF_MONTH) else -1

    // Build days of this month
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayCal = calendar.clone() as Calendar
    firstDayCal.set(Calendar.DAY_OF_MONTH, 1)
    val startingDayOfWeek = firstDayCal.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sunday, 6=Saturday

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 5.dp, shape = RoundedCornerShape(26.dp), spotColor = Color.Black.copy(alpha = 0.05f))
            .testTag("monthly_pregnancy_tracker"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with Month Title & Prev/Next buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SquircleItemBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = DeepEspressoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CALENDRIER MENSUEL & SOINS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWarmTaupe,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = monthYearTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso,
                                fontSize = 16.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { monthOffset -= 1 },
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(SquircleItemBg)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Month",
                            tint = DeepEspressoPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { monthOffset += 1 },
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(SquircleItemBg)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Month",
                            tint = DeepEspressoPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Day of Week Header Row (S M T W T F S)
            val weekDayLetters = listOf("D", "L", "M", "M", "J", "V", "S")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekDayLetters.forEach { letter ->
                    Text(
                        text = letter,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextWarmTaupe,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Grid Rows
            val totalCells = startingDayOfWeek + daysInMonth
            val totalRows = (totalCells + 6) / 7

            for (row in 0 until totalRows) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (col in 0 until 7) {
                        val dayIndex = row * 7 + col - startingDayOfWeek + 1
                        if (dayIndex in 1..daysInMonth) {
                            val isToday = dayIndex == todayDayOfMonth
                            val dateCal = calendar.clone() as Calendar
                            dateCal.set(Calendar.DAY_OF_MONTH, dayIndex)
                            val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(dateCal.time)
                            val isSelected = dateStr == selectedDateString

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when {
                                            isToday -> DeepEspressoPrimary
                                            isSelected -> SoftToffeeSecondary
                                            else -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        width = if (isSelected && !isToday) 1.5.dp else 0.dp,
                                        color = if (isSelected && !isToday) DeepEspressoPrimary else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        onDateSelected(dateStr)
                                        clickedDateForPopup = dateStr
                                    }
                                    .testTag("month_day_$dayIndex"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$dayIndex",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = when {
                                                isToday -> Color.White
                                                isSelected -> DeepEspressoPrimary
                                                else -> TextDarkEspresso
                                            },
                                            fontSize = 12.sp
                                        )
                                    )
                                    if (dayIndex % 5 == 0) {
                                        Box(
                                            modifier = Modifier
                                                .size(3.dp)
                                                .clip(CircleShape)
                                                .background(if (isToday) Color.White else DeepEspressoPrimary)
                                        )
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f).height(36.dp))
                        }
                    }
                }
            }
        }
    }

    // Calendar Date Popup Dialog
    if (clickedDateForPopup != null) {
        val dateVal = clickedDateForPopup!!
        AlertDialog(
            onDismissRequest = { clickedDateForPopup = null },
            containerColor = CrispWhiteSurface,
            shape = RoundedCornerShape(22.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Event, contentDescription = null, tint = DeepEspressoPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Date: $dateVal",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextDarkEspresso)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Événements, rendez-vous ou notes pour cette journée :",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextDarkEspresso, fontSize = 13.sp)
                    )
                    Text(
                        text = "• Consultation prénatale ou suivi de grossesse recommandé\n• Enregistrement de poids, glycémie et notes de santé",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextWarmTaupe, fontSize = 11.5.sp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val chosenDate = dateVal
                        clickedDateForPopup = null
                        if (onAddAppointmentForDate != null) {
                            onAddAppointmentForDate(chosenDate)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
                ) {
                    Text("+ Ajouter un RDV", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { clickedDateForPopup = null }) {
                    Text("Fermer", color = DeepEspressoPrimary)
                }
            }
        )
    }
}
