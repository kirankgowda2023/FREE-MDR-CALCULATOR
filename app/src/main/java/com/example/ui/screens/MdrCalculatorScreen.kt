package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MdrCalculatorViewModel
import com.example.ui.viewmodel.UpiCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MdrCalculatorScreen(
    onOpenProfile: () -> Unit = {},
    onOpenFreeMdr: () -> Unit = {},
    onOpenDesignSystem: () -> Unit = {},
    viewModel: MdrCalculatorViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showInfoDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = AppBackground,
        modifier = modifier
            .fillMaxSize()
            .testTag("mdr_calculator_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UPI Fee Calculator",
                        style = HeadingStyle,
                        color = DeepNavy
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "New UPI Fee Structure • Effective 15 Oct 2026",
                        style = CaptionStyle.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WarningOrange
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile Action Button
                    IconButton(
                        onClick = onOpenProfile,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("open_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Merchant Business Profile",
                            tint = DeepNavy
                        )
                    }

                    // Design System Action
                    IconButton(
                        onClick = onOpenDesignSystem,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("view_design_system_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = "View Design System Catalog",
                            tint = DeepNavy
                        )
                    }

                    // Help / Circular Details Action
                    IconButton(
                        onClick = { showInfoDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("open_info_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Circular Details & Rules",
                            tint = DeepNavy
                        )
                    }
                }
            }

            // Quick Business Profile Link
            Surface(
                onClick = onOpenProfile,
                shape = RoundedCornerShape(12.dp),
                color = CardBackground,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("merchant_profile_quick_link")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .background(PrimaryLight, shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBox,
                                contentDescription = null,
                                tint = DeepNavy,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Merchant Business Profile",
                                style = CaptionStyle.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "View & update basic business info, GSTIN & UPI VPA",
                                style = CaptionStyle.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                    Text(
                        text = "Edit →",
                        style = CaptionStyle.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy,
                            fontSize = 12.sp
                        )
                    )
                }
            }


            // Circular Policy Announcement Banner
            UpiPolicyAnnouncementBanner(
                onLearnMoreClick = { showInfoDialog = true },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Transaction Details Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardBackground,
                border = BorderStroke(1.dp, BorderColor),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Transaction Details",
                            style = SectionHeadingStyle.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )

                        Text(
                            text = "UPI (P2M)",
                            style = CaptionStyle.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy
                            )
                        )
                    }

                    // FIELD 1: Category Selector (from official circular)
                    UpiCategorySelector(
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = { viewModel.onCategorySelected(it) }
                    )

                    // FIELD 2: Transaction Amount Input
                    MdrAmountInput(
                        value = uiState.amountText,
                        onValueChange = { viewModel.onAmountChanged(it) },
                        label = "Transaction Amount",
                        placeholder = "e.g. 5000",
                        errorMessage = uiState.amountErrorMessage
                    )

                    // FIELD 3: Rule Overview / Custom Rate
                    if (uiState.selectedCategory == UpiCategory.CUSTOM) {
                        MdrPercentageInput(
                            value = uiState.customMdrRateText,
                            onValueChange = { viewModel.onCustomMdrRateChanged(it) },
                            label = "Custom MDR Rate",
                            supportingText = "Enter negotiated merchant discount rate percentage.",
                            errorMessage = uiState.mdrErrorMessage,
                            onInfoClick = { showInfoDialog = true }
                        )

                        MdrFixedFeeInput(
                            value = uiState.customFixedFeeText,
                            onValueChange = { viewModel.onCustomFixedFeeChanged(it) },
                            label = "Fixed Fee",
                            supportingText = "Optional fixed gateway charge per transaction."
                        )
                    } else {
                        // Official Applied Rule Card
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PrimaryLight.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, PrimaryLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.VerifiedUser,
                                    contentDescription = null,
                                    tint = DeepNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Official Applicable Rate",
                                        style = CaptionStyle.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DeepNavy
                                        )
                                    )
                                    Text(
                                        text = uiState.selectedCategory.ruleSummary,
                                        style = CaptionStyle.copy(
                                            fontSize = 11.sp,
                                            color = TextPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // FIELD 4: GST on MDR (18%)
                    MdrGstToggle(
                        checked = uiState.includeGst,
                        onCheckedChange = { viewModel.onIncludeGstToggled(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // CALCULATE BUTTON
            MdrPrimaryButton(
                text = "Calculate Settlement",
                onClick = { viewModel.calculateSettlement() },
                enabled = uiState.isCalculateEnabled && uiState.amountText.isNotEmpty(),
                testTag = "calculate_settlement_button"
            )

            Spacer(modifier = Modifier.height(22.dp))

            // RESULT SECTION
            val result = uiState.calculationResult
            if (result == null) {
                MdrEmptyStateCard()
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Settlement Result Card with circular badges and rule text
                    MdrResultCard(
                        grossAmount = result.grossAmount,
                        mdrRate = result.effectiveMdrRate,
                        mdrRateDisplay = result.mdrRateDisplay,
                        mdrAmount = result.mdrAmount,
                        gstAmount = result.gstAmount,
                        fixedFee = result.fixedFee,
                        totalCharges = result.totalCharges,
                        netSettlement = result.netSettlement,
                        includeGst = result.includeGst,
                        ruleAppliedText = result.ruleAppliedText,
                        isCapApplied = result.isCapApplied,
                        isFreeEverydayPayment = result.isFreeEverydayPayment,
                        categoryName = result.category.title
                    )

                    // Optional suggestion to split into Zero-MDR QR Codes if fees apply
                    if (result.totalCharges > 0.0) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SuccessBackground,
                            border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenFreeMdr() }
                                .testTag("free_mdr_suggestion_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.QrCodeScanner,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Save ₹${String.format(java.util.Locale.US, "%.2f", result.totalCharges)} with Free MDR!",
                                            style = BodyMediumStyle.copy(
                                                color = SuccessGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        )
                                        Text(
                                            text = "Distribute into zero-fee QR codes (sub-₹2,000)",
                                            style = CaptionStyle.copy(color = TextSecondary, fontSize = 11.sp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Split Now →",
                                    style = CaptionStyle.copy(
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }

                    // Visual Flow
                    MdrVisualFlow(
                        grossAmount = result.grossAmount,
                        mdrAmount = result.mdrAmount,
                        gstAmount = result.gstAmount,
                        fixedFee = result.fixedFee,
                        netSettlement = result.netSettlement,
                        includeGst = result.includeGst
                    )

                    // Quick Insight Card
                    MdrInsightCard(costPercentage = result.costPercentage)

                    // Reset Action
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MdrResetButton(onClick = { viewModel.resetCalculator() })
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showInfoDialog) {
        MdrInfoDialog(onDismiss = { showInfoDialog = false })
    }
}
