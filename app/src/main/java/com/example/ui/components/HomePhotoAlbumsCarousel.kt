package com.example.ui.components

import android.net.Uri
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.example.R
import com.example.data.local.JournalEntryEntity
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.SoftPeachCard
import com.example.ui.theme.SoftPinkCard
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.viewmodel.PregnancyViewModel

data class HomeAlbumDef(
    val category: String,
    val displayName: String,
    val iconEmoji: String,
    val placeholderIcon: ImageVector,
    val gradientColors: List<Color>
)

// STRICT DISPLAY ORDER: Family, Ultrasound, Belly Bump, Baby Milestones, Nursery
val OrderedHomeAlbums = listOf(
    HomeAlbumDef(
        category = "Family",
        displayName = "Family",
        iconEmoji = "👨‍👩‍👧",
        placeholderIcon = Icons.Default.Favorite,
        gradientColors = listOf(Color(0xFFFF9A9E), Color(0xFFFAD0C4))
    ),
    HomeAlbumDef(
        category = "Ultrasound",
        displayName = "Ultrasound",
        iconEmoji = "🩺",
        placeholderIcon = Icons.Default.MedicalServices,
        gradientColors = listOf(Color(0xFFA18CD1), Color(0xFFFBC2EB))
    ),
    HomeAlbumDef(
        category = "Belly Bump",
        displayName = "Belly Bump",
        iconEmoji = "🤰",
        placeholderIcon = Icons.Default.CameraAlt,
        gradientColors = listOf(Color(0xFFFF85A1), Color(0xFFFFAAA6))
    ),
    HomeAlbumDef(
        category = "Baby Milestones",
        displayName = "Baby Milestones",
        iconEmoji = "👶",
        placeholderIcon = Icons.Default.Star,
        gradientColors = listOf(Color(0xFFFFD166), Color(0xFFF78C6B))
    ),
    HomeAlbumDef(
        category = "Nursery",
        displayName = "Nursery",
        iconEmoji = "🧸",
        placeholderIcon = Icons.Default.Home,
        gradientColors = listOf(Color(0xFF84DCC6), Color(0xFFA5FFD6))
    )
)

/**
 * Prominent Horizontal Photo Album Carousel for Home Screen
 * Placed immediately below the top header.
 */
