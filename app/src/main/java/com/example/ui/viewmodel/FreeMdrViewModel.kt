package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.repository.BusinessProfileRepository
import com.example.util.QrCodeGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class FreeMdrPart(
    val partIndex: Int,          // 1, 2, 3...
    val totalParts: Int,         // total parts (e.g. 3)
    val amount: Double,          // e.g. 1999.0
    val upiUri: String,          // standard upi URI
    val qrBitmap: Bitmap? = null,
    val isPaid: Boolean = false
) {
    val amountFormatted: String
        get() = if (amount % 1.0 == 0.0) {
            String.format(Locale.US, "%,d", amount.toLong())
        } else {
            String.format(Locale.US, "%,.2f", amount)
        }
}

data class FreeMdrUiState(
    val totalAmountText: String = "",
    val parsedAmount: Double = 0.0,
    val merchantUpiId: String = "",
    val merchantName: String = "Merchant",
    val splitLimit: Double = 1999.0, // Default 1999 as requested
    val isUpiConfigured: Boolean = false, // Mandatory 1st-time setup flag
    val parts: List<FreeMdrPart> = emptyList(),
    val selectedPartIndex: Int = 0,
    val estimatedMdrSaved: Double = 0.0,
    val estimatedGstSaved: Double = 0.0,
    val estimatedTotalSaved: Double = 0.0,
    val isGeneratingQrs: Boolean = false,
    val errorMessage: String? = null,
    val vpaError: String? = null,
    val hasUnsavedVpaChanges: Boolean = false,
    val isVpaSaved: Boolean = false,
    val copyFeedback: String? = null
)

