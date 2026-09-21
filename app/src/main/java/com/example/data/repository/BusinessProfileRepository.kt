package com.example.data.repository

import com.example.data.local.dao.BusinessProfileDao
import com.example.data.local.entity.BusinessProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BusinessProfileRepository(
    private val dao: BusinessProfileDao,
    private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.IO
) {

    val businessProfile: Flow<BusinessProfileEntity?> = dao.getBusinessProfile()

    suspend fun getProfileOnce(): BusinessProfileEntity? = withContext(ioDispatcher) {
        dao.getBusinessProfileOnce()
    }

    suspend fun ensureProfileExists() = withContext(ioDispatcher) {
        val current = dao.getBusinessProfileOnce()
        if (current == null) {
            dao.insertOrUpdate(
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
        } else if (
            current.businessName.contains("Kiran", ignoreCase = true) ||
            current.ownerName.contains("Kiran", ignoreCase = true) ||
            current.upiVpa.contains("kiran", ignoreCase = true) ||
            current.email.contains("kiran", ignoreCase = true) ||
            current.phoneNumber.contains("98765")
        ) {
            // Remove legacy default user data completely
            dao.insertOrUpdate(
                current.copy(
                    businessName = "",
                    ownerName = "",
                    upiVpa = "",
                    gstin = "",
                    phoneNumber = "",
                    email = "",
                    address = "",
                    estimatedMonthlyVolume = 0.0,
                    isVerifiedMerchant = false,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun updateProfile(profile: BusinessProfileEntity) = withContext(ioDispatcher) {
        dao.insertOrUpdate(profile.copy(id = 1, updatedAt = System.currentTimeMillis()))
    }

    suspend fun updateUpiVpa(newVpa: String): Boolean = withContext(ioDispatcher) {
        val clean = newVpa.trim()
        val current = dao.getBusinessProfileOnce()
        if (current != null) {
            dao.insertOrUpdate(current.copy(upiVpa = clean, updatedAt = System.currentTimeMillis()))
            true
        } else {
            dao.insertOrUpdate(
                BusinessProfileEntity(
                    id = 1,
                    businessName = "",
                    ownerName = "",
                    merchantCategory = "REGULAR_P2M",
                    upiVpa = clean,
                    gstin = "",
                    businessType = "Retail Store",
                    phoneNumber = "",
                    email = "",
                    address = "",
                    estimatedMonthlyVolume = 0.0,
                    isVerifiedMerchant = false
                )
            )
            true
        }
    }
}
