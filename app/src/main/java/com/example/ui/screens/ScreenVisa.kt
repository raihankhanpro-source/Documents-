package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
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
fun ScreenVisa(
    visaDocuments: List<DocumentEntity>,
    onAddVisa: (number: String, expiry: String, sponsor: String) -> Unit,
    onDeleteVisa: (DocumentEntity) -> Unit,
    onBackClick: () -> Unit,
    canManage: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var visaNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("12/12/2026") }
    var sponsorName by remember { mutableStateOf("Business Multi-Entry") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CivilBackground,
        topBar = {
            SimpleTopBar(
                title = "My Visa",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            if (canManage) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = CivilGreenPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_visa_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Visa")
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
            if (visaDocuments.isEmpty()) {
                // Empty state matching Screenshot 14
                EmptyStateView(
                    icon = Icons.Default.Description,
                    title = "No Visa",
                    description = "Once you have a Visa, the details will display here.",
                    actionButtonText = if (canManage) "Add Visa Record" else null,
                    onActionClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(visaDocuments) { visa ->
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
                                        text = visa.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        color = CivilTextPrimary
                                    )
                                    if (canManage) {
                                        IconButton(onClick = { onDeleteVisa(visa) }) {
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
                                DetailRow(label = "Visa Number", value = visa.documentNumber, canCopy = true)
                                DetailRow(label = "Expiry Date", value = visa.expiryDate)
                                if (visa.notes.isNotBlank()) {
                                    DetailRow(label = "Visa Type / Sponsor", value = visa.notes)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Visa Record", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = visaNumber,
                            onValueChange = { visaNumber = it },
                            label = { Text("Visa Number") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = expiryDate,
                            onValueChange = { expiryDate = it },
                            label = { Text("Expiry Date (DD/MM/YYYY)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = sponsorName,
                            onValueChange = { sponsorName = it },
                            label = { Text("Visa Type / Sponsor") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (visaNumber.isNotBlank()) {
                                onAddVisa(visaNumber, expiryDate, sponsorName)
                                showAddDialog = false
                                visaNumber = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
                    ) {
                        Text("Add Visa", color = Color.White)
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
