package com.example.data.repository

import com.example.data.dao.CivilDao
import com.example.data.entity.AppBrandingEntity
import com.example.data.entity.DocumentEntity
import com.example.data.entity.FamilyMemberEntity
import com.example.data.entity.PermissionKeys
import com.example.data.entity.ProfileEntity
import com.example.data.entity.QuickAccessItemEntity
import com.example.data.entity.ServiceEntity
import com.example.data.entity.TravelRecordEntity
import com.example.data.entity.UserEntity
import com.example.data.entity.UserPermissionEntity
import com.example.data.entity.WorkerEntity
import com.example.security.SecurityManager
import kotlinx.coroutines.flow.Flow

class CivilRepository(private val dao: CivilDao) {

    val brandingFlow: Flow<AppBrandingEntity?> = dao.getBrandingFlow()
    val profileFlow: Flow<ProfileEntity?> = dao.getProfileFlow()
    val allServicesFlow: Flow<List<ServiceEntity>> = dao.getAllServicesFlow()
    val enabledServicesFlow: Flow<List<ServiceEntity>> = dao.getEnabledServicesFlow()
    val quickAccessFlow: Flow<List<QuickAccessItemEntity>> = dao.getEnabledQuickAccessFlow()
    val allQuickAccessFlow: Flow<List<QuickAccessItemEntity>> = dao.getAllQuickAccessFlow()
    val allDocumentsFlow: Flow<List<DocumentEntity>> = dao.getAllDocumentsFlow()
    val familyMembersFlow: Flow<List<FamilyMemberEntity>> = dao.getAllFamilyMembersFlow()
    val workersFlow: Flow<List<WorkerEntity>> = dao.getAllWorkersFlow()
    val travelRecordsFlow: Flow<List<TravelRecordEntity>> = dao.getAllTravelRecordsFlow()
    val allUsersFlow: Flow<List<UserEntity>> = dao.getAllUsersFlow()

    suspend fun getBranding(): AppBrandingEntity? = dao.getBranding()
    suspend fun updateBranding(branding: AppBrandingEntity) = dao.insertOrUpdateBranding(branding)

    suspend fun getProfile(): ProfileEntity? = dao.getProfile()
    suspend fun updateProfile(profile: ProfileEntity) = dao.insertOrUpdateProfile(profile)

    suspend fun getMasterUser(): UserEntity? = dao.getMasterUser()
    suspend fun getUserByUsername(username: String): UserEntity? = dao.getUserByUsername(username)
    suspend fun getUserById(id: Long): UserEntity? = dao.getUserById(id)

    suspend fun getUserPermissions(userId: Long): List<UserPermissionEntity> = dao.getUserPermissions(userId)
    fun getUserPermissionsFlow(userId: Long): Flow<List<UserPermissionEntity>> = dao.getUserPermissionsFlow(userId)

    suspend fun createUser(
        username: String,
        password: String,
        displayName: String,
        role: String,
        permissions: List<String> = emptyList()
    ): Long {
        val salt = SecurityManager.generateSalt()
        val passwordHash = SecurityManager.hashPassword(password, salt)
        val user = UserEntity(
            username = username.trim(),
            passwordHash = passwordHash,
            salt = salt,
            role = role,
            displayName = displayName.trim()
        )
        val userId = dao.insertUser(user)
        if (role == "USER" && permissions.isNotEmpty()) {
            val permEntities = permissions.map { key ->
                UserPermissionEntity(userId = userId, permissionKey = key, isGranted = true)
            }
            dao.insertPermissions(permEntities)
        }
        return userId
    }

    suspend fun updateUserPassword(userId: Long, newPassword: String) {
        val user = dao.getUserById(userId) ?: return
        val newSalt = SecurityManager.generateSalt()
        val newHash = SecurityManager.hashPassword(newPassword, newSalt)
        dao.updateUser(user.copy(passwordHash = newHash, salt = newSalt))
    }

