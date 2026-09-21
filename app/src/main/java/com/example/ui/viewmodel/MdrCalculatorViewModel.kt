package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Payment Method - UPI Only as requested.
 */
enum class PaymentMethod(
    val title: String,
    val typicalMdr: String,
    val description: String
) {
    UPI(
        title = "UPI",
        typicalMdr = "0.40%",
        description = "Unified Payments Interface (P2M & Merchant QR)"
    )
}

/**
 * Official Merchant & Transaction Categories under the New UPI Fee Structure (Effective 15 October 2026).
 */
enum class UpiCategory(
    val title: String,
    val shortName: String,
    val description: String,
    val ruleSummary: String
) {
    REGULAR_P2M(
        title = "Regular Merchants (P2M)",
        shortName = "Regular Merchant",
        description = "Retail shops, supermarkets, restaurants, commerce",
        ruleSummary = "≤ ₹2,000: 0% Free | > ₹2,000: 0.40% (Max ₹300 for ≥ ₹75,000)"
    ),
    FUEL(
        title = "Fuel",
        shortName = "Fuel",
        description = "Petrol, diesel & CNG fuel dispensing pumps",
        ruleSummary = "≤ ₹2,000: 0% Free | > ₹2,000: ₹5 (flat)"
    ),
    RAILWAYS(
        title = "Railways",
        shortName = "Railways",
        description = "IRCTC and railway counter ticketing",
        ruleSummary = "≤ ₹2,000: 0% Free | > ₹2,000: ₹5 (flat)"
    ),
    TELECOM(
        title = "Telecom",
        shortName = "Telecom",
        description = "Mobile recharges, broadband, utility bills",
        ruleSummary = "≤ ₹2,000: 0% Free | > ₹2,000: ₹5 (flat)"
    ),
    INSURANCE(
        title = "Insurance",
        shortName = "Insurance",
        description = "Life, health & vehicle insurance premiums",
        ruleSummary = "≤ ₹2,000: 0% Free | > ₹2,000: ₹5 (flat)"
    ),
    CAPITAL_MARKETS(
        title = "Capital Markets",
        shortName = "Capital Markets",
        description = "Securities, Mutual Funds, Brokers & Dealers",
        ruleSummary = "≤ ₹2,000: 0% Free | > ₹2,000: 0.02% (Max ₹300)"
    ),
    SMALL_MERCHANT(
        title = "Small Merchants / P2PM",
        shortName = "Small Merchant",
        description = "Eligible small vendors & micro merchants",
        ruleSummary = "Eligible transactions remain 0% FREE"
    ),
    P2P(
        title = "Person to Person (P2P)",
        shortName = "P2P Transfer",
        description = "Personal remittances between individuals",
        ruleSummary = "All P2P transactions are 0% FREE"
    ),
    CUSTOM(
        title = "Custom Rate",
        shortName = "Custom",
        description = "User specified custom MDR percentage",
        ruleSummary = "Manual percentage and fixed fee entry"
    )
}

/**
 * Result details for a calculated UPI transaction.
 */
data class CalculationResult(
    val grossAmount: Double,
    val mdrRateDisplay: String,
    val effectiveMdrRate: Double,
    val mdrAmount: Double,
    val gstAmount: Double,
    val fixedFee: Double,
    val totalCharges: Double,
    val netSettlement: Double,
    val costPercentage: Double,
    val includeGst: Boolean,
    val ruleAppliedText: String,
    val isCapApplied: Boolean = false,
    val isFreeEverydayPayment: Boolean = false,
    val category: UpiCategory = UpiCategory.REGULAR_P2M
)

/**
 * UI State for the MDR Calculator screen.
 */
data class MdrUiState(
    val amountText: String = "5000",
    val selectedCategory: UpiCategory = UpiCategory.REGULAR_P2M,
    val customMdrRateText: String = "0.40",
    val customFixedFeeText: String = "0",
    val includeGst: Boolean = true,
    val calculationResult: CalculationResult? = null,
    val amountErrorMessage: String? = null,
    val mdrErrorMessage: String? = null,
    val isCalculateEnabled: Boolean = true
)

/**
 * ViewModel implementing official New UPI Fee Structure rules (Effective 15 October 2026).
 */
class MdrCalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        MdrUiState(
            amountText = "5000",
            selectedCategory = UpiCategory.REGULAR_P2M,
            customMdrRateText = "0.40",
            customFixedFeeText = "0",
            includeGst = true
        )
    )
    val uiState: StateFlow<MdrUiState> = _uiState.asStateFlow()

    init {
        calculateSettlement()
    }

    /**
     * Updates transaction amount.
     */
    fun onAmountChanged(newAmount: String) {
        val sanitized = newAmount.filter { it.isDigit() || it == '.' }
        val amountVal = sanitized.toDoubleOrNull()
        val error = if (sanitized.isNotEmpty() && (amountVal == null || amountVal <= 0.0)) {
            "Enter a valid positive transaction amount."
        } else null

        _uiState.update { current ->
            current.copy(
                amountText = sanitized,
                amountErrorMessage = error,
                isCalculateEnabled = isFormValid(sanitized, current.customMdrRateText, current.selectedCategory)
            )
        }
    }

    /**
     * Sets a preset transaction amount and category.
     */
    fun onQuickExampleSelected(amount: String, category: UpiCategory) {
        _uiState.update {
            it.copy(
                amountText = amount,
                selectedCategory = category,
                amountErrorMessage = null,
                isCalculateEnabled = true
            )
        }
        calculateSettlement()
    }

    /**
     * Changes merchant / transaction category according to circular.
     */
    fun onCategorySelected(category: UpiCategory) {
        _uiState.update { current ->
            current.copy(
                selectedCategory = category,
                isCalculateEnabled = isFormValid(current.amountText, current.customMdrRateText, category)
            )
        }
        calculateSettlement()
    }

    /**
     * Updates custom MDR percentage (for Custom mode).
     */
    fun onCustomMdrRateChanged(newRate: String) {
        val sanitized = newRate.filter { it.isDigit() || it == '.' }
        val rateVal = sanitized.toDoubleOrNull()
        val error = if (sanitized.isNotEmpty() && (rateVal == null || rateVal < 0.0 || rateVal > 100.0)) {
            "Enter an MDR rate between 0% and 100%."
        } else null

        _uiState.update { current ->
            current.copy(
                customMdrRateText = sanitized,
                mdrErrorMessage = error,
                isCalculateEnabled = isFormValid(current.amountText, sanitized, current.selectedCategory)
            )
        }
    }

    /**
     * Updates custom fixed fee.
     */
    fun onCustomFixedFeeChanged(newFee: String) {
        val sanitized = newFee.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(customFixedFeeText = sanitized) }
    }

    /**
     * Toggles 18% GST on MDR.
     */
    fun onIncludeGstToggled(included: Boolean) {
        _uiState.update { it.copy(includeGst = included) }
        if (_uiState.value.calculationResult != null) {
            calculateSettlement()
        }
    }

    /**
     * Core business calculation following the New UPI Fee Structure rules:
     * - Person to Person (P2P): Any value -> 0%
     * - Small Merchants / P2PM: Eligible transactions -> 0%
     * - Everyday payments <= ₹2,000 remain FREE for all categories!
     * - Regular Merchants (P2M):
     *     - Up to ₹2,000: 0%
     *     - > ₹2,000 and < ₹75,000: 0.40% (no cap)
     *     - >= ₹75,000: 0.40% (Maximum Fee: ₹300 cap)
     * - Railways, Fuel, Telecom, Insurance:
     *     - Up to ₹2,000: 0% (Everyday payments remain free)
     *     - > ₹2,000: ₹5 flat (Maximum Fee: ₹5)
     * - Capital Markets:
     *     - Up to ₹2,000: 0%
     *     - > ₹2,000: 0.02% (Maximum Fee: ₹300 cap)
     */
    fun calculateSettlement() {
        val currentState = _uiState.value
        val amount = currentState.amountText.toDoubleOrNull() ?: return
        if (amount <= 0.0) return

        val category = currentState.selectedCategory
        var mdrRateDisplay: String
        var effectiveMdrRate: Double
        var mdrAmount: Double
        var ruleAppliedText: String
        var isCapApplied = false
        var isFreeEverydayPayment = false
        var fixedFee = 0.0

        when (category) {
            UpiCategory.P2P -> {
                mdrRateDisplay = "0% (Free)"
                effectiveMdrRate = 0.0
                mdrAmount = 0.0
                ruleAppliedText = "All Person-to-Person (P2P) transfers remain 100% FREE"
                isFreeEverydayPayment = true
            }

            UpiCategory.SMALL_MERCHANT -> {
                mdrRateDisplay = "0% (Free)"
                effectiveMdrRate = 0.0
                mdrAmount = 0.0
                ruleAppliedText = "Eligible small merchants / P2PM transactions remain 100% FREE"
                isFreeEverydayPayment = true
            }

            UpiCategory.REGULAR_P2M -> {
                if (amount <= 2000.0) {
                    mdrRateDisplay = "0% (Free)"
                    effectiveMdrRate = 0.0
                    mdrAmount = 0.0
                    ruleAppliedText = "Everyday payments up to ₹2,000 remain FREE (0% MDR)"
                    isFreeEverydayPayment = true
                } else if (amount < 75000.0) {
                    mdrRateDisplay = "0.40%"
                    effectiveMdrRate = 0.40
                    mdrAmount = (amount * 0.40) / 100.0
                    ruleAppliedText = "Regular P2M fee of 0.40% applies (> ₹2,000 to < ₹75,000)"
                } else {
                    // >= 75000: 0.40% with Maximum Fee of ₹300 cap
                    val rawMdr = (amount * 0.40) / 100.0
                    mdrAmount = minOf(rawMdr, 300.0)
                    isCapApplied = rawMdr >= 300.0
                    effectiveMdrRate = (mdrAmount / amount) * 100.0
                    mdrRateDisplay = if (isCapApplied) "0.40% (₹300 Cap)" else "0.40%"
                    ruleAppliedText = if (isCapApplied) {
                        "Maximum Fee cap of ₹300 applied per transaction (≥ ₹75,000)"
                    } else {
                        "Regular P2M fee of 0.40% applies"
                    }
                }
            }

            UpiCategory.FUEL,
            UpiCategory.RAILWAYS,
            UpiCategory.TELECOM,
            UpiCategory.INSURANCE -> {
                if (amount <= 2000.0) {
                    mdrRateDisplay = "0% (Free)"
                    effectiveMdrRate = 0.0
                    mdrAmount = 0.0
                    ruleAppliedText = "Everyday payments up to ₹2,000 remain FREE (0% MDR)"
                    isFreeEverydayPayment = true
                } else {
                    mdrRateDisplay = "₹5 (flat)"
                    mdrAmount = 5.0
                    effectiveMdrRate = (5.0 / amount) * 100.0
                    isCapApplied = true
                    ruleAppliedText = "Flat ₹5 fee applies for ${category.shortName} (> ₹2,000, Max: ₹5)"
                }
            }

            UpiCategory.CAPITAL_MARKETS -> {
                if (amount <= 2000.0) {
                    mdrRateDisplay = "0% (Free)"
                    effectiveMdrRate = 0.0
                    mdrAmount = 0.0
                    ruleAppliedText = "Everyday payments up to ₹2,000 remain FREE (0% MDR)"
                    isFreeEverydayPayment = true
                } else {
                    val rawMdr = (amount * 0.02) / 100.0
                    mdrAmount = minOf(rawMdr, 300.0)
                    isCapApplied = rawMdr >= 300.0
                    effectiveMdrRate = (mdrAmount / amount) * 100.0
                    mdrRateDisplay = if (isCapApplied) "0.02% (₹300 Cap)" else "0.02%"
                    ruleAppliedText = if (isCapApplied) {
                        "Capital Markets 0.02% capped at Maximum Fee of ₹300 (> ₹2,000)"
                    } else {
                        "Capital Markets applicable fee of 0.02% applies (> ₹2,000)"
                    }
                }
            }

            UpiCategory.CUSTOM -> {
                val customRate = currentState.customMdrRateText.toDoubleOrNull() ?: 0.0
                fixedFee = currentState.customFixedFeeText.toDoubleOrNull() ?: 0.0
                effectiveMdrRate = customRate
                mdrRateDisplay = "%.2f%%".format(customRate)
                mdrAmount = (amount * customRate) / 100.0
                ruleAppliedText = "Custom merchant discount rate of %.2f%% applied".format(customRate)
            }
        }

        // 18% GST applies strictly to MDR charges
        val gstAmount = if (currentState.includeGst && mdrAmount > 0.0) {
            mdrAmount * 0.18
        } else {
            0.0
        }

        val totalCharges = mdrAmount + gstAmount + fixedFee
        val netSettlement = amount - totalCharges
        val costPercentage = if (amount > 0.0) (totalCharges / amount) * 100.0 else 0.0

        val result = CalculationResult(
            grossAmount = amount,
            mdrRateDisplay = mdrRateDisplay,
            effectiveMdrRate = effectiveMdrRate,
            mdrAmount = mdrAmount,
            gstAmount = gstAmount,
            fixedFee = fixedFee,
            totalCharges = totalCharges,
            netSettlement = netSettlement,
            costPercentage = costPercentage,
            includeGst = currentState.includeGst,
            ruleAppliedText = ruleAppliedText,
            isCapApplied = isCapApplied,
            isFreeEverydayPayment = isFreeEverydayPayment,
            category = category
        )

        _uiState.update { it.copy(calculationResult = result) }
    }

    /**
     * Resets inputs and state back to default.
     */
    fun resetCalculator() {
        _uiState.value = MdrUiState(
            amountText = "",
            selectedCategory = UpiCategory.REGULAR_P2M,
            customMdrRateText = "0.40",
            customFixedFeeText = "0",
            includeGst = true,
            calculationResult = null,
            amountErrorMessage = null,
            mdrErrorMessage = null,
            isCalculateEnabled = false
        )
    }

    private fun isFormValid(amountStr: String, customMdrStr: String, category: UpiCategory): Boolean {
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0.0) return false

        if (category == UpiCategory.CUSTOM) {
            val mdr = customMdrStr.toDoubleOrNull()
            if (mdr == null || mdr < 0.0 || mdr > 100.0) return false
        }

        return true
    }
}
