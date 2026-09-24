package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.CivilDatabase
import com.example.data.entity.*
import com.example.data.repository.CivilRepository
import com.example.security.BackupManager
import com.example.security.SecurityManager
import com.example.security.SessionUser
import com.example.storage.LocalFileManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CivilViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CivilRepository

    init {
        val db = CivilDatabase.getDatabase(application)
        repository = CivilRepository(db.civilDao())

        // Ensure database is initialized with seed data
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    val brandingState: StateFlow<AppBrandingEntity?> = repository.brandingFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val profileState: StateFlow<ProfileEntity?> = repository.profileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allServicesState: StateFlow<List<ServiceEntity>> = repository.allServicesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val enabledServicesState: StateFlow<List<ServiceEntity>> = repository.enabledServicesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quickAccessState: StateFlow<List<QuickAccessItemEntity>> = repository.quickAccessFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allQuickAccessState: StateFlow<List<QuickAccessItemEntity>> = repository.allQuickAccessFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val documentsState: StateFlow<List<DocumentEntity>> = repository.allDocumentsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val familyState: StateFlow<List<FamilyMemberEntity>> = repository.familyMembersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workersState: StateFlow<List<WorkerEntity>> = repository.workersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val travelState: StateFlow<List<TravelRecordEntity>> = repository.travelRecordsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsersState: StateFlow<List<UserEntity>> = repository.allUsersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentUser = MutableStateFlow<SessionUser?>(null)
    val currentUser: StateFlow<SessionUser?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    fun clearLoginError() {
        _loginError.value = null
    }

    fun login(usernameInput: String, passwordInput: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loginError.value = null
            val user = repository.getUserByUsername(usernameInput.trim())
            if (user == null) {
                _loginError.value = "User not found"
                onResult(false)
                return@launch
            }
            if (!user.isEnabled) {
                _loginError.value = "Account is disabled. Contact Master Administrator."
                onResult(false)
                return@launch
            }
            val valid = SecurityManager.verifyPassword(passwordInput, user.salt, user.passwordHash)
            if (!valid) {
                _loginError.value = "Invalid credentials"
                onResult(false)
                return@launch
            }

            val perms = if (user.role == "MASTER") {
                emptySet()
            } else {
                val dbPerms = repository.getUserPermissions(user.id)
                dbPerms.filter { it.isGranted }.map { it.permissionKey }.toSet()
            }

            _currentUser.value = SessionUser(
                id = user.id,
                username = user.username,
                displayName = user.displayName,
                role = user.role,
                permissions = perms
            )
            onResult(true)
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    suspend fun verifyMasterPassword(password: String): Boolean {
        val master = repository.getMasterUser() ?: return false
        return SecurityManager.verifyPassword(password, master.salt, master.passwordHash)
    }

    fun completeFirstTimeSetup(
        masterId: String,
        masterPass: String,
        appName: String,
        profileName: String,
        photoUri: Uri?,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            var localPhotoPath = ""
            if (photoUri != null) {
                localPhotoPath = LocalFileManager.saveUriToInternalStorage(
                    getApplication(),
                    photoUri,
                    "profile"
                ) ?: ""
            }

            repository.seedInitialDataIfEmpty(
                masterId = masterId,
                masterPass = masterPass,
                profileName = profileName,
                appName = appName,
                isFirstTimeSetup = true
            )

            // Update branding with setup complete
            val currentBranding = repository.getBranding() ?: AppBrandingEntity()
            repository.updateBranding(
                currentBranding.copy(
                    appName = appName.ifBlank { "Civil ID" },
                    profilePhotoPath = localPhotoPath,
                    isSetupCompleted = true
                )
            )

            // Update profile with name and photo
            val currentProfile = repository.getProfile() ?: ProfileEntity()
            repository.updateProfile(
                currentProfile.copy(
                    fullName = profileName.ifBlank { "AZIZUL ISMAIL HOSSAIN O" },
                    avatarPath = localPhotoPath
                )
            )

            // Auto-login as master
            val master = repository.getMasterUser()
            if (master != null) {
                _currentUser.value = SessionUser(
                    id = master.id,
                    username = master.username,
                    displayName = master.displayName,
                    role = "MASTER"
                )
            }

            onComplete()
        }
    }

    // Branding updates
    fun updateBranding(
        appName: String,
        primaryColorHex: String,
        footerText: String,
        floatingChatEnabled: Boolean,
        showFamilyTab: Boolean,
        showWorkersTab: Boolean,
        showOtherServicesTab: Boolean,
        primaryLogoUri: Uri? = null,
        secondaryLogoUri: Uri? = null,
        backgroundImageUri: Uri? = null,
        fontColor: String = "#000000"
    ) {
        viewModelScope.launch {
            val current = repository.getBranding() ?: AppBrandingEntity()
            var primaryLogoPath = current.primaryLogoPath
            var secondaryLogoPath = current.secondaryLogoPath
            var backgroundImagePath = current.backgroundImagePath

            if (primaryLogoUri != null) {
                LocalFileManager.saveUriToInternalStorage(getApplication(), primaryLogoUri, "primary_logo")?.let {
                    primaryLogoPath = it
                }
            }
            if (secondaryLogoUri != null) {
                LocalFileManager.saveUriToInternalStorage(getApplication(), secondaryLogoUri, "secondary_logo")?.let {
                    secondaryLogoPath = it
                }
            }
            if (backgroundImageUri != null) {
                LocalFileManager.saveUriToInternalStorage(getApplication(), backgroundImageUri, "background_image")?.let {
                    backgroundImagePath = it
                }
            }

            repository.updateBranding(
                current.copy(
                    appName = appName,
                    primaryColorHex = primaryColorHex,
                    footerText = footerText,
                    floatingChatEnabled = floatingChatEnabled,
                    showFamilyTab = showFamilyTab,
                    showWorkersTab = showWorkersTab,
                    showOtherServicesTab = showOtherServicesTab,
                    primaryLogoPath = primaryLogoPath,
                    secondaryLogoPath = secondaryLogoPath,
                    backgroundImagePath = backgroundImagePath,
                    fontColor = fontColor
                )
            )
        }
    }

    // Profile updates
    fun updateProfile(profile: ProfileEntity, newAvatarUri: Uri? = null) {
        viewModelScope.launch {
            var avatarPath = profile.avatarPath
            if (newAvatarUri != null) {
                LocalFileManager.saveUriToInternalStorage(getApplication(), newAvatarUri, "avatar")?.let {
                    avatarPath = it
                }
            }
            repository.updateProfile(profile.copy(avatarPath = avatarPath))
        }
    }

    // Services
    fun addOrUpdateService(service: ServiceEntity) {
        viewModelScope.launch {
            if (service.id == 0L) {
                repository.addService(service)
            } else {
                repository.updateService(service)
            }
        }
    }

    fun deleteService(service: ServiceEntity) {
        viewModelScope.launch {
            repository.deleteService(service)
        }
    }

    fun toggleService(service: ServiceEntity) {
        viewModelScope.launch {
            repository.updateService(service.copy(isEnabled = !service.isEnabled))
        }
    }

    // Quick Access
    fun addOrUpdateQuickAccess(item: QuickAccessItemEntity) {
        viewModelScope.launch {
            if (item.id == 0L) {
                repository.addQuickAccess(item)
            } else {
                repository.updateQuickAccess(item)
            }
        }
    }

    fun deleteQuickAccess(item: QuickAccessItemEntity) {
        viewModelScope.launch {
            repository.deleteQuickAccess(item)
        }
    }

    fun toggleQuickAccess(item: QuickAccessItemEntity) {
        viewModelScope.launch {
            repository.updateQuickAccess(item.copy(isEnabled = !item.isEnabled))
        }
    }

    // Documents
    fun addDocument(
        title: String,
        number: String,
        category: String,
        issueDate: String,
        expiryDate: String,
        notes: String,
        fileUri: Uri?
    ) {
        viewModelScope.launch {
            var filePath = ""
            if (fileUri != null) {
                filePath = LocalFileManager.saveUriToInternalStorage(getApplication(), fileUri, "doc") ?: ""
            }
            repository.addDocument(
                DocumentEntity(
                    title = title,
                    documentNumber = number,
                    category = category,
                    issueDate = issueDate,
                    expiryDate = expiryDate,
                    localFilePath = filePath,
                    notes = notes,
                    qrData = "DOC:$number|TITLE:$title|EXP:$expiryDate"
                )
            )
        }
    }

    fun deleteDocument(doc: DocumentEntity) {
        viewModelScope.launch {
            repository.deleteDocument(doc)
        }
    }

    // Family
    fun addFamilyMember(name: String, idNumber: String, relationship: String, birthDate: String) {
        viewModelScope.launch {
            repository.addFamilyMember(
                FamilyMemberEntity(
                    name = name,
                    idNumber = idNumber,
                    relationship = relationship,
                    birthDate = birthDate
                )
            )
        }
    }

    fun deleteFamilyMember(member: FamilyMemberEntity) {
        viewModelScope.launch {
            repository.deleteFamilyMember(member)
        }
    }

    // Workers
    fun addWorker(name: String, idNumber: String, profession: String, nationality: String) {
        viewModelScope.launch {
            repository.addWorker(
                WorkerEntity(
                    name = name,
                    idNumber = idNumber,
                    profession = profession,
                    nationality = nationality
                )
            )
        }
    }

    fun deleteWorker(worker: WorkerEntity) {
        viewModelScope.launch {
            repository.deleteWorker(worker)
        }
    }

    // Travel Records
    fun addTravelRecord(
        destination: String,
        borderPoint: String,
        departureDate: String,
        returnDate: String
    ) {
        viewModelScope.launch {
            val reqNum = "TRV-${System.currentTimeMillis().toString().takeLast(6)}"
            repository.addTravelRecord(
                TravelRecordEntity(
                    requestNumber = reqNum,
                    destination = destination,
                    borderPoint = borderPoint,
                    departureDate = departureDate,
                    returnDate = returnDate,
                    status = "Active"
                )
            )
        }
    }

    fun deleteTravelRecord(record: TravelRecordEntity) {
        viewModelScope.launch {
            repository.deleteTravelRecord(record)
        }
    }

    // User management
    fun createNormalUser(
        username: String,
        pass: String,
        displayName: String,
        permissions: List<String>,
        onDone: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.createUser(
                    username = username,
                    password = pass,
                    displayName = displayName,
                    role = "USER",
                    permissions = permissions
                )
                onDone(true)
            } catch (e: Exception) {
                onDone(false)
            }
        }
    }

    fun toggleUserEnabled(userId: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleUserEnabled(userId, enabled)
        }
    }

    fun updateUserPassword(userId: Long, newPass: String) {
        viewModelScope.launch {
            repository.updateUserPassword(userId, newPass)
        }
    }

    fun updateUserPermissions(userId: Long, grantedKeys: List<String>) {
        viewModelScope.launch {
            repository.updateUserPermissions(userId, grantedKeys)
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }

    // Encrypted Backup
    fun exportBackup(masterSecret: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            try {
                val encrypted = BackupManager.createEncryptedBackupJson(repository, masterSecret)
                onResult(Result.success(encrypted))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }

    fun restoreBackup(encryptedData: String, masterSecret: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = BackupManager.restoreFromEncryptedBackup(repository, encryptedData, masterSecret)
            onResult(res)
        }
    }

    fun resetApp() {
        viewModelScope.launch {
            repository.resetAllData()
            logout()
        }
    }
}
