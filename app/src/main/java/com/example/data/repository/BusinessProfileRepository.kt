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
        if (dao.getProfileCount() == 0) {
            dao.insertOrUpdate(
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
                    businessName = "Kiran Supermarket & Stores",
                    ownerName = "Kiran Gowda",
                    merchantCategory = "REGULAR_P2M",
                    upiVpa = clean,
                    gstin = "29AABCU9603R1ZM",
                    businessType = "Retail Store",
                    phoneNumber = "+91 98765 43210",
                    email = "kirankgowda123@gmail.com",
                    address = "12th Main, Indiranagar, Bengaluru, Karnataka 560038",
                    estimatedMonthlyVolume = 250000.0,
                    isVerifiedMerchant = true
                )
            )
            true
        }
    }
}
