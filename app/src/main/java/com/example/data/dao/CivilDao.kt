package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.data.entity.AppBrandingEntity
import com.example.data.entity.DocumentEntity
import com.example.data.entity.FamilyMemberEntity
import com.example.data.entity.ProfileEntity
import com.example.data.entity.QuickAccessItemEntity
import com.example.data.entity.ServiceEntity
import com.example.data.entity.TravelRecordEntity
import com.example.data.entity.UserEntity
import com.example.data.entity.UserPermissionEntity
import com.example.data.entity.WorkerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CivilDao {
    // Branding
    @Query("SELECT * FROM app_branding WHERE id = 1")
    fun getBrandingFlow(): Flow<AppBrandingEntity?>

    @Query("SELECT * FROM app_branding WHERE id = 1")
    suspend fun getBranding(): AppBrandingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBranding(branding: AppBrandingEntity)

    // Users
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE role = 'MASTER' LIMIT 1")
    suspend fun getMasterUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    // Permissions
    @Query("SELECT * FROM user_permissions WHERE userId = :userId")
    fun getUserPermissionsFlow(userId: Long): Flow<List<UserPermissionEntity>>

    @Query("SELECT * FROM user_permissions WHERE userId = :userId")
    suspend fun getUserPermissions(userId: Long): List<UserPermissionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermissions(permissions: List<UserPermissionEntity>)

    @Query("DELETE FROM user_permissions WHERE userId = :userId")
    suspend fun clearUserPermissions(userId: Long)

    // Profile
    @Query("SELECT * FROM profile WHERE id = 1")
    fun getProfileFlow(): Flow<ProfileEntity?>

    @Query("SELECT * FROM profile WHERE id = 1")
    suspend fun getProfile(): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: ProfileEntity)

    // Services
    @Query("SELECT * FROM services ORDER BY displayOrder ASC")
    fun getAllServicesFlow(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE category = :category AND isEnabled = 1 ORDER BY displayOrder ASC")
    fun getServicesByCategoryFlow(category: String): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE isEnabled = 1 ORDER BY displayOrder ASC")
    fun getEnabledServicesFlow(): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Delete
    suspend fun deleteService(service: ServiceEntity)

    @Query("DELETE FROM services")
    suspend fun clearAllServices()

    // Quick Access
    @Query("SELECT * FROM quick_access_items ORDER BY displayOrder ASC")
    fun getAllQuickAccessFlow(): Flow<List<QuickAccessItemEntity>>

    @Query("SELECT * FROM quick_access_items WHERE isEnabled = 1 ORDER BY displayOrder ASC")
    fun getEnabledQuickAccessFlow(): Flow<List<QuickAccessItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuickAccess(item: QuickAccessItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuickAccessList(items: List<QuickAccessItemEntity>)

    @Update
    suspend fun updateQuickAccess(item: QuickAccessItemEntity)

    @Delete
    suspend fun deleteQuickAccess(item: QuickAccessItemEntity)

    // Documents
    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun getAllDocumentsFlow(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE category = :category ORDER BY createdAt DESC")
    fun getDocumentsByCategoryFlow(category: String): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentEntity): Long

    @Update
    suspend fun updateDocument(doc: DocumentEntity)

    @Delete
    suspend fun deleteDocument(doc: DocumentEntity)

    // Family Members
    @Query("SELECT * FROM family_members ORDER BY id DESC")
    fun getAllFamilyMembersFlow(): Flow<List<FamilyMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMember(member: FamilyMemberEntity): Long

    @Delete
    suspend fun deleteFamilyMember(member: FamilyMemberEntity)

    // Workers
    @Query("SELECT * FROM workers ORDER BY id DESC")
    fun getAllWorkersFlow(): Flow<List<WorkerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorker(worker: WorkerEntity): Long

    @Delete
    suspend fun deleteWorker(worker: WorkerEntity)

    // Travel Records
    @Query("SELECT * FROM travel_records ORDER BY id DESC")
    fun getAllTravelRecordsFlow(): Flow<List<TravelRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTravelRecord(record: TravelRecordEntity): Long

    @Delete
    suspend fun deleteTravelRecord(record: TravelRecordEntity)
}
