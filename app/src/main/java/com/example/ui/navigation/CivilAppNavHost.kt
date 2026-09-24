package com.example.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.entity.PermissionKeys
import com.example.ui.components.NavTab
import com.example.ui.screens.*
import com.example.ui.theme.CivilGreenPrimary
import com.example.ui.viewmodel.CivilViewModel
import kotlinx.coroutines.launch

object CivilRoutes {
    const val FIRST_TIME_SETUP = "first_time_setup"
    const val PUBLIC_LANDING = "public_landing"
    const val LOGIN = "login"
    const val HOME = "home"
    const val MY_SERVICES = "my_services"
    const val FAMILY = "family"
    const val WORKERS = "workers"
    const val OTHER_SERVICES = "other_services"
    const val MY_PROFILE = "my_profile"
    const val PERSONAL_DETAILS = "personal_details"
    const val PASSPORT = "passport"
    const val RESIDENT_ID = "resident_id"
    const val VISA = "visa"
    const val DRIVING_LICENSE = "driving_license"
    const val TRAVEL_REQUESTS = "travel_requests"
    const val LABOUR_IMPORTATIONS = "labour_importations"
    const val DIGITAL_DOC_DETAIL = "digital_doc_detail"
    const val MASTER_CONTROL = "master_control"
}

