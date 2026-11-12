package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import com.example.health.HealthConnectAvailability
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.SageContainer
import com.example.ui.theme.SageTertiary
import com.example.ui.theme.SoftPeachCard
import com.example.ui.theme.SoftSageCard
import com.example.ui.viewmodel.PregnancyViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HealthConnectDialog(
    viewModel: PregnancyViewModel,
    onDismiss: () -> Unit
) {
    val status by viewModel.healthConnectStatus.collectAsState()
    val hasPerms by viewModel.hasHealthPermissions.collectAsState()
    val syncedWeights by viewModel.syncedWeights.collectAsState()
    val syncedSleep by viewModel.syncedSleep.collectAsState()
    val currentWeight by viewModel.todayWeightKg.collectAsState()

    // Health Connect Permission Contract Launcher
    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { grantedPermissions ->
        val allGranted = viewModel.healthConnectManager.permissions.all { it in grantedPermissions }
        viewModel.onHealthPermissionsResult(allGranted)
    }

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm", Locale.FRENCH)
        .withZone(ZoneId.systemDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = if (hasPerms) SageTertiary else RosePrimary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Android Santé Connect",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("health_connect_dialog_content"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Security & Privacy Guarantee
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = if (hasPerms) SoftSageCard else SoftPeachCard)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = if (hasPerms) Icons.Default.CheckCircle else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (hasPerms) SageTertiary else RosePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (hasPerms) "Connexion Sécurisée Active" else "Stockage Santé Chiffré sur l'Appareil",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasPerms) SageTertiary else RosePrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Maman & Bébé utilise Android Santé Connect pour lire et enregistrer votre poids et vos séances de sommeil directement sur votre appareil. Vos données restent strictement privées.",
                                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp)
                                )
                            }
                        }
                    }
                }

                // Status & Action button
                item {
                    when (status) {
                        HealthConnectAvailability.NOT_SUPPORTED -> {
                            Text(
                                text = "Santé Connect n'est pas pris en charge sur cette version de l'appareil. La saisie locale hors ligne reste entièrement disponible.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                            )
                        }
                        HealthConnectAvailability.NOT_INSTALLED -> {
                            Text(
                                text = "L'application Santé Connect n'est pas installée ou nécessite une mise à jour.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                            )
                        }
                        HealthConnectAvailability.INSTALLED -> {
                            if (!hasPerms) {
                                Button(
                                    onClick = {
                                        requestPermissionsLauncher.launch(viewModel.healthConnectManager.permissions)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("btn_grant_health_connect_permissions"),
                                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary)
                                ) {
                                    Text("Autoriser Santé Connect")
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Statut : Connecté & Autorisé",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = SageTertiary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    OutlinedButton(
                                        onClick = { viewModel.refreshHealthConnectState() },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("btn_refresh_health_connect")
                                    ) {
                                        Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Synchroniser", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Write Test Buttons (when connected)
                if (hasPerms) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Actions Rapides de Synchronisation",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.saveTodayLog() },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("btn_sync_weight_now"),
                                        colors = ButtonDefaults.buttonColors(containerColor = SageTertiary),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Scale, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Enreg. $currentWeight kg", style = MaterialTheme.typography.labelSmall)
                                    }

                                    Button(
                                        onClick = { viewModel.logSleepSession(480, isBaby = false, notes = "Sommeil nocturne maman") },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("btn_sync_sleep_now"),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Bedtime, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Enreg. 8h Sommeil", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }

                    // Display Synced Weight Records from Health Connect
                    item {
                        Text(
                            text = "Poids Récents dans Santé Connect (${syncedWeights.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    if (syncedWeights.isEmpty()) {
                        item {
                            Text(
                                text = "Aucun poids enregistré dans Santé Connect pour le moment. Appuyez sur « Enreg. » pour synchroniser.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                            )
                        }
                    } else {
                        items(syncedWeights.take(5)) { pt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "⚖️ ${String.format(Locale.getDefault(), "%.1f kg", pt.weightKg)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = dateFormatter.format(pt.time),
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                                )
                            }
                        }
                    }

                    // Display Synced Sleep Sessions from Health Connect
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Séances de Sommeil Récentes (${syncedSleep.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    if (syncedSleep.isEmpty()) {
                        item {
                            Text(
                                text = "Aucune séance de sommeil enregistrée dans Santé Connect pour le moment.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                            )
                        }
                    } else {
                        items(syncedSleep.take(5)) { s ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "🌙 ${s.title} (${s.durationMinutes} min)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = dateFormatter.format(s.startTime),
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                modifier = Modifier.testTag("btn_close_health_dialog")
            ) {
                Text("Terminé")
            }
        }
    )
}
