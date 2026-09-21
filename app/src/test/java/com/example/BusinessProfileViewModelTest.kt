package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.BusinessProfileRepository
import com.example.ui.viewmodel.BusinessProfileViewModel
import com.example.ui.viewmodel.UpiCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BusinessProfileViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var application: Application
    private lateinit var viewModel: BusinessProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        application = ApplicationProvider.getApplicationContext()
        val dao = AppDatabase.getInstance(application).businessProfileDao()
        val repository = BusinessProfileRepository(dao, ioDispatcher = testDispatcher)
        viewModel = BusinessProfileViewModel(application, repository)
        ShadowLooper.idleMainLooper()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `default business profile state has valid initial turnover and category`() {
        val state = viewModel.uiState.value
        assertEquals("250000", state.monthlyVolumeText)
        assertEquals(250000.0, state.estimatedMonthlyVolume, 0.001)
        assertEquals(UpiCategory.REGULAR_P2M, state.selectedCategory)
        // 250000 * 0.40% = 1000.0
        assertEquals(1000.0, state.estimatedMonthlyMdr, 0.001)
        // GST 18% of 1000 = 180.0
        assertEquals(180.0, state.estimatedMonthlyGst, 0.001)
        assertEquals(1180.0, state.estimatedTotalMonthlyCharges, 0.001)
    }

    @Test
    fun `updating business details modifies UI state`() {
        viewModel.onBusinessNameChanged("Kiran Electronics & Appliances")
        viewModel.onOwnerNameChanged("Kiran G")
        viewModel.onUpiVpaChanged("kiranelectronics@upi")
        viewModel.onGstinChanged("29AABCU9603R1ZM")
        viewModel.onBusinessTypeChanged("Retail Store")
        viewModel.onCategoryChanged(UpiCategory.REGULAR_P2M)

        val state = viewModel.uiState.value
        assertEquals("Kiran Electronics & Appliances", state.businessName)
        assertEquals("Kiran G", state.ownerName)
        assertEquals("kiranelectronics@upi", state.upiVpa)
        assertEquals("29AABCU9603R1ZM", state.gstin)
        assertNull(state.businessNameError)
        assertNull(state.upiVpaError)
        assertNull(state.gstinError)
    }

    @Test
    fun `validation catches invalid UPI ID format`() {
        viewModel.onUpiVpaChanged("invalidupiidwithoutat")
        val state = viewModel.uiState.value
        assertNotNull(state.upiVpaError)
    }

    @Test
    fun `validation catches empty business name on save`() {
        viewModel.onBusinessNameChanged("")
        viewModel.saveBusinessProfile()
        val state = viewModel.uiState.value
        assertNotNull(state.businessNameError)
    }

    @Test
    fun `saving valid profile persists successfully`() = runTest(testDispatcher) {
        viewModel.onBusinessNameChanged("Kiran Supermarket")
        viewModel.onOwnerNameChanged("Kiran")
        viewModel.onUpiVpaChanged("kiransupermarket@upi")
        viewModel.saveBusinessProfile()
        ShadowLooper.idleMainLooper()
        advanceUntilIdle()

        val state = withTimeoutOrNull(3000) {
            viewModel.uiState.first { it.saveSuccessMessage != null }
        } ?: viewModel.uiState.value
        assertEquals("Business profile updated successfully!", state.saveSuccessMessage)
    }
}
