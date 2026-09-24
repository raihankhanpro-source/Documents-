package com.example.security

import android.content.Context
import com.example.data.entity.AppBrandingEntity
import com.example.data.entity.DocumentEntity
import com.example.data.entity.ProfileEntity
import com.example.data.entity.QuickAccessItemEntity
import com.example.data.entity.ServiceEntity
import com.example.data.repository.CivilRepository
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONArray
import org.json.JSONObject

object BackupManager {

    suspend fun createEncryptedBackupJson(
        repository: CivilRepository,
        masterPasswordSecret: String
    ): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())

        // Branding
        val branding = repository.getBranding()
        if (branding != null) {
            val bObj = JSONObject()
            bObj.put("appName", branding.appName)
            bObj.put("primaryColorHex", branding.primaryColorHex)
            bObj.put("footerText", branding.footerText)
            bObj.put("floatingChatEnabled", branding.floatingChatEnabled)
            root.put("branding", bObj)
        }

        // Profile
        val profile = repository.getProfile()
        if (profile != null) {
            val pObj = JSONObject()
            pObj.put("fullName", profile.fullName)
            pObj.put("arabicName", profile.arabicName)
            pObj.put("idNumber", profile.idNumber)
            pObj.put("birthCity", profile.birthCity)
            pObj.put("birthCountry", profile.birthCountry)
            pObj.put("dateOfBirth", profile.dateOfBirth)
            pObj.put("maritalStatus", profile.maritalStatus)
            pObj.put("religion", profile.religion)
            pObj.put("sponsorName", profile.sponsorName)
            pObj.put("sponsorId", profile.sponsorId)
            pObj.put("insuranceIssuingDate", profile.insuranceIssuingDate)
            pObj.put("insuranceExpiryDate", profile.insuranceExpiryDate)
            pObj.put("hajjStatus", profile.hajjStatus)
            pObj.put("passportNumber", profile.passportNumber)
            pObj.put("passportIssueDate", profile.passportIssueDate)
            pObj.put("passportExpiryDate", profile.passportExpiryDate)
            pObj.put("residentIdNumber", profile.residentIdNumber)
            pObj.put("residentIssueDate", profile.residentIssueDate)
            pObj.put("residentExpiryDate", profile.residentExpiryDate)
            pObj.put("profession", profile.profession)
            pObj.put("workLocation", profile.workLocation)
            root.put("profile", pObj)
        }

        // Services
        val services = repository.allServicesFlow.firstOrNull() ?: emptyList()
        val sArr = JSONArray()
        for (s in services) {
            val sObj = JSONObject()
            sObj.put("title", s.title)
            sObj.put("description", s.description)
            sObj.put("iconName", s.iconName)
            sObj.put("category", s.category)
            sObj.put("displayOrder", s.displayOrder)
            sObj.put("isEnabled", s.isEnabled)
            sObj.put("targetScreen", s.targetScreen)
            sArr.put(sObj)
        }
        root.put("services", sArr)

        // Quick Access
        val quick = repository.allQuickAccessFlow.firstOrNull() ?: emptyList()
        val qArr = JSONArray()
        for (q in quick) {
            val qObj = JSONObject()
            qObj.put("title", q.title)
            qObj.put("subtitle", q.subtitle)
            qObj.put("iconName", q.iconName)
            qObj.put("displayOrder", q.displayOrder)
            qObj.put("isEnabled", q.isEnabled)
            qObj.put("targetScreen", q.targetScreen)
            qArr.put(qObj)
        }
        root.put("quickAccess", qArr)

        // Documents
        val docs = repository.allDocumentsFlow.firstOrNull() ?: emptyList()
        val dArr = JSONArray()
        for (d in docs) {
            val dObj = JSONObject()
            dObj.put("title", d.title)
            dObj.put("documentNumber", d.documentNumber)
            dObj.put("category", d.category)
            dObj.put("issueDate", d.issueDate)
            dObj.put("expiryDate", d.expiryDate)
            dObj.put("notes", d.notes)
            dObj.put("qrData", d.qrData)
            dArr.put(dObj)
        }
        root.put("documents", dArr)

        val plainJson = root.toString(2)
        return SecurityManager.encryptBackup(plainJson, masterPasswordSecret)
    }

    suspend fun restoreFromEncryptedBackup(
        repository: CivilRepository,
        encryptedPayload: String,
        masterPasswordSecret: String
    ): Result<Unit> {
        return try {
            val plainJson = SecurityManager.decryptBackup(encryptedPayload.trim(), masterPasswordSecret)
            val root = JSONObject(plainJson)

            if (root.has("branding")) {
                val bObj = root.getJSONObject("branding")
                val current = repository.getBranding() ?: AppBrandingEntity()
                repository.updateBranding(
                    current.copy(
                        appName = bObj.optString("appName", current.appName),
                        primaryColorHex = bObj.optString("primaryColorHex", current.primaryColorHex),
                        footerText = bObj.optString("footerText", current.footerText),
                        floatingChatEnabled = bObj.optBoolean("floatingChatEnabled", current.floatingChatEnabled)
                    )
                )
            }

            if (root.has("profile")) {
                val pObj = root.getJSONObject("profile")
                val current = repository.getProfile() ?: ProfileEntity()
                repository.updateProfile(
                    current.copy(
                        fullName = pObj.optString("fullName", current.fullName),
                        arabicName = pObj.optString("arabicName", current.arabicName),
                        idNumber = pObj.optString("idNumber", current.idNumber),
                        birthCity = pObj.optString("birthCity", current.birthCity),
                        birthCountry = pObj.optString("birthCountry", current.birthCountry),
                        dateOfBirth = pObj.optString("dateOfBirth", current.dateOfBirth),
                        maritalStatus = pObj.optString("maritalStatus", current.maritalStatus),
                        religion = pObj.optString("religion", current.religion),
                        sponsorName = pObj.optString("sponsorName", current.sponsorName),
                        sponsorId = pObj.optString("sponsorId", current.sponsorId),
                        insuranceIssuingDate = pObj.optString("insuranceIssuingDate", current.insuranceIssuingDate),
                        insuranceExpiryDate = pObj.optString("insuranceExpiryDate", current.insuranceExpiryDate),
                        hajjStatus = pObj.optString("hajjStatus", current.hajjStatus),
                        passportNumber = pObj.optString("passportNumber", current.passportNumber),
                        passportIssueDate = pObj.optString("passportIssueDate", current.passportIssueDate),
                        passportExpiryDate = pObj.optString("passportExpiryDate", current.passportExpiryDate),
                        residentIdNumber = pObj.optString("residentIdNumber", current.residentIdNumber),
                        residentIssueDate = pObj.optString("residentIssueDate", current.residentIssueDate),
                        residentExpiryDate = pObj.optString("residentExpiryDate", current.residentExpiryDate),
                        profession = pObj.optString("profession", current.profession),
                        workLocation = pObj.optString("workLocation", current.workLocation)
                    )
                )
            }

            if (root.has("services")) {
                val sArr = root.getJSONArray("services")
                for (i in 0 until sArr.length()) {
                    val sObj = sArr.getJSONObject(i)
                    repository.addService(
                        ServiceEntity(
                            title = sObj.optString("title", "Service"),
                            description = sObj.optString("description", ""),
                            iconName = sObj.optString("iconName", "badge"),
                            category = sObj.optString("category", "MY_SERVICES"),
                            displayOrder = sObj.optInt("displayOrder", i),
                            isEnabled = sObj.optBoolean("isEnabled", true),
                            targetScreen = sObj.optString("targetScreen", "")
                        )
                    )
                }
            }

            if (root.has("quickAccess")) {
                val qArr = root.getJSONArray("quickAccess")
                for (i in 0 until qArr.length()) {
                    val qObj = qArr.getJSONObject(i)
                    repository.addQuickAccess(
                        QuickAccessItemEntity(
                            title = qObj.optString("title", "Item"),
                            subtitle = qObj.optString("subtitle", ""),
                            iconName = qObj.optString("iconName", "badge"),
                            displayOrder = qObj.optInt("displayOrder", i),
                            isEnabled = qObj.optBoolean("isEnabled", true),
                            targetScreen = qObj.optString("targetScreen", "")
                        )
                    )
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
