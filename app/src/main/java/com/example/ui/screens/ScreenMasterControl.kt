package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.*
import com.example.ui.components.IconMapper
import com.example.ui.components.SimpleTopBar
import com.example.ui.theme.*

enum class MasterTab(val title: String) {
    BRANDING("Branding"),
    PROFILE("Profile"),
    SERVICES("Services"),
    QUICK_ACCESS("Quick Access"),
    DOCUMENTS("Documents"),
    USERS("Users"),
    BACKUP("Backup & Transfer")
}

@Composable
fun ScreenMasterControl(
    branding: AppBrandingEntity?,
    profile: ProfileEntity?,
    services: List<ServiceEntity>,
    quickAccessItems: List<QuickAccessItemEntity>,
    documents: List<DocumentEntity>,
    users: List<UserEntity>,
    onUpdateBranding: (
        appName: String,
        primaryColorHex: String,
        footerText: String,
        floatingChatEnabled: Boolean,
        showFamilyTab: Boolean,
        showWorkersTab: Boolean,
        showOtherServicesTab: Boolean,
        primaryLogoUri: Uri?,
        secondaryLogoUri: Uri?,
        backgroundImageUri: Uri?,
        fontColor: String
    ) -> Unit,
    onUpdateProfile: (ProfileEntity, Uri?) -> Unit,
    onAddOrUpdateService: (ServiceEntity) -> Unit,
    onDeleteService: (ServiceEntity) -> Unit,
    onToggleService: (ServiceEntity) -> Unit,
    onAddOrUpdateQuickAccess: (QuickAccessItemEntity) -> Unit,
    onDeleteQuickAccess: (QuickAccessItemEntity) -> Unit,
    onToggleQuickAccess: (QuickAccessItemEntity) -> Unit,
    onAddDocument: (title: String, number: String, category: String, issueDate: String, expiryDate: String, notes: String, fileUri: Uri?) -> Unit,
    onDeleteDocument: (DocumentEntity) -> Unit,
    onCreateUser: (username: String, pass: String, displayName: String, permissions: List<String>, onDone: (Boolean) -> Unit) -> Unit,
    onToggleUserEnabled: (userId: Long, enabled: Boolean) -> Unit,
    onUpdateUserPassword: (userId: Long, newPass: String) -> Unit,
    onDeleteUser: (UserEntity) -> Unit,
    onExportBackup: (secret: String, onResult: (Result<String>) -> Unit) -> Unit,
    onRestoreBackup: (encryptedData: String, secret: String, onResult: (Result<Unit>) -> Unit) -> Unit,
    onResetApp: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(MasterTab.BRANDING) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CivilBackground,
        topBar = {
            Column {
                SimpleTopBar(
                    title = "Master Control",
                    onBackClick = onBackClick
                )

                // Tab Row
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = Color.White,
                    contentColor = CivilGreenPrimary,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                            color = CivilGreenPrimary
                        )
                    }
                ) {
                    MasterTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier.testTag("master_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                MasterTab.BRANDING -> MasterBrandingSection(
                    branding = branding,
                    onSave = onUpdateBranding
                )
                MasterTab.PROFILE -> MasterProfileSection(
                    profile = profile,
                    onSave = onUpdateProfile
                )
                MasterTab.SERVICES -> MasterServicesSection(
                    services = services,
                    onAddOrUpdate = onAddOrUpdateService,
                    onDelete = onDeleteService,
                    onToggle = onToggleService
                )
                MasterTab.QUICK_ACCESS -> MasterQuickAccessSection(
                    items = quickAccessItems,
                    onAddOrUpdate = onAddOrUpdateQuickAccess,
                    onDelete = onDeleteQuickAccess,
                    onToggle = onToggleQuickAccess
                )
                MasterTab.DOCUMENTS -> MasterDocumentsSection(
                    documents = documents,
                    onAdd = onAddDocument,
                    onDelete = onDeleteDocument
                )
                MasterTab.USERS -> MasterUsersSection(
                    users = users,
                    onCreateUser = onCreateUser,
                    onToggleEnabled = onToggleUserEnabled,
                    onUpdatePassword = onUpdateUserPassword,
                    onDeleteUser = onDeleteUser
                )
                MasterTab.BACKUP -> MasterBackupSection(
                    onExport = onExportBackup,
                    onRestore = onRestoreBackup,
                    onResetApp = onResetApp
                )
            }
        }
    }
}

