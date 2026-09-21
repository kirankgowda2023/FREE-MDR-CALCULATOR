package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.BusinessProfileRepository
import com.example.ui.viewmodel.FreeMdrViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class FreeMdrViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var application: Application
    private lateinit var viewModel: FreeMdrViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        application = ApplicationProvider.getApplicationContext()
        val dao = AppDatabase.getInstance(application).businessProfileDao()
        val profileRepository = BusinessProfileRepository(dao, ioDispatcher = testDispatcher)
        viewModel = FreeMdrViewModel(application, profileRepository)
        ShadowLooper.idleMainLooper()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty amount text and zero parts without example presets`() = runTest {
        val state = viewModel.uiState.value
        assertEquals("", state.totalAmountText)
        assertEquals(0.0, state.parsedAmount, 0.001)
        assertTrue(state.parts.isEmpty())
        assertEquals(0.0, state.estimatedTotalSaved, 0.001)
    }

    @Test
    fun `default 5500 amount splits into exactly 1999, 1999, and 1502 parts`() = runTest {
        viewModel.onTotalAmountChanged("5500")
        viewModel.onMerchantUpiChanged("merchant@ptaxis")
        ShadowLooper.idleMainLooper()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(5500.0, state.parsedAmount, 0.001)
        assertEquals(3, state.parts.size)

        // Part 1
        assertEquals(1, state.parts[0].partIndex)
        assertEquals(1999.0, state.parts[0].amount, 0.001)
        assertTrue(state.parts[0].upiUri.contains("pa=merchant@ptaxis"))
        assertTrue(state.parts[0].upiUri.contains("am=1999.00"))

        // Part 2
        assertEquals(2, state.parts[1].partIndex)
        assertEquals(1999.0, state.parts[1].amount, 0.001)
        assertTrue(state.parts[1].upiUri.contains("am=1999.00"))

        // Part 3
        assertEquals(3, state.parts[2].partIndex)
        assertEquals(1502.0, state.parts[2].amount, 0.001)
        assertTrue(state.parts[2].upiUri.contains("am=1502.00"))

        // Sum of all parts equals total amount exactly
        val sum = state.parts.sumOf { it.amount }
        assertEquals(5500.0, sum, 0.001)

        // Normal MDR for 5500 @ 0.40% = 22.0, GST @ 18% = 3.96, Total saved = 25.96
        assertEquals(22.0, state.estimatedMdrSaved, 0.001)
        assertEquals(3.96, state.estimatedGstSaved, 0.001)
        assertEquals(25.96, state.estimatedTotalSaved, 0.001)
    }

    @Test
    fun `amount under 2000 generates single part with zero fees`() = runTest {
        viewModel.onTotalAmountChanged("1500")
        ShadowLooper.idleMainLooper()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.parts.size)
        assertEquals(1500.0, state.parts[0].amount, 0.001)
        assertEquals(0.0, state.estimatedMdrSaved, 0.001)
        assertEquals(0.0, state.estimatedTotalSaved, 0.001)
    }

    @Test
    fun `toggling part paid updates payment status`() = runTest {
        viewModel.onTotalAmountChanged("5500")
        ShadowLooper.idleMainLooper()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.parts[0].isPaid)

        viewModel.togglePartPaid(1)
        assertTrue(viewModel.uiState.value.parts[0].isPaid)

        viewModel.togglePartPaid(1)
        assertFalse(viewModel.uiState.value.parts[0].isPaid)
    }

    @Test
    fun `split limit of 2000 divides 6000 into 3 parts of 2000 each`() = runTest {
        viewModel.onSplitLimitChanged(2000.0)
        viewModel.onTotalAmountChanged("6000")
        ShadowLooper.idleMainLooper()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.parts.size)
        assertEquals(2000.0, state.parts[0].amount, 0.001)
        assertEquals(2000.0, state.parts[1].amount, 0.001)
        assertEquals(2000.0, state.parts[2].amount, 0.001)
    }

    @Test
    fun `editing and saving new upi id in VPA persists to profile and updates QR codes`() = runTest {
        viewModel.onTotalAmountChanged("5000")
        val newUpi = "newmerchant99@ptaxis"
        viewModel.onMerchantUpiChanged(newUpi)
        assertTrue(viewModel.uiState.value.hasUnsavedVpaChanges)

        viewModel.saveMerchantUpi()
        ShadowLooper.idleMainLooper()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(newUpi, state.merchantUpiId)
        assertFalse(state.hasUnsavedVpaChanges)
        assertTrue(state.isVpaSaved)
        assertNull(state.vpaError)

        // Verify QR codes use the newly saved UPI ID
        assertTrue(state.parts[0].upiUri.contains("pa=$newUpi"))
    }

    @Test
    fun `saving invalid upi id shows error and does not save`() = runTest {
        viewModel.onMerchantUpiChanged("invalidupiwithoutat")
        assertNotNull(viewModel.uiState.value.vpaError)

        viewModel.saveMerchantUpi()
        ShadowLooper.idleMainLooper()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.vpaError)
        assertFalse(viewModel.uiState.value.isVpaSaved)
    }
}
