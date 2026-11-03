package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.local.JournalEntryEntity
import com.example.ui.components.VoiceNotePlayerComponent
import com.example.ui.theme.*
import com.example.ui.viewmodel.PregnancyViewModel

private val albumCategories = listOf("All", "Belly Bump", "Ultrasound", "Baby Milestones", "Video Album 🎥", "Nursery", "Family")

@Composable
fun MilestoneJournalScreen(
    viewModel: PregnancyViewModel,
    onBack: () -> Unit
) {
    val entries by viewModel.journalEntries.collectAsState()
    val currentWeek by viewModel.currentWeekNumber.collectAsState()
    val motherName by viewModel.profileName.collectAsState()
    val fatherName by viewModel.fatherName.collectAsState()
    val context = LocalContext.current

    var selectedAlbum by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<JournalEntryEntity?>(null) }
    var entryToDelete by remember { mutableStateOf<JournalEntryEntity?>(null) }
    var entryToCrop by remember { mutableStateOf<JournalEntryEntity?>(null) }

    val filteredEntries = if (selectedAlbum == "All") {
        entries
    } else {
        entries.filter { it.category.equals(selectedAlbum, ignoreCase = true) }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = RosePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_journal_entry")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Reflection / Photo / Video")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_journal_back")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RosePrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Albums & Souvenirs",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = RosePrimary
                            )
                        )
                        Text(
                            text = "Photos, vidéos & notes vocales de ${motherName.ifBlank { "Fatou" }} et ${fatherName.ifBlank { "Bala" }}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }

            // Featured Voice Note Player Component
            item {
                VoiceNotePlayerComponent(
                    title = "Note Vocale — Battements de cœur & Pensées de Fatou",
                    recordedDate = "Semaine $currentWeek • Aujourd'hui",
                    durationSeconds = 52
                )
            }

            // Category Album Filter Chips
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(albumCategories) { category ->
                        val isSelected = selectedAlbum == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) RosePrimary else SoftPinkCard)
                                .clickable { selectedAlbum = category }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("album_chip_$category"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isSelected) Color.White else RosePrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            if (filteredEntries.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftPinkCard)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = if (selectedAlbum.contains("Video")) "🎬" else "📸", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Aucun souvenir dans '$selectedAlbum'",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Ajoutez une photo, une vidéo ou une note vocale à cet album.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = RosePrimary)
                            ) {
                                Text("Ajouter un souvenir")
                            }
                        }
                    }
                }
            } else {
                items(filteredEntries, key = { it.id }) { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("journal_card_${entry.id}"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            // Photo Preview
                            if (!entry.photoUri.isNullOrBlank()) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = Uri.parse(entry.photoUri)),
                                        contentDescription = "Bump Photo",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp),
                                        contentScale = ContentScale.Crop
                                    )

                                    // Category Badge Overlay
                                    Box(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .align(Alignment.TopStart)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Black.copy(alpha = 0.6f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = entry.category,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }

                            // Video Preview Banner
                            if (!entry.videoUri.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .background(Color(0xFF1E1E1E))
                                        .clickable {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                                    setDataAndType(Uri.parse(entry.videoUri), "video/*")
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }
                                                context.startActivity(intent)
                                            } catch (_: Exception) {}
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(CircleShape)
                                                .background(RosePrimary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Play Video",
                                                tint = Color.White,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Vidéo Souvenir • Toucher pour lire",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }

                                    // Video Tag
                                    Box(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .align(Alignment.TopStart)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(RosePrimary)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Album Vidéo",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // Embedded Audio Player if entry has voice note
                            if (!entry.audioUri.isNullOrBlank()) {
                                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                    VoiceNotePlayerComponent(
                                        title = "Note Vocale : ${entry.title}",
                                        recordedDate = entry.dateString,
                                        audioUri = entry.audioUri,
                                        durationSeconds = 38
                                    )
                                }
                            }

                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = entry.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(RoseContainer)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Semaine ${entry.weekNumber}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = RosePrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }

                                Text(
                                    text = entry.dateString,
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
                                )

                                if (entry.reflection.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = entry.reflection,
                                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Action Buttons Row: Save, Crop, Share, Edit, Delete
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!entry.photoUri.isNullOrBlank()) {
                                        IconButton(
                                            onClick = { viewModel.savePhotoToDeviceGallery(context, entry.photoUri) },
                                            modifier = Modifier.testTag("btn_save_photo_${entry.id}")
                                        ) {
                                            Icon(imageVector = Icons.Default.SaveAlt, contentDescription = "Save to Device", tint = RosePrimary)
                                        }

                                        IconButton(
                                            onClick = { entryToCrop = entry },
                                            modifier = Modifier.testTag("btn_crop_photo_${entry.id}")
                                        ) {
                                            Icon(imageVector = Icons.Default.Crop, contentDescription = "Crop Photo", tint = RosePrimary)
                                        }

                                        IconButton(
                                            onClick = { viewModel.sharePhoto(context, entry.photoUri, "${entry.title} - Semaine ${entry.weekNumber}") },
                                            modifier = Modifier.testTag("btn_share_photo_${entry.id}")
                                        ) {
                                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share Photo", tint = RosePrimary)
                                        }
                                    }

                                    IconButton(
                                        onClick = { entryToEdit = entry },
                                        modifier = Modifier.testTag("btn_edit_entry_${entry.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    IconButton(
                                        onClick = { entryToDelete = entry },
                                        modifier = Modifier.testTag("btn_delete_entry_${entry.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Dialog
    if (showAddDialog) {
        JournalEntryDialog(
            titleText = "Nouveau Souvenir ou Média",
            defaultWeek = currentWeek,
            initialTitle = "Souvenir Semaine $currentWeek",
            initialReflection = "",
            initialCategory = if (selectedAlbum == "All") "Belly Bump" else selectedAlbum,
            initialPhotoUri = null,
            initialVideoUri = null,
            initialAudioUri = null,
            onDismiss = { showAddDialog = false },
            onSave = { week, title, reflection, photoUri, category, videoUri, audioUri ->
                viewModel.addJournalEntry(
                    weekNumber = week,
                    title = title,
                    reflection = reflection,
                    photoUri = photoUri,
                    category = category,
                    audioUri = audioUri,
                    videoUri = videoUri
                )
                showAddDialog = false
            }
        )
    }

    // Edit Dialog
    if (entryToEdit != null) {
        val editing = entryToEdit!!
        JournalEntryDialog(
            titleText = "Modifier Souvenir",
            defaultWeek = editing.weekNumber,
            initialTitle = editing.title,
            initialReflection = editing.reflection,
            initialCategory = editing.category,
            initialPhotoUri = editing.photoUri,
            initialVideoUri = editing.videoUri,
            initialAudioUri = editing.audioUri,
            onDismiss = { entryToEdit = null },
            onSave = { week, title, reflection, photoUri, category, videoUri, audioUri ->
                viewModel.updateJournalEntry(
                    editing.copy(
                        weekNumber = week,
                        title = title,
                        reflection = reflection,
                        photoUri = photoUri,
                        category = category,
                        videoUri = videoUri,
                        audioUri = audioUri
                    )
                )
                entryToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (entryToDelete != null) {
        val target = entryToDelete!!
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Supprimer le souvenir ?", fontWeight = FontWeight.Bold) },
            text = { Text("Voulez-vous vraiment supprimer '${target.title}' ? Cette action est irréversible.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteJournalEntry(target)
                        entryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Supprimer", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
                    Text("Annuler")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Photo Crop Dialog
    if (entryToCrop != null) {
        val entry = entryToCrop!!
        com.example.ui.components.PhotoCropDialog(
            photoUri = entry.photoUri ?: "",
            viewModel = viewModel,
            onDismiss = { entryToCrop = null },
            onCropApplied = { croppedPath ->
                viewModel.updateJournalEntry(entry.copy(photoUri = croppedPath))
                entryToCrop = null
            }
        )
    }
}

@Composable
private fun JournalEntryDialog(
    titleText: String,
    defaultWeek: Int,
    initialTitle: String,
    initialReflection: String,
    initialCategory: String,
    initialPhotoUri: String?,
    initialVideoUri: String? = null,
    initialAudioUri: String? = null,
    onDismiss: () -> Unit,
    onSave: (Int, String, String, String?, String, String?, String?) -> Unit
) {
    var week by remember { mutableIntStateOf(defaultWeek) }
    var title by remember { mutableStateOf(initialTitle) }
    var reflection by remember { mutableStateOf(initialReflection) }
    var category by remember { mutableStateOf(initialCategory) }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(initialPhotoUri?.let { Uri.parse(it) }) }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(initialVideoUri?.let { Uri.parse(it) }) }
    var attachedAudioUri by remember { mutableStateOf<String?>(initialAudioUri) }

    val categoriesList = listOf("Belly Bump", "Ultrasound", "Baby Milestones", "Video Album 🎥", "Nursery", "Family")

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            if (category != "Video Album 🎥") {
                category = "Video Album 🎥"
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titleText, fontWeight = FontWeight.Bold, color = RosePrimary) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_journal_title")
                    )
                }

                item {
                    Text("Catégorie de l'Album", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categoriesList) { cat ->
                            val isSelected = category == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) RosePrimary else SoftPinkCard)
                                    .clickable { category = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color.White else RosePrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = reflection,
                        onValueChange = { reflection = it },
                        label = { Text("Notes & Pensées") },
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth().testTag("input_journal_reflection")
                    )
                }

                // Media Pickers Row: Photo, Video, Voice Note
                item {
                    Text("Médias Attachés", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Photo Picker
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = RosePrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (selectedPhotoUri != null) "Photo ✓" else "Photo", fontSize = 11.sp)
                        }

                        // Video Picker
                        OutlinedButton(
                            onClick = {
                                videoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = Color(0xFFD97724), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (selectedVideoUri != null) "Vidéo ✓" else "Vidéo", fontSize = 11.sp)
                        }

                        // Voice Note Toggle
                        OutlinedButton(
                            onClick = {
                                attachedAudioUri = if (attachedAudioUri == null) {
                                    "content://maman_audio/voice_note_${System.currentTimeMillis()}"
                                } else null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = SageTertiary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (attachedAudioUri != null) "Vocal ✓" else "Vocal", fontSize = 11.sp)
                        }
                    }
                }

                if (selectedPhotoUri != null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = selectedPhotoUri),
                                contentDescription = "Photo attachée",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                if (selectedVideoUri != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, tint = Color(0xFFD97724))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Vidéo attachée prête à être enregistrée", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }

                if (attachedAudioUri != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = SageContainer.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = SageTertiary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Note vocale jointe au souvenir (avec lecteur audio)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            week,
                            title,
                            reflection,
                            selectedPhotoUri?.toString(),
                            category,
                            selectedVideoUri?.toString(),
                            attachedAudioUri
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                modifier = Modifier.testTag("btn_confirm_journal_save")
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        },
        shape = RoundedCornerShape(22.dp)
    )
}
