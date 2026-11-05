package com.example.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.AppointmentEntity
import com.example.ui.components.GlycemiaTrackerDialog
import com.example.ui.components.InteractiveAudioGrowthView
import com.example.ui.components.MonthlyPregnancyTracker
import com.example.ui.components.PregnancyMilestoneTimeline
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.LatteBorderSoft
import com.example.ui.theme.PeachHeaderBackground
import com.example.ui.theme.SoftPeachCard
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.SquircleItemBg
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.theme.WarmCanvasBackground
import com.example.ui.viewmodel.PregnancyViewModel
import com.example.ui.viewmodel.ScreenTab
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TodayScreen(
    viewModel: PregnancyViewModel,
    onNavigateWeekDetail: () -> Unit,
    onOpenHealthConnectDialog: () -> Unit,
    onOpenLanguageDialog: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenPublicShareDialog: () -> Unit = {}
) {
    val currentWeek by viewModel.currentWeekNumber.collectAsState()
    val weekData by viewModel.currentWeekData.collectAsState()
    val dueDateMillis by viewModel.dueDateMillis.collectAsState()
    val isTwinMode by viewModel.isTwinMode.collectAsState()
    val sizeTheme by viewModel.selectedSizeTheme.collectAsState()
    val calendarDays by viewModel.calendarStrip.collectAsState()
    val selectedDate by viewModel.selectedCalendarDate.collectAsState()
    val hasHealthPerms by viewModel.hasHealthPermissions.collectAsState()
    val todayWeight by viewModel.todayWeightKg.collectAsState()
    val healthMsg by viewModel.healthConnectMessage.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val kickSessions by viewModel.kickSessions.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val journalEntries by viewModel.journalEntries.collectAsState()
    val context = LocalContext.current

    val fullTodayDate = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()).uppercase()
    val dueDateFormatted = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(dueDateMillis))
    val diffDays = ((dueDateMillis - System.currentTimeMillis()) / (1000L * 3600 * 24)).toInt().coerceAtLeast(0)

    val currentComparison = weekData.getComparison(sizeTheme)

    val flagEmoji = when (currentLanguage) {
        "fr" -> "🇫🇷 FR"
        "es" -> "🇪🇸 ES"
        "pt" -> "🇵🇹 PT"
        "it" -> "🇮🇹 IT"
        "ar" -> "🇸🇦 AR"
        else -> "🇺🇸 EN"
    }

    // Photo Activity Launchers (Standard shortcuts for Photo Camera & Gallery access)
    var photoFeedbackMessage by remember { mutableStateOf<String?>(null) }
    var selectedAlbumForDialog by remember { mutableStateOf<com.example.ui.components.HomeAlbumDef?>(null) }
    var showGlycemiaDialog by remember { mutableStateOf(false) }
    var showAddAppointmentDialog by remember { mutableStateOf(false) }
    var appointmentPrefillTitle by remember { mutableStateOf("") }
    var appointmentPrefillDate by remember { mutableStateOf("") }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.saveBumpPhoto(
                bitmap = bitmap,
                week = currentWeek,
                title = "Week $currentWeek Bump Photo",
                notes = "Captured with camera on $fullTodayDate"
            )
            photoFeedbackMessage = "Week $currentWeek bump photo captured and saved!"
            Toast.makeText(context, "Bump photo saved to your journal!", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.saveBumpPhotoUri(
                uriString = uri.toString(),
                week = currentWeek,
                title = "Week $currentWeek Bump Photo",
                notes = "Imported from gallery"
            )
            photoFeedbackMessage = "Photo imported from gallery and saved!"
            Toast.makeText(context, "Photo added to your journal!", Toast.LENGTH_SHORT).show()
        }
    }

    // Sort upcoming appointments
    val upcomingAppointments = remember(appointments) {
        appointments.filter { !it.isCompleted }.sortedBy { it.dateTimestamp }
    }
    val nextAppointment = upcomingAppointments.firstOrNull()

    val pregnancyLogs by viewModel.pregnancyLogs.collectAsState()
    val babyLogs by viewModel.babyLogs.collectAsState()
    val checklistItems by viewModel.checklistItems.collectAsState()
    val cachedContacts by viewModel.cachedContacts.collectAsState()
    val profileName by viewModel.profileName.collectAsState()
    val profileAge by viewModel.profileAge.collectAsState()
    val profilePhotoUri by viewModel.profilePhotoUri.collectAsState()

    var showTextExportViewer by remember { mutableStateOf(false) }

    val fullArchiveText = remember(
        currentWeek,
        dueDateMillis,
        isTwinMode,
        appointments,
        journalEntries,
        kickSessions,
        pregnancyLogs,
        babyLogs,
        checklistItems,
        cachedContacts,
        profileName,
        profileAge,
        profilePhotoUri
    ) {
        val convertedContacts = cachedContacts.map { c ->
            com.example.data.local.MedicalContactEntity(
                id = 0,
                type = c.type,
                name = c.name,
                phone = c.phone ?: "",
                address = c.address ?: "",
                notes = c.notes ?: ""
            )
        }
        com.example.util.MamanDataTextExporter.buildCompleteArchiveText(
            profileName = profileName,
            profileAge = profileAge,
            profilePhotoUri = profilePhotoUri,
            currentWeek = currentWeek,
            dueDateMillis = dueDateMillis,
            isTwinMode = isTwinMode,
            userEmail = "app@balatec.com",
            pregnancyLogs = pregnancyLogs,
            kickSessions = kickSessions,
            babyLogs = babyLogs,
            journalEntries = journalEntries,
            appointments = appointments,
            checklistItems = checklistItems,
            medicalContacts = convertedContacts
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCanvasBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        // Compact Greeting & Date Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = fullTodayDate,
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWarmTaupe,
                        fontSize = 9.sp
                    )
                )
                Text(
                    text = stringResource(id = R.string.good_morning),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkEspresso,
                        fontSize = 20.sp
                    )
                )
            }

            // Week summary pill in header
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(DeepEspressoPrimary)
                    .clickable { onNavigateWeekDetail() }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Week $currentWeek",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Main Scrollable Dashboard
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {


            // Notification / Feedback banner if present
            if (photoFeedbackMessage != null || healthMsg != null) {
                item {
                    val msg = photoFeedbackMessage ?: healthMsg ?: ""
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SoftToffeeSecondary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = DeepEspressoPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = msg, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            Text(
                                text = stringResource(id = R.string.dismiss),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier
                                    .clickable {
                                        photoFeedbackMessage = null
                                        viewModel.clearHealthMessage()
                                    }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }

            // 1. Editable Pregnancy Countdown Banner on Top matching reference design
            item {
                com.example.ui.components.PregnancyCountdownHeroCard(
                    currentWeek = currentWeek,
                    weekData = weekData,
                    diffDays = diffDays,
                    sizeTheme = sizeTheme,
                    onEditDueDate = {
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
                )
            }

            // 2. Large Monthly Tracker at the bottom of the Pregnancy Countdown section
            item {
                MonthlyPregnancyTracker(
                    selectedDateString = selectedDate,
                    onDateSelected = { date -> viewModel.selectCalendarDate(date) },
                    currentGestationalWeek = currentWeek,
                    onAddAppointmentForDate = { date ->
                        appointmentPrefillTitle = "Consultation Prénatale"
                        appointmentPrefillDate = date
                        showAddAppointmentDialog = true
                    }
                )
            }

            // 2b. Main Screen Appointment & Prenatal Event Add-On (Directly below calendar)
            item {
                com.example.ui.components.MainScreenAppointmentAddOn(
                    selectedDate = selectedDate,
                    appointments = appointments,
                    onAddAppointmentClick = { title, date ->
                        appointmentPrefillTitle = title
                        appointmentPrefillDate = date
                        showAddAppointmentDialog = true
                    },
                    onToggleAppointment = { app ->
                        viewModel.toggleAppointmentCompleted(app)
                    },
                    onViewAllAgenda = {
                        viewModel.selectTab(ScreenTab.AGENDA)
                    }
                )
            }

            // 3. Interactive Visual Model with Audio Descriptions & Size Guide
            item {
                InteractiveAudioGrowthView(
                    currentWeek = currentWeek,
                    weekData = weekData,
                    selectedTheme = sizeTheme,
                    onThemeChanged = { newTheme -> viewModel.setSizeTheme(newTheme) }
                )
            }

            // 4. Pregnancy Timeline highlighting important milestones on Home screen
            item {
                PregnancyMilestoneTimeline(
                    currentWeek = currentWeek
                )
            }

            // 4. Upcoming Doctor Appointment data directly on Home Screen
            item {
                UpcomingDoctorAppointmentsCard(
                    nextAppointment = nextAppointment,
                    totalAppointmentsCount = upcomingAppointments.size,
                    onAddAppointmentClick = { viewModel.selectTab(ScreenTab.AGENDA) },
                    onViewAgendaClick = { viewModel.selectTab(ScreenTab.AGENDA) },
                    onToggleComplete = { app -> viewModel.toggleAppointmentCompleted(app) },
                    onExportPdfClick = {
                        val exportedContacts = viewModel.cachedContacts.value.map { c ->
                            com.example.data.local.MedicalContactEntity(
                                id = 0,
                                type = c.type,
                                name = c.name,
                                phone = c.phone ?: "",
                                address = c.address ?: "",
                                notes = c.notes ?: ""
                            )
                        }
                        val file = com.example.util.CareDirectoryPdfExporter.generateCareDirectoryPdf(
                            context = context,
                            currentWeek = currentWeek,
                            dueDateMillis = dueDateMillis,
                            appointments = appointments,
                            contacts = exportedContacts
                        )
                        if (file != null) {
                            Toast.makeText(context, "Care directory PDF generated!", Toast.LENGTH_SHORT).show()
                            com.example.util.CareDirectoryPdfExporter.sharePdf(context, file)
                        } else {
                            Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onExportTextClick = { showTextExportViewer = true }
                )
            }

            // 4b. Highly visible Complete Data & Codebase Text Export Banner
            item {
                CompleteDataTextExportBanner(
                    onExportClick = { showTextExportViewer = true }
                )
            }

            // 5. Standard Shortcuts/Buttons for Photo Camera and Gallery Access on Home Screen
            item {
                BumpPhotoStudioCard(
                    currentWeek = currentWeek,
                    savedPhotosCount = journalEntries.count { it.photoUri != null },
                    onCameraClick = { cameraLauncher.launch(null) },
                    onGalleryClick = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onViewJournalAlbumClick = { viewModel.selectTab(ScreenTab.JOURNAL) }
                )
            }

            // Photo Album Highlights Carousel (Family, Ultrasound, Belly Bump, Baby Milestones, Nursery)
            item {
                com.example.ui.components.HomePhotoAlbumsCarousel(
                    entries = journalEntries,
                    onAlbumSelected = { album ->
                        selectedAlbumForDialog = album
                    }
                )
            }

            // 6. Featured Big Stage Card (Daily Journey & Size Comparison)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 5.dp, shape = RoundedCornerShape(26.dp), spotColor = Color.Black.copy(alpha = 0.06f))
                        .testTag("featured_stage_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(id = R.string.todays_journey),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWarmTaupe,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = if (isTwinMode) {
                                        stringResource(id = R.string.twin_week_trimester, currentWeek)
                                    } else {
                                        stringResource(id = R.string.week_trimester, currentWeek, weekData.trimester)
                                    },
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextDarkEspresso)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DeepEspressoPrimary)
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
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("due_date_pill")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = stringResource(id = R.string.days_left, diffDays),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit due date",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3 Squircle Item Thumbnails
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SquircleThumbnail(
                                title = currentComparison.title,
                                emojiOrIcon = currentComparison.emoji,
                                subtitle = "~${weekData.lengthCm} cm",
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateWeekDetail
                            )

                            SquircleThumbnail(
                                title = "${todayWeight} kg",
                                emojiOrIcon = "⚖️",
                                subtitle = if (hasHealthPerms) "Synced" else "Logged",
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.selectTab(ScreenTab.HEALTH_LOG) }
                            )

                            SquircleThumbnail(
                                title = "10 Kicks",
                                emojiOrIcon = "🦶",
                                subtitle = stringResource(id = R.string.daily_kick_goal),
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.selectTab(ScreenTab.KICK_COUNTER) }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.selectTab(ScreenTab.HEALTH_LOG) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("btn_wear_it_style_cta"),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.log_todays_symptoms),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SquircleItemBg)
                                    .clickable { onNavigateWeekDetail() }
                                    .testTag("btn_expand_journey"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInFull,
                                    contentDescription = "Expand Guide",
                                    tint = DeepEspressoPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 7. Compact Pill Badges Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CompactPillBadge(
                        icon = "👶",
                        text = stringResource(id = R.string.week_of_40, currentWeek),
                        onClick = onNavigateWeekDetail
                    )
                    CompactPillBadge(
                        icon = "🗓️",
                        text = stringResource(id = R.string.days_remaining, diffDays),
                        onClick = {
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
                    )
                }
            }

            // 8. Search Bar Pill
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { viewModel.selectTab(ScreenTab.ASSISTANT) }
                        .testTag("search_bar_pill"),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextWarmTaupe,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(id = R.string.search_placeholder),
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextWarmTaupe, fontSize = 13.sp)
                        )
                    }
                }
            }

            // 9. Interactive 14-Day Calendar Strip
            item {
                Column {
                    Text(
                        text = "14-Day Daily Timeline",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextDarkEspresso)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(calendarDays) { day ->
                            val isSelected = day.dateString == selectedDate
                            Card(
                                modifier = Modifier
                                    .width(54.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        viewModel.selectCalendarDate(day.dateString)
                                        if (day.hasLog) {
                                            viewModel.selectTab(ScreenTab.HEALTH_LOG)
                                        }
                                    }
                                    .testTag("calendar_day_${day.dayOfMonth}"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) DeepEspressoPrimary else CrispWhiteSurface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = day.dayOfWeek,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextWarmTaupe,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = day.dayOfMonth,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else TextDarkEspresso,
                                            fontSize = 15.sp
                                        )
                                    )
                                    if (day.hasLog) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color.White else DeepEspressoPrimary)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 9b. Maternity & Family Suite (Fatou & Bala)
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Suite Maternité & Famille",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkEspresso
                                )
                            )
                            Text(
                                text = "Outils de Fatou & Bala : couple, contractions, valise & guide",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextWarmTaupe,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Compact Couple Co-Pilot & Contractions Quick Banner
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectTab(ScreenTab.COUPLE_HUB) }
                            .shadow(2.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftPeachCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LatteBorderSoft)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(text = "💑", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Espace Couple • Fatou & Bala",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkEspresso,
                                            fontSize = 12.sp
                                        )
                                    )
                                    Text(
                                        text = "Chrono contractions 5-1-1 • Guide Papa Bala • Idées Prénoms",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextWarmTaupe,
                                            fontSize = 10.sp
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
                                    text = "Ouvrir",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Compact Public App Share Quick Banner
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenPublicShareDialog() }
                            .shadow(2.dp, RoundedCornerShape(12.dp))
                            .testTag("banner_public_share"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SquircleItemBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LatteBorderSoft)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(text = "🌐", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Partager l'App Maman & Bébé",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkEspresso,
                                            fontSize = 12.sp
                                        )
                                    )
                                    Text(
                                        text = "Lien public cloud • Inviter Papa Bala & les proches",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextWarmTaupe,
                                            fontSize = 10.sp
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
                                    text = "Partager",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            RecentLogCard(
                                title = "Partager l'App",
                                subtitle = "Lien public & famille",
                                icon = "🌐",
                                onClick = onOpenPublicShareDialog
                            )
                        }
                        item {
                            RecentLogCard(
                                title = "Espace Couple",
                                subtitle = "Contractions & Papa",
                                icon = "💑",
                                onClick = { viewModel.selectTab(ScreenTab.COUPLE_HUB) }
                            )
                        }
                        item {
                            RecentLogCard(
                                title = "Valise Maternité",
                                subtitle = "Jour J & liste achats",
                                icon = "🎒",
                                onClick = { viewModel.selectTab(ScreenTab.MATERNITY_BAG) }
                            )
                        }
                        item {
                            RecentLogCard(
                                title = "Guide 1er Bébé",
                                subtitle = "Pas-à-pas première fois",
                                icon = "🤱",
                                onClick = { viewModel.selectTab(ScreenTab.NEWBORN_GUIDE) }
                            )
                        }
                        item {
                            RecentLogCard(
                                title = "Carnet Glycémie",
                                subtitle = "Télécharger PDF officiel",
                                icon = "🩸",
                                onClick = { showGlycemiaDialog = true }
                            )
                        }
                        item {
                            RecentLogCard(
                                title = "Dépenses & Reçus",
                                subtitle = "Budget & scan factures",
                                icon = "💶",
                                onClick = { viewModel.selectTab(ScreenTab.EXPENSES) }
                            )
                        }
                        item {
                            RecentLogCard(
                                title = "Portefeuille Santé",
                                subtitle = "Cartes & Google Drive",
                                icon = "🪪",
                                onClick = { viewModel.selectTab(ScreenTab.WALLET) }
                            )
                        }
                    }
                }
            }

            // 10. "Recently logged" Carousel
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.recently_logged),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextDarkEspresso)
                        )
                        Text(
                            text = stringResource(id = R.string.see_all),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = DeepEspressoPrimary),
                            modifier = Modifier.clickable { viewModel.selectTab(ScreenTab.HEALTH_LOG) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        item {
                            RecentLogCard(
                                title = "Maternal Weight",
                                subtitle = "${todayWeight} kg logged",
                                icon = "⚖️",
                                onClick = { viewModel.selectTab(ScreenTab.HEALTH_LOG) }
                            )
                        }
                        item {
                            RecentLogCard(
                                title = "Kick Session",
                                subtitle = "${kickSessions.size} recorded",
                                icon = "🦶",
                                onClick = { viewModel.selectTab(ScreenTab.KICK_COUNTER) }
                            )
                        }
                        item {
                            RecentLogCard(
                                title = "Baby Care",
                                subtitle = "Nursing & sleep logs",
                                icon = "🍼",
                                onClick = { viewModel.selectTab(ScreenTab.BABY_LOG) }
                            )
                        }
                        item {
                            RecentLogCard(
                                title = "AI Guidance",
                                subtitle = "Clinical Q&A",
                                icon = "✨",
                                onClick = { viewModel.selectTab(ScreenTab.ASSISTANT) }
                            )
                        }
                    }
                }
            }

            // 11. Medical Disclaimer Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = DeepEspressoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(id = R.string.medical_disclaimer),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextWarmTaupe,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
        }

        if (selectedAlbumForDialog != null) {
            com.example.ui.components.PhotoAlbumDetailDialog(
                album = selectedAlbumForDialog!!,
                viewModel = viewModel,
                onDismiss = { selectedAlbumForDialog = null }
            )
        }

        if (showTextExportViewer) {
            com.example.ui.components.TextExportViewerDialog(
                textContent = fullArchiveText,
                currentWeek = currentWeek,
                onDismiss = { showTextExportViewer = false }
            )
        }

        if (showGlycemiaDialog) {
            GlycemiaTrackerDialog(
                viewModel = viewModel,
                onDismiss = { showGlycemiaDialog = false }
            )
        }

        if (showAddAppointmentDialog) {
            com.example.ui.components.MainScreenAddAppointmentDialog(
                initialTitle = appointmentPrefillTitle,
                initialDateString = appointmentPrefillDate,
                onDismiss = { showAddAppointmentDialog = false },
                onSave = { title, doctor, location, timestamp, notes ->
                    viewModel.addAppointment(
                        title = title,
                        doctor = doctor,
                        location = location,
                        dateTimestamp = timestamp,
                        notes = notes
                    )
                }
            )
        }
    }
}

