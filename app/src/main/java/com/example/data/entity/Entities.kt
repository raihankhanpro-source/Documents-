package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val salt: String,
    val role: String, // "MASTER" or "USER"
    val displayName: String,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "user_permissions",
    indices = [Index(value = ["userId", "permissionKey"], unique = true)]
)
data class UserPermissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val permissionKey: String,
    val isGranted: Boolean = true
)

object PermissionKeys {
    const val VIEW_DOCUMENTS = "VIEW_DOCUMENTS"
    const val ADD_DOCUMENTS = "ADD_DOCUMENTS"
    const val EDIT_DOCUMENTS = "EDIT_DOCUMENTS"
    const val DELETE_DOCUMENTS = "DELETE_DOCUMENTS"
    const val VIEW_PROFILE = "VIEW_PROFILE"
    const val EDIT_PROFILE = "EDIT_PROFILE"
    const val VIEW_SERVICES = "VIEW_SERVICES"
    const val USE_SERVICES = "USE_SERVICES"
    const val SEARCH = "SEARCH"
}

@Entity(tableName = "app_branding")
data class AppBrandingEntity(
    @PrimaryKey val id: Int = 1,
    val appName: String = "Civil ID",
    val primaryLogoPath: String = "",
    val secondaryLogoPath: String = "",
    val loginImagePath: String = "",
    val profilePhotoPath: String = "",
    val primaryColorHex: String = "#006848",
    val footerText: String = "All rights reserved. Secure Offline Portal",
    val isSetupCompleted: Boolean = false,
    val floatingChatEnabled: Boolean = true,
    val showFamilyTab: Boolean = true,
    val showWorkersTab: Boolean = true,
    val showOtherServicesTab: Boolean = true,
    val backgroundImagePath: String = "",
    val fontColor: String = "#000000"
)

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "AZIZUL ISMAIL HOSSAIN O",
    val arabicName: String = "عزيزال اسلام حسين و",
    val idNumber: String = "2471999587",
    val birthCity: String = "-",
    val birthCountry: String = "Bangladesh",
    val dateOfBirth: String = "01/01/1981",
    val maritalStatus: String = "Married",
    val sponsorshipTransfers: Int = 0,
    val religion: String = "Islam",
    val workPermit: String = "-",
    val biometricsCollected: String = "Yes",
    val travelStatus: String = "Inside",
    val sponsorName: String = "مؤسسة الضمان العربي للمقاولات",
    val sponsorId: String = "7004899147",
    val insuranceIssuingDate: String = "18/06/2026",
    val insuranceExpiryDate: String = "17/06/2027",
    val bloodType: String = "-",
    val hajjStatus: String = "Eligible for Hajj",
    val lastHajjYear: String = "-",
    val passportDepositAmount: String = "SAR 00.0",
    val passportNumber: String = "A20144174",
    val passportType: String = "Normal",
    val passportIssueDate: String = "03/09/2025",
    val passportExpiryDate: String = "02/09/2035",
    val passportIssueCity: String = "113",
    val passportStatus: String = "-",
    val residentIdNumber: String = "2471999587",
    val residentIdVersion: String = "1",
    val residentIssueDate: String = "23/09/2019",
    val residentExpiryDate: String = "08/05/2027",
    val profession: String = "دهان",
    val workLocation: String = "منطقة الرياض",
    val avatarPath: String = ""
)

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val iconName: String,
    val category: String, // "PUBLIC", "MY_SERVICES", "OTHER_SERVICES", "PREFERRED"
    val displayOrder: Int = 0,
    val isEnabled: Boolean = true,
    val targetScreen: String = ""
)

@Entity(tableName = "quick_access_items")
data class QuickAccessItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String = "",
    val iconName: String,
    val displayOrder: Int = 0,
    val isEnabled: Boolean = true,
    val targetScreen: String = ""
)

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val documentNumber: String,
    val category: String, // "RESIDENT_ID", "PASSPORT", "DRIVING_LICENSE", "VISA", "OTHER"
    val issueDate: String = "",
    val expiryDate: String = "",
    val localFilePath: String = "",
    val notes: String = "",
    val qrData: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val idNumber: String,
    val relationship: String,
    val birthDate: String = "",
    val photoPath: String = ""
)

@Entity(tableName = "workers")
data class WorkerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val idNumber: String,
    val profession: String,
    val nationality: String,
    val photoPath: String = ""
)

@Entity(tableName = "travel_records")
data class TravelRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val requestNumber: String,
    val destination: String,
    val borderPoint: String,
    val departureDate: String,
    val returnDate: String,
    val status: String = "Active"
)
