package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
fun ChecklistScreen(
    viewModel: PregnancyViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.checklistItems.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var newItemTitle by remember { mutableStateOf("") }
    var newItemCategory by remember { mutableStateOf("Hospital Bag (Mom)") }

    val checkedCount = items.count { it.isChecked }
    val totalCount = items.size

    val momItems = items.filter { it.category == "Hospital Bag (Mom)" }
    val babyItems = items.filter { it.category == "Hospital Bag (Baby)" }
    val nurseryItems = items.filter { it.category == "Nursery & Sleep" }
    val feedingItems = items.filter { it.category.contains("Feeding") || it.category.contains("Diapering") }

    val checkedPreview = items.filter { it.isChecked }.take(3).ifEmpty {
        items.take(3)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCanvasBackground)
    ) {
        // Top Bar (matching Screen 4 "Plan an outfit" from screenshot)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("btn_checklist_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DeepEspressoPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Plan essentials & bag",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkEspresso
                    )
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.testTag("btn_add_checklist_custom")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add custom item",
                        tint = DeepEspressoPrimary
                    )
                }
            }
        }

        // Scrollable content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Top Preview Card with 3 Squircles (matching Screen 4's top preview card)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(26.dp), spotColor = Color.Black.copy(alpha = 0.04f))
                        .testTag("checklist_preview_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = SquircleItemBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        checkedPreview.forEach { item ->
                            SquircleChecklistPreview(
                                item = item,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Category 1: Hospital Bag (Mom)
            item {
                ChecklistCategorySection(
                    title = "Hospital Bag (Mom)",
                    items = momItems,
                    onToggle = { viewModel.toggleChecklistItem(it) }
                )
            }

            // Category 2: Hospital Bag (Baby)
            item {
                ChecklistCategorySection(
                    title = "Hospital Bag (Baby)",
                    items = babyItems,
                    onToggle = { viewModel.toggleChecklistItem(it) }
                )
            }

            // Category 3: Nursery & Sleep
            item {
                ChecklistCategorySection(
                    title = "Nursery & Sleep",
                    items = nurseryItems,
                    onToggle = { viewModel.toggleChecklistItem(it) }
                )
            }

            // Category 4: Feeding & Diapering
            item {
                ChecklistCategorySection(
                    title = "Feeding & Diapering",
                    items = feedingItems,
                    onToggle = { viewModel.toggleChecklistItem(it) }
                )
            }
        }

        // Bottom Full-width Deep Espresso Pill Button (matching Screen 4 "Save as today's outfit")
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = WarmCanvasBackground,
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_save_checklist_bottom"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepEspressoPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Save essentials ($checkedCount of $totalCount packed)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(text = "Add Essential Item", fontWeight = FontWeight.Bold, color = TextDarkEspresso) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newItemTitle,
                        onValueChange = { newItemTitle = it },
                        label = { Text("Item Name") },
                        modifier = Modifier.fillMaxWidth().testTag("input_new_checklist_title")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Category:", style = MaterialTheme.typography.labelMedium)
                    val cats = listOf("Hospital Bag (Mom)", "Hospital Bag (Baby)", "Nursery & Sleep", "Feeding & Diapering")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(cats) { cat ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (newItemCategory == cat) DeepEspressoPrimary else SoftToffeeSecondary)
                                    .clickable { newItemCategory = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (newItemCategory == cat) Color.White else DeepEspressoPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
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
                            viewModel.addChecklistItem(newItemTitle, newItemCategory)
                            newItemTitle = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEspressoPrimary)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextWarmTaupe)
                }
            }
        )
    }
}

@Composable
private fun SquircleChecklistPreview(
    item: ChecklistItemEntity,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(CrispWhiteSurface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = getEmojiForItem(item.title),
                fontSize = 32.sp
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                color = TextDarkEspresso,
                fontSize = 11.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ChecklistCategorySection(
    title: String,
    items: List<ChecklistItemEntity>,
    onToggle: (ChecklistItemEntity) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextDarkEspresso,
                fontSize = 14.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                SquircleChecklistItem(
                    item = item,
                    onClick = { onToggle(item) }
                )
            }
        }
    }
}

@Composable
private fun SquircleChecklistItem(
    item: ChecklistItemEntity,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(82.dp)
            .clickable { onClick() }
            .testTag("checklist_item_${item.id}")
    ) {
        Box(
            modifier = Modifier
                .size(78.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(if (item.isChecked) CrispWhiteSurface else SquircleItemBg)
                .border(
                    width = if (item.isChecked) 2.dp else 0.dp,
                    color = if (item.isChecked) DeepEspressoPrimary else Color.Transparent,
                    shape = RoundedCornerShape(22.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = getEmojiForItem(item.title),
                fontSize = 34.sp
            )

            // Small circular checkmark badge in top-right corner (matching Screen 4)
            if (item.isChecked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(DeepEspressoPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Checked",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (item.isChecked) FontWeight.Bold else FontWeight.Normal,
                color = TextDarkEspresso,
                fontSize = 11.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

private fun getEmojiForItem(title: String): String {
    val t = title.lowercase()
    return when {
        t.contains("id") || t.contains("insurance") || t.contains("card") -> "🪪"
        t.contains("plan") || t.contains("birth") -> "📋"
        t.contains("robe") || t.contains("gown") || t.contains("nightgown") -> "👘"
        t.contains("panties") || t.contains("underwear") -> "🩲"
        t.contains("bra") -> "👙"
        t.contains("toiletries") || t.contains("toothbrush") -> "🪥"
        t.contains("charger") || t.contains("phone") -> "🔌"
        t.contains("onesie") || t.contains("sleeper") || t.contains("outfit") -> "👶"
        t.contains("blanket") || t.contains("swaddle") -> "🧣"
        t.contains("hat") || t.contains("cap") -> "🧢"
        t.contains("socks") || t.contains("booties") -> "🧦"
        t.contains("car seat") -> "🚗"
        t.contains("diaper") || t.contains("wipes") -> "🧻"
        t.contains("crib") || t.contains("bassinet") -> "🛏️"
        t.contains("mattress") -> "🛌"
        t.contains("monitor") -> "📹"
        t.contains("noise") || t.contains("sound") -> "🔊"
        t.contains("pump") || t.contains("breast pump") -> "🍼"
        t.contains("bottle") -> "🍼"
        t.contains("pacifier") -> "🍼"
        t.contains("cream") || t.contains("balm") -> "🧴"
        else -> "✨"
    }
}
