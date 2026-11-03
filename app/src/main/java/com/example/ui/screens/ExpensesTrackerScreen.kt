package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.local.ExpenseEntity
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.theme.WarmCanvasBackground
import com.example.ui.viewmodel.PregnancyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpensesTrackerScreen(
    viewModel: PregnancyViewModel,
    onBack: () -> Unit
) {
    val expenses by viewModel.expenses.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedReceiptUriToView by remember { mutableStateOf<String?>(null) }
    var expenseToDelete by remember { mutableStateOf<ExpenseEntity?>(null) }

    val totalSpent = expenses.sumOf { it.amount }
    val categories = listOf("Équipement Bébé", "Santé & Frais Médicaux", "Vêtements & Soins", "Maternité & Séjour", "Autre")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCanvasBackground)
    ) {
        // Top White Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp,
            tonalElevation = 0.dp
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
                        modifier = Modifier.testTag("btn_expenses_back")
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
                            text = "Dépenses & Reçus Bébé",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "${expenses.size} dépenses enregistrées",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DeepEspressoPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.testTag("btn_add_expense_modal"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ajouter",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Total Budget Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepEspressoPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Budget Total Investi",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format(Locale.FRANCE, "%.2f €", totalSpent),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 26.sp
                                )
                            )
                            Text(
                                text = "Factures & reçus numérisés",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SoftToffeeSecondary,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }

            if (expenses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = DeepEspressoPrimary.copy(alpha = 0.6f),
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Aucune dépense enregistrée",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkEspresso
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ajoutez vos achats poussette, berceau, consultations ou vêtements avec leur reçu scanné !",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextWarmTaupe,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            )
                        }
                    }
                }
            } else {
                items(expenses, key = { it.id }) { expense ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expense_card_${expense.id}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Scanned receipt thumbnail or receipt placeholder
                            if (!expense.receiptUri.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, DeepEspressoPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                        .clickable { selectedReceiptUriToView = expense.receiptUri }
                                        .testTag("thumb_receipt_${expense.id}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = expense.receiptUri,
                                        contentDescription = "Reçu scanné",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Visibility,
                                            contentDescription = "Consulter le reçu",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(SoftToffeeSecondary.copy(alpha = 0.35f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Receipt,
                                        contentDescription = null,
                                        tint = DeepEspressoPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = expense.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkEspresso,
                                        fontSize = 14.sp
                                    )
                                )
                                Text(
                                    text = "${expense.category} • ${expense.dateString}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextWarmTaupe,
                                        fontSize = 11.sp
                                    )
                                )
                                if (expense.notes.isNotBlank()) {
                                    Text(
                                        text = expense.notes,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = DeepEspressoPrimary,
                                            fontSize = 10.sp
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = String.format(Locale.FRANCE, "%.2f €", expense.amount),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepEspressoPrimary,
                                        fontSize = 15.sp
                                    )
                                )

                                IconButton(
                                    onClick = { expenseToDelete = expense },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Supprimer",
                                        tint = Color.Red.copy(alpha = 0.6f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Add Expense with Receipt Photo Attachment
    if (showAddDialog) {
        var titleInput by remember { mutableStateOf("") }
        var amountInput by remember { mutableStateOf("") }
        var categoryInput by remember { mutableStateOf(categories.first()) }
        var notesInput by remember { mutableStateOf("") }
        var receiptUriInput by remember { mutableStateOf<String?>(null) }

        val receiptPhotoPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            if (uri != null) {
                receiptUriInput = uri.toString()
            }
        }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = CrispWhiteSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Ajouter une dépense & Reçu",
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
                        label = { Text("Article ou soin") },
                        placeholder = { Text("Ex: Poussette trio, Consultation...") },
                        modifier = Modifier.fillMaxWidth().testTag("input_expense_title"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Montant (€)") },
                        placeholder = { Text("120.50") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("input_expense_amount"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Scanned Receipt Attachment Button / Preview
                    OutlinedButton(
                        onClick = {
                            receiptPhotoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_pick_receipt_photo"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = DeepEspressoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (receiptUriInput != null) "Reçu scanné attaché ✓" else "Attacher / Scanner le reçu",
                            color = DeepEspressoPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    if (receiptUriInput != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, DeepEspressoPrimary, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = receiptUriInput,
                                contentDescription = "Aperçu reçu",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Notes (Garantie, magasin...)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amountVal = amountInput.toDoubleOrNull() ?: 0.0
                        if (titleInput.isNotBlank() && amountVal > 0) {
                            val todayStr = SimpleDateFormat("dd MMM yyyy", Locale.FRANCE).format(Date())
                            viewModel.addExpense(
                                title = titleInput.trim(),
                                amount = amountVal,
                                category = categoryInput,
                                dateString = todayStr,
                                receiptUri = receiptUriInput,
                                notes = notesInput.trim()
                            )
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("btn_confirm_add_expense"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary),
                    enabled = titleInput.isNotBlank() && (amountInput.toDoubleOrNull() ?: 0.0) > 0
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

    // Full-Screen Scanned Receipt Viewer Dialog
    if (selectedReceiptUriToView != null) {
        Dialog(onDismissRequest = { selectedReceiptUriToView = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .testTag("dialog_scanned_receipt_viewer"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reçu / Facture Scannée",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso
                            )
                        )
                        IconButton(onClick = { selectedReceiptUriToView = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = selectedReceiptUriToView,
                            contentDescription = "Reçu agrandi",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (expenseToDelete != null) {
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = { Text("Supprimer cette dépense ?") },
            text = { Text("Voulez-vous supprimer '${expenseToDelete?.title}' de ${String.format(Locale.FRANCE, "%.2f €", expenseToDelete?.amount ?: 0.0)} ?") },
            confirmButton = {
                Button(
                    onClick = {
                        expenseToDelete?.let { viewModel.deleteExpense(it) }
                        expenseToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Supprimer", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}