    suspend fun toggleUserEnabled(userId: Long, enabled: Boolean) {
        val user = dao.getUserById(userId) ?: return
        dao.updateUser(user.copy(isEnabled = enabled))
    }

    suspend fun updateUserPermissions(userId: Long, grantedKeys: List<String>) {
        dao.clearUserPermissions(userId)
        val entities = grantedKeys.map { key ->
            UserPermissionEntity(userId = userId, permissionKey = key, isGranted = true)
        }
        dao.insertPermissions(entities)
    }

    suspend fun deleteUser(user: UserEntity) {
        if (user.role != "MASTER") {
            dao.clearUserPermissions(user.id)
            dao.deleteUser(user)
        }
    }

    // Services
    suspend fun addService(service: ServiceEntity): Long = dao.insertService(service)
    suspend fun updateService(service: ServiceEntity) = dao.updateService(service)
    suspend fun deleteService(service: ServiceEntity) = dao.deleteService(service)

    // Quick access
    suspend fun addQuickAccess(item: QuickAccessItemEntity): Long = dao.insertQuickAccess(item)
    suspend fun updateQuickAccess(item: QuickAccessItemEntity) = dao.updateQuickAccess(item)
    suspend fun deleteQuickAccess(item: QuickAccessItemEntity) = dao.deleteQuickAccess(item)

    // Documents
    suspend fun addDocument(doc: DocumentEntity): Long = dao.insertDocument(doc)
    suspend fun updateDocument(doc: DocumentEntity) = dao.updateDocument(doc)
    suspend fun deleteDocument(doc: DocumentEntity) = dao.deleteDocument(doc)

    // Family
    suspend fun addFamilyMember(member: FamilyMemberEntity): Long = dao.insertFamilyMember(member)
    suspend fun deleteFamilyMember(member: FamilyMemberEntity) = dao.deleteFamilyMember(member)

    // Workers
    suspend fun addWorker(worker: WorkerEntity): Long = dao.insertWorker(worker)
    suspend fun deleteWorker(worker: WorkerEntity) = dao.deleteWorker(worker)

    // Travel Records
    suspend fun addTravelRecord(record: TravelRecordEntity): Long = dao.insertTravelRecord(record)
    suspend fun deleteTravelRecord(record: TravelRecordEntity) = dao.deleteTravelRecord(record)

