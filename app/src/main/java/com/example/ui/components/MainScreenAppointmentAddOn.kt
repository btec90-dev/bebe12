package com.example.ui.components

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppointmentEntity
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.LatteBorderSoft
import com.example.ui.theme.SoftPeachCard
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.SquircleItemBg
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class QuickEventChip(
    val title: String,
    val icon: String,
    val doctorSuggestion: String,
    val locationSuggestion: String
)

@Composable
fun MainScreenAppointmentAddOn(
    selectedDate: String,
    appointments: List<AppointmentEntity>,
    onAddAppointmentClick: (initialTitle: String, initialDate: String) -> Unit,
    onToggleAppointment: (AppointmentEntity) -> Unit,
    onViewAllAgenda: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quickChips = listOf(
        QuickEventChip("Consultation Suivi", "🩺", "Dr. Laurent (Gynécologue)", "Cabinet Médical"),
        QuickEventChip("Échographie T2 / T3", "👶", "Dr. Mercier (Échographiste)", "Centre d'Imagerie"),
        QuickEventChip("Prise de Sang & Bilan", "🩸", "Laboratoire d'Analyses", "Laboratoire Médical"),
        QuickEventChip("Test Glycémie / HGPO", "🧪", "Laboratoire Maman", "Laboratoire Médical"),
        QuickEventChip("Préparation Naissance", "🧘", "Sage-femme Fatoumata", "Maternité Hôpital"),
        QuickEventChip("Consultation Anesthésie", "💉", "Dr. Anesthésiste", "Clinique Maternité")
    )

    // Filter appointments for the selected calendar date
    val selectedDayAppointments = remember(selectedDate, appointments) {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        appointments.filter { app ->
            val appDateStr = sdf.format(Date(app.dateTimestamp))
            appDateStr.equals(selectedDate, ignoreCase = true)
        }
    }

    // Top upcoming pending appointments
    val upcomingAppointments = remember(appointments) {
        appointments.filter { !it.isCompleted }.sortedBy { it.dateTimestamp }.take(3)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 5.dp, shape = RoundedCornerShape(26.dp), spotColor = Color.Black.copy(alpha = 0.05f))
            .testTag("main_screen_appointment_addon"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header row with Icon, Title, and Add Button
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
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = DeepEspressoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Rendez-vous & Événements",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "Planning prénatal • $selectedDate",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextWarmTaupe,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                Button(
                    onClick = { onAddAppointmentClick("", selectedDate) },
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("btn_add_appointment_addon"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ajouter",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Category Chips
            Text(
                text = "Ajout rapide par catégorie :",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextWarmTaupe,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickChips) { chip ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(SquircleItemBg)
                            .border(1.dp, LatteBorderSoft, RoundedCornerShape(14.dp))
                            .clickable {
                                onAddAppointmentClick(chip.title, selectedDate)
                            }
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                            .testTag("chip_add_${chip.title}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = chip.icon, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = chip.title,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = TextDarkEspresso,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Appointments Display Section
            if (selectedDayAppointments.isNotEmpty()) {
                Text(
                    text = "Prévu le $selectedDate :",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepEspressoPrimary,
                        fontSize = 11.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                selectedDayAppointments.forEach { app ->
                    AppointmentAddOnItemRow(
                        appointment = app,
                        onToggle = { onToggleAppointment(app) }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            } else {
                // If nothing on the selected day, display nice helper card + upcoming visits
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SoftPeachCard)
                        .border(1.dp, LatteBorderSoft, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(text = "📅", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Aucun événement le $selectedDate",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextDarkEspresso,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = "Appuyez sur '+ Ajouter' pour planifier un rappel ou une visite.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextWarmTaupe,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }

                        TextButton(
                            onClick = { onAddAppointmentClick("", selectedDate) },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "+ Planifier",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DeepEspressoPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                if (upcomingAppointments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Prochains rendez-vous programmés :",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkEspresso,
                            fontSize = 11.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    upcomingAppointments.forEach { app ->
                        AppointmentAddOnItemRow(
                            appointment = app,
                            onToggle = { onToggleAppointment(app) }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer link to complete Agenda
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewAllAgenda() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Voir tout l'agenda médical et les contacts (${appointments.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepEspressoPrimary,
                        fontSize = 11.5.sp
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = DeepEspressoPrimary,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

@Composable
fun AppointmentAddOnItemRow(
    appointment: AppointmentEntity,
    onToggle: () -> Unit
) {
    val dateFormatted = remember(appointment.dateTimestamp) {
        SimpleDateFormat("EEE dd MMM • HH:mm", Locale.getDefault()).format(Date(appointment.dateTimestamp))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (appointment.isCompleted) Color(0xFFF6F6F6) else SquircleItemBg)
            .border(1.dp, LatteBorderSoft, RoundedCornerShape(12.dp))
            .clickable { onToggle() }
            .padding(10.dp)
    ) {
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
                    onClick = onToggle,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (appointment.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = if (appointment.isCompleted) "Terminé" else "À faire",
                        tint = if (appointment.isCompleted) Color(0xFF4A8A52) else DeepEspressoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = appointment.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (appointment.isCompleted) Color.Gray else TextDarkEspresso,
                            textDecoration = if (appointment.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = TextWarmTaupe,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = dateFormatted,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextWarmTaupe,
                                fontSize = 10.5.sp
                            )
                        )
                        if (appointment.doctorName.isNotBlank()) {
                            Text(text = " • ", style = MaterialTheme.typography.bodySmall.copy(color = TextWarmTaupe, fontSize = 10.5.sp))
                            Text(
                                text = appointment.doctorName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DeepEspressoPrimary,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            if (appointment.location.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CrispWhiteSurface)
                        .border(0.8.dp, LatteBorderSoft, RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = DeepEspressoPrimary,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = appointment.location,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                color = TextDarkEspresso
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreenAddAppointmentDialog(
    initialTitle: String = "",
    initialDateString: String = "",
    onDismiss: () -> Unit,
    onSave: (title: String, doctor: String, location: String, timestamp: Long, notes: String) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(initialTitle.ifBlank { "Consultation Prénatale" }) }
    var doctor by remember { mutableStateOf("Dr. Laurent (Gynécologue)") }
    var location by remember { mutableStateOf("Maternité / Clinique") }
    var notes by remember { mutableStateOf("") }

    val calendar = remember {
        val cal = Calendar.getInstance()
        // Try parsing initialDateString if provided
        if (initialDateString.isNotBlank()) {
            try {
                val parsed = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(initialDateString)
                if (parsed != null) cal.time = parsed
            } catch (_: Exception) {}
        }
        cal.set(Calendar.HOUR_OF_DAY, 10)
        cal.set(Calendar.MINUTE, 0)
        cal
    }

    var selectedTimestamp by remember { mutableLongStateOf(calendar.timeInMillis) }
    val displayDateStr = remember(selectedTimestamp) {
        SimpleDateFormat("EEE dd MMMM yyyy", Locale.getDefault()).format(Date(selectedTimestamp))
    }
    val displayTimeStr = remember(selectedTimestamp) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(selectedTimestamp))
    }

    val timeSlots = listOf("08:30", "09:30", "11:00", "14:00", "15:30", "17:00")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CrispWhiteSurface,
        shape = RoundedCornerShape(26.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SoftToffeeSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = DeepEspressoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Planifier un Rendez-vous",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkEspresso
                        )
                    )
                    Text(
                        text = "Agenda médical de Fatou & Bala",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWarmTaupe,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre ou motif du rendez-vous", fontSize = 12.sp, color = TextDarkEspresso) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_appointment_title"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = CrispWhiteSurface,
                        unfocusedContainerColor = CrispWhiteSurface,
                        focusedBorderColor = DeepEspressoPrimary,
                        unfocusedBorderColor = LatteBorderSoft,
                        focusedLabelColor = DeepEspressoPrimary,
                        unfocusedLabelColor = TextDarkEspresso,
                        cursorColor = Color.Black
                    )
                )

                // Doctor / Practitioner field
                OutlinedTextField(
                    value = doctor,
                    onValueChange = { doctor = it },
                    label = { Text("Praticien ou spécialiste (Docteur / Sage-femme)", fontSize = 12.sp, color = TextDarkEspresso) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_appointment_doctor"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = CrispWhiteSurface,
                        unfocusedContainerColor = CrispWhiteSurface,
                        focusedBorderColor = DeepEspressoPrimary,
                        unfocusedBorderColor = LatteBorderSoft,
                        focusedLabelColor = DeepEspressoPrimary,
                        unfocusedLabelColor = TextDarkEspresso,
                        cursorColor = Color.Black
                    )
                )

                // Location field
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lieu ou Cabinet médical", fontSize = 12.sp, color = TextDarkEspresso) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_appointment_location"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = CrispWhiteSurface,
                        unfocusedContainerColor = CrispWhiteSurface,
                        focusedBorderColor = DeepEspressoPrimary,
                        unfocusedBorderColor = LatteBorderSoft,
                        focusedLabelColor = DeepEspressoPrimary,
                        unfocusedLabelColor = TextDarkEspresso,
                        cursorColor = Color.Black
                    )
                )

                // Date Picker row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SquircleItemBg)
                        .border(1.dp, LatteBorderSoft, RoundedCornerShape(14.dp))
                        .clickable {
                            val c = Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val newCal = Calendar.getInstance().apply {
                                        timeInMillis = selectedTimestamp
                                        set(Calendar.YEAR, year)
                                        set(Calendar.MONTH, month)
                                        set(Calendar.DAY_OF_MONTH, day)
                                    }
                                    selectedTimestamp = newCal.timeInMillis
                                },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = DeepEspressoPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = displayDateStr,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextDarkEspresso,
                                fontSize = 13.sp
                            )
                        )
                    }
                    Text(
                        text = "Modifier",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DeepEspressoPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                    )
                }

                // Time slots selector
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Heure : $displayTimeStr",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(timeSlots) { slot ->
                            val isSelected = slot == displayTimeStr
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) DeepEspressoPrimary else SquircleItemBg)
                                    .border(1.dp, if (isSelected) DeepEspressoPrimary else LatteBorderSoft, RoundedCornerShape(10.dp))
                                    .clickable {
                                        val parts = slot.split(":")
                                        if (parts.size == 2) {
                                            val hour = parts[0].toIntOrNull() ?: 9
                                            val minute = parts[1].toIntOrNull() ?: 0
                                            val newCal = Calendar.getInstance().apply {
                                                timeInMillis = selectedTimestamp
                                                set(Calendar.HOUR_OF_DAY, hour)
                                                set(Calendar.MINUTE, minute)
                                            }
                                            selectedTimestamp = newCal.timeInMillis
                                        }
                                    }
                                    .padding(horizontal = 9.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = slot,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) Color.White else TextDarkEspresso,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Notes field
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Consignes & Rappels (ex: à jeun, dossier)", fontSize = 12.sp, color = TextDarkEspresso) },
                    singleLine = false,
                    maxLines = 2,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontSize = 12.5.sp
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_appointment_notes"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = CrispWhiteSurface,
                        unfocusedContainerColor = CrispWhiteSurface,
                        focusedBorderColor = DeepEspressoPrimary,
                        unfocusedBorderColor = LatteBorderSoft,
                        focusedLabelColor = DeepEspressoPrimary,
                        unfocusedLabelColor = TextDarkEspresso,
                        cursorColor = Color.Black
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        Toast.makeText(context, "Veuillez entrer un titre", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    onSave(title, doctor, location, selectedTimestamp, notes)
                    Toast.makeText(context, "Rendez-vous enregistré avec succès !", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                modifier = Modifier.testTag("btn_save_appointment_confirm"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
            ) {
                Text("Enregistrer le Rendez-vous", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = DeepEspressoPrimary)
            }
        }
    )
}