class FreeMdrViewModel @JvmOverloads constructor(
    application: Application,
    private val profileRepository: BusinessProfileRepository = BusinessProfileRepository(
        AppDatabase.getInstance(application).businessProfileDao()
    )
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("free_mdr_prefs", android.content.Context.MODE_PRIVATE)
    private val _uiState = MutableStateFlow(FreeMdrUiState())
    val uiState: StateFlow<FreeMdrUiState> = _uiState.asStateFlow()

    private var hasUserEditedVpa: Boolean = false
    private var lastSavedVpa: String = ""

    init {
        val hasSavedUpi = prefs.getBoolean("has_saved_upi_vpa", false)
        _uiState.update { it.copy(isUpiConfigured = hasSavedUpi) }

        // Observe profile to auto-fill merchant UPI ID & Business Name if available
        viewModelScope.launch {
            profileRepository.ensureProfileExists()
            profileRepository.businessProfile.collect { profile ->
                if (profile != null) {
                    val hasConfigured = prefs.getBoolean("has_saved_upi_vpa", false)
                    _uiState.update { current ->
                        val vpaToUse = if (!hasUserEditedVpa && profile.upiVpa.isNotBlank()) {
                            profile.upiVpa
                        } else {
                            current.merchantUpiId
                        }
                        if (!hasUserEditedVpa && profile.upiVpa.isNotBlank()) {
                            lastSavedVpa = profile.upiVpa
                        }
                        val nameToUse = if (profile.businessName.isNotBlank()) {
                            profile.businessName
                        } else {
                            current.merchantName
                        }
                        current.copy(
                            merchantUpiId = vpaToUse,
                            merchantName = nameToUse,
                            isUpiConfigured = hasConfigured
                        )
                    }
                    generatePartsAndQrs()
                }
            }
        }

        // Initial generation
        generatePartsAndQrs()
    }

    fun onTotalAmountChanged(input: String) {
        val filtered = input.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(totalAmountText = filtered, copyFeedback = null) }
        recalculateAndGenerate()
    }

    fun onMerchantUpiChanged(input: String) {
        hasUserEditedVpa = true
        val clean = input.filter { !it.isWhitespace() }
        val error = if (clean.isNotEmpty() && !clean.contains("@")) {
            "Enter a valid UPI ID (e.g. your_business_upi_id@bankid)"
        } else null
        _uiState.update {
            it.copy(
                merchantUpiId = clean,
                vpaError = error,
                hasUnsavedVpaChanges = true,
                isVpaSaved = false,
                copyFeedback = null
            )
        }
        recalculateAndGenerate()
    }

    fun saveMerchantUpi() {
        val upi = _uiState.value.merchantUpiId.filter { !it.isWhitespace() }
        if (upi.isBlank()) {
            _uiState.update { it.copy(vpaError = "Merchant UPI ID cannot be empty (e.g. your_business_upi_id@bankid)") }
            return
        }
        if (!upi.contains("@")) {
            _uiState.update { it.copy(vpaError = "Enter a valid UPI ID with @ handle (e.g. your_business_upi_id@bankid)") }
            return
        }

        lastSavedVpa = upi
        hasUserEditedVpa = false
        prefs.edit().putBoolean("has_saved_upi_vpa", true).apply()
        _uiState.update {
            it.copy(
                merchantUpiId = upi,
                vpaError = null,
                hasUnsavedVpaChanges = false,
                isVpaSaved = true,
                isUpiConfigured = true,
                copyFeedback = "Merchant UPI ID ($upi) saved successfully!"
            )
        }
        generatePartsAndQrs()

        viewModelScope.launch {
            profileRepository.updateUpiVpa(upi)
        }
    }

    fun resetVpaToProfile() {
        viewModelScope.launch {
            val profile = profileRepository.getProfileOnce()
            if (profile != null && profile.upiVpa.isNotBlank()) {
                hasUserEditedVpa = false
                lastSavedVpa = profile.upiVpa
                _uiState.update {
                    it.copy(
                        merchantUpiId = profile.upiVpa,
                        vpaError = null,
                        hasUnsavedVpaChanges = false,
                        isVpaSaved = false,
                        copyFeedback = "Reset UPI VPA to saved profile: ${profile.upiVpa}"
                    )
                }
                recalculateAndGenerate()
            }
        }
    }

    fun onMerchantNameChanged(input: String) {
        _uiState.update { it.copy(merchantName = input, copyFeedback = null) }
        recalculateAndGenerate()
    }

    fun onSplitLimitChanged(newLimit: Double) {
        _uiState.update { it.copy(splitLimit = newLimit, copyFeedback = null) }
        recalculateAndGenerate()
    }

    fun onSelectPart(index: Int) {
        if (index in _uiState.value.parts.indices) {
            _uiState.update { it.copy(selectedPartIndex = index) }
        }
    }

    fun togglePartPaid(partIndex: Int) {
        _uiState.update { current ->
            val updated = current.parts.map { part ->
                if (part.partIndex == partIndex) {
                    part.copy(isPaid = !part.isPaid)
                } else {
                    part
                }
            }
            current.copy(parts = updated)
        }
    }

    fun setQuickAmount(amount: Double) {
        val amountStr = if (amount % 1.0 == 0.0) amount.toLong().toString() else amount.toString()
        _uiState.update { it.copy(totalAmountText = amountStr) }
        recalculateAndGenerate()
    }

    fun addQuickAmount(increment: Double) {
        val currentAmount = _uiState.value.totalAmountText.toDoubleOrNull() ?: 0.0
        val newAmount = currentAmount + increment
        val amountStr = if (newAmount % 1.0 == 0.0) newAmount.toLong().toString() else String.format(Locale.US, "%.2f", newAmount)
        _uiState.update { it.copy(totalAmountText = amountStr) }
        recalculateAndGenerate()
    }

    fun clearFeedback() {
        _uiState.update { it.copy(copyFeedback = null) }
    }

    fun setCopyFeedback(message: String) {
        _uiState.update { it.copy(copyFeedback = message) }
    }

    private fun recalculateAndGenerate() {
        val state = _uiState.value
        val amount = state.totalAmountText.toDoubleOrNull()

        if (amount == null || amount <= 0.0) {
            _uiState.update {
                it.copy(
                    parsedAmount = 0.0,
                    parts = emptyList(),
                    estimatedMdrSaved = 0.0,
                    estimatedGstSaved = 0.0,
                    estimatedTotalSaved = 0.0,
                    errorMessage = if (state.totalAmountText.isNotEmpty()) "Please enter a valid amount" else null
                )
            }
            return
        }

        if (state.merchantUpiId.isBlank() || !state.merchantUpiId.contains("@")) {
            _uiState.update {
                it.copy(
                    errorMessage = "Please provide a valid UPI ID (e.g. name@bank)"
                )
            }
        } else {
            _uiState.update { it.copy(errorMessage = null) }
        }

        generatePartsAndQrs()
    }

    /**
     * Splits total amount into parts <= splitLimit (e.g. 1999).
     * Calculates the MDR fees saved compared to a lump sum transaction.
     * Generates QR Code bitmaps for each part.
     */
    private fun generatePartsAndQrs() {
        val currentState = _uiState.value
        val amount = currentState.totalAmountText.toDoubleOrNull() ?: return
        if (amount <= 0.0) return

        val limit = currentState.splitLimit

        // 1. Calculate split parts
        val partAmounts = mutableListOf<Double>()
        var remaining = Math.round(amount * 100.0) / 100.0

        while (remaining > 0.0) {
            if (remaining > limit) {
                partAmounts.add(limit)
                remaining = Math.round((remaining - limit) * 100.0) / 100.0
            } else {
                partAmounts.add(remaining)
                remaining = 0.0
            }
        }

        // 2. Calculate savings vs standard single transaction
        // Single transaction > 2000 attracts 0.40% MDR (capped at Rs. 60) + 18% GST
        val normalMdr = if (amount <= 2000.0) {
            0.0
        } else {
            minOf(60.0, amount * 0.004)
        }
        val normalGst = normalMdr * 0.18
        val totalSaved = normalMdr + normalGst

        val upiVpa = currentState.merchantUpiId.ifBlank { "your_business_upi_id@bankid" }
        val merchantName = currentState.merchantName.ifBlank { "Merchant" }
        val totalCount = partAmounts.size

        // Build parts without bitmaps first for fast UI reaction
        val initialParts = partAmounts.mapIndexed { index, partAmt ->
            val partNum = index + 1
            val note = if (totalCount > 1) "Part $partNum of $totalCount (Free MDR)" else "Payment (Free MDR)"
            val uri = QrCodeGenerator.buildUpiUri(
                upiVpa = upiVpa,
                merchantName = merchantName,
                amount = partAmt,
                note = note
            )
            FreeMdrPart(
                partIndex = partNum,
                totalParts = totalCount,
                amount = partAmt,
                upiUri = uri,
                qrBitmap = null
            )
        }

        _uiState.update {
            it.copy(
                parsedAmount = amount,
                parts = initialParts,
                selectedPartIndex = if (it.selectedPartIndex in initialParts.indices) it.selectedPartIndex else 0,
                estimatedMdrSaved = normalMdr,
                estimatedGstSaved = normalGst,
                estimatedTotalSaved = totalSaved,
                isGeneratingQrs = true
            )
        }

        // 3. Generate QR Bitmaps asynchronously
        viewModelScope.launch {
            val partsWithQrs = initialParts.map { part ->
                val bitmap = try {
                    QrCodeGenerator.generateQrBitmap(part.upiUri, size = 512)
                } catch (e: Exception) {
                    null
                }
                part.copy(qrBitmap = bitmap)
            }

            _uiState.update {
                it.copy(
                    parts = partsWithQrs,
                    isGeneratingQrs = false
                )
            }
        }
    }
}
