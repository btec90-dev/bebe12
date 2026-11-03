package com.example.ui.screens

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CachedContact
import com.example.data.local.AppointmentEntity
import com.example.ui.viewmodel.PregnancyViewModel
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.SageContainer
import com.example.ui.theme.SageTertiary
import com.example.ui.theme.SoftPeachCard
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.OutlinedButton
import com.example.util.CareDirectoryPdfExporter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AppointmentsScreen(
    viewModel: PregnancyViewModel,
    onBack: () -> Unit
) {
    val appointments by viewModel.appointments.collectAsState()
    val cachedContacts by viewModel.cachedContacts.collectAsState()
    val currentWeek by viewModel.currentWeekNumber.collectAsState()
    val dueDateMillis by viewModel.dueDateMillis.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) } // 0 = Appointments, 1 = Medical Contacts

    // Appointment dialog state
    var showAppointmentDialog by remember { mutableStateOf(false) }
    var editingAppointment by remember { mutableStateOf<AppointmentEntity?>(null) }
    var appTitleInput by remember { mutableStateOf("") }
    var appDoctorInput by remember { mutableStateOf("") }
    var appPhoneInput by remember { mutableStateOf("") }
    var appLocationInput by remember { mutableStateOf("") }
    var appNotesInput by remember { mutableStateOf("") }
    var appDateMillis by remember { mutableLongStateOf(System.currentTimeMillis() + 86400000L * 3) }

    // Contact dialog state
    var showContactDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<CachedContact?>(null) }
    var contactTypeInput by remember { mutableStateOf("DOCTOR") } // DOCTOR, PHARMACY, CLINIC, HOSPITAL
    var contactNameInput by remember { mutableStateOf("") }
    var contactPhoneInput by remember { mutableStateOf("") }
    var contactAddressInput by remember { mutableStateOf("") }
    var contactNotesInput by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) {
                        editingAppointment = null
                        appTitleInput = ""
                        appDoctorInput = ""
                        appPhoneInput = ""
                        appLocationInput = ""
                        appNotesInput = ""
                        appDateMillis = System.currentTimeMillis() + 86400000L * 3
                        showAppointmentDialog = true
                    } else {
                        editingContact = null
                        contactTypeInput = "DOCTOR"
                        contactNameInput = ""
                        contactPhoneInput = ""
                        contactAddressInput = ""
                        contactNotesInput = ""
                        showContactDialog = true
                    }
                },
                containerColor = RosePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("btn_add_appointment")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add New Entry")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingVals ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
        ) {
            // Top App Bar Header (compact)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RoseContainer)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(34.dp)) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RosePrimary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Rendez-vous & Contacts Santé",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "Suivi prénatal, médecins & répertoire",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // PDF Export Banner Card (compacted & slim)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SoftPeachCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(RoseContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = RosePrimary, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Export PDF Rendez-vous",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = "Fiche prénatale A4 imprimable",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        val exportedContacts = cachedContacts.map { c ->
                            com.example.data.local.MedicalContactEntity(
                                id = 0,
                                type = c.type,
                                name = c.name,
                                phone = c.phone ?: "",
                                address = c.address ?: "",
                                notes = c.notes ?: ""
                            )
                        }
                        Button(
                            onClick = {
                                val file = CareDirectoryPdfExporter.generateCareDirectoryPdf(
                                    context = context,
                                    currentWeek = currentWeek,
                                    dueDateMillis = dueDateMillis,
                                    appointments = appointments,
                                    contacts = exportedContacts
                                )
                                if (file != null) {
                                    Toast.makeText(context, "Care directory PDF generated!", Toast.LENGTH_SHORT).show()
                                    CareDirectoryPdfExporter.sharePdf(context, file)
                                } else {
                                    Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_export_pdf_share")
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Export", modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = "PDF", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val file = CareDirectoryPdfExporter.generateCareDirectoryPdf(
                                    context = context,
                                    currentWeek = currentWeek,
                                    dueDateMillis = dueDateMillis,
                                    appointments = appointments,
                                    contacts = exportedContacts
                                )
                                if (file != null) {
                                    CareDirectoryPdfExporter.viewPdf(context, file)
                                } else {
                                    Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_export_pdf_print")
                        ) {
                            Icon(imageVector = Icons.Default.Print, contentDescription = "Print", modifier = Modifier.size(12.dp), tint = RosePrimary)
                        }
                    }
                }
            }

            // Tab Row (compact)
            TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.surface) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Visites (${appointments.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Contacts (${cachedContacts.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }

            if (selectedTab == 0) {
                // Appointments List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(appointments) { app ->
                        val dateStr = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                            .format(Date(app.dateTimestamp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .testTag("appointment_card_${app.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (app.isCompleted) SoftPeachCard else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (app.isCompleted) SageContainer else RoseContainer)
                                            .clickable { viewModel.toggleAppointmentCompleted(app) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (app.isCompleted) Icons.Default.Check else Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = if (app.isCompleted) SageTertiary else RosePrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = app.title,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 13.sp
                                            )
                                        )
                                        Text(
                                            text = dateStr,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = RosePrimary,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            editingAppointment = app
                                            appTitleInput = app.title
                                            appDoctorInput = app.doctorName
                                            appPhoneInput = "" // Can be edited or linked
                                            appLocationInput = app.location
                                            appNotesInput = app.notes
                                            appDateMillis = app.dateTimestamp
                                            showAppointmentDialog = true
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Visit", tint = RosePrimary, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteAppointment(app) },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Visit", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    }
                                }

                                if (app.doctorName.isNotBlank() || app.location.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Dr. ${app.doctorName} • ${app.location}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                if (app.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = app.notes,
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Room Database-backed Cached Contacts List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cachedContacts) { contact ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .testTag("contact_card_${contact.id}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(RoseContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val icon = when (contact.type) {
                                            "PHARMACY" -> Icons.Default.LocalPharmacy
                                            "CLINIC", "HOSPITAL" -> Icons.Default.MedicalServices
                                            else -> Icons.Default.Phone
                                        }
                                        Icon(imageVector = icon, contentDescription = null, tint = RosePrimary, modifier = Modifier.size(16.dp))
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = contact.name,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(SageContainer)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = contact.type,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = SageTertiary,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 9.sp
                                                    )
                                                )
                                            }
                                        }
                                        if (!contact.phone.isNullOrBlank()) {
                                            Text(
                                                text = contact.phone,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = RosePrimary,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            )
                                        }
                                    }

                                    // Call Button
                                    if (!contact.phone.isNullOrBlank()) {
                                        IconButton(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                                    data = Uri.parse("tel:${contact.phone}")
                                                }
                                                context.startActivity(intent)
                                            }
                                        ) {
                                            Icon(imageVector = Icons.Default.Phone, contentDescription = "Call Contact", tint = SageTertiary)
                                        }
                                    }

                                    // Edit & Delete
                                    IconButton(
                                        onClick = {
                                            editingContact = contact
                                            contactTypeInput = contact.type
                                            contactNameInput = contact.name
                                            contactPhoneInput = contact.phone ?: ""
                                            contactAddressInput = contact.address ?: ""
                                            contactNotesInput = contact.notes ?: ""
                                            showContactDialog = true
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Contact", tint = RosePrimary)
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteCachedContact(contact) }
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Contact", tint = Color.Gray)
                                    }
                                }

                                if (!contact.address.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = contact.address,
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                                if (!contact.notes.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = contact.notes,
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Clinical Visit Dialog
    if (showAppointmentDialog) {
        AlertDialog(
            onDismissRequest = { showAppointmentDialog = false },
            title = { Text(if (editingAppointment == null) "Schedule Clinical Visit" else "Edit Clinical Visit") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = appTitleInput,
                        onValueChange = { appTitleInput = it },
                        label = { Text("Visit Title (e.g. 20-Week Anatomy Scan)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = appDoctorInput,
                        onValueChange = { appDoctorInput = it },
                        label = { Text("Preferred Doctor Name (e.g. Dr. Sarah Jenkins)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = appPhoneInput,
                        onValueChange = { appPhoneInput = it },
                        label = { Text("Doctor / Clinic Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = appLocationInput,
                        onValueChange = { appLocationInput = it },
                        label = { Text("Clinic / Hospital Location") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = appNotesInput,
                        onValueChange = { appNotesInput = it },
                        label = { Text("Notes / Questions to Ask") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = appDateMillis
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val picked = Calendar.getInstance()
                                    picked.set(year, month, day)
                                    appDateMillis = picked.timeInMillis
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoseContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Date: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(appDateMillis))}",
                            color = RosePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (appTitleInput.isNotBlank()) {
                            val docName = appDoctorInput.ifBlank { "Dr. Assigned" }
                            val clinicLoc = appLocationInput.ifBlank { "Medical Center" }
                            if (editingAppointment == null) {
                                viewModel.addAppointment(
                                    title = appTitleInput,
                                    doctor = docName,
                                    location = clinicLoc,
                                    dateTimestamp = appDateMillis,
                                    notes = appNotesInput
                                )
                            } else {
                                viewModel.updateAppointment(
                                    editingAppointment!!.copy(
                                        title = appTitleInput,
                                        doctorName = docName,
                                        location = clinicLoc,
                                        notes = appNotesInput,
                                        dateTimestamp = appDateMillis
                                    )
                                )
                            }

                            // Persistently save preferred doctor to Room cached_contacts database
                            if (appDoctorInput.isNotBlank()) {
                                viewModel.addCachedContact(
                                    type = "DOCTOR",
                                    name = appDoctorInput,
                                    phone = appPhoneInput,
                                    address = clinicLoc,
                                    notes = appNotesInput
                                )
                            }

                            showAppointmentDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary)
                ) {
                    Text("Save Visit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAppointmentDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add / Edit Room Database-backed Cached Contact Dialog
    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = { Text(if (editingContact == null) "Add Medical Contact" else "Edit Medical Contact") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Contact Type:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("DOCTOR", "PHARMACY", "CLINIC", "HOSPITAL").forEach { type ->
                            val isSelected = contactTypeInput == type
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) RosePrimary else RoseContainer)
                                    .clickable { contactTypeInput = type }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = type,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color.White else RosePrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = contactNameInput,
                        onValueChange = { contactNameInput = it },
                        label = { Text("Name (e.g. Dr. Sarah / City Pharmacy)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = contactPhoneInput,
                        onValueChange = { contactPhoneInput = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = contactAddressInput,
                        onValueChange = { contactAddressInput = it },
                        label = { Text("Clinic / Hospital Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = contactNotesInput,
                        onValueChange = { contactNotesInput = it },
                        label = { Text("Notes (e.g. Hours, Insurance)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (contactNameInput.isNotBlank()) {
                            if (editingContact == null) {
                                viewModel.addCachedContact(
                                    type = contactTypeInput,
                                    name = contactNameInput,
                                    phone = contactPhoneInput,
                                    address = contactAddressInput,
                                    notes = contactNotesInput
                                )
                            } else {
                                viewModel.updateCachedContact(
                                    editingContact!!.copy(
                                        type = contactTypeInput,
                                        name = contactNameInput,
                                        phone = contactPhoneInput,
                                        address = contactAddressInput,
                                        notes = contactNotesInput
                                    )
                                )
                            }
                            showContactDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary)
                ) {
                    Text("Save Contact")
                }
            },
            dismissButton = {
                TextButton(onClick = { showContactDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
