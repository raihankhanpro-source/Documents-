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
import com.example.data.entity.AppBrandingEntity
import com.example.data.entity.FamilyMemberEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ScreenFamily(
    branding: AppBrandingEntity?,
    familyMembers: List<FamilyMemberEntity>,
    onAddFamilyMember: (name: String, idNumber: String, rel: String, dob: String) -> Unit,
    onDeleteMember: (FamilyMemberEntity) -> Unit,
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
    var newRel by remember { mutableStateOf("Son") }
    var newDob by remember { mutableStateOf("15/04/2012") }

    val filteredList = familyMembers.filter {
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
                selectedTab = NavTab.FAMILY,
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
                    modifier = Modifier.testTag("add_family_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Family Member")
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
                text = "Family",
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

            if (filteredList.isEmpty()) {
                // Empty state (matching Screenshot 5)
                EmptyStateView(
                    icon = Icons.Default.Group,
                    title = "No Family Members",
                    description = "Once you have family members, they will display here.",
                    actionButtonText = if (canManage) "Add Family Member" else null,
                    onActionClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredList) { member ->
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
                                        text = member.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        ),
                                        color = CivilTextPrimary
                                    )
                                    Text(
                                        text = "ID: ${member.idNumber} • ${member.relationship}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = CivilTextSecondary
                                    )
                                    if (member.birthDate.isNotBlank()) {
                                        Text(
                                            text = "DOB: ${member.birthDate}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CivilTextMuted
                                        )
                                    }
                                }

                                if (canManage) {
                                    IconButton(onClick = { onDeleteMember(member) }) {
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
                title = { Text("Add Family Member", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Full Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newIdNumber,
                            onValueChange = { newIdNumber = it },
                            label = { Text("Civil ID Number") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newRel,
                            onValueChange = { newRel = it },
                            label = { Text("Relationship (e.g. Son, Daughter, Wife)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newDob,
                            onValueChange = { newDob = it },
                            label = { Text("Date of Birth (DD/MM/YYYY)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newName.isNotBlank() && newIdNumber.isNotBlank()) {
                                onAddFamilyMember(newName, newIdNumber, newRel, newDob)
                                showAddDialog = false
                                newName = ""
                                newIdNumber = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
                    ) {
                        Text("Add Member", color = Color.White)
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