// ---------------------- BRANDING TAB ----------------------
@Composable
private fun MasterBrandingSection(
    branding: AppBrandingEntity?,
    onSave: (
        appName: String,
        primaryColorHex: String,
        footerText: String,
        floatingChatEnabled: Boolean,
        showFamilyTab: Boolean,
        showWorkersTab: Boolean,
        showOtherServicesTab: Boolean,
        primaryLogoUri: Uri?,
        secondaryLogoUri: Uri?,
        backgroundImageUri: Uri?,
        fontColor: String
    ) -> Unit
) {
    val context = LocalContext.current
    var appName by remember(branding) { mutableStateOf(branding?.appName ?: "Civil ID") }
    var primaryColorHex by remember(branding) { mutableStateOf(branding?.primaryColorHex ?: "#006848") }
    var footerText by remember(branding) { mutableStateOf(branding?.footerText ?: "All rights reserved. Secure Offline Portal") }
    var floatingChatEnabled by remember(branding) { mutableStateOf(branding?.floatingChatEnabled ?: true) }
    var showFamilyTab by remember(branding) { mutableStateOf(branding?.showFamilyTab ?: true) }
    var showWorkersTab by remember(branding) { mutableStateOf(branding?.showWorkersTab ?: true) }
    var showOtherServicesTab by remember(branding) { mutableStateOf(branding?.showOtherServicesTab ?: true) }
    var fontColor by remember(branding) { mutableStateOf(branding?.fontColor ?: "#000000") }

    var primaryLogoUri by remember { mutableStateOf<Uri?>(null) }
    var secondaryLogoUri by remember { mutableStateOf<Uri?>(null) }
    var backgroundImageUri by remember { mutableStateOf<Uri?>(null) }

    val primaryLogoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) primaryLogoUri = it
    }
    val secondaryLogoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) secondaryLogoUri = it
    }
    val backgroundImagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) backgroundImageUri = it
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Application Branding & Logos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CivilGreenDark)

        OutlinedTextField(
            value = appName,
            onValueChange = { appName = it },
            label = { Text("App Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("master_branding_app_name")
        )

        OutlinedTextField(
            value = primaryColorHex,
            onValueChange = { primaryColorHex = it },
            label = { Text("Primary Brand Color (Hex)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = footerText,
            onValueChange = { footerText = it },
            label = { Text("Public Footer Text") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Font Color Theme (Black / White)
        Text("Font Color Theme", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CivilGreenDark)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { fontColor = "#000000" }
            ) {
                RadioButton(
                    selected = fontColor == "#000000",
                    onClick = { fontColor = "#000000" }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Black Text", fontSize = 13.sp, color = Color.Black)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { fontColor = "#FFFFFF" }
            ) {
                RadioButton(
                    selected = fontColor == "#FFFFFF",
                    onClick = { fontColor = "#FFFFFF" }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("White Text", fontSize = 13.sp, color = Color.DarkGray)
            }
        }

        // Logo & Background Image Uploaders
        Text("Logos & Background Image", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CivilGreenDark)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { primaryLogoPicker.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (primaryLogoUri != null) "Primary *" else "Primary Logo", fontSize = 11.sp)
            }

            OutlinedButton(
                onClick = { secondaryLogoPicker.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (secondaryLogoUri != null) "Secondary *" else "Sec Logo", fontSize = 11.sp)
            }

            OutlinedButton(
                onClick = { backgroundImagePicker.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (backgroundImageUri != null) "BG Picked *" else "Background", fontSize = 11.sp)
            }
        }

        HorizontalDivider(color = CivilBorder.copy(alpha = 0.5f))

        Text("Navigation & Layout Toggles", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CivilGreenDark)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show Floating Assistant Button", fontSize = 14.sp)
            Switch(
                checked = floatingChatEnabled,
                onCheckedChange = { floatingChatEnabled = it },
                colors = SwitchDefaults.colors(checkedThumbColor = CivilGreenPrimary)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Family Navigation Tab", fontSize = 14.sp)
            Switch(
                checked = showFamilyTab,
                onCheckedChange = { showFamilyTab = it },
                colors = SwitchDefaults.colors(checkedThumbColor = CivilGreenPrimary)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Workers Navigation Tab", fontSize = 14.sp)
            Switch(
                checked = showWorkersTab,
                onCheckedChange = { showWorkersTab = it },
                colors = SwitchDefaults.colors(checkedThumbColor = CivilGreenPrimary)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Other Services Navigation Tab", fontSize = 14.sp)
            Switch(
                checked = showOtherServicesTab,
                onCheckedChange = { showOtherServicesTab = it },
                colors = SwitchDefaults.colors(checkedThumbColor = CivilGreenPrimary)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                onSave(
                    appName,
                    primaryColorHex,
                    footerText,
                    floatingChatEnabled,
                    showFamilyTab,
                    showWorkersTab,
                    showOtherServicesTab,
                    primaryLogoUri,
                    secondaryLogoUri,
                    backgroundImageUri,
                    fontColor
                )
                Toast.makeText(context, "Branding updated successfully", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("master_save_branding_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
        ) {
            Text("Save Branding Changes", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// ---------------------- PROFILE TAB ----------------------
@Composable
private fun MasterProfileSection(
    profile: ProfileEntity?,
    onSave: (ProfileEntity, Uri?) -> Unit
) {
    val context = LocalContext.current
    var fullName by remember(profile) { mutableStateOf(profile?.fullName ?: "") }
    var arabicName by remember(profile) { mutableStateOf(profile?.arabicName ?: "") }
    var idNumber by remember(profile) { mutableStateOf(profile?.idNumber ?: "") }
    var birthCity by remember(profile) { mutableStateOf(profile?.birthCity ?: "") }
    var birthCountry by remember(profile) { mutableStateOf(profile?.birthCountry ?: "") }
    var dob by remember(profile) { mutableStateOf(profile?.dateOfBirth ?: "") }
    var maritalStatus by remember(profile) { mutableStateOf(profile?.maritalStatus ?: "") }
    var religion by remember(profile) { mutableStateOf(profile?.religion ?: "") }
    var travelStatus by remember(profile) { mutableStateOf(profile?.travelStatus ?: "") }
    var sponsorName by remember(profile) { mutableStateOf(profile?.sponsorName ?: "") }
    var sponsorId by remember(profile) { mutableStateOf(profile?.sponsorId ?: "") }
    var insIssue by remember(profile) { mutableStateOf(profile?.insuranceIssuingDate ?: "") }
    var insExpiry by remember(profile) { mutableStateOf(profile?.insuranceExpiryDate ?: "") }
    var hajjStatus by remember(profile) { mutableStateOf(profile?.hajjStatus ?: "") }
    var passportNo by remember(profile) { mutableStateOf(profile?.passportNumber ?: "") }
    var passportIssue by remember(profile) { mutableStateOf(profile?.passportIssueDate ?: "") }
    var passportExpiry by remember(profile) { mutableStateOf(profile?.passportExpiryDate ?: "") }
    var residentNo by remember(profile) { mutableStateOf(profile?.residentIdNumber ?: "") }
    var residentIssue by remember(profile) { mutableStateOf(profile?.residentIssueDate ?: "") }
    var residentExpiry by remember(profile) { mutableStateOf(profile?.residentExpiryDate ?: "") }
    var profession by remember(profile) { mutableStateOf(profile?.profession ?: "") }
    var workLocation by remember(profile) { mutableStateOf(profile?.workLocation ?: "") }

    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) avatarUri = it
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Profile & Identity Editor", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CivilGreenDark)

        OutlinedButton(
            onClick = { avatarPicker.launch("image/*") },
            modifier = Modifier.fillMaxWidth().testTag("master_pick_avatar")
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (avatarUri != null) "New Profile Photo Selected" else "Change Profile Photo")
        }

        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name (English)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = arabicName, onValueChange = { arabicName = it }, label = { Text("Full Name (Arabic)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = idNumber, onValueChange = { idNumber = it }, label = { Text("Civil ID Number") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = profession, onValueChange = { profession = it }, label = { Text("Profession / Occupation") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = workLocation, onValueChange = { workLocation = it }, label = { Text("Work Location / Region") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = birthCountry, onValueChange = { birthCountry = it }, label = { Text("Nationality / Country") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = dob, onValueChange = { dob = it }, label = { Text("Date of Birth") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = maritalStatus, onValueChange = { maritalStatus = it }, label = { Text("Marital Status") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = religion, onValueChange = { religion = it }, label = { Text("Religion") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = travelStatus, onValueChange = { travelStatus = it }, label = { Text("Travel Status (e.g. Inside / Outside)") }, modifier = Modifier.fillMaxWidth())

        HorizontalDivider(color = CivilBorder.copy(alpha = 0.5f))
        Text("Sponsorship & Insurance", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CivilGreenDark)

        OutlinedTextField(value = sponsorName, onValueChange = { sponsorName = it }, label = { Text("Sponsor Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = sponsorId, onValueChange = { sponsorId = it }, label = { Text("Sponsor ID Number") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = insIssue, onValueChange = { insIssue = it }, label = { Text("Insurance Issuing Date") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = insExpiry, onValueChange = { insExpiry = it }, label = { Text("Insurance Expiry Date") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = hajjStatus, onValueChange = { hajjStatus = it }, label = { Text("Hajj Status Badge") }, modifier = Modifier.fillMaxWidth())

        HorizontalDivider(color = CivilBorder.copy(alpha = 0.5f))
        Text("Passport & Residency Dates", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CivilGreenDark)

        OutlinedTextField(value = passportNo, onValueChange = { passportNo = it }, label = { Text("Passport Number") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = passportIssue, onValueChange = { passportIssue = it }, label = { Text("Passport Issue Date") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = passportExpiry, onValueChange = { passportExpiry = it }, label = { Text("Passport Expiry Date") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = residentNo, onValueChange = { residentNo = it }, label = { Text("Resident ID Number") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = residentIssue, onValueChange = { residentIssue = it }, label = { Text("Resident Issue Date") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = residentExpiry, onValueChange = { residentExpiry = it }, label = { Text("Resident Expiry Date") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val updated = (profile ?: ProfileEntity()).copy(
                    fullName = fullName,
                    arabicName = arabicName,
                    idNumber = idNumber,
                    profession = profession,
                    workLocation = workLocation,
                    birthCountry = birthCountry,
                    dateOfBirth = dob,
                    maritalStatus = maritalStatus,
                    religion = religion,
                    travelStatus = travelStatus,
                    sponsorName = sponsorName,
                    sponsorId = sponsorId,
                    insuranceIssuingDate = insIssue,
                    insuranceExpiryDate = insExpiry,
                    hajjStatus = hajjStatus,
                    passportNumber = passportNo,
                    passportIssueDate = passportIssue,
                    passportExpiryDate = passportExpiry,
                    residentIdNumber = residentNo,
                    residentIssueDate = residentIssue,
                    residentExpiryDate = residentExpiry
                )
                onSave(updated, avatarUri)
                Toast.makeText(context, "Profile details saved", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("master_save_profile_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
        ) {
            Text("Save Profile Changes", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// ---------------------- SERVICES TAB ----------------------
@Composable
private fun MasterServicesSection(
    services: List<ServiceEntity>,
    onAddOrUpdate: (ServiceEntity) -> Unit,
    onDelete: (ServiceEntity) -> Unit,
    onToggle: (ServiceEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingService by remember { mutableStateOf<ServiceEntity?>(null) }

    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var iconName by remember { mutableStateOf("badge") }
    var category by remember { mutableStateOf("MY_SERVICES") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Configured Services (${services.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CivilGreenDark)

            Button(
                onClick = {
                    editingService = null
                    title = ""
                    desc = ""
                    iconName = "badge"
                    category = "MY_SERVICES"
                    showDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("master_add_service_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Service", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(services) { s ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CivilCardBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(CivilGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(IconMapper.getIcon(s.iconName), contentDescription = null, tint = CivilGreenPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(s.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CivilTextPrimary)
                            Text("${s.category} • Icon: ${s.iconName}", fontSize = 11.sp, color = CivilTextSecondary)
                        }
                        Switch(
                            checked = s.isEnabled,
                            onCheckedChange = { onToggle(s) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CivilGreenPrimary)
                        )
                        IconButton(onClick = { onDelete(s) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F))
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editingService == null) "Add New Service" else "Edit Service", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Service Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = iconName, onValueChange = { iconName = it }, label = { Text("Icon Name (badge, flight, car, etc.)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (PUBLIC, MY_SERVICES, OTHER_SERVICES)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val item = editingService?.copy(title = title, description = desc, iconName = iconName, category = category)
                                ?: ServiceEntity(title = title, description = desc, iconName = iconName, category = category)
                            onAddOrUpdate(item)
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ---------------------- QUICK ACCESS TAB ----------------------
@Composable
private fun MasterQuickAccessSection(
    items: List<QuickAccessItemEntity>,
    onAddOrUpdate: (QuickAccessItemEntity) -> Unit,
    onDelete: (QuickAccessItemEntity) -> Unit,
    onToggle: (QuickAccessItemEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var iconName by remember { mutableStateOf("directions_car") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Quick Access Cards (${items.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CivilGreenDark)

            Button(
                onClick = {
                    title = ""
                    subtitle = ""
                    iconName = "directions_car"
                    showDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Card", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CivilCardBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(CivilGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(IconMapper.getIcon(item.iconName), contentDescription = null, tint = CivilGreenPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(item.subtitle, fontSize = 11.sp, color = CivilTextSecondary, maxLines = 1)
                        }
                        Switch(
                            checked = item.isEnabled,
                            onCheckedChange = { onToggle(item) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CivilGreenPrimary)
                        )
                        IconButton(onClick = { onDelete(item) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F))
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Quick Access Card", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Subtitle") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = iconName, onValueChange = { iconName = it }, label = { Text("Icon Name (car, flight, auth, etc.)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onAddOrUpdate(QuickAccessItemEntity(title = title, subtitle = subtitle, iconName = iconName))
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
                ) {
                    Text("Add Card", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ---------------------- DOCUMENTS TAB ----------------------
@Composable
private fun MasterDocumentsSection(
    documents: List<DocumentEntity>,
    onAdd: (title: String, number: String, category: String, issueDate: String, expiryDate: String, notes: String, fileUri: Uri?) -> Unit,
    onDelete: (DocumentEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("RESIDENT_ID") }
    var issueDate by remember { mutableStateOf("01/01/2024") }
    var expiryDate by remember { mutableStateOf("01/01/2029") }
    var notes by remember { mutableStateOf("") }
    var fileUri by remember { mutableStateOf<Uri?>(null) }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) fileUri = it
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Stored Documents (${documents.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CivilGreenDark)

            Button(
                onClick = {
                    title = ""
                    number = ""
                    category = "RESIDENT_ID"
                    fileUri = null
                    showDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Document", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(documents) { doc ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CivilCardBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(doc.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("No: ${doc.documentNumber} • Cat: ${doc.category}", fontSize = 12.sp, color = CivilTextSecondary)
                            if (doc.expiryDate.isNotBlank()) {
                                Text("Expires: ${doc.expiryDate}", fontSize = 11.sp, color = CivilTextMuted)
                            }
                        }
                        IconButton(onClick = { onDelete(doc) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F))
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Document Record", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Document Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = number, onValueChange = { number = it }, label = { Text("Document Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (RESIDENT_ID, PASSPORT, VISA, etc.)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = expiryDate, onValueChange = { expiryDate = it }, label = { Text("Expiry Date") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    OutlinedButton(
                        onClick = { filePicker.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (fileUri != null) "File Attached" else "Attach Local Image/File")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && number.isNotBlank()) {
                            onAdd(title, number, category, issueDate, expiryDate, notes, fileUri)
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
                ) {
                    Text("Save Document", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ---------------------- USERS TAB ----------------------
@Composable
private fun MasterUsersSection(
    users: List<UserEntity>,
    onCreateUser: (username: String, pass: String, displayName: String, perms: List<String>, onDone: (Boolean) -> Unit) -> Unit,
    onToggleEnabled: (userId: Long, enabled: Boolean) -> Unit,
    onUpdatePassword: (userId: Long, newPass: String) -> Unit,
    onDeleteUser: (UserEntity) -> Unit
) {
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var newDisplayName by remember { mutableStateOf("") }

    // Permissions checkboxes
    var permViewDocs by remember { mutableStateOf(true) }
    var permAddDocs by remember { mutableStateOf(false) }
    var permEditDocs by remember { mutableStateOf(false) }
    var permDeleteDocs by remember { mutableStateOf(false) }
    var permViewProfile by remember { mutableStateOf(true) }
    var permEditProfile by remember { mutableStateOf(false) }
    var permViewServices by remember { mutableStateOf(true) }
    var permUseServices by remember { mutableStateOf(true) }
    var permSearch by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("User Management (${users.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CivilGreenDark)

            Button(
                onClick = {
                    newUsername = ""
                    newPassword = ""
                    newDisplayName = ""
                    showCreateDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create User", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CivilCardBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(CivilGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (user.role == "MASTER") Icons.Default.Security else Icons.Default.Person,
                                contentDescription = null,
                                tint = CivilGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${user.username} • Role: ${user.role}", fontSize = 12.sp, color = CivilTextSecondary)
                        }
                        if (user.role != "MASTER") {
                            Switch(
                                checked = user.isEnabled,
                                onCheckedChange = { onToggleEnabled(user.id, it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = CivilGreenPrimary)
                            )
                            IconButton(onClick = { onDeleteUser(user) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F))
                            }
                        } else {
                            Surface(shape = RoundedCornerShape(8.dp), color = CivilGreenLight) {
                                Text("MASTER", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CivilGreenDark)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Normal User", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(value = newUsername, onValueChange = { newUsername = it }, label = { Text("Username") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = newDisplayName, onValueChange = { newDisplayName = it }, label = { Text("Display Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    Text("Granular Permissions:", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = permViewDocs, onCheckedChange = { permViewDocs = it })
                        Text("View Documents", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = permAddDocs, onCheckedChange = { permAddDocs = it })
                        Text("Add Documents", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = permEditDocs, onCheckedChange = { permEditDocs = it })
                        Text("Edit Documents", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = permDeleteDocs, onCheckedChange = { permDeleteDocs = it })
                        Text("Delete Documents", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = permViewProfile, onCheckedChange = { permViewProfile = it })
                        Text("View Profile", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = permEditProfile, onCheckedChange = { permEditProfile = it })
                        Text("Edit Profile", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = permViewServices, onCheckedChange = { permViewServices = it })
                        Text("View Services", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = permUseServices, onCheckedChange = { permUseServices = it })
                        Text("Use Services", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = permSearch, onCheckedChange = { permSearch = it })
                        Text("Search Access", fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newUsername.isNotBlank() && newPassword.length >= 4) {
                            val perms = mutableListOf<String>()
                            if (permViewDocs) perms.add(PermissionKeys.VIEW_DOCUMENTS)
                            if (permAddDocs) perms.add(PermissionKeys.ADD_DOCUMENTS)
                            if (permEditDocs) perms.add(PermissionKeys.EDIT_DOCUMENTS)
                            if (permDeleteDocs) perms.add(PermissionKeys.DELETE_DOCUMENTS)
                            if (permViewProfile) perms.add(PermissionKeys.VIEW_PROFILE)
                            if (permEditProfile) perms.add(PermissionKeys.EDIT_PROFILE)
                            if (permViewServices) perms.add(PermissionKeys.VIEW_SERVICES)
                            if (permUseServices) perms.add(PermissionKeys.USE_SERVICES)
                            if (permSearch) perms.add(PermissionKeys.SEARCH)

                            onCreateUser(newUsername, newPassword, newDisplayName.ifBlank { newUsername }, perms) { success ->
                                if (success) {
                                    Toast.makeText(context, "User created successfully", Toast.LENGTH_SHORT).show()
                                    showCreateDialog = false
                                } else {
                                    Toast.makeText(context, "Username already exists", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Enter valid credentials", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
                ) {
                    Text("Create", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ---------------------- BACKUP & TRANSFER TAB ----------------------
@Composable
private fun MasterBackupSection(
    onExport: (secret: String, onResult: (Result<String>) -> Unit) -> Unit,
    onRestore: (encryptedData: String, secret: String, onResult: (Result<Unit>) -> Unit) -> Unit,
    onResetApp: () -> Unit
) {
    val context = LocalContext.current
    var masterSecret by remember { mutableStateOf("") }
    var encryptedPayload by remember { mutableStateOf("") }
    var showResetConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Offline Encrypted Backup & Restore", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CivilGreenDark)

        Text(
            "Transfer your complete database (branding, profile, services, documents, quick access) offline between devices. All exported files are AES-GCM encrypted using your Master Password.",
            style = MaterialTheme.typography.bodySmall,
            color = CivilTextSecondary
        )

        OutlinedTextField(
            value = masterSecret,
            onValueChange = { masterSecret = it },
            label = { Text("Master Password for Encryption / Decryption") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("master_backup_password_input")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (masterSecret.isBlank()) {
                        Toast.makeText(context, "Enter Master Password first", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    onExport(masterSecret) { result ->
                        result.onSuccess { payload ->
                            encryptedPayload = payload
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Civil Backup", payload))
                            Toast.makeText(context, "Encrypted backup copied to clipboard!", Toast.LENGTH_LONG).show()
                        }.onFailure {
                            Toast.makeText(context, "Export failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.weight(1f).height(46.dp).testTag("master_export_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export Backup", fontSize = 13.sp, color = Color.White)
            }

            OutlinedButton(
                onClick = {
                    if (masterSecret.isBlank()) {
                        Toast.makeText(context, "Enter Master Password first", Toast.LENGTH_SHORT).show()
                        return@OutlinedButton
                    }
                    if (encryptedPayload.isBlank()) {
                        Toast.makeText(context, "Paste encrypted backup payload below", Toast.LENGTH_SHORT).show()
                        return@OutlinedButton
                    }
                    onRestore(encryptedPayload, masterSecret) { result ->
                        result.onSuccess {
                            Toast.makeText(context, "Backup restored successfully!", Toast.LENGTH_LONG).show()
                        }.onFailure {
                            Toast.makeText(context, "Restore failed: Invalid key or corrupt data", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.weight(1f).height(46.dp).testTag("master_restore_button"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CivilGreenPrimary)
            ) {
                Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Import Backup", fontSize = 13.sp)
            }
        }

        OutlinedTextField(
            value = encryptedPayload,
            onValueChange = { encryptedPayload = it },
            label = { Text("Encrypted Backup Payload (Base64)") },
            placeholder = { Text("Paste exported encrypted backup code here") },
            modifier = Modifier.fillMaxWidth().height(140.dp).testTag("master_backup_payload_input"),
            maxLines = 6
        )

        HorizontalDivider(color = CivilBorder.copy(alpha = 0.5f))

        Text("Danger Zone", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFD32F2F))

        Button(
            onClick = { showResetConfirm = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("master_reset_app_button")
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reset App to Factory Defaults", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset Entire Application?", fontWeight = FontWeight.Bold) },
            text = {
                Text("This will wipe all custom configurations, local users, and restore initial seed data. Are you sure?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirm = false
                        onResetApp()
                        Toast.makeText(context, "Application reset", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Yes, Reset Everything", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