@Composable
private fun CompleteDataTextExportBanner(
    onExportClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(22.dp), spotColor = Color.Black.copy(alpha = 0.05f))
            .clickable { onExportClick() }
            .testTag("card_home_text_export"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PeachHeaderBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DeepEspressoPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Complete Text Export",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Complete Text Export (.txt)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkEspresso,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = "View, copy & download complete archive text",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWarmTaupe,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(DeepEspressoPrimary)
                    .padding(horizontal = 12.dp, vertical = 7.dp)
                    .testTag("btn_banner_open_text_export")
            ) {
                Text(
                    text = "View Text",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

// Doctor Appointments Card directly on Home Screen
@Composable
private fun UpcomingDoctorAppointmentsCard(
    nextAppointment: AppointmentEntity?,
    totalAppointmentsCount: Int,
    onAddAppointmentClick: () -> Unit,
    onViewAgendaClick: () -> Unit,
    onToggleComplete: (AppointmentEntity) -> Unit,
    onExportPdfClick: (() -> Unit)? = null,
    onExportTextClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(14.dp), spotColor = Color.Black.copy(alpha = 0.05f))
            .testTag("upcoming_appointments_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
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
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = DeepEspressoPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = stringResource(id = R.string.upcoming_appointments),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = if (totalAppointmentsCount > 0) "$totalAppointmentsCount visites" else "Calendrier prénatal",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextWarmTaupe, fontSize = 9.sp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (onExportPdfClick != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SoftToffeeSecondary)
                                .clickable { onExportPdfClick() }
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                .testTag("btn_home_export_pdf")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "PDF Export",
                                    tint = DeepEspressoPrimary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "PDF",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepEspressoPrimary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    if (onExportTextClick != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DeepEspressoPrimary)
                                .clickable { onExportTextClick() }
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                .testTag("btn_home_export_txt")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = "TXT Export",
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "TXT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Text(
                        text = stringResource(id = R.string.view_agenda),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepEspressoPrimary,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.clickable { onViewAgendaClick() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (nextAppointment != null) {
                // Next scheduled appointment card
                val dateFmt = SimpleDateFormat("EEE, dd MMM • HH:mm", Locale.getDefault())
                    .format(Date(nextAppointment.dateTimestamp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SquircleItemBg)
                        .padding(9.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DeepEspressoPrimary)
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "PROCHAIN",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = dateFmt,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DeepEspressoPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = nextAppointment.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkEspresso,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "${nextAppointment.doctorName} • ${nextAppointment.location}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextWarmTaupe,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        IconButton(
                            onClick = { onToggleComplete(nextAppointment) },
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(CrispWhiteSurface)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Complete appointment",
                                tint = DeepEspressoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            } else {
                // Empty state with quick add CTA
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SquircleItemBg)
                        .padding(9.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_upcoming_appointments),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextWarmTaupe,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = onAddAppointmentClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = stringResource(id = R.string.schedule_appointment),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White, fontSize = 10.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Bump & Baby Photo Studio with Camera and Gallery access shortcuts
@Composable
private fun BumpPhotoStudioCard(
    currentWeek: Int,
    savedPhotosCount: Int,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onViewJournalAlbumClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 5.dp, shape = RoundedCornerShape(26.dp), spotColor = Color.Black.copy(alpha = 0.05f))
            .testTag("bump_photo_studio_card"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SoftToffeeSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = DeepEspressoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = stringResource(id = R.string.bump_photo_studio),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "Week $currentWeek • $savedPhotosCount memories saved",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextWarmTaupe, fontSize = 10.sp)
                        )
                    }
                }

                Text(
                    text = "View Album",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepEspressoPrimary
                    ),
                    modifier = Modifier.clickable { onViewJournalAlbumClick() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Camera and Gallery Shortcut Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Take Photo (Camera) Button
                Button(
                    onClick = onCameraClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_take_bump_photo"),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(id = R.string.take_bump_photo),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                // Choose from Gallery Button
                OutlinedButton(
                    onClick = onGalleryClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_pick_bump_gallery"),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, DeepEspressoPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepEspressoPrimary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Gallery",
                            tint = DeepEspressoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(id = R.string.pick_from_gallery),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepEspressoPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SquircleThumbnail(
    title: String,
    emojiOrIcon: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SquircleItemBg)
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(CrispWhiteSurface),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emojiOrIcon, fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextDarkEspresso, fontSize = 11.sp),
            maxLines = 1
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall.copy(color = TextWarmTaupe, fontSize = 9.sp),
            maxLines = 1
        )
    }
}

@Composable
private fun CompactPillBadge(
    icon: String,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(CrispWhiteSurface)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, color = TextDarkEspresso, fontSize = 11.sp)
            )
        }
    }
}

@Composable
private fun RecentLogCard(
    title: String,
    subtitle: String,
    icon: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(135.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SquircleItemBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextDarkEspresso, fontSize = 12.sp))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = TextWarmTaupe, fontSize = 10.sp), maxLines = 1)
        }
    }
}
