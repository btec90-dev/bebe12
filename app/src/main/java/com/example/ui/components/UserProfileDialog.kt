package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.LatteBorderSoft
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.viewmodel.PregnancyViewModel

@Composable
fun UserProfileDialog(
    viewModel: PregnancyViewModel,
    onDismiss: () -> Unit
) {
    val currentName by viewModel.profileName.collectAsState()
    val currentFatherName by viewModel.fatherName.collectAsState()
    val currentAge by viewModel.profileAge.collectAsState()
    val currentPhoto by viewModel.profilePhotoUri.collectAsState()
    val currentTitleColor by viewModel.titleColorHex.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current
    var nameInput by remember { mutableStateOf(currentName.ifBlank { "Fatou" }) }
    var fatherInput by remember { mutableStateOf(currentFatherName.ifBlank { "Bala" }) }
    var ageInput by remember { mutableStateOf(currentAge.toString()) }
    var photoUriInput by remember {
        mutableStateOf(
            if (currentPhoto.isNotBlank()) {
                currentPhoto
            } else {
                val localFile = java.io.File(context.filesDir, "user_profile_avatar.jpg")
                if (localFile.exists() && localFile.length() > 0) {
                    android.net.Uri.fromFile(localFile).toString()
                } else ""
            }
        )
    }
    var selectedColorHex by remember { mutableStateOf(currentTitleColor) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val permanentUri = viewModel.persistProfilePhoto(uri)
            photoUriInput = permanentUri
        }
    }

    val colorOptions = listOf(
        Pair("Deep Espresso", "#2D1B10"),
        Pair("Warm Caramel", "#9E5727"),
        Pair("Cognac Brown", "#7A3E15"),
        Pair("Terracotta", "#C86D51"),
        Pair("Forest Olive", "#4A5D4E")
    )

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
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = DeepEspressoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Maman Profile & Style",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkEspresso
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Profile Avatar Picker
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            galleryLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(SoftToffeeSecondary)
                            .border(2.dp, DeepEspressoPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUriInput.isNotBlank()) {
                            AsyncImage(
                                model = photoUriInput,
                                contentDescription = "Profile Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    tint = DeepEspressoPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Add Photo",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DeepEspressoPrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Nom de la Maman (ex: Fatou)", fontSize = 12.sp, color = TextDarkEspresso) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_profile_name"),
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

                OutlinedTextField(
                    value = fatherInput,
                    onValueChange = { fatherInput = it },
                    label = { Text("Nom du Papa (ex: Bala)", fontSize = 12.sp, color = TextDarkEspresso) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_profile_father_name"),
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

                OutlinedTextField(
                    value = ageInput,
                    onValueChange = { if (it.all { c -> c.isDigit() }) ageInput = it },
                    label = { Text("Âge de Maman (Années)", fontSize = 12.sp, color = TextDarkEspresso) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_profile_age"),
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

                // Title Text Color Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = DeepEspressoPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Title Text Color",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        colorOptions.forEach { (name, hex) ->
                            val isSelected = selectedColorHex.equals(hex, ignoreCase = true)
                            val parsedColor = try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { DeepEspressoPrimary }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(parsedColor)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) DeepEspressoPrimary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColorHex = hex }
                                    .testTag("color_option_$hex"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Remembered Family Google Account (bala1fatou@gmail.com)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftToffeeSecondary.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(DeepEspressoPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Compte Famille mémorisé",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepEspressoPrimary
                                )
                            )
                            Text(
                                text = "bala1fatou@gmail.com • Session permanente",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextDarkEspresso,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ageVal = ageInput.toIntOrNull() ?: 28
                    viewModel.updateUserProfile(
                        name = if (nameInput.isNotBlank()) nameInput else "Fatou",
                        age = ageVal,
                        photoUri = photoUriInput,
                        father = if (fatherInput.isNotBlank()) fatherInput else "Bala"
                    )
                    viewModel.updateTitleColor(selectedColorHex)
                    onDismiss()
                },
                modifier = Modifier.testTag("btn_save_profile"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
            ) {
                Text("Save Profile", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = DeepEspressoPrimary)
            }
        }
    )
}
