package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BabyNameEntity
import com.example.data.local.ContractionEntity
import com.example.data.local.CoupleNoteEntity
import com.example.ui.theme.CaramelSecondary
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.LatteBorderSoft
import com.example.ui.theme.SoftPeachCard
import com.example.ui.theme.SoftToffeeContainer
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.SquircleItemBg
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.viewmodel.PregnancyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CoupleHubScreen(
    viewModel: PregnancyViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("⏱️ Contractions 5-1-1", "👨‍🍼 Guide Papa Bala", "👶 Prénoms Bébé", "💌 Mots Doux")

    val contractions by viewModel.contractions.collectAsState()
    val isContractionRunning by viewModel.isContractionRunning.collectAsState()
    val currentContractionSeconds by viewModel.currentContractionSeconds.collectAsState()
    val babyNames by viewModel.babyNames.collectAsState()
    val coupleNotes by viewModel.coupleNotes.collectAsState()

    var showAddNameDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showClearContractionsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CrispWhiteSurface)
    ) {
        // Top App Bar Header (Compact & Rectangular)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SoftToffeeContainer)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DeepEspressoPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Espace Couple • Fatou & Bala",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkEspresso,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = "Chrono contractions, guide papa, prénoms & amour",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWarmTaupe,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // Scrollable Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = CrispWhiteSurface,
            contentColor = DeepEspressoPrimary,
            edgePadding = 12.dp,
            divider = {}
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) DeepEspressoPrimary else TextWarmTaupe,
                                fontSize = 11.sp
                            )
                        )
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            when (selectedTab) {
                0 -> ContractionsTimerTab(
                    contractions = contractions,
                    isRunning = isContractionRunning,
                    runningSeconds = currentContractionSeconds,
                    onStart = { viewModel.startContractionTimer() },
                    onStop = { intensity -> viewModel.stopAndSaveContraction(intensity) },
                    onCancel = { viewModel.cancelContractionTimer() },
                    onDelete = { item -> viewModel.deleteContraction(item) },
                    onClearAll = { showClearContractionsDialog = true }
                )
                1 -> DadBalaGuideTab()
                2 -> BabyNamesTab(
                    names = babyNames,
                    onAddClick = { showAddNameDialog = true },
                    onToggleFav = { item -> viewModel.toggleBabyNameFavorite(item) },
                    onDelete = { item -> viewModel.deleteBabyName(item) },
                    onRate = { item, fatou, bala -> viewModel.updateBabyNameRating(item, fatou, bala) }
                )
                3 -> CoupleNotesTab(
                    notes = coupleNotes,
                    onAddClick = { showAddNoteDialog = true },
                    onDelete = { note -> viewModel.deleteCoupleNote(note) }
                )
            }
        }
    }

    // Dialog: Add Baby Name
    if (showAddNameDialog) {
        AddBabyNameDialog(
            onDismiss = { showAddNameDialog = false },
            onConfirm = { name, gender, origin, fatou, bala, notes ->
                viewModel.addBabyName(name, gender, origin, fatou, bala, notes)
                showAddNameDialog = false
                Toast.makeText(context, "Prénom « $name » ajouté !", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Dialog: Add Couple Note
    if (showAddNoteDialog) {
        AddCoupleNoteDialog(
            onDismiss = { showAddNoteDialog = false },
            onConfirm = { author, recipient, title, msg ->
                viewModel.addCoupleNote(author, recipient, title, msg)
                showAddNoteDialog = false
                Toast.makeText(context, "Mot doux enregistré avec amour ❤️", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Dialog: Clear All Contractions
    if (showClearContractionsDialog) {
        AlertDialog(
            onDismissRequest = { showClearContractionsDialog = false },
            title = { Text("Effacer l'historique des contractions ?", fontWeight = FontWeight.Bold, color = TextDarkEspresso) },
            text = { Text("Cette action réinitialisera toutes les contractions enregistrées.", color = TextWarmTaupe) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllContractions()
                        showClearContractionsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
                ) {
                    Text("Effacer", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearContractionsDialog = false }) {
                    Text("Annuler", color = DeepEspressoPrimary)
                }
            }
        )
    }
}

// ==========================================
// TAB 1: CONTRACTIONS TIMER (RÈGLE 5-1-1)
// ==========================================
@Composable
private fun ContractionsTimerTab(
    contractions: List<ContractionEntity>,
    isRunning: Boolean,
    runningSeconds: Long,
    onStart: () -> Unit,
    onStop: (String) -> Unit,
    onCancel: () -> Unit,
    onDelete: (ContractionEntity) -> Unit,
    onClearAll: () -> Unit
) {
    val context = LocalContext.current
    var selectedIntensity by remember { mutableStateOf("MODEREE") }

    // Evaluate 5-1-1 rule: Contractions ~5 min apart, lasting ~60s, over the last hour
    val oneHourAgo = System.currentTimeMillis() - 3600000L
    val recentContractions = contractions.filter { it.startTimestamp >= oneHourAgo }
    val is511Alert = recentContractions.size >= 4 &&
            recentContractions.map { it.intervalSeconds }.filter { it > 0 }.let { intervals ->
                intervals.isNotEmpty() && intervals.average() in 180.0..360.0 // 3 to 6 min average
            }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Obstetric 5-1-1 Assessment Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (is511Alert) Color(0xFFFFECEB) else SoftToffeeContainer
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (is511Alert) Color(0xFFD32F2F) else LatteBorderSoft
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = if (is511Alert) "🚨" else "ℹ️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (is511Alert) "RÈGLE 5-1-1 ATTEINTE ! DÉPART MATERNITÉ" else "Règle 5-1-1 : Repère Obstétrical",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (is511Alert) Color(0xFFB71C1C) else TextDarkEspresso,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        // Emergency Call button
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:15"))
                                try { context.startActivity(intent) } catch (_: Exception) {}
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (is511Alert) Color(0xFFD32F2F) else DeepEspressoPrimary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Appel Urgence", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (is511Alert) {
                            "Les contractions de Fatou sont espacées d'environ 5 minutes et durent près d'1 minute depuis 1 heure. Prenez la valise et rendez-vous calmement à la maternité !"
                        } else {
                            "Dès que les contractions sont espacées de 5 minutes, durent 1 minute, depuis au moins 1 heure -> C'est le moment de partir à la maternité !"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (is511Alert) Color(0xFFB71C1C) else TextWarmTaupe,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // 2. Big Timer Interactive Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftPeachCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val minutes = runningSeconds / 60
                    val seconds = runningSeconds % 60
                    val timeStr = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                    Text(
                        text = if (isRunning) "CONTRACTION EN COURS..." else "CHRONOMÈTRE DE CONTRACTION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRunning) Color(0xFFD32F2F) else TextWarmTaupe,
                            fontSize = 10.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isRunning) Color(0xFFD32F2F) else DeepEspressoPrimary,
                            fontSize = 42.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Intensity Selector
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            "DOUCE" to "Douce 😊",
                            "MODEREE" to "Moyenne 😐",
                            "INTENSE" to "Intense 😣"
                        ).forEach { (code, label) ->
                            val isSelected = selectedIntensity == code
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) DeepEspressoPrimary else CrispWhiteSurface)
                                    .border(1.dp, if (isSelected) DeepEspressoPrimary else LatteBorderSoft, RoundedCornerShape(8.dp))
                                    .clickable { selectedIntensity = code }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color.White else TextDarkEspresso,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Button (Start / Stop)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (!isRunning) {
                            Button(
                                onClick = onStart,
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(48.dp)
                                    .testTag("btn_start_contraction"),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "DÉMARRER LA CONTRACTION",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onCancel,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text("Annuler", color = Color.Gray, fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { onStop(selectedIntensity) },
                                    modifier = Modifier
                                        .weight(2f)
                                        .height(46.dp)
                                        .testTag("btn_stop_contraction"),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                                ) {
                                    Icon(imageVector = Icons.Default.Stop, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ARRÊTER & SAUVER",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Contraction History Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Historique (${contractions.size})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkEspresso,
                        fontSize = 13.sp
                    )
                )
                if (contractions.isNotEmpty()) {
                    Text(
                        text = "Effacer tout",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.clickable { onClearAll() }
                    )
                }
            }
        }

        if (contractions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SquircleItemBg)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune contraction enregistrée. Appuyez sur Démarrer dès que Fatou ressent une contraction.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWarmTaupe,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        } else {
            items(contractions) { item ->
                val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(item.startTimestamp))
                val dateStr = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(item.startTimestamp))
                val intervalText = if (item.intervalSeconds > 0) {
                    val intMin = item.intervalSeconds / 60
                    val intSec = item.intervalSeconds % 60
                    "Espacée de ${intMin}m ${intSec}s"
                } else "Première"

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LatteBorderSoft)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (item.intensity) {
                                            "INTENSE" -> Color(0xFFFFCDD2)
                                            "MODEREE" -> SoftToffeeContainer
                                            else -> Color(0xFFE8F5E9)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = when (item.intensity) {
                                        "INTENSE" -> Color(0xFFC62828)
                                        "MODEREE" -> DeepEspressoPrimary
                                        else -> Color(0xFF2E7D32)
                                    },
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$timeStr • Durée ${item.durationSeconds}s",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkEspresso,
                                            fontSize = 12.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(SoftToffeeSecondary.copy(alpha = 0.4f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = item.intensity.lowercase().replaceFirstChar { it.uppercase() },
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkEspresso
                                        )
                                    }
                                }
                                Text(
                                    text = "$intervalText • $dateStr",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextWarmTaupe,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = { onDelete(item) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.LightGray, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 2: GUIDE PAPA BALA
// ==========================================
@Composable
private fun DadBalaGuideTab() {
    val guideSections = listOf(
        DadTip(
            title = "1. Soulager Fatou pendant les Contractions",
            icon = "💆‍♂️",
            items = listOf(
                "Pression sacrée continue : Appuie avec la paume ou une balle de tennis sur le bas du dos de Fatou pendant chaque contraction.",
                "Respiration synchronisée : Respire face à elle pour lui donner le tempo : inspiration calme par le nez, longue expiration détendue.",
                "Hydratation & réconfort : Propose-lui une gorgée d'eau fraîche, passe une serviette tiède sur sa nuque ou vaporise un brumisateur."
            )
        ),
        DadTip(
            title = "2. Missions du Papa pour le Jour J (Départ)",
            icon = "🎒",
            items = listOf(
                "Vérifier le réservoir de la voiture et l'itinéraire le plus rapide vers la clinique.",
                "Installer le cosy / siège auto dos à la route et tester les attaches avant la naissance.",
                "Prendre la Valise de Maternité de Fatou + les dossiers médicaux & cartes d'assurance.",
                "Emporter les chargeurs de téléphone, de l'eau et quelques collations pour Fatou et toi."
            )
        ),
        DadTip(
            title = "3. Démarches Officielles & Administratives",
            icon = "📝",
            items = listOf(
                "Reconnaissance anticipée : Peut être faite en mairie avant la naissance avec les pièces d'identité.",
                "Déclaration de naissance : Obligatoire dans les 5 jours suivant l'accouchement à la mairie du lieu de naissance avec le certificat médical.",
                "Déclarer bébé à la Sécurité Sociale & Mutuelle pour rattachement aux droits des deux parents."
            )
        ),
        DadTip(
            title = "4. Premières Heures avec Bébé (Post-Partum)",
            icon = "👶",
            items = listOf(
                "Peau à peau du papa : Placer bébé torse nu contre ton torse chaud dès la salle de naissance pour le rassurer.",
                "Protéger le repos de Fatou : Filtrer les appels et les visites pour lui permettre de récupérer.",
                "Devenir l'expert des premiers changes : Couche, body, et nettoyage délicat du cordon ombilical."
            )
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SoftPeachCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, LatteBorderSoft)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "👨‍🍼", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Bala, Co-Pilote de Choc",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = "Ta présence, ton calme et tes gestes précis font toute la différence pour Fatou.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextWarmTaupe,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        items(guideSections) { section ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LatteBorderSoft)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = section.icon, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = section.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepEspressoPrimary,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    section.items.forEach { bullet ->
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Text(text = "• ", color = DeepEspressoPrimary, fontWeight = FontWeight.Bold)
                            Text(
                                text = bullet,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextDarkEspresso,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class DadTip(val title: String, val icon: String, val items: List<String>)

// ==========================================
// TAB 3: PRÉNOMS & COUPS DE CŒUR
// ==========================================
@Composable
private fun BabyNamesTab(
    names: List<BabyNameEntity>,
    onAddClick: () -> Unit,
    onToggleFav: (BabyNameEntity) -> Unit,
    onDelete: (BabyNameEntity) -> Unit,
    onRate: (BabyNameEntity, Int, Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Idées Prénoms Fatou & Bala (${names.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkEspresso,
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text = "Votez chacun vos étoiles pour trouver le coup de cœur commun",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWarmTaupe,
                            fontSize = 10.sp
                        )
                    )
                }

                Button(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_add_baby_name")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Ajouter", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (names.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SquircleItemBg)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun prénom dans la liste. Cliquez sur « Ajouter » pour noter vos premières idées !",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextWarmTaupe, fontSize = 11.sp)
                    )
                }
            }
        } else {
            items(names) { nameItem ->
                val isSharedMatch = nameItem.fatouRating >= 4 && nameItem.balaRating >= 4

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSharedMatch) SoftPeachCard else CrispWhiteSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSharedMatch) 1.5.dp else 1.dp,
                        color = if (isSharedMatch) CaramelSecondary else LatteBorderSoft
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = when (nameItem.gender) {
                                        "FILLE" -> "👧"
                                        "GARCON" -> "👦"
                                        else -> "👶"
                                    },
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = nameItem.name,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextDarkEspresso,
                                                fontSize = 15.sp
                                            )
                                        )
                                        if (isSharedMatch) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFFE8F5E9))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Match Coup de Cœur ❤️",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF2E7D32)
                                                )
                                            }
                                        }
                                    }
                                    if (nameItem.originOrMeaning.isNotBlank()) {
                                        Text(
                                            text = nameItem.originOrMeaning,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextWarmTaupe,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onToggleFav(nameItem) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (nameItem.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (nameItem.isFavorite) Color(0xFFD32F2F) else Color.LightGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onDelete(nameItem) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.LightGray, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Star ratings: Fatou and Bala
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SquircleItemBg)
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Fatou's stars
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Fatou :", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextDarkEspresso)
                                Spacer(modifier = Modifier.width(4.dp))
                                (1..5).forEach { star ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (star <= nameItem.fatouRating) Color(0xFFFFA000) else Color(0xFFD0C4BD),
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { onRate(nameItem, star, nameItem.balaRating) }
                                    )
                                }
                            }

                            // Bala's stars
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Bala :", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextDarkEspresso)
                                Spacer(modifier = Modifier.width(4.dp))
                                (1..5).forEach { star ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (star <= nameItem.balaRating) Color(0xFFFFA000) else Color(0xFFD0C4BD),
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { onRate(nameItem, nameItem.fatouRating, star) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 4: MOTS DOUX & BOÎTE À AMOUR
// ==========================================
@Composable
private fun CoupleNotesTab(
    notes: List<CoupleNoteEntity>,
    onAddClick: () -> Unit,
    onDelete: (CoupleNoteEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Boîte à Mots Doux (${notes.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkEspresso,
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text = "Messages d'amour entre mari & femme et lettres pour bébé",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWarmTaupe,
                            fontSize = 10.sp
                        )
                    )
                }

                Button(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_add_couple_note")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Écrire", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (notes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SquircleItemBg)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun mot doux pour l'instant. Laissez une pensée tendre pour votre partenaire ou pour votre bébé !",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextWarmTaupe, fontSize = 11.sp)
                    )
                }
            }
        } else {
            items(notes) { note ->
                val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(note.timestamp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftPeachCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LatteBorderSoft)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DeepEspressoPrimary)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${note.author} ➔ ${note.recipient}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = dateStr,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextWarmTaupe,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            IconButton(
                                onClick = { onDelete(note) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.LightGray, modifier = Modifier.size(14.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso,
                                fontSize = 13.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "« ${note.message} »",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextDarkEspresso,
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// DIALOG: ADD BABY NAME
// ==========================================
@Composable
private fun AddBabyNameDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, gender: String, origin: String, fatou: Int, bala: Int, notes: String) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var genderInput by remember { mutableStateOf("SURPRISE") }
    var originInput by remember { mutableStateOf("") }
    var fatouRating by remember { mutableIntStateOf(5) }
    var balaRating by remember { mutableIntStateOf(5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter une Idée Prénom", fontWeight = FontWeight.Bold, color = TextDarkEspresso) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Prénom (ex: Amina, Issa...)", fontSize = 12.sp) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black, fontWeight = FontWeight.SemiBold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_baby_name"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = CrispWhiteSurface,
                        unfocusedContainerColor = CrispWhiteSurface
                    )
                )

                // Gender Selector
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("FILLE" to "Fille 👧", "GARCON" to "Garçon 👦", "SURPRISE" to "Surprise 👶").forEach { (code, label) ->
                        val isSel = genderInput == code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) DeepEspressoPrimary else CrispWhiteSurface)
                                .border(1.dp, if (isSel) DeepEspressoPrimary else LatteBorderSoft, RoundedCornerShape(8.dp))
                                .clickable { genderInput = code }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(text = label, fontSize = 11.sp, color = if (isSel) Color.White else TextDarkEspresso)
                        }
                    }
                }

                OutlinedTextField(
                    value = originInput,
                    onValueChange = { originInput = it },
                    label = { Text("Signification / Origine", fontSize = 12.sp) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = CrispWhiteSurface,
                        unfocusedContainerColor = CrispWhiteSurface
                    )
                )

                // Rating Fatou
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Note de Fatou :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDarkEspresso)
                    Spacer(modifier = Modifier.width(6.dp))
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (star <= fatouRating) Color(0xFFFFA000) else Color.LightGray,
                            modifier = Modifier.size(20.dp).clickable { fatouRating = star }
                        )
                    }
                }

                // Rating Bala
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Note de Bala :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDarkEspresso)
                    Spacer(modifier = Modifier.width(6.dp))
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (star <= balaRating) Color(0xFFFFA000) else Color.LightGray,
                            modifier = Modifier.size(20.dp).clickable { balaRating = star }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameInput.isNotBlank()) {
                        onConfirm(nameInput, genderInput, originInput, fatouRating, balaRating, "")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
            ) {
                Text("Enregistrer", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = DeepEspressoPrimary)
            }
        }
    )
}

