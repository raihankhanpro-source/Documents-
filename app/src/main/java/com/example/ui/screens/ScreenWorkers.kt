package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.AppBrandingEntity
import com.example.data.entity.WorkerEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ScreenWorkers(
    branding: AppBrandingEntity?,
    workers: List<WorkerEntity>,
    onAddWorker: (name: String, idNumber: String, profession: String, nationality: String) -> Unit,
    onDeleteWorker: (WorkerEntity) -> Unit,
    onSettingsClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onTabSelected: (NavTab) -> Unit,
    canManage: Boolean = true,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    var newName by remember { mutableStateOf("") }
    var newIdNumber by remember { mutableStateOf("") }
    var newProfession by remember { mutableStateOf("Driver") }
    var newNationality by remember { mutableStateOf("India") }

    val filteredWorkers = workers.filter {
        searchQuery.isBlank() ||
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.idNumber.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CivilBackground,
        topBar = {
            CivilTopHeader(
                appName = branding?.appName ?: "Civil ID",
                primaryLogoPath = branding?.primaryLogoPath ?: "",
                secondaryLogoPath = branding?.secondaryLogoPath ?: "",
                onSettingsClick = onSettingsClick,
                onNotificationClick = onNotificationClick
            )
        },
        bottomBar = {
            CivilBottomNavigation(
                selectedTab = NavTab.WORKERS,
                onTabSelected = onTabSelected,
                showFamily = branding?.showFamilyTab ?: true,
                showWorkers = branding?.showWorkersTab ?: true,
                showOther = branding?.showOtherServicesTab ?: true
            )
        },
        floatingActionButton = {
            if (canManage) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = CivilGreenPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_worker_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Worker")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Workers",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = CivilTextPrimary,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )

            SearchInputField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search by name, ID"
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredWorkers.isEmpty()) {
                // Empty state (matching Screenshot 6)
                EmptyStateView(
                    icon = Icons.Default.Engineering,
                    title = "No Workers",
                    description = "Once you have workers under your sponsorship they will display here.",
                    actionButtonText = if (canManage) "Add Worker" else null,
                    onActionClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredWorkers) { worker ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, CivilCardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = worker.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        ),
                                        color = CivilTextPrimary
                                    )
                                    Text(
                                        text = "ID: ${worker.idNumber} • ${worker.profession}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = CivilTextSecondary
                                    )
                                    if (worker.nationality.isNotBlank()) {
                                        Text(
                                            text = "Nationality: ${worker.nationality}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CivilTextMuted
                                        )
                                    }
                                }

                                if (canManage) {
                                    IconButton(onClick = { onDeleteWorker(worker) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFD32F2F)
                                        )
                                    }
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
                title = { Text("Add Sponsored Worker", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Worker Full Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newIdNumber,
                            onValueChange = { newIdNumber = it },
                            label = { Text("Resident / Iqama ID") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newProfession,
                            onValueChange = { newProfession = it },
                            label = { Text("Profession (e.g. Driver, Housekeeper)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newNationality,
                            onValueChange = { newNationality = it },
                            label = { Text("Nationality") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newName.isNotBlank() && newIdNumber.isNotBlank()) {
                                onAddWorker(newName, newIdNumber, newProfession, newNationality)
                                showAddDialog = false
                                newName = ""
                                newIdNumber = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
                    ) {
                        Text("Add Worker", color = Color.White)
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