@Composable
fun CivilAppNavHost(
    viewModel: CivilViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val branding by viewModel.brandingState.collectAsState()
    val profile by viewModel.profileState.collectAsState()
    val allServices by viewModel.allServicesState.collectAsState()
    val quickAccess by viewModel.quickAccessState.collectAsState()
    val documents by viewModel.documentsState.collectAsState()
    val familyMembers by viewModel.familyState.collectAsState()
    val workers by viewModel.workersState.collectAsState()
    val travelRecords by viewModel.travelState.collectAsState()
    val users by viewModel.allUsersState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    var showMasterPasswordDialog by remember { mutableStateOf(false) }
    var masterPasswordInput by remember { mutableStateOf("") }
    var masterPasswordError by remember { mutableStateOf<String?>(null) }

    var showServiceDetailDialog by remember { mutableStateOf<String?>(null) }
    var showChatDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    // Check start destination
    val startDestination = if (branding?.isSetupCompleted == false) {
        CivilRoutes.FIRST_TIME_SETUP
    } else {
        CivilRoutes.PUBLIC_LANDING
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // First-Time Setup Screen
        composable(CivilRoutes.FIRST_TIME_SETUP) {
            ScreenFirstTimeSetup(
                onSetupCompleted = {
                    navController.navigate(CivilRoutes.HOME) {
                        popUpTo(CivilRoutes.FIRST_TIME_SETUP) { inclusive = true }
                    }
                },
                onCompleteSetup = { masterId, masterPass, appName, profName, photoUri ->
                    viewModel.completeFirstTimeSetup(masterId, masterPass, appName, profName, photoUri) {
                        navController.navigate(CivilRoutes.HOME) {
                            popUpTo(CivilRoutes.FIRST_TIME_SETUP) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Screen 1: Public Landing
        composable(CivilRoutes.PUBLIC_LANDING) {
            val publicServices = allServices.filter { it.category == "PUBLIC" && it.isEnabled }
            ScreenPublicLanding(
                appName = branding?.appName ?: "Civil ID",
                primaryLogoPath = branding?.primaryLogoPath ?: "",
                secondaryLogoPath = branding?.secondaryLogoPath ?: "",
                loginImagePath = branding?.loginImagePath ?: "",
                footerText = branding?.footerText ?: "All rights reserved.",
                publicServices = publicServices,
                onLoginClick = {
                    viewModel.clearLoginError()
                    navController.navigate(CivilRoutes.LOGIN)
                },
                onSettingsClick = {
                    showMasterPasswordDialog = true
                },
                onNotificationClick = {
                    showNotificationsDialog = true
                },
                onServiceClick = { service ->
                    showServiceDetailDialog = "${service.title}: Please log in with your Civil ID to access this service."
                }
            )
        }

        // Screen 2: Login
        composable(CivilRoutes.LOGIN) {
            ScreenLogin(
                primaryLogoPath = branding?.primaryLogoPath ?: "",
                loginImagePath = branding?.loginImagePath ?: "",
                errorMessage = loginError,
                onLoginSubmit = { user, pass ->
                    viewModel.login(user, pass) { success ->
                        if (success) {
                            navController.navigate(CivilRoutes.HOME) {
                                popUpTo(CivilRoutes.PUBLIC_LANDING) { inclusive = true }
                            }
                        }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Screen 3: Home
        composable(CivilRoutes.HOME) {
            ScreenHome(
                branding = branding,
                profile = profile,
                quickAccessItems = quickAccess.filter { it.isEnabled },
                documents = documents,
                onProfileClick = {
                    if (currentUser?.isMaster == true || currentUser?.hasPermission(PermissionKeys.VIEW_PROFILE) == true) {
                        navController.navigate(CivilRoutes.MY_PROFILE)
                    } else {
                        Toast.makeText(context, "You do not have permission to view profile", Toast.LENGTH_SHORT).show()
                    }
                },
                onDigitalDocClick = {
                    if (currentUser?.isMaster == true || currentUser?.hasPermission(PermissionKeys.VIEW_DOCUMENTS) == true) {
                        navController.navigate(CivilRoutes.DIGITAL_DOC_DETAIL)
                    } else {
                        Toast.makeText(context, "You do not have permission to view documents", Toast.LENGTH_SHORT).show()
                    }
                },
                onQuickAccessClick = { item ->
                    when {
                        item.title.contains("Vehicles", ignoreCase = true) -> navController.navigate(CivilRoutes.DRIVING_LICENSE)
                        item.title.contains("Travel", ignoreCase = true) -> navController.navigate(CivilRoutes.TRAVEL_REQUESTS)
                        else -> showServiceDetailDialog = "${item.title}: Active and authorized offline."
                    }
                },
                onSearchClick = {
                    navController.navigate(CivilRoutes.MY_SERVICES)
                },
                onSettingsClick = {
                    if (currentUser?.isMaster == true) {
                        navController.navigate(CivilRoutes.MASTER_CONTROL)
                    } else {
                        showMasterPasswordDialog = true
                    }
                },
                onNotificationClick = {
                    showNotificationsDialog = true
                },
                onChatClick = {
                    showChatDialog = true
                },
                onTabSelected = { tab ->
                    when (tab) {
                        NavTab.HOME -> {}
                        NavTab.MY_SERVICES -> navController.navigate(CivilRoutes.MY_SERVICES)
                        NavTab.FAMILY -> navController.navigate(CivilRoutes.FAMILY)
                        NavTab.WORKERS -> navController.navigate(CivilRoutes.WORKERS)
                        NavTab.OTHER_SERVICES -> navController.navigate(CivilRoutes.OTHER_SERVICES)
                    }
                }
            )
        }

        // Screen 4: My Services
        composable(CivilRoutes.MY_SERVICES) {
            ScreenMyServices(
                branding = branding,
                services = allServices.filter { it.isEnabled },
                onServiceClick = { s ->
                    showServiceDetailDialog = "${s.title}\n\n${s.description}"
                },
                onSettingsClick = {
                    if (currentUser?.isMaster == true) navController.navigate(CivilRoutes.MASTER_CONTROL)
                    else showMasterPasswordDialog = true
                },
                onNotificationClick = { showNotificationsDialog = true },
                onChatClick = { showChatDialog = true },
                onTabSelected = { tab ->
                    when (tab) {
                        NavTab.HOME -> navController.navigate(CivilRoutes.HOME)
                        NavTab.MY_SERVICES -> {}
                        NavTab.FAMILY -> navController.navigate(CivilRoutes.FAMILY)
                        NavTab.WORKERS -> navController.navigate(CivilRoutes.WORKERS)
                        NavTab.OTHER_SERVICES -> navController.navigate(CivilRoutes.OTHER_SERVICES)
                    }
                }
            )
        }

        // Screen 5: Family
        composable(CivilRoutes.FAMILY) {
            val canManage = currentUser?.isMaster == true || currentUser?.hasPermission(PermissionKeys.ADD_DOCUMENTS) == true
            ScreenFamily(
                branding = branding,
                familyMembers = familyMembers,
                onAddFamilyMember = { name, id, rel, dob ->
                    viewModel.addFamilyMember(name, id, rel, dob)
                },
                onDeleteMember = { viewModel.deleteFamilyMember(it) },
                onSettingsClick = {
                    if (currentUser?.isMaster == true) navController.navigate(CivilRoutes.MASTER_CONTROL)
                    else showMasterPasswordDialog = true
                },
                onNotificationClick = { showNotificationsDialog = true },
                onTabSelected = { tab ->
                    when (tab) {
                        NavTab.HOME -> navController.navigate(CivilRoutes.HOME)
                        NavTab.MY_SERVICES -> navController.navigate(CivilRoutes.MY_SERVICES)
                        NavTab.FAMILY -> {}
                        NavTab.WORKERS -> navController.navigate(CivilRoutes.WORKERS)
                        NavTab.OTHER_SERVICES -> navController.navigate(CivilRoutes.OTHER_SERVICES)
                    }
                },
                canManage = canManage
            )
        }

        // Screen 6: Workers
        composable(CivilRoutes.WORKERS) {
            val canManage = currentUser?.isMaster == true || currentUser?.hasPermission(PermissionKeys.ADD_DOCUMENTS) == true
            ScreenWorkers(
                branding = branding,
                workers = workers,
                onAddWorker = { name, id, prof, nat ->
                    viewModel.addWorker(name, id, prof, nat)
                },
                onDeleteWorker = { viewModel.deleteWorker(it) },
                onSettingsClick = {
                    if (currentUser?.isMaster == true) navController.navigate(CivilRoutes.MASTER_CONTROL)
                    else showMasterPasswordDialog = true
                },
                onNotificationClick = { showNotificationsDialog = true },
                onTabSelected = { tab ->
                    when (tab) {
                        NavTab.HOME -> navController.navigate(CivilRoutes.HOME)
                        NavTab.MY_SERVICES -> navController.navigate(CivilRoutes.MY_SERVICES)
                        NavTab.FAMILY -> navController.navigate(CivilRoutes.FAMILY)
                        NavTab.WORKERS -> {}
                        NavTab.OTHER_SERVICES -> navController.navigate(CivilRoutes.OTHER_SERVICES)
                    }
                },
                canManage = canManage
            )
        }

        // Screen 7: Other Services
        composable(CivilRoutes.OTHER_SERVICES) {
            ScreenOtherServices(
                branding = branding,
                services = allServices.filter { it.isEnabled },
                onServiceClick = { s ->
                    showServiceDetailDialog = "${s.title}\n\n${s.description}"
                },
                onSettingsClick = {
                    if (currentUser?.isMaster == true) navController.navigate(CivilRoutes.MASTER_CONTROL)
                    else showMasterPasswordDialog = true
                },
                onNotificationClick = { showNotificationsDialog = true },
                onChatClick = { showChatDialog = true },
                onTabSelected = { tab ->
                    when (tab) {
                        NavTab.HOME -> navController.navigate(CivilRoutes.HOME)
                        NavTab.MY_SERVICES -> navController.navigate(CivilRoutes.MY_SERVICES)
                        NavTab.FAMILY -> navController.navigate(CivilRoutes.FAMILY)
                        NavTab.WORKERS -> navController.navigate(CivilRoutes.WORKERS)
                        NavTab.OTHER_SERVICES -> {}
                    }
                }
            )
        }

        // Screen 8: My Profile
        composable(CivilRoutes.MY_PROFILE) {
            ScreenMyProfile(
                profile = profile,
                onBackClick = { navController.popBackStack() },
                onPersonalDetailsClick = { navController.navigate(CivilRoutes.PERSONAL_DETAILS) },
                onPassportClick = { navController.navigate(CivilRoutes.PASSPORT) },
                onResidentIdClick = { navController.navigate(CivilRoutes.RESIDENT_ID) },
                onVisaClick = { navController.navigate(CivilRoutes.VISA) },
                onDrivingLicenseClick = { navController.navigate(CivilRoutes.DRIVING_LICENSE) },
                onTravelRecordsClick = { navController.navigate(CivilRoutes.TRAVEL_REQUESTS) },
                onLabourImportationsClick = { navController.navigate(CivilRoutes.LABOUR_IMPORTATIONS) }
            )
        }

        // Screen 9: Personal Details
        composable(CivilRoutes.PERSONAL_DETAILS) {
            ScreenPersonalDetails(
                profile = profile,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Screen 10: Passport
        composable(CivilRoutes.PASSPORT) {
            ScreenPassport(
                profile = profile,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Screen 11: Resident ID
        composable(CivilRoutes.RESIDENT_ID) {
            ScreenResidentId(
                profile = profile,
                onBackClick = { navController.popBackStack() },
                onViewDigitalCard = { navController.navigate(CivilRoutes.DIGITAL_DOC_DETAIL) }
            )
        }

        // Screen 12: Visa
        composable(CivilRoutes.VISA) {
            val visas = documents.filter { it.category == "VISA" }
            val canManage = currentUser?.isMaster == true || currentUser?.hasPermission(PermissionKeys.ADD_DOCUMENTS) == true
            ScreenVisa(
                visaDocuments = visas,
                onAddVisa = { num, exp, sponsor ->
                    viewModel.addDocument("Exit & Re-entry Visa", num, "VISA", "01/01/2026", exp, sponsor, null)
                },
                onDeleteVisa = { viewModel.deleteDocument(it) },
                onBackClick = { navController.popBackStack() },
                canManage = canManage
            )
        }

        // Screen 13: Driving License
        composable(CivilRoutes.DRIVING_LICENSE) {
            val licenses = documents.filter { it.category == "DRIVING_LICENSE" }
            val canManage = currentUser?.isMaster == true || currentUser?.hasPermission(PermissionKeys.ADD_DOCUMENTS) == true
            ScreenDrivingLicense(
                licenses = licenses,
                onAddLicense = { num, catType, exp ->
                    viewModel.addDocument("Driving License", num, "DRIVING_LICENSE", "01/01/2020", exp, catType, null)
                },
                onDeleteLicense = { viewModel.deleteDocument(it) },
                onBackClick = { navController.popBackStack() },
                canManage = canManage
            )
        }

        // Screen 14: Travel Requests
        composable(CivilRoutes.TRAVEL_REQUESTS) {
            val canManage = currentUser?.isMaster == true || currentUser?.hasPermission(PermissionKeys.ADD_DOCUMENTS) == true
            ScreenTravelRequests(
                travelRecords = travelRecords,
                onCreateRequest = { dest, border, dep, ret ->
                    viewModel.addTravelRecord(dest, border, dep, ret)
                },
                onDeleteRequest = { viewModel.deleteTravelRecord(it) },
                onBackClick = { navController.popBackStack() },
                canManage = canManage
            )
        }

        // Screen 15: Labour Importations
        composable(CivilRoutes.LABOUR_IMPORTATIONS) {
            val labours = documents.filter { it.category == "LABOUR" }
            val canManage = currentUser?.isMaster == true || currentUser?.hasPermission(PermissionKeys.ADD_DOCUMENTS) == true
            ScreenLabourImportations(
                labourRecords = labours,
                onAddLabour = { visaNo, occ, nat ->
                    viewModel.addDocument("Labor Recruitment Permit", visaNo, "LABOUR", "01/01/2026", "01/01/2027", "$occ ($nat)", null)
                },
                onDeleteLabour = { viewModel.deleteDocument(it) },
                onBackClick = { navController.popBackStack() },
                canManage = canManage
            )
        }

        // Screen 16: Digital Document Detail (Full ID Card)
        composable(CivilRoutes.DIGITAL_DOC_DETAIL) {
            ScreenDigitalDocumentDetail(
                profile = profile,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Screen 17: Master Control
        composable(CivilRoutes.MASTER_CONTROL) {
            ScreenMasterControl(
                branding = branding,
                profile = profile,
                services = allServices,
                quickAccessItems = quickAccess,
                documents = documents,
                users = users,
                onUpdateBranding = { appName, color, footer, chat, fam, work, oth, pLogo, sLogo, bgUri, fColor ->
                    viewModel.updateBranding(appName, color, footer, chat, fam, work, oth, pLogo, sLogo, bgUri, fColor)
                },
                onUpdateProfile = { updated, uri ->
                    viewModel.updateProfile(updated, uri)
                },
                onAddOrUpdateService = { viewModel.addOrUpdateService(it) },
                onDeleteService = { viewModel.deleteService(it) },
                onToggleService = { viewModel.toggleService(it) },
                onAddOrUpdateQuickAccess = { viewModel.addOrUpdateQuickAccess(it) },
                onDeleteQuickAccess = { viewModel.deleteQuickAccess(it) },
                onToggleQuickAccess = { viewModel.toggleQuickAccess(it) },
                onAddDocument = { t, n, c, i, e, notes, uri ->
                    viewModel.addDocument(t, n, c, i, e, notes, uri)
                },
                onDeleteDocument = { viewModel.deleteDocument(it) },
                onCreateUser = { u, p, d, perms, onDone ->
                    viewModel.createNormalUser(u, p, d, perms, onDone)
                },
                onToggleUserEnabled = { id, enabled -> viewModel.toggleUserEnabled(id, enabled) },
                onUpdateUserPassword = { id, pass -> viewModel.updateUserPassword(id, pass) },
                onDeleteUser = { viewModel.deleteUser(it) },
                onExportBackup = { secret, onResult -> viewModel.exportBackup(secret, onResult) },
                onRestoreBackup = { payload, secret, onResult -> viewModel.restoreBackup(payload, secret, onResult) },
                onResetApp = {
                    viewModel.resetApp()
                    navController.navigate(CivilRoutes.PUBLIC_LANDING) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }

    // Master Password Prompt Dialog
    if (showMasterPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showMasterPasswordDialog = false
                masterPasswordInput = ""
                masterPasswordError = null
            },
            title = { Text("Master Control Authentication", fontWeight = FontWeight.Bold) },
            text = {
                androidx.compose.foundation.layout.Column {
                    Text(
                        "Enter Master Password to open administrative Master Control.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                    OutlinedTextField(
                        value = masterPasswordInput,
                        onValueChange = {
                            masterPasswordInput = it
                            masterPasswordError = null
                        },
                        label = { Text("Master Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("master_password_prompt_input")
                    )
                    if (masterPasswordError != null) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = masterPasswordError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val valid = viewModel.verifyMasterPassword(masterPasswordInput)
                            if (valid) {
                                showMasterPasswordDialog = false
                                masterPasswordInput = ""
                                masterPasswordError = null
                                navController.navigate(CivilRoutes.MASTER_CONTROL)
                            } else {
                                masterPasswordError = "Incorrect Master Password"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary),
                    modifier = Modifier.testTag("master_password_prompt_submit")
                ) {
                    Text("Enter Master Control", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showMasterPasswordDialog = false
                    masterPasswordInput = ""
                    masterPasswordError = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Service Detail Dialog
    if (showServiceDetailDialog != null) {
        AlertDialog(
            onDismissRequest = { showServiceDetailDialog = null },
            title = { Text("Service Information", fontWeight = FontWeight.Bold) },
            text = { Text(showServiceDetailDialog ?: "") },
            confirmButton = {
                Button(
                    onClick = { showServiceDetailDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }

    // Assistant / Help Dialog
    if (showChatDialog) {
        AlertDialog(
            onDismissRequest = { showChatDialog = false },
            title = { Text("Offline Civil Help Assistant", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Welcome to Civil Portal.\n\n" +
                    "• Offline Mode: All operations, cards, and documents are stored securely on this device.\n" +
                    "• Master Control: Tap the gear icon in the header to modify branding, documents, services, and accounts.\n" +
                    "• Digital Resident ID: Accessible directly from Home or My Profile screen."
                )
            },
            confirmButton = {
                Button(
                    onClick = { showChatDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = { Text("Notifications", fontWeight = FontWeight.Bold) },
            text = {
                Text("• Resident ID document is verified and active offline.\n• Health insurance valid until 17/06/2027.\n• No pending alerts or violations.")
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
                ) {
                    Text("Clear", color = Color.White)
                }
            }
        )
    }
}
