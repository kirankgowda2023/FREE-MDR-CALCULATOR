package com.example

import com.example.ui.viewmodel.MdrCalculatorViewModel
import com.example.ui.viewmodel.UpiCategory
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MdrCalculatorViewModelTest {

    private lateinit var viewModel: MdrCalculatorViewModel

    @Before
    fun setUp() {
        viewModel = MdrCalculatorViewModel()
    }

    @Test
    fun `initial state is Regular Merchants with 5000 default`() {
        val state = viewModel.uiState.value
        assertEquals(UpiCategory.REGULAR_P2M, state.selectedCategory)
        assertEquals("5000", state.amountText)
        assertTrue(state.includeGst)
        assertNotNull(state.calculationResult)

        val result = state.calculationResult!!
        assertEquals(5000.0, result.grossAmount, 0.001)
        // 5000 is > 2000 and < 75000 -> 0.40% = 20.0
        assertEquals(20.0, result.mdrAmount, 0.001)
        // GST = 18% of 20 = 3.60
        assertEquals(3.60, result.gstAmount, 0.001)
        assertEquals(23.60, result.totalCharges, 0.001)
        assertEquals(4976.40, result.netSettlement, 0.001)
    }

    @Test
    fun `quick example 1 - Rs 1500 at a shop is FREE`() {
        viewModel.onQuickExampleSelected("1500", UpiCategory.REGULAR_P2M)

        val result = viewModel.uiState.value.calculationResult!!
        assertEquals(1500.0, result.grossAmount, 0.001)
        assertEquals(0.0, result.mdrAmount, 0.001)
        assertEquals(0.0, result.gstAmount, 0.001)
        assertEquals(0.0, result.totalCharges, 0.001)
        assertEquals(1500.0, result.netSettlement, 0.001)
        assertTrue(result.isFreeEverydayPayment)
    }

    @Test
    fun `quick example 2 - Rs 5000 at regular merchant is Rs 20 plus GST`() {
        viewModel.onQuickExampleSelected("5000", UpiCategory.REGULAR_P2M)

        val result = viewModel.uiState.value.calculationResult!!
        assertEquals(5000.0, result.grossAmount, 0.001)
        assertEquals(20.0, result.mdrAmount, 0.001)
        assertEquals(3.60, result.gstAmount, 0.001)
        assertEquals(23.60, result.totalCharges, 0.001)
        assertEquals(4976.40, result.netSettlement, 0.001)
    }

    @Test
    fun `quick example 3 - Rs 75000 at regular merchant hits Rs 300 cap`() {
        viewModel.onQuickExampleSelected("75000", UpiCategory.REGULAR_P2M)

        val result = viewModel.uiState.value.calculationResult!!
        assertEquals(75000.0, result.grossAmount, 0.001)
        assertEquals(300.0, result.mdrAmount, 0.001)
        assertEquals(54.0, result.gstAmount, 0.001)
        assertEquals(354.0, result.totalCharges, 0.001)
        assertEquals(74646.0, result.netSettlement, 0.001)
        assertTrue(result.isCapApplied)
    }

    @Test
    fun `regular merchant higher amount Rs 100000 is still capped at Rs 300`() {
        viewModel.onAmountChanged("100000")
        viewModel.onCategorySelected(UpiCategory.REGULAR_P2M)

        val result = viewModel.uiState.value.calculationResult!!
        // Without cap 0.40% would be 400.0, but capped at 300.0
        assertEquals(300.0, result.mdrAmount, 0.001)
        assertTrue(result.isCapApplied)
    }

    @Test
    fun `quick example 4 - Rs 5000 for fuel is Rs 5 flat fee`() {
        viewModel.onQuickExampleSelected("5000", UpiCategory.FUEL)

        val result = viewModel.uiState.value.calculationResult!!
        assertEquals(5000.0, result.grossAmount, 0.001)
        assertEquals(5.0, result.mdrAmount, 0.001)
        assertEquals(0.90, result.gstAmount, 0.001)
        assertEquals(5.90, result.totalCharges, 0.001)
        assertEquals(4994.10, result.netSettlement, 0.001)
    }

    @Test
    fun `fuel transaction under Rs 2000 remains FREE`() {
        viewModel.onQuickExampleSelected("1200", UpiCategory.FUEL)

        val result = viewModel.uiState.value.calculationResult!!
        assertEquals(1200.0, result.grossAmount, 0.001)
        assertEquals(0.0, result.mdrAmount, 0.001)
        assertEquals(0.0, result.totalCharges, 0.001)
        assertTrue(result.isFreeEverydayPayment)
    }

    @Test
    fun `P2P transactions of any value remain completely FREE`() {
        viewModel.onAmountChanged("50000")
        viewModel.onCategorySelected(UpiCategory.P2P)

        val result = viewModel.uiState.value.calculationResult!!
        assertEquals(0.0, result.mdrAmount, 0.001)
        assertEquals(0.0, result.totalCharges, 0.001)
        assertEquals(50000.0, result.netSettlement, 0.001)
        assertTrue(result.isFreeEverydayPayment)
    }

    @Test
    fun `small merchants P2PM remain FREE`() {
        viewModel.onAmountChanged("15000")
        viewModel.onCategorySelected(UpiCategory.SMALL_MERCHANT)

        val result = viewModel.uiState.value.calculationResult!!
        assertEquals(0.0, result.mdrAmount, 0.001)
        assertEquals(0.0, result.totalCharges, 0.001)
        assertEquals(15000.0, result.netSettlement, 0.001)
        assertTrue(result.isFreeEverydayPayment)
    }

    @Test
    fun `capital markets capped at Rs 300`() {
        // Rs 25,00,000 -> 0.02% is Rs 500, should be capped at Rs 300
        viewModel.onAmountChanged("2500000")
        viewModel.onCategorySelected(UpiCategory.CAPITAL_MARKETS)

        val result = viewModel.uiState.value.calculationResult!!
        assertEquals(300.0, result.mdrAmount, 0.001)
        assertTrue(result.isCapApplied)
    }

    @Test
    fun `calculation without GST toggle`() {
        viewModel.onQuickExampleSelected("5000", UpiCategory.REGULAR_P2M)
        viewModel.onIncludeGstToggled(false)

        val result = viewModel.uiState.value.calculationResult!!
        assertEquals(20.0, result.mdrAmount, 0.001)
        assertEquals(0.0, result.gstAmount, 0.001)
        assertEquals(20.0, result.totalCharges, 0.001)
        assertEquals(4980.0, result.netSettlement, 0.001)
    }

    @Test
    fun `reset clears calculations and inputs`() {
        viewModel.resetCalculator()
        val state = viewModel.uiState.value
        assertEquals("", state.amountText)
        assertNull(state.calculationResult)
        assertFalse(state.isCalculateEnabled)
    }
}
