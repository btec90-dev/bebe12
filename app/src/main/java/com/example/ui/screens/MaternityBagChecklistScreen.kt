package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChecklistItemEntity
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.DeepEspressoPrimary
import com.example.ui.theme.SoftToffeeSecondary
import com.example.ui.theme.SquircleItemBg
import com.example.ui.theme.TextDarkEspresso
import com.example.ui.theme.TextWarmTaupe
import com.example.ui.theme.WarmCanvasBackground
import com.example.ui.viewmodel.PregnancyViewModel

@Composable
fun MaternityBagChecklistScreen(
    viewModel: PregnancyViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.checklistItems.collectAsState()
    val motherName by viewModel.profileName.collectAsState()
    val fatherName by viewModel.fatherName.collectAsState()

    val categories = listOf(
        "Toutes",
        "Valise Maternité (Maman)",
        "Valise Maternité (Bébé - Jour J)",
        "Valise Accompagnant (Papa)",
        "À Acheter Avant la Naissance",
        "À Acheter Après la Naissance"
    )
    var selectedCategory by remember { mutableStateOf("Toutes") }

    var showAddDialog by remember { mutableStateOf(false) }
    var newItemTitle by remember { mutableStateOf("") }
    var newItemCategory by remember { mutableStateOf("Valise Maternité (Maman)") }

    val filteredItems = remember(items, selectedCategory) {
        if (selectedCategory == "Toutes") items else items.filter { it.category == selectedCategory }
    }

    val checkedCount = filteredItems.count { it.isChecked }
    val totalCount = filteredItems.size
    val progress = if (totalCount > 0) checkedCount / totalCount.toFloat() else 0f

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
                        modifier = Modifier.testTag("btn_maternity_bag_back")
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
                            text = "Valise de Maternité & Jour J",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkEspresso,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "Pour $motherName & $fatherName",
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
                    modifier = Modifier.testTag("btn_add_custom_item"),
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Progress Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(SoftToffeeSecondary.copy(alpha = 0.35f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Luggage,
                                        contentDescription = null,
                                        tint = DeepEspressoPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Préparation de la valise",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkEspresso
                                        )
                                    )
                                    Text(
                                        text = "$checkedCount / $totalCount articles prêts (${(progress * 100).toInt()}%)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextWarmTaupe,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                            if (progress >= 1f && totalCount > 0) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = DeepEspressoPrimary,
                            trackColor = SoftToffeeSecondary.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            // Category Filter Chips
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepEspressoPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = CrispWhiteSurface,
                                labelColor = TextDarkEspresso
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) DeepEspressoPrimary else Color(0xFFE2D6C8)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }

            // Checklist Items
            if (filteredItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucun article dans cette catégorie.\nAppuyez sur 'Ajouter' pour en créer un !",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextWarmTaupe,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                    }
                }
            } else {
                items(filteredItems, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleChecklistItem(item) }
                            .testTag("checklist_item_${item.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.isChecked) Color(0xFFFBF8F3) else CrispWhiteSurface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isChecked) 0.dp else 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (item.isChecked) DeepEspressoPrimary else Color.Transparent)
                                    .then(
                                        if (!item.isChecked) Modifier.background(Color.White, RoundedCornerShape(6.dp))
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (item.isChecked) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (item.isChecked) TextWarmTaupe else TextDarkEspresso,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = item.category,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DeepEspressoPrimary.copy(alpha = 0.8f),
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

    // Add Custom Checklist Item Dialog
    if (showAddDialog) {
        val availableCategories = listOf(
            "Valise Maternité (Maman)",
            "Valise Maternité (Bébé - Jour J)",
            "Valise Accompagnant (Papa)",
            "À Acheter Avant la Naissance",
            "À Acheter Après la Naissance"
        )
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = CrispWhiteSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Ajouter un article personnalisé",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkEspresso
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = newItemTitle,
                        onValueChange = { newItemTitle = it },
                        label = { Text("Nom de l'article") },
                        placeholder = { Text("Ex: Coussinets en soie, Veilleuse...") },
                        modifier = Modifier.fillMaxWidth().testTag("input_custom_checklist_title"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Text(
                        text = "Catégorie :",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkEspresso
                        )
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        availableCategories.forEach { cat ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { newItemCategory = cat }
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = newItemCategory == cat,
                                    onClick = { newItemCategory = cat },
                                    colors = RadioButtonDefaults.colors(selectedColor = DeepEspressoPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = cat,
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
                        if (newItemTitle.isNotBlank()) {
                            viewModel.addCustomChecklistItem(newItemCategory, newItemTitle.trim())
                            newItemTitle = ""
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("btn_confirm_add_checklist_item"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary),
                    enabled = newItemTitle.isNotBlank()
                ) {
                    Text("Ajouter à la valise", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Annuler", color = DeepEspressoPrimary)
                }
            }
        )
    }
}
