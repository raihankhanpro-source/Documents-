package com.example.security

import com.example.data.entity.PermissionKeys
import com.example.data.entity.UserEntity

data class SessionUser(
    val id: Long,
    val username: String,
    val displayName: String,
    val role: String, // "MASTER" or "USER"
    val permissions: Set<String> = emptySet()
) {
    val isMaster: Boolean get() = role == "MASTER"

    fun hasPermission(permissionKey: String): Boolean {
        if (isMaster) return true
        return permissions.contains(permissionKey)
    }

    fun canViewDocs(): Boolean = isMaster || hasPermission(PermissionKeys.VIEW_DOCUMENTS)
    fun canAddDocs(): Boolean = isMaster || hasPermission(PermissionKeys.ADD_DOCUMENTS)
    fun canEditDocs(): Boolean = isMaster || hasPermission(PermissionKeys.EDIT_DOCUMENTS)
    fun canDeleteDocs(): Boolean = isMaster || hasPermission(PermissionKeys.DELETE_DOCUMENTS)
    fun canViewProfile(): Boolean = isMaster || hasPermission(PermissionKeys.VIEW_PROFILE)
    fun canEditProfile(): Boolean = isMaster || hasPermission(PermissionKeys.EDIT_PROFILE)
    fun canViewServices(): Boolean = isMaster || hasPermission(PermissionKeys.VIEW_SERVICES)
    fun canUseServices(): Boolean = isMaster || hasPermission(PermissionKeys.USE_SERVICES)
    fun canSearch(): Boolean = isMaster || hasPermission(PermissionKeys.SEARCH)
}
