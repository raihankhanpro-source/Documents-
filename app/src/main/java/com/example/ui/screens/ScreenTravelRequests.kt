package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.TravelRecordEntity
import com.example.ui.components.DetailRow
import com.example.ui.components.EmptyStateView
import com.example.ui.components.SimpleTopBar
import com.example.ui.theme.*

@Composable
fun ScreenTravelRequests(
    travelRecords: List<TravelRecordEntity>,
    onCreateRequest: (dest: String, border: String, depDate: String, retDate: String) -> Unit,
    onDeleteRequest: (TravelRecordEntity) -> Unit,
    onBackClick: () -> Unit,
    canManage: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var destination by remember { mutableStateOf("Bahrain (Causeway)") }
    var borderPoint by remember { mutableStateOf("King Fahd Causeway") }
    var departureDate by remember { mutableStateOf("25/10/2026") }
    var returnDate by remember { mutableStateOf("28/10/2026") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CivilBackground,
        topBar = {
            SimpleTopBar(
                title = "Travel Requests",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (travelRecords.isEmpty()) {
                // Empty state matching Screenshot 16
                EmptyStateView(
                    icon = Icons.Default.LocationOn,
                    title = "No Travel Requests",
                    description = "Once you create or join a travel request for crossing Gulf country borders they will display here.",
                    actionButtonText = if (canManage) "Create Travel Request" else null,
                    onActionClick = { showCreateDialog = true },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(travelRecords) { record ->
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
                                        text = record.destination,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        color = CivilTextPrimary
                                    )
                                    if (canManage) {
                                        IconButton(onClick = { onDeleteRequest(record) }) {
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
                                DetailRow(label = "Request Number", value = record.requestNumber, canCopy = true)
                                DetailRow(label = "Border Crossing", value = record.borderPoint)
                                DetailRow(label = "Departure Date", value = record.departureDate)
                                DetailRow(label = "Return Date", value = record.returnDate)
                                DetailRow(label = "Status", value = "", badgeText = record.status)
                            }
                        }
                    }
                }

                if (canManage) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showCreateDialog = true },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("create_travel_request_button")
                    ) {
                        Text(
                            text = "Create Travel Request",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Create Travel Request", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = destination,
                            onValueChange = { destination = it },
                            label = { Text("Destination Country") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = borderPoint,
                            onValueChange = { borderPoint = it },
                            label = { Text("Border Crossing Point") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = departureDate,
                            onValueChange = { departureDate = it },
                            label = { Text("Departure Date (DD/MM/YYYY)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = returnDate,
                            onValueChange = { returnDate = it },
                            label = { Text("Return Date (DD/MM/YYYY)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (destination.isNotBlank()) {
                                onCreateRequest(destination, borderPoint, departureDate, returnDate)
                                showCreateDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
                    ) {
                        Text("Submit Request", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