// ==========================================
// DIALOG: ADD COUPLE NOTE
// ==========================================
@Composable
private fun AddCoupleNoteDialog(
    onDismiss: () -> Unit,
    onConfirm: (author: String, recipient: String, title: String, message: String) -> Unit
) {
    var authorInput by remember { mutableStateOf("Bala") }
    var recipientInput by remember { mutableStateOf("Fatou") }
    var titleInput by remember { mutableStateOf("") }
    var messageInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Écrire un Mot Doux", fontWeight = FontWeight.Bold, color = TextDarkEspresso) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Author & Recipient quick toggles
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("De :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDarkEspresso)
                    listOf("Bala", "Fatou").forEach { name ->
                        val isSel = authorInput == name
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) DeepEspressoPrimary else CrispWhiteSurface)
                                .border(1.dp, if (isSel) DeepEspressoPrimary else LatteBorderSoft, RoundedCornerShape(6.dp))
                                .clickable {
                                    authorInput = name
                                    recipientInput = if (name == "Bala") "Fatou" else "Bala"
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = name, fontSize = 11.sp, color = if (isSel) Color.White else TextDarkEspresso)
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pour :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDarkEspresso)
                    listOf(recipientInput, "Bébé").distinct().forEach { rec ->
                        val isSel = recipientInput == rec
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) DeepEspressoPrimary else CrispWhiteSurface)
                                .border(1.dp, if (isSel) DeepEspressoPrimary else LatteBorderSoft, RoundedCornerShape(6.dp))
                                .clickable { recipientInput = rec }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = rec, fontSize = 11.sp, color = if (isSel) Color.White else TextDarkEspresso)
                        }
                    }
                }

                OutlinedTextField(
                    value = titleInput,
                    onValueChange = { titleInput = it },
                    label = { Text("Titre du mot doux", fontSize = 12.sp) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black, fontWeight = FontWeight.SemiBold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_couple_note_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = CrispWhiteSurface,
                        unfocusedContainerColor = CrispWhiteSurface
                    )
                )

                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    label = { Text("Votre message d'amour", fontSize = 12.sp) },
                    maxLines = 4,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_couple_note_msg"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = CrispWhiteSurface,
                        unfocusedContainerColor = CrispWhiteSurface
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titleInput.isNotBlank() && messageInput.isNotBlank()) {
                        onConfirm(authorInput, recipientInput, titleInput, messageInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
            ) {
                Text("Envoyer avec amour ❤️", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = DeepEspressoPrimary)
            }
        }
    )
}
