package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfileEntity(
    @PrimaryKey val id: Int = 1,
    val businessName: String,
    val ownerName: String,
    val merchantCategory: String,
    val upiVpa: String,
    val gstin: String,
    val businessType: String,
    val phoneNumber: String,
    val email: String,
    val address: String,
    val estimatedMonthlyVolume: Double = 150000.0,
    val isVerifiedMerchant: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