@Composable
fun HomePhotoAlbumsCarousel(
    entries: List<JournalEntryEntity>,
    onAlbumSelected: (HomeAlbumDef) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(22.dp), spotColor = Color.Black.copy(alpha = 0.04f))
            .testTag("home_photo_albums_carousel"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            // Title Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📸", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Photo Albums",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkEspresso,
                            fontSize = 14.sp
                        )
                    )
                }
                Text(
                    text = "Tap to open",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextWarmTaupe,
                        fontSize = 10.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Horizontal Story Highlights Carousel
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(OrderedHomeAlbums) { album ->
                    val albumPhotos = entries.filter {
                        it.category.equals(album.category, ignoreCase = true) && !it.photoUri.isNullOrBlank()
                    }
                    val latestPhoto = albumPhotos.maxByOrNull { it.timestamp }
                    val hasPhotos = albumPhotos.isNotEmpty()

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(72.dp)
                            .clickable { onAlbumSelected(album) }
                            .testTag("album_thumb_${album.category}")
                    ) {
                        // Outer Ring / Story Border
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(
                                    if (hasPhotos) {
                                        Brush.linearGradient(
                                            listOf(
                                                RosePrimary,
                                                Color(0xFFFFB199),
                                                Color(0xFFE26D77)
                                            )
                                        )
                                    } else {
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFFEFE6E1),
                                                Color(0xFFE4D7CF)
                                            )
                                        )
                                    }
                                )
                                .padding(2.5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Inner Circular Thumbnail
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(CrispWhiteSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                if (latestPhoto != null && !latestPhoto.photoUri.isNullOrBlank()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            model = if (latestPhoto.photoUri.startsWith("/")) java.io.File(latestPhoto.photoUri) else Uri.parse(latestPhoto.photoUri)
                                        ),
                                        contentDescription = album.displayName,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    // Fallback Placeholder Icon / Emoji
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.linearGradient(album.gradientColors)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = album.iconEmoji, fontSize = 24.sp)
                                    }
                                }

                                // Plus / Count indicator badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(if (hasPhotos) RosePrimary else DeepEspressoPrimary)
                                        .border(1.5.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (hasPhotos) {
                                        Text(
                                            text = "${albumPhotos.size}",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add",
                                            tint = Color.White,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Text Label
                        Text(
                            text = album.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextDarkEspresso,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = if (hasPhotos) "${albumPhotos.size} photos" else "Add photo",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (hasPhotos) RosePrimary else TextWarmTaupe,
                                fontSize = 9.sp,
                                fontWeight = if (hasPhotos) FontWeight.Bold else FontWeight.Normal
                            ),
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * Full Interactive Album Detail Viewer with Share, Edit, Crop, and Delete tools.
 */
@Composable
fun PhotoAlbumDetailDialog(
    album: HomeAlbumDef,
    viewModel: PregnancyViewModel,
    onDismiss: () -> Unit
) {
    val entries by viewModel.journalEntries.collectAsState()
    val currentWeek by viewModel.currentWeekNumber.collectAsState()
    val context = LocalContext.current

    val albumPhotos = entries.filter {
        it.category.equals(album.category, ignoreCase = true)
    }

    var photoToCrop by remember { mutableStateOf<JournalEntryEntity?>(null) }
    var photoToEdit by remember { mutableStateOf<JournalEntryEntity?>(null) }
    var photoToDelete by remember { mutableStateOf<JournalEntryEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.saveBumpPhoto(
                bitmap = bitmap,
                week = currentWeek,
                title = "${album.displayName} Photo",
                notes = "Captured in ${album.displayName} album"
            )
            // Re-categorize to this album
            val last = viewModel.journalEntries.value.lastOrNull()
            if (last != null) {
                viewModel.updateJournalEntry(last.copy(category = album.category))
            }
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.addJournalEntry(
                weekNumber = currentWeek,
                title = "${album.displayName} Photo",
                reflection = "Added to ${album.displayName} album",
                photoUri = uri.toString(),
                category = album.category
            )
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RoseContainer)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = album.iconEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${album.displayName} Album",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "${albumPhotos.size} photos & memories",
                                style = MaterialTheme.typography.bodySmall.copy(color = RosePrimary, fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Quick Add Button
                        IconButton(
                            onClick = { showAddDialog = true },
                            modifier = Modifier.testTag("btn_album_add_photo")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Photo", tint = RosePrimary)
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("btn_close_album_dialog")
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }
                }

                // Photos List / Empty State
                if (albumPhotos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = SoftPinkCard)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = album.iconEmoji, fontSize = 56.sp)
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "No ${album.displayName} Photos Yet",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkEspresso
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Start capturing special ${album.displayName.lowercase()} moments of your pregnancy journey.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextWarmTaupe),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Button(
                                        onClick = { cameraLauncher.launch(null) },
                                        colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Take Photo")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            galleryLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp), tint = RosePrimary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Gallery", color = RosePrimary)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(albumPhotos, key = { it.id }) { entry ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("album_photo_card_${entry.id}"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column {
                                    if (!entry.photoUri.isNullOrBlank()) {
                                        Box(modifier = Modifier.fillMaxWidth()) {
                                            Image(
                                                painter = rememberAsyncImagePainter(
                                                    model = if (entry.photoUri.startsWith("/")) java.io.File(entry.photoUri) else Uri.parse(entry.photoUri)
                                                ),
                                                contentDescription = entry.title,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(260.dp),
                                                contentScale = ContentScale.Crop
                                            )

                                            // Week badge
                                            Box(
                                                modifier = Modifier
                                                    .padding(12.dp)
                                                    .align(Alignment.TopEnd)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color.Black.copy(alpha = 0.65f))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "Week ${entry.weekNumber}",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = entry.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = entry.dateString,
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                                        )

                                        if (entry.reflection.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = entry.reflection,
                                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        // Action Toolbar: Share, Crop, Save, Edit, Delete
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Crop Button
                                            if (!entry.photoUri.isNullOrBlank()) {
                                                OutlinedButton(
                                                    onClick = { photoToCrop = entry },
                                                    shape = RoundedCornerShape(10.dp),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                    modifier = Modifier.testTag("btn_crop_photo_${entry.id}")
                                                ) {
                                                    Icon(imageVector = Icons.Default.Crop, contentDescription = "Crop", modifier = Modifier.size(15.dp), tint = RosePrimary)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Crop", fontSize = 11.sp, color = RosePrimary, fontWeight = FontWeight.Bold)
                                                }
                                            } else {
                                                Spacer(modifier = Modifier.width(1.dp))
                                            }

                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                // Save to Device Gallery
                                                if (!entry.photoUri.isNullOrBlank()) {
                                                    IconButton(
                                                        onClick = { viewModel.savePhotoToDeviceGallery(context, entry.photoUri) }
                                                    ) {
                                                        Icon(imageVector = Icons.Default.SaveAlt, contentDescription = "Save to Gallery", tint = RosePrimary)
                                                    }

                                                    // Share Photo
                                                    IconButton(
                                                        onClick = { viewModel.sharePhoto(context, entry.photoUri, "${entry.title} - ${album.displayName}") },
                                                        modifier = Modifier.testTag("btn_share_${entry.id}")
                                                    ) {
                                                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = RosePrimary)
                                                    }
                                                }

                                                // Edit Entry
                                                IconButton(
                                                    onClick = { photoToEdit = entry },
                                                    modifier = Modifier.testTag("btn_edit_${entry.id}")
                                                ) {
                                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }

                                                // Delete Entry
                                                IconButton(
                                                    onClick = { photoToDelete = entry },
                                                    modifier = Modifier.testTag("btn_delete_${entry.id}")
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
            }
        }
    }

    // Photo Cropping Tool Dialog
    if (photoToCrop != null) {
        val entry = photoToCrop!!
        PhotoCropDialog(
            photoUri = entry.photoUri ?: "",
            onDismiss = { photoToCrop = null },
            onCropApplied = { croppedPath ->
                viewModel.updateJournalEntry(entry.copy(photoUri = croppedPath))
                photoToCrop = null
            },
            viewModel = viewModel
        )
    }

    // Edit Photo Details Dialog
    if (photoToEdit != null) {
        val editing = photoToEdit!!
        var titleInput by remember { mutableStateOf(editing.title) }
        var reflectionInput by remember { mutableStateOf(editing.reflection) }
        var weekInput by remember { mutableStateOf(editing.weekNumber.toString()) }

        AlertDialog(
            onDismissRequest = { photoToEdit = null },
            title = { Text("Edit ${album.displayName} Photo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Photo Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = weekInput,
                        onValueChange = { weekInput = it },
                        label = { Text("Pregnancy Week (1-42)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = reflectionInput,
                        onValueChange = { reflectionInput = it },
                        label = { Text("Notes / Caption") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val wk = weekInput.toIntOrNull() ?: editing.weekNumber
                        viewModel.updateJournalEntry(
                            editing.copy(
                                title = titleInput,
                                reflection = reflectionInput,
                                weekNumber = wk
                            )
                        )
                        photoToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { photoToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (photoToDelete != null) {
        val deleting = photoToDelete!!
        AlertDialog(
            onDismissRequest = { photoToDelete = null },
            title = { Text("Delete Photo") },
            text = { Text("Are you sure you want to delete '${deleting.title}' from your ${album.displayName} album?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteJournalEntry(deleting)
                        photoToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { photoToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Memory Choice Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add to ${album.displayName} Album") },
            text = { Text("Choose how you would like to add a new memory to this album:") },
            confirmButton = {
                Button(
                    onClick = {
                        showAddDialog = false
                        cameraLauncher.launch(null)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary)
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Camera")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showAddDialog = false
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp), tint = RosePrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Gallery", color = RosePrimary)
                }
            }
        )
    }
}

/**
 * Interactive Crop & Rotate Tool Dialog
 */
@Composable
fun PhotoCropDialog(
    photoUri: String,
    viewModel: PregnancyViewModel,
    onDismiss: () -> Unit,
    onCropApplied: (String) -> Unit
) {
    val context = LocalContext.current
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var selectedRatio by remember { mutableStateOf("1:1") } // "1:1", "4:5", "16:9", "ORIGINAL"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Crop, contentDescription = null, tint = RosePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crop & Rotate Photo", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Preview with Rotation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = if (photoUri.startsWith("/")) java.io.File(photoUri) else Uri.parse(photoUri)
                        ),
                        contentDescription = "Crop Preview",
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotationAngle),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Aspect Ratio Selector Chips
                Text("Aspect Ratio Crop:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("1:1" to "Square", "4:5" to "Portrait", "16:9" to "Wide", "ORIGINAL" to "Fit").forEach { (ratioKey, label) ->
                        val isSelected = selectedRatio == ratioKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) RosePrimary else SoftPeachCard)
                                .clickable { selectedRatio = ratioKey }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color.White else DeepEspressoPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rotate Button
                Button(
                    onClick = { rotationAngle = (rotationAngle + 90f) % 360f },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftToffeeSecondary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.RotateRight, contentDescription = null, tint = DeepEspressoPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Rotate 90° (${rotationAngle.toInt()}°)", color = DeepEspressoPrimary, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.cropAndSavePhoto(
                        context = context,
                        originalUriStr = photoUri,
                        aspectRatio = selectedRatio,
                        rotationAngle = rotationAngle,
                        onSuccess = { newPath ->
                            onCropApplied(newPath)
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = RosePrimary)
            ) {
                Text("Apply & Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
