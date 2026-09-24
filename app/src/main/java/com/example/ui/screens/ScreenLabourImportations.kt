package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.DocumentEntity
import com.example.ui.components.DetailRow
import com.example.ui.components.EmptyStateView
import com.example.ui.components.SimpleTopBar
import com.example.ui.theme.*

@Composable
fun ScreenLabourImportations(
    labourRecords: List<DocumentEntity>,
    onAddLabour: (visaNo: String, occupation: String, nationality: String) -> Unit,
    onDeleteLabour: (DocumentEntity) -> Unit,
    onBackClick: () -> Unit,
    canManage: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var visaNumber by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("Technician") }
    var nationality by remember { mutableStateOf("Philippines") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CivilBackground,
        topBar = {
            SimpleTopBar(
                title = "Labour Importations",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            if (canManage) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = CivilGreenPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_labour_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Record")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (labourRecords.isEmpty()) {
                // Empty state matching Screenshot 17
                EmptyStateView(
                    icon = Icons.Default.Group,
                    title = "No Labor Importations",
                    description = "Once you have labor importations, they will be displayed here.",
                    actionButtonText = if (canManage) "Add Labor Record" else null,
                    onActionClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(labourRecords) { record ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, CivilCardBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = record.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        color = CivilTextPrimary
                                    )
                                    if (canManage) {
                                        IconButton(onClick = { onDeleteLabour(record) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color(0xFFD32F2F)
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider(color = CivilBorder.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(8.dp))
                                DetailRow(label = "Visa Authorization", value = record.documentNumber, canCopy = true)
                                DetailRow(label = "Occupation", value = record.notes)
                                DetailRow(label = "Status", value = "", badgeText = "Approved")
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Labor Importation", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = visaNumber,
                            onValueChange = { visaNumber = it },
                            label = { Text("Visa Block / Application No.") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = occupation,
                            onValueChange = { occupation = it },
                            label = { Text("Occupation") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = nationality,
                            onValueChange = { nationality = it },
                            label = { Text("Country of Recruitment") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (visaNumber.isNotBlank()) {
                                onAddLabour(visaNumber, occupation, nationality)
                                showAddDialog = false
                                visaNumber = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
                    ) {
                        Text("Add Record", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
