package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.IvfMilestone
import com.example.ui.viewmodel.PregnancyViewModel

@Composable
fun IvfJourneyScreen(
    viewModel: PregnancyViewModel
) {
    val milestones by viewModel.ivfMilestones.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Add custom milestone */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Custom Milestone")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Dosing Banner
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text("Nov 6–9: Oral Estrogen 2x/day; Nov 10+: 3x/day; Next Clinic Visit: Nov 12", modifier = Modifier.padding(16.dp))
            }
            
            LazyColumn {
                items(milestones) { milestone ->
                    ListItem(
                        headlineContent = { Text(milestone.title) },
                        supportingContent = { Text("${milestone.dateString} - ${milestone.phase}") },
                        trailingContent = {
                            IconButton(onClick = { /* Open Edit Modal */ }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                        }
                    )
                }
            }
        }
    }
}
