package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.CivilDao
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

@Database(
    entities = [
        UserEntity::class,
        UserPermissionEntity::class,
        AppBrandingEntity::class,
        ProfileEntity::class,
        ServiceEntity::class,
        QuickAccessItemEntity::class,
        DocumentEntity::class,
        FamilyMemberEntity::class,
        WorkerEntity::class,
        TravelRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CivilDatabase : RoomDatabase() {
    abstract fun civilDao(): CivilDao

    companion object {
        @Volatile
        private var INSTANCE: CivilDatabase? = null

        fun getDatabase(context: Context): CivilDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CivilDatabase::class.java,
                    "civil_identity_offline.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
