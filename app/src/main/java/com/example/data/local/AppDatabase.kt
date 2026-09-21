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
                            // Seed default business profile
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.businessProfileDao()?.insertOrUpdate(
                                    BusinessProfileEntity(
                                        id = 1,
                                        businessName = "Kiran Supermarket & Stores",
                                        ownerName = "Kiran Gowda",
                                        merchantCategory = "REGULAR_P2M",
                                        upiVpa = "kirangowda@upi",
                                        gstin = "29AABCU9603R1ZM",
                                        businessType = "Retail Store",
                                        phoneNumber = "+91 98765 43210",
                                        email = "kirankgowda123@gmail.com",
                                        address = "12th Main, Indiranagar, Bengaluru, Karnataka 560038",
                                        estimatedMonthlyVolume = 250000.0,
                                        isVerifiedMerchant = true
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
