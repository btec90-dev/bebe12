package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import androidx.credentials.CredentialManager
import com.example.ui.screens.performSignOut
import com.example.util.UserSessionManager
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.SquircleItemBg
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.viewmodel.PregnancyViewModel

@Composable
fun SettingsPrivacyDialog(
    viewModel: PregnancyViewModel,
    onDismiss: () -> Unit
) {
    val isPinEnabled by viewModel.isPinEnabled.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val pinResetStatus by viewModel.pinResetStatus.collectAsState()
    val isTwinMode by viewModel.isTwinMode.collectAsState()

    var showPinInputMode by remember { mutableStateOf(false) }
    var showResetMode by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }

    var verificationCodeInput by remember { mutableStateOf("") }
    var resetNewPinInput by remember { mutableStateOf("") }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(true) }
    var cloudSyncEnabled by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val pregnancyLogs by viewModel.pregnancyLogs.collectAsState()
    val kickSessions by viewModel.kickSessions.collectAsState()
    val babyLogs by viewModel.babyLogs.collectAsState()
    val journalEntries by viewModel.journalEntries.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val checklistItems by viewModel.checklistItems.collectAsState()
    val medicalContacts by viewModel.medicalContacts.collectAsState()
    val profileName by viewModel.profileName.collectAsState()
    val profileAge by viewModel.profileAge.collectAsState()
    val profilePhotoUri by viewModel.profilePhotoUri.collectAsState()
    val currentWeek by viewModel.currentWeekNumber.collectAsState()
    val dueDateMillis by viewModel.dueDateMillis.collectAsState()

    var lastExportedText by remember { mutableStateOf<String?>(null) }
    var lastExportedFile by remember { mutableStateOf<java.io.File?>(null) }

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
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = DeepEspressoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "App Settings & Privacy",
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
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (pinResetStatus != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftToffeeSecondary)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = pinResetStatus ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DeepEspressoPrimary
                            )
                        )
                    }
                }

                // 1. Privacy PIN Lock
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SquircleItemBg.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isPinEnabled) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = DeepEspressoPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Privacy PIN Lock",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkEspresso
                                    )
                                )
                                Text(
                                    text = if (isPinEnabled) "PIN protection active" else "Secure logs & photos",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextWarmTaupe, fontSize = 11.sp)
                                )
                            }
                        }

                        Switch(
                            checked = isPinEnabled,
                            onCheckedChange = { enable ->
                                if (enable) {
                                    showPinInputMode = true
                                    showResetMode = false
                                } else {
                                    viewModel.disablePin()
                                    showPinInputMode = false
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = DeepEspressoPrimary
                            ),
                            modifier = Modifier.testTag("switch_pin_toggle")
                        )
                    }
                }

                if (showPinInputMode) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SquircleItemBg.copy(alpha = 0.4f))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Set 4-Digit Security PIN",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepEspressoPrimary
                            )
                        )
                        OutlinedTextField(
                            value = newPinInput,
                            onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) newPinInput = it },
                            label = { Text("Enter 4 digits", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_new_pin"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = confirmPinInput,
                            onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) confirmPinInput = it },
                            label = { Text("Confirm 4 digits", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_confirm_pin"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        if (pinError != null) {
                            Text(text = pinError ?: "", color = Color.Red, fontSize = 11.sp)
                        }
                        Button(
                            onClick = {
                                if (newPinInput.length == 4 && newPinInput == confirmPinInput) {
                                    viewModel.setPin(newPinInput)
                                    showPinInputMode = false
                                    pinError = null
                                } else {
                                    pinError = "PINs must match and be 4 digits"
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("btn_save_pin"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
                        ) {
                            Text("Save PIN", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // 2. Biometric Unlock Setting
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SquircleItemBg.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = DeepEspressoPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Biometric Unlock",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkEspresso
                                    )
                                )
                                Text(
                                    text = "Use fingerprint or Face ID",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextWarmTaupe, fontSize = 11.sp)
                                )
                            }
                        }
                        Switch(
                            checked = biometricEnabled,
                            onCheckedChange = { biometricEnabled = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = DeepEspressoPrimary)
                        )
                    }
                }

                // 3. Cloud Sync Status & Backup
                val currentUser = remember { Firebase.auth.currentUser }
                val isConnected = remember { UserSessionManager.isConnected(context) || currentUser != null }
                val userEmail = remember {
                    currentUser?.email ?: UserSessionManager.getConnectedEmail(context)
                }
                val coroutineScope = rememberCoroutineScope()
                val credentialManager = remember { CredentialManager.create(context) }
                var isSyncingNow by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SquircleItemBg.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = DeepEspressoPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Firestore Cloud Backup",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkEspresso
                                        )
                                    )
                                    Text(
                                        text = if (cloudSyncEnabled) "Active & syncing securely" else "Paused",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextWarmTaupe, fontSize = 11.sp)
                                    )
                                }
                            }
                            Switch(
                                checked = cloudSyncEnabled,
                                onCheckedChange = { cloudSyncEnabled = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = DeepEspressoPrimary)
                            )
                        }

                        if (isConnected) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CrispWhiteSurface)
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = DeepEspressoPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = userEmail,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextDarkEspresso,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Mémorisé & Connecté en permanence • Aucun mot de passe requis",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        isSyncingNow = true
                                        com.example.repository.PregnancySyncManager.startBackgroundSync(context)
                                        android.widget.Toast.makeText(context, "Cloud sync started...", android.widget.Toast.LENGTH_SHORT).show()
                                        coroutineScope.launch {
                                            kotlinx.coroutines.delay(1200)
                                            isSyncingNow = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f).testTag("btn_sync_now"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary),
                                    enabled = !isSyncingNow
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isSyncingNow) "Syncing..." else "Sync Now",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        UserSessionManager.clearSession(context)
                                        performSignOut(
                                            context = context,
                                            credentialManager = credentialManager,
                                            onSignOutComplete = {
                                                onDismiss()
                                            },
                                            scope = coroutineScope
                                        )
                                    },
                                    modifier = Modifier.weight(1f).testTag("btn_google_sign_out"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = null,
                                        tint = DeepEspressoPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Sign Out",
                                        color = DeepEspressoPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Notifications Reminder Toggle
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SquircleItemBg.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = DeepEspressoPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Daily Reminders",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkEspresso
                                    )
                                )
                                Text(
                                    text = "Water, kick counter & symptoms",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextWarmTaupe, fontSize = 11.sp)
                                )
                            }
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = DeepEspressoPrimary)
                        )
                    }
                }

                // 5. Single vs. Twin Mode
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SquircleItemBg.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Single vs. Twin Pregnancy",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkEspresso
                                )
                            )
                            Text(
                                text = if (isTwinMode) "Twin pregnancy tracked" else "Single baby tracked",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextWarmTaupe, fontSize = 11.sp)
                            )
                        }
                        Switch(
                            checked = isTwinMode,
                            onCheckedChange = { viewModel.toggleTwinMode() },
                            colors = SwitchDefaults.colors(checkedTrackColor = DeepEspressoPrimary)
                        )
                    }
                }

                // 6. Export All Contents as a Text File
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SquircleItemBg.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = DeepEspressoPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Export All Contents as Text File",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkEspresso
                                    )
                                )
                                Text(
                                    text = "Generates a complete .txt archive of all logs, visits & profile",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextWarmTaupe, fontSize = 11.sp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val text = com.example.util.MamanDataTextExporter.buildCompleteArchiveText(
                                    profileName = profileName,
                                    profileAge = profileAge,
                                    profilePhotoUri = profilePhotoUri,
                                    currentWeek = currentWeek,
                                    dueDateMillis = dueDateMillis,
                                    isTwinMode = isTwinMode,
                                    userEmail = userEmail,
                                    pregnancyLogs = pregnancyLogs,
                                    kickSessions = kickSessions,
                                    babyLogs = babyLogs,
                                    journalEntries = journalEntries,
                                    appointments = appointments,
                                    checklistItems = checklistItems,
                                    medicalContacts = medicalContacts
                                )
                                lastExportedText = text
                                val file = com.example.util.MamanDataTextExporter.saveArchiveToFile(context, text, currentWeek)
                                lastExportedFile = file
                                if (file != null) {
                                    android.widget.Toast.makeText(context, "Archive saved: ${file.name}", android.widget.Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("btn_export_all_text"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lastExportedFile != null) "Re-Generate Text File (.txt)" else "Export All Contents as Text File",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        if (lastExportedFile != null && lastExportedText != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        lastExportedFile?.let { file ->
                                            com.example.util.MamanDataTextExporter.shareTextFile(context, file)
                                        }
                                    },
                                    modifier = Modifier.weight(1f).testTag("btn_share_exported_text"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SoftToffeeSecondary)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        tint = DeepEspressoPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Share File",
                                        color = DeepEspressoPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        lastExportedText?.let { text ->
                                            com.example.util.MamanDataTextExporter.copyToClipboard(context, text)
                                        }
                                    },
                                    modifier = Modifier.weight(1f).testTag("btn_copy_exported_text"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SoftToffeeSecondary)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        tint = DeepEspressoPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Copy Text",
                                        color = DeepEspressoPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
            ) {
                Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}
