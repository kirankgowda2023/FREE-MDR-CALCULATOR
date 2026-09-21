package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.BusinessProfileDao
import com.example.data.local.entity.BusinessProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [BusinessProfileEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun businessProfileDao(): BusinessProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mdr_calculator.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed empty business profile (no personal user data)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.businessProfileDao()?.insertOrUpdate(
                                    BusinessProfileEntity(
                                        id = 1,
                                        businessName = "",
                                        ownerName = "",
                                        merchantCategory = "REGULAR_P2M",
                                        upiVpa = "",
                                        gstin = "",
                                        businessType = "Retail Store",
                                        phoneNumber = "",
                                        email = "",
                                        address = "",
                                        estimatedMonthlyVolume = 0.0,
                                        isVerifiedMerchant = false
                                    )
                                )
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
