package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.WalletDocumentEntity
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.theme.WarmCanvasBackground
import com.example.ui.viewmodel.PregnancyViewModel

@Composable
fun MedicalWalletScreen(
    viewModel: PregnancyViewModel,
    onBack: () -> Unit
) {
    val documents by viewModel.walletDocuments.collectAsState()
    val motherName by viewModel.profileName.collectAsState()
    val fatherName by viewModel.fatherName.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<WalletDocumentEntity?>(null) }
    var showDriveDialog by remember { mutableStateOf(false) }
    var googleDriveLink by remember { mutableStateOf("https://drive.google.com/drive/folders/maman-bebe-dossier") }
    var selectedFilter by remember { mutableStateOf("Tous") } // "Tous", "Identités", "Assurances"
    var isShuffleMode by remember { mutableStateOf(false) }
    var shuffledDocuments by remember { mutableStateOf(documents) }

    LaunchedEffect(documents) {
        if (!isShuffleMode) {
            shuffledDocuments = documents
        }
    }

    val context = LocalContext.current

    // Prepopulate default documents if empty
    LaunchedEffect(documents.isEmpty()) {
        if (documents.isEmpty()) {
            viewModel.addWalletDocument(
                title = "Carte Vitale Mère",
                documentType = "VITAL_CARD",
                holderName = motherName,
                documentNumber = "2 95 04 75 123 456 78",
                expirationDate = "Permanent",
                notes = "À présenter à chaque consultation & maternité"
            )
            viewModel.addWalletDocument(
                title = "Carte Mutuelle Santé",
                documentType = "MUTUAL",
                holderName = "$motherName & $fatherName",
                documentNumber = "MUT-849204-A",
                expirationDate = "31/12/2026",
                notes = "Prise en charge chambre particulière maternité"
            )
            viewModel.addWalletDocument(
                title = "Pièce d'Identité (Fatou)",
                documentType = "ID_MOM",
                holderName = motherName,
                documentNumber = "CNI-2401948",
                expirationDate = "15/06/2032",
                notes = "Pour déclaration de naissance en mairie"
            )
            viewModel.addWalletDocument(
                title = "Pièce d'Identité (Bala)",
                documentType = "ID_DAD",
                holderName = fatherName,
                documentNumber = "CNI-1983021",
                expirationDate = "10/09/2030",
                notes = "Pour reconnaissance anticipée & paternité"
            )
            viewModel.addWalletDocument(
                title = "Carte Groupe Sanguin & Rhésus",
                documentType = "BLOOD_TYPE",
                holderName = motherName,
                documentNumber = "A Positif (A+)",
                expirationDate = "Définitif (2 déterminations)",
                notes = "Document obligatoire salle d'accouchement"
            )
        }
    }

    val filteredList = when (selectedFilter) {
        "Identités" -> shuffledDocuments.filter { it.documentType.contains("ID", ignoreCase = true) }
        "Assurances" -> shuffledDocuments.filter { it.documentType.contains("MUTUAL", ignoreCase = true) || it.documentType.contains("VITAL", ignoreCase = true) || it.documentType.contains("INSURANCE", ignoreCase = true) }
        else -> shuffledDocuments
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCanvasBackground)
    ) {
        // Top White Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_wallet_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = DeepEspressoPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Portefeuille Médical & ID",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "$motherName & $fatherName • Multi-ID & Assurances",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DeepEspressoPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            isShuffleMode = !isShuffleMode
                            if (isShuffleMode && shuffledDocuments.size > 1) {
                                shuffledDocuments = shuffledDocuments.shuffled()
                                Toast.makeText(context, "Shuffle portefeuille actif (Glissement vertical)", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (isShuffleMode) DeepEspressoPrimary else TextWarmTaupe,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("btn_add_card_wallet"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Ajouter",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("Tous", "Identités", "Assurances")) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepEspressoPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Google Drive Cloud Connection Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("google_drive_connection_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8F0FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = null,
                                    tint = Color(0xFF1A73E8),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Dossier Pièces & Assurances Cloud",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkEspresso
                                    )
                                )
                                Text(
                                    text = "Scans, CNI, passeports & cartes mutuelles connectés",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextWarmTaupe,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleDriveLink))
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    showDriveDialog = true
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ouvrir", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Wallet Documents List
            items(filteredList, key = { it.id }) { doc ->
                val gradient = when (doc.documentType) {
                    "VITAL_CARD" -> listOf(Color(0xFF1B5E20), Color(0xFF388E3C))
                    "MUTUAL" -> listOf(Color(0xFF0D47A1), Color(0xFF1976D2))
                    "ID_MOM" -> listOf(Color(0xFF5D4037), Color(0xFF8D6E63))
                    "ID_DAD" -> listOf(Color(0xFF263238), Color(0xFF455A64))
                    "BLOOD_TYPE" -> listOf(Color(0xFFB71C1C), Color(0xFFE53935))
                    else -> listOf(DeepEspressoPrimary, SoftToffeeSecondary)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEditDialog = doc }
                        .testTag("wallet_card_${doc.id}"),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(gradient))
                            .padding(18.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when (doc.documentType) {
                                            "BLOOD_TYPE" -> Icons.Default.HealthAndSafety
                                            "ID_MOM", "ID_DAD", "OTHER_ID" -> Icons.Default.PermIdentity
                                            else -> Icons.Default.CreditCard
                                        },
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = doc.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { showEditDialog = doc },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Modifier",
                                            tint = Color.White.copy(alpha = 0.85f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteWalletDocument(doc) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Supprimer",
                                            tint = Color.White.copy(alpha = 0.7f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = doc.documentNumber,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                    fontSize = 17.sp
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "TITULAIRE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 9.sp
                                        )
                                    )
                                    Text(
                                        text = doc.holderName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "EXPIRATION",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 9.sp
                                        )
                                    )
                                    Text(
                                        text = doc.expirationDate.ifBlank { "Valide" },
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }

                            if (!doc.photoUri.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = doc.photoUri,
                                        contentDescription = "Recto CNI / Carte",
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(80.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    if (!doc.photoBackUri.isNullOrBlank()) {
                                        AsyncImage(
                                            model = doc.photoBackUri,
                                            contentDescription = "Verso CNI / Carte",
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(80.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }

                            if (doc.notes.isNotBlank()) {
                                Text(
                                    text = doc.notes,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Document Dialog
    if (showAddDialog) {
        var titleInput by remember { mutableStateOf("") }
        var typeInput by remember { mutableStateOf("ID_MOM") } // ID_MOM, ID_DAD, MUTUAL, VITAL_CARD, OTHER
        var holderInput by remember { mutableStateOf(motherName) }
        var numberInput by remember { mutableStateOf("") }
        var expiryInput by remember { mutableStateOf("") }
        var notesInput by remember { mutableStateOf("") }
        var frontPhotoUri by remember { mutableStateOf<String?>(null) }
        var backPhotoUri by remember { mutableStateOf<String?>(null) }

        val frontPhotoPicker = rememberLauncherFilePicker { uri ->
            frontPhotoUri = uri?.toString()
        }
        val backPhotoPicker = rememberLauncherFilePicker { uri ->
            backPhotoUri = uri?.toString()
        }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = CrispWhiteSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Ajouter Carte ID ou Assurance",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkEspresso
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            OutlinedTextField(
                                value = titleInput,
                                onValueChange = { titleInput = it },
                                label = { Text("Nom (ex: Passeport Bala, Mutuelle...)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = holderInput,
                                onValueChange = { holderInput = it },
                                label = { Text("Titulaire (Fatou / Bala)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = numberInput,
                                onValueChange = { numberInput = it },
                                label = { Text("Numéro de carte ou pièce") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = expiryInput,
                                onValueChange = { expiryInput = it },
                                label = { Text("Date d'expiration") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { frontPhotoPicker.launch("image/*") },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SoftToffeeSecondary)
                                ) {
                                    Text(if (frontPhotoUri == null) "Photo Recto" else "Recto OK", color = DeepEspressoPrimary, fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { backPhotoPicker.launch("image/*") },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SoftToffeeSecondary)
                                ) {
                                    Text(if (backPhotoUri == null) "Photo Verso" else "Verso OK", color = DeepEspressoPrimary, fontSize = 11.sp)
                                }
                            }
                        }
                        item {
                            OutlinedTextField(
                                value = notesInput,
                                onValueChange = { notesInput = it },
                                label = { Text("Notes & Instructions") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleInput.isNotBlank()) {
                            viewModel.addWalletDocument(
                                title = titleInput.trim(),
                                documentType = typeInput,
                                holderName = holderInput.trim(),
                                documentNumber = numberInput.trim(),
                                expirationDate = expiryInput.trim(),
                                photoUri = frontPhotoUri,
                                photoBackUri = backPhotoUri,
                                notes = notesInput.trim()
                            )
                            showAddDialog = false
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary),
                    enabled = titleInput.isNotBlank()
                ) {
                    Text("Enregistrer", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Annuler", color = DeepEspressoPrimary)
                }
            }
        )
    }

    // Edit Document Dialog
    if (showEditDialog != null) {
        val doc = showEditDialog!!
        var titleInput by remember { mutableStateOf(doc.title) }
        var holderInput by remember { mutableStateOf(doc.holderName) }
        var numberInput by remember { mutableStateOf(doc.documentNumber) }
        var expiryInput by remember { mutableStateOf(doc.expirationDate) }
        var notesInput by remember { mutableStateOf(doc.notes) }
        var frontPhotoUri by remember { mutableStateOf(doc.photoUri) }
        var backPhotoUri by remember { mutableStateOf(doc.photoBackUri) }

        val editFrontPicker = rememberLauncherFilePicker { uri ->
            frontPhotoUri = uri?.toString()
        }
        val editBackPicker = rememberLauncherFilePicker { uri ->
            backPhotoUri = uri?.toString()
        }

        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            containerColor = CrispWhiteSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Modifier ${doc.title}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkEspresso
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Nom du document") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = holderInput,
                        onValueChange = { holderInput = it },
                        label = { Text("Titulaire") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = numberInput,
                        onValueChange = { numberInput = it },
                        label = { Text("Numéro") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = expiryInput,
                        onValueChange = { expiryInput = it },
                        label = { Text("Expiration") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { editFrontPicker.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SoftToffeeSecondary)
                        ) {
                            Text(if (frontPhotoUri == null) "Changer Recto" else "Recto OK", color = DeepEspressoPrimary, fontSize = 11.sp)
                        }
                        Button(
                            onClick = { editBackPicker.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SoftToffeeSecondary)
                        ) {
                            Text(if (backPhotoUri == null) "Changer Verso" else "Verso OK", color = DeepEspressoPrimary, fontSize = 11.sp)
                        }
                    }
                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateWalletDocument(
                            doc.copy(
                                title = titleInput.trim(),
                                holderName = holderInput.trim(),
                                documentNumber = numberInput.trim(),
                                expirationDate = expiryInput.trim(),
                                photoUri = frontPhotoUri,
                                photoBackUri = backPhotoUri,
                                notes = notesInput.trim()
                            )
                        )
                        showEditDialog = null
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
                ) {
                    Text("Sauvegarder", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = null }) {
                    Text("Annuler", color = DeepEspressoPrimary)
                }
            }
        )
    }

    // Google Drive Configure Dialog
    if (showDriveDialog) {
        var linkInput by remember { mutableStateOf(googleDriveLink) }
        AlertDialog(
            onDismissRequest = { showDriveDialog = false },
            containerColor = CrispWhiteSurface,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Lien Google Drive du Dossier") },
            text = {
                OutlinedTextField(
                    value = linkInput,
                    onValueChange = { linkInput = it },
                    label = { Text("URL Google Drive ou cloud") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        googleDriveLink = linkInput
                        showDriveDialog = false
                        Toast.makeText(context, "Lien Google Drive mis à jour", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
                ) {
                    Text("Enregistrer", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDriveDialog = false }) {
                    Text("Fermer")
                }
            }
        )
    }
}

@Composable
fun rememberLauncherFilePicker(onResult: (Uri?) -> Unit): androidx.activity.result.ActivityResultLauncher<String> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = onResult
    )
}