    suspend fun seedInitialDataIfEmpty(
        masterId: String = "admin",
        masterPass: String = "admin123",
        profileName: String = "AZIZUL ISMAIL HOSSAIN O",
        appName: String = "Civil ID",
        isFirstTimeSetup: Boolean = false
    ) {
        val existingMaster = dao.getMasterUser()
        if (existingMaster == null) {
            val salt = SecurityManager.generateSalt()
            val hash = SecurityManager.hashPassword(masterPass, salt)
            dao.insertUser(
                UserEntity(
                    username = masterId.trim(),
                    passwordHash = hash,
                    salt = salt,
                    role = "MASTER",
                    displayName = profileName
                )
            )

            // Also create a demo normal user "user" / "user123" with default view permissions
            val userSalt = SecurityManager.generateSalt()
            val userHash = SecurityManager.hashPassword("user123", userSalt)
            val demoUserId = dao.insertUser(
                UserEntity(
                    username = "user",
                    passwordHash = userHash,
                    salt = userSalt,
                    role = "USER",
                    displayName = "Demo User"
                )
            )
            val demoPerms = listOf(
                PermissionKeys.VIEW_DOCUMENTS,
                PermissionKeys.VIEW_PROFILE,
                PermissionKeys.VIEW_SERVICES,
                PermissionKeys.SEARCH
            ).map { key ->
                UserPermissionEntity(userId = demoUserId, permissionKey = key, isGranted = true)
            }
            dao.insertPermissions(demoPerms)
        }

        val existingBranding = dao.getBranding()
        if (existingBranding == null) {
            dao.insertOrUpdateBranding(
                AppBrandingEntity(
                    appName = appName,
                    primaryColorHex = "#006848",
                    isSetupCompleted = !isFirstTimeSetup,
                    footerText = "All rights reserved. Secure Offline Portal"
                )
            )
        }

        val existingProfile = dao.getProfile()
        if (existingProfile == null) {
            dao.insertOrUpdateProfile(
                ProfileEntity(
                    fullName = profileName,
                    arabicName = "عزيزال اسلام حسين و",
                    idNumber = "2471999587",
                    birthCity = "-",
                    birthCountry = "Bangladesh",
                    dateOfBirth = "01/01/1981",
                    maritalStatus = "Married",
                    sponsorshipTransfers = 0,
                    religion = "Islam",
                    workPermit = "-",
                    biometricsCollected = "Yes",
                    travelStatus = "Inside",
                    sponsorName = "مؤسسة الضمان العربي للمقاولات",
                    sponsorId = "7004899147",
                    insuranceIssuingDate = "18/06/2026",
                    insuranceExpiryDate = "17/06/2027",
                    bloodType = "-",
                    hajjStatus = "Eligible for Hajj",
                    lastHajjYear = "-",
                    passportDepositAmount = "SAR 00.0",
                    passportNumber = "A20144174",
                    passportType = "Normal",
                    passportIssueDate = "03/09/2025",
                    passportExpiryDate = "02/09/2035",
                    passportIssueCity = "113",
                    passportStatus = "-",
                    residentIdNumber = "2471999587",
                    residentIdVersion = "1",
                    residentIssueDate = "23/09/2019",
                    residentExpiryDate = "08/05/2027",
                    profession = "دهان",
                    workLocation = "منطقة الرياض"
                )
            )
        }

        // Seed Services if none exist
        val initialServices = listOf(
            // Public Landing (Screenshot 1)
            ServiceEntity(title = "Manage Digital Identity", description = "Access and configure identity", iconName = "badge", category = "PUBLIC", displayOrder = 1, targetScreen = "DIGITAL_DOCUMENT_DETAIL"),
            ServiceEntity(title = "Travel for Visitor", description = "Gulf and international travel", iconName = "flight", category = "PUBLIC", displayOrder = 2, targetScreen = "TRAVEL_REQUESTS"),
            ServiceEntity(title = "Civil Appointments", description = "Book and view appointments", iconName = "calendar", category = "PUBLIC", displayOrder = 3, targetScreen = "MY_SERVICES"),
            ServiceEntity(title = "Authentication Services", description = "Biometrics & verified auth", iconName = "fingerprint", category = "PUBLIC", displayOrder = 4, targetScreen = "MY_SERVICES"),
            ServiceEntity(title = "View Digital Documents", description = "Check your stored documents", iconName = "folder", category = "PUBLIC", displayOrder = 5, targetScreen = "MY_PROFILE"),

            // My Services (Screenshot 4)
            ServiceEntity(title = "Register Newborn", description = "Civil registration of dependents", iconName = "child_care", category = "MY_SERVICES", displayOrder = 1, targetScreen = "FAMILY"),
            ServiceEntity(title = "Renew Driving License", description = "Fast renewal of civil license", iconName = "directions_car", category = "MY_SERVICES", displayOrder = 2, targetScreen = "MY_DRIVING_LICENSE"),
            ServiceEntity(title = "Renew Resident ID", description = "Extend residency documentation", iconName = "badge", category = "MY_SERVICES", displayOrder = 3, targetScreen = "MY_RESIDENT_ID"),
            ServiceEntity(title = "Authentication Services", description = "Manage digital authorization keys", iconName = "fingerprint", category = "MY_SERVICES", displayOrder = 4, targetScreen = "MY_SERVICES"),
            ServiceEntity(title = "Change Resident Photo", description = "Update photo biometrics", iconName = "photo_camera", category = "MY_SERVICES", displayOrder = 5, targetScreen = "MY_PROFILE"),
            ServiceEntity(title = "Report Minor Accident", description = "Vehicle incident documentation", iconName = "car_crash", category = "MY_SERVICES", displayOrder = 6, targetScreen = "MY_SERVICES"),

            // Other Services (Screenshot 7)
            ServiceEntity(title = "Manage Appointments", description = "Civil service visit reservations", iconName = "calendar", category = "OTHER_SERVICES", displayOrder = 1, targetScreen = "MY_SERVICES"),
            ServiceEntity(title = "Document Delivery", description = "Postal and physical dispatch", iconName = "local_shipping", category = "OTHER_SERVICES", displayOrder = 2, targetScreen = "MY_SERVICES"),
            ServiceEntity(title = "App Travel", description = "Border and transit passes", iconName = "flight", category = "OTHER_SERVICES", displayOrder = 3, targetScreen = "TRAVEL_REQUESTS"),
            ServiceEntity(title = "Manage Authorizations", description = "Power of attorney and delegation", iconName = "description", category = "OTHER_SERVICES", displayOrder = 4, targetScreen = "MY_SERVICES"),
            ServiceEntity(title = "Donate with Furijat", description = "Community welfare support", iconName = "volunteer_activism", category = "OTHER_SERVICES", displayOrder = 5, targetScreen = "MY_SERVICES"),
            ServiceEntity(title = "Donate with Ehsan", description = "Charity and community assistance", iconName = "favorite", category = "OTHER_SERVICES", displayOrder = 6, targetScreen = "MY_SERVICES"),
            ServiceEntity(title = "Manage Visit Visa", description = "Family and business permits", iconName = "card_travel", category = "OTHER_SERVICES", displayOrder = 7, targetScreen = "MY_VISA"),
            ServiceEntity(title = "Activation Sites", description = "Biometric self-service locations", iconName = "location_on", category = "OTHER_SERVICES", displayOrder = 8, targetScreen = "MY_SERVICES")
        )
        dao.insertServices(initialServices)

        // Seed Quick Access (Screenshot 3)
        val initialQuickAccess = listOf(
            QuickAccessItemEntity(
                title = "My Vehicles",
                subtitle = "View details, renew documents, report accidents, and much more.",
                iconName = "directions_car",
                displayOrder = 1,
                targetScreen = "MY_SERVICES"
            ),
            QuickAccessItemEntity(
                title = "Authentication Services",
                subtitle = "Biometric verification and secure access token confirmation.",
                iconName = "fingerprint",
                displayOrder = 2,
                targetScreen = "MY_SERVICES"
            ),
            QuickAccessItemEntity(
                title = "App Travel",
                subtitle = "Explore Gulf country border crossings and travel permits.",
                iconName = "flight",
                displayOrder = 3,
                targetScreen = "TRAVEL_REQUESTS"
            )
        )
        dao.insertQuickAccessList(initialQuickAccess)

        // Seed default Digital Document (Resident ID)
        dao.insertDocument(
            DocumentEntity(
                title = "Resident Identity Card",
                documentNumber = "2471999587",
                category = "RESIDENT_ID",
                issueDate = "23/09/2019",
                expiryDate = "08/05/2027",
                notes = "Official Civil Resident Card",
                qrData = "CIVIL-ID:2471999587|NAME:AZIZUL ISMAIL HOSSAIN O|EXP:08/05/2027"
            )
        )
        dao.insertDocument(
            DocumentEntity(
                title = "Passport Document",
                documentNumber = "A20144174",
                category = "PASSPORT",
                issueDate = "03/09/2025",
                expiryDate = "02/09/2035",
                notes = "International Passport",
                qrData = "PASSPORT:A20144174|EXP:02/09/2035"
            )
        )
    }

    suspend fun resetAllData() {
        dao.clearAllServices()
        // Reset DB to default seed
        seedInitialDataIfEmpty(isFirstTimeSetup = true)
    }
}
