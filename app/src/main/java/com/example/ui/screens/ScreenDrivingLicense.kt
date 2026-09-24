package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
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
fun ScreenDrivingLicense(
    licenses: List<DocumentEntity>,
    onAddLicense: (number: String, categoryType: String, expiry: String) -> Unit,
    onDeleteLicense: (DocumentEntity) -> Unit,
    onBackClick: () -> Unit,
    canManage: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var licenseNumber by remember { mutableStateOf("") }
    var licenseType by remember { mutableStateOf("Private (خصوصي)") }
    var expiryDate by remember { mutableStateOf("15/09/2030") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CivilBackground,
        topBar = {
            SimpleTopBar(
                title = "My Driving License",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            if (canManage) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = CivilGreenPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_license_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add License")
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
            if (licenses.isEmpty()) {
                // Empty state matching Screenshot 15
                EmptyStateView(
                    icon = Icons.Default.DirectionsCar,
                    title = "No Driving Licenses",
                    description = "Once you have a driving license, the details will display here.",
                    actionButtonText = if (canManage) "Add Driving License" else null,
                    onActionClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(licenses) { item ->
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
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        color = CivilTextPrimary
                                    )
                                    if (canManage) {
                                        IconButton(onClick = { onDeleteLicense(item) }) {
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
                                DetailRow(label = "License Number", value = item.documentNumber, canCopy = true)
                                DetailRow(label = "License Class", value = item.notes.ifBlank { "Private" })
                                DetailRow(label = "Expiry Date", value = item.expiryDate)
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Driving License", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = licenseNumber,
                            onValueChange = { licenseNumber = it },
                            label = { Text("License Number") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = licenseType,
                            onValueChange = { licenseType = it },
                            label = { Text("License Class (e.g. Private / Heavy)") },
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
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (licenseNumber.isNotBlank()) {
                                onAddLicense(licenseNumber, licenseType, expiryDate)
                                showAddDialog = false
                                licenseNumber = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
                    ) {
                        Text("Save License", color = Color.White)
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
