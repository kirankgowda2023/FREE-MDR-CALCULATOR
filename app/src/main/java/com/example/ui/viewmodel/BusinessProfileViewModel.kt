package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.BusinessProfileEntity
import com.example.data.repository.BusinessProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BusinessProfileUiState(
    val businessName: String = "",
    val ownerName: String = "",
    val selectedCategory: UpiCategory = UpiCategory.REGULAR_P2M,
    val upiVpa: String = "",
    val gstin: String = "",
    val businessType: String = "Retail Store",
    val phoneNumber: String = "",
    val email: String = "",
    val address: String = "",
    val monthlyVolumeText: String = "250000",
    val isVerifiedMerchant: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccessMessage: String? = null,
    val businessNameError: String? = null,
    val upiVpaError: String? = null,
    val gstinError: String? = null,
    val phoneError: String? = null,
    val isEditing: Boolean = false
) {
    val estimatedMonthlyVolume: Double
        get() = monthlyVolumeText.toDoubleOrNull() ?: 0.0

    val estimatedMonthlyMdr: Double
        get() {
            val vol = estimatedMonthlyVolume
            return when (selectedCategory) {
                UpiCategory.REGULAR_P2M -> vol * 0.0040
                UpiCategory.FUEL, UpiCategory.RAILWAYS, UpiCategory.TELECOM, UpiCategory.INSURANCE -> {
                    // Avg 100 txns of ₹2,500 = ₹500
                    (vol / 2500.0) * 5.0
                }
                UpiCategory.CAPITAL_MARKETS -> vol * 0.0002
                UpiCategory.SMALL_MERCHANT, UpiCategory.P2P -> 0.0
                UpiCategory.CUSTOM -> vol * 0.015
            }
        }

    val estimatedMonthlyGst: Double
        get() = estimatedMonthlyMdr * 0.18

    val estimatedTotalMonthlyCharges: Double
        get() = estimatedMonthlyMdr + estimatedMonthlyGst
}

class BusinessProfileViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: BusinessProfileRepository = BusinessProfileRepository(
        AppDatabase.getInstance(application).businessProfileDao()
    )
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(BusinessProfileUiState())
    val uiState: StateFlow<BusinessProfileUiState> = _uiState.asStateFlow()

    private var isUserEditing: Boolean = false
    private var isSavingProfile: Boolean = false

    init {
        viewModelScope.launch {
            repository.ensureProfileExists()
            repository.businessProfile.collectLatest { profile ->
                if (profile != null && !isUserEditing && !isSavingProfile) {
                    val category = try {
                        UpiCategory.valueOf(profile.merchantCategory)
                    } catch (e: Exception) {
                        UpiCategory.REGULAR_P2M
                    }
                    _uiState.update { current ->
                        current.copy(
                            businessName = profile.businessName,
                            ownerName = profile.ownerName,
                            selectedCategory = category,
                            upiVpa = profile.upiVpa,
                            gstin = profile.gstin,
                            businessType = profile.businessType,
                            phoneNumber = profile.phoneNumber,
                            email = profile.email,
                            address = profile.address,
                            monthlyVolumeText = if (profile.estimatedMonthlyVolume > 0) {
                                profile.estimatedMonthlyVolume.toLong().toString()
                            } else "150000",
                            isVerifiedMerchant = profile.isVerifiedMerchant
                        )
                    }
                }
            }
        }
    }

    fun onBusinessNameChanged(name: String) {
        isUserEditing = true
        isSavingProfile = false
        _uiState.update {
            it.copy(
                businessName = name,
                businessNameError = if (name.isBlank()) "Business name cannot be empty" else null,
                saveSuccessMessage = null
            )
        }
    }

    fun onOwnerNameChanged(name: String) {
        isUserEditing = true
        _uiState.update { it.copy(ownerName = name, saveSuccessMessage = null) }
    }

    fun onCategoryChanged(category: UpiCategory) {
        isUserEditing = true
        _uiState.update { it.copy(selectedCategory = category, saveSuccessMessage = null) }
    }

    fun onUpiVpaChanged(vpa: String) {
        isUserEditing = true
        val clean = vpa.filter { !it.isWhitespace() }
        val error = if (clean.isNotEmpty() && !clean.contains("@")) {
            "Enter a valid UPI ID (e.g. your_business_upi_id@bankid)"
        } else null
        _uiState.update { it.copy(upiVpa = clean, upiVpaError = error, saveSuccessMessage = null) }
    }

    fun onGstinChanged(gstin: String) {
        isUserEditing = true
        val uppercase = gstin.uppercase().filter { !it.isWhitespace() }
        val error = if (uppercase.isNotEmpty() && uppercase.length != 15) {
            "GSTIN must be 15 alphanumeric characters"
        } else null
        _uiState.update { it.copy(gstin = uppercase, gstinError = error, saveSuccessMessage = null) }
    }

    fun onBusinessTypeChanged(type: String) {
        isUserEditing = true
        _uiState.update { it.copy(businessType = type, saveSuccessMessage = null) }
    }

    fun onPhoneNumberChanged(phone: String) {
        isUserEditing = true
        _uiState.update { it.copy(phoneNumber = phone, phoneError = null, saveSuccessMessage = null) }
    }

    fun onEmailChanged(email: String) {
        isUserEditing = true
        _uiState.update { it.copy(email = email.trim(), saveSuccessMessage = null) }
    }

    fun onAddressChanged(address: String) {
        isUserEditing = true
        _uiState.update { it.copy(address = address, saveSuccessMessage = null) }
    }

    fun onMonthlyVolumeChanged(volume: String) {
        isUserEditing = true
        val clean = volume.filter { it.isDigit() }
        _uiState.update { it.copy(monthlyVolumeText = clean, saveSuccessMessage = null) }
    }

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditing = !it.isEditing, saveSuccessMessage = null) }
    }

    fun clearSuccessMessage() {
        isSavingProfile = false
        isUserEditing = false
        _uiState.update { it.copy(saveSuccessMessage = null) }
    }

    fun saveBusinessProfile() {
        val state = _uiState.value
        if (state.businessName.isBlank()) {
            _uiState.update { it.copy(businessNameError = "Business name cannot be empty") }
            return
        }
        if (state.upiVpa.isNotBlank() && !state.upiVpa.contains("@")) {
            _uiState.update { it.copy(upiVpaError = "Enter a valid UPI ID with @ handle (e.g. your_business_upi_id@bankid)") }
            return
        }

        isSavingProfile = true
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val cleanVpa = state.upiVpa.trim()
            val entity = BusinessProfileEntity(
                id = 1,
                businessName = state.businessName.trim(),
                ownerName = state.ownerName.trim(),
                merchantCategory = state.selectedCategory.name,
                upiVpa = cleanVpa,
                gstin = state.gstin.trim(),
                businessType = state.businessType,
                phoneNumber = state.phoneNumber.trim(),
                email = state.email.trim(),
                address = state.address.trim(),
                estimatedMonthlyVolume = state.monthlyVolumeText.toDoubleOrNull() ?: 150000.0,
                isVerifiedMerchant = state.isVerifiedMerchant
            )
            repository.updateProfile(entity)
            if (cleanVpa.isNotBlank() && cleanVpa.contains("@")) {
                getApplication<Application>().getSharedPreferences("free_mdr_prefs", android.content.Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("has_saved_upi_vpa", true)
                    .apply()
            }
            isUserEditing = false
            isSavingProfile = false
            _uiState.update {
                it.copy(
                    isSaving = false,
                    isEditing = false,
                    saveSuccessMessage = "Business profile updated successfully!"
                )
            }
        }
    }
}
