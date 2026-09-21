package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreeMdrPart
import com.example.ui.viewmodel.FreeMdrViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeMdrScreen(
    onNavigateToCalculator: () -> Unit = {},
    viewModel: FreeMdrViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var showConfigOptions by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .background(SuccessBackground, shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Free MDR Splitter",
                                    style = HeadingStyle.copy(fontSize = 18.sp, color = DeepNavy),
                                    modifier = Modifier.testTag("free_mdr_title")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = SuccessBackground,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "0% MDR",
                                        style = CaptionStyle.copy(
                                            color = SuccessGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Distribute payments into zero-MDR QR codes",
                                style = CaptionStyle.copy(color = TextSecondary, fontSize = 11.sp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showConfigOptions = !showConfigOptions },
                        modifier = Modifier.testTag("toggle_settings_button")
                    ) {
                        Icon(
                            imageVector = if (showConfigOptions) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = if (showConfigOptions) "Close Settings" else "Edit Merchant Settings",
                            tint = if (uiState.hasUnsavedVpaChanges) WarningOrange else DeepNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBackground,
                    titleContentColor = DeepNavy
                )
            )
        },
        containerColor = AppBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Zero Fee Banner with circular citation
            Surface(
                color = SuccessBackground,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .testTag("zero_mdr_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, shape = CircleShape)
                    ) {
                        Text(
                            text = "₹0",
                            style = HeadingStyle.copy(
                                color = SuccessGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "100% Zero MDR Guarantee",
                            style = SectionHeadingStyle.copy(
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = "Under the Oct 15, 2026 circular, P2M UPI transactions ≤ ₹2,000 carry 0% MDR. Splitting distributes payments into legal free limit parts.",
                            style = CaptionStyle.copy(
                                color = SuccessGreen.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Optional Config Collapsible (Merchant UPI VPA & Split Limit)
            AnimatedVisibility(visible = showConfigOptions) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                        .testTag("merchant_config_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Merchant VPA & Split Settings",
                                style = SectionHeadingStyle.copy(fontSize = 14.sp, color = DeepNavy)
                            )
                            IconButton(
                                onClick = { showConfigOptions = false },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("close_settings_card_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Return to Splitter",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        // Merchant UPI VPA Input
                        OutlinedTextField(
                            value = uiState.merchantUpiId,
                            onValueChange = { viewModel.onMerchantUpiChanged(it) },
                            label = { Text("Merchant UPI ID (VPA) *") },
                            placeholder = { Text("your_business_upi_id@bankid") },
                            supportingText = {
                                Text(
                                    text = "e.g. your_business_upi_id@bankid",
                                    style = CaptionStyle.copy(color = TextSecondary, fontSize = 11.sp)
                                )
                            },
                            singleLine = true,
                            isError = uiState.vpaError != null,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    viewModel.saveMerchantUpi()
                                    showConfigOptions = false
                                }
                            ),
                            trailingIcon = {
                                if (uiState.hasUnsavedVpaChanges) {
                                    IconButton(
                                        onClick = {
                                            focusManager.clearFocus()
                                            viewModel.saveMerchantUpi()
                                            showConfigOptions = false
                                        },
                                        modifier = Modifier.testTag("quick_save_vpa_icon_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Save UPI ID",
                                            tint = SuccessGreen
                                        )
                                    }
                                } else if (uiState.isVpaSaved) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "VPA Saved",
                                        tint = SuccessGreen
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = InputBackground,
                                unfocusedContainerColor = InputBackground,
                                focusedBorderColor = DeepNavy,
                                unfocusedBorderColor = BorderColor,
                                errorBorderColor = DangerRed
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("merchant_vpa_input")
                        )

                        if (uiState.vpaError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.vpaError!!,
                                style = CaptionStyle.copy(color = DangerRed),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        } else if (uiState.isVpaSaved) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "✓ New UPI ID saved to profile",
                                style = CaptionStyle.copy(color = SuccessGreen, fontWeight = FontWeight.SemiBold),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        } else if (uiState.hasUnsavedVpaChanges) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Edited • Tap 'Save UPI ID' below to save",
                                style = CaptionStyle.copy(color = TextSecondary),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        // Caution Notice
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = WarningOrange.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, WarningOrange.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("caution_wrong_upi_settings_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Caution",
                                    tint = WarningOrange,
                                    modifier = Modifier.size(16.dp).padding(top = 1.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Wrong UPI ID may lead to wrong vendor payment. Check your UPI ID before proceeding.",
                                    style = CaptionStyle.copy(
                                        color = Color(0xFF9A3412),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Save VPA and Reset Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.saveMerchantUpi()
                                    showConfigOptions = false
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DeepNavy,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("save_vpa_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Save UPI ID",
                                    style = ButtonTextStyle.copy(fontSize = 13.sp, color = Color.White)
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.resetVpaToProfile()
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, BorderColor),
                                modifier = Modifier
                                    .height(42.dp)
                                    .testTag("reset_vpa_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Reset",
                                    style = CaptionStyle.copy(color = TextSecondary, fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Split Limit Toggle: 1999 (Recommended Safe) vs 2000
                        Text(
                            text = "Safe Split Limit per QR Code:",
                            style = CaptionStyle.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                1999.0 to "₹1,999 (Safe < ₹2,000)",
                                2000.0 to "₹2,000 (Exact Cap)"
                            ).forEach { (limit, label) ->
                                val isSelected = uiState.splitLimit == limit
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.onSplitLimitChanged(limit) },
                                    label = {
                                        Text(
                                            text = label,
                                            style = CaptionStyle.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp
                                            )
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = DeepNavy,
                                        selectedLabelColor = Color.White,
                                        containerColor = InputBackground,
                                        labelColor = TextPrimary
                                    ),
                                    border = BorderStroke(1.dp, if (isSelected) DeepNavy else BorderColor),
                                    modifier = Modifier.testTag("split_limit_chip_${limit.toInt()}")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                focusManager.clearFocus()
                                showConfigOptions = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BorderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("done_settings_button")
                        ) {
                            Text(
                                text = "Done & View Split QR Codes",
                                style = CaptionStyle.copy(color = DeepNavy, fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }

            // Top Feedback Toast if VPA was saved or copied
            AnimatedVisibility(visible = uiState.copyFeedback != null) {
                Surface(
                    color = DeepNavy,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("vpa_saved_feedback_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.copyFeedback ?: "",
                                style = CaptionStyle.copy(color = Color.White, fontWeight = FontWeight.Medium)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.clearFeedback() },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (!uiState.isUpiConfigured) {
                // Mandatory 1st-Time UPI ID Setup Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.5.dp, DeepNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("mandatory_upi_setup_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(WarningOrange.copy(alpha = 0.15f), shape = CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = WarningOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Mandatory UPI ID Setup",
                                    style = SectionHeadingStyle.copy(fontSize = 15.sp, color = DeepNavy)
                                )
                                Text(
                                    text = "Update your UPI ID to activate the FREE MDR Feature.",
                                    style = CaptionStyle.copy(color = TextSecondary, fontSize = 11.sp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Merchant UPI VPA Input
                        OutlinedTextField(
                            value = uiState.merchantUpiId,
                            onValueChange = { viewModel.onMerchantUpiChanged(it) },
                            label = { Text("Merchant UPI ID (VPA) *") },
                            placeholder = { Text("your_business_upi_id@bankid") },
                            supportingText = {
                                Text(
                                    text = "e.g. your_business_upi_id@bankid",
                                    style = CaptionStyle.copy(color = TextSecondary, fontSize = 11.sp)
                                )
                            },
                            singleLine = true,
                            isError = uiState.vpaError != null,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    viewModel.saveMerchantUpi()
                                }
                            ),
                            trailingIcon = {
                                if (uiState.merchantUpiId.isNotBlank() && uiState.vpaError == null) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Format Valid",
                                        tint = SuccessGreen
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = InputBackground,
                                unfocusedContainerColor = InputBackground,
                                focusedBorderColor = DeepNavy,
                                unfocusedBorderColor = BorderColor,
                                errorBorderColor = DangerRed
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("mandatory_upi_vpa_input")
                        )

                        if (uiState.vpaError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.vpaError!!,
                                style = CaptionStyle.copy(color = DangerRed),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        // Mandatory Caution
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = WarningOrange.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, WarningOrange.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("caution_wrong_upi_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Caution",
                                    tint = WarningOrange,
                                    modifier = Modifier.size(16.dp).padding(top = 1.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Wrong UPI ID may lead to wrong vendor payment. Check your UPI ID before proceeding.",
                                    style = CaptionStyle.copy(
                                        color = Color(0xFF9A3412),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Save UPI ID Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.saveMerchantUpi()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepNavy,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("mandatory_save_upi_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save UPI ID",
                                style = ButtonTextStyle.copy(fontSize = 14.sp, color = Color.White)
                            )
                        }
                    }
                }

                // Feature Locked Placeholder Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.6f)),
                    border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.8f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("locked_free_mdr_feature_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(56.dp)
                                .background(InputBackground, shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked Feature",
                                tint = TextSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "MDR Feature Locked",
                            style = SectionHeadingStyle.copy(fontSize = 16.sp, color = DeepNavy)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Update & save your UPI ID to unlock free MDR calculation.",
                            style = CaptionStyle.copy(
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            } else {
                // Input Card: Total Transaction Amount
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, BorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("amount_input_card")
                ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Transaction Amount",
                            style = SectionHeadingStyle.copy(fontSize = 14.sp, color = DeepNavy)
                        )
                        // Current Active UPI ID pill
                        Surface(
                            color = PrimaryLight,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { showConfigOptions = !showConfigOptions }
                        ) {
                            Text(
                                text = "VPA: ${uiState.merchantUpiId}",
                                style = CaptionStyle.copy(
                                    color = DeepNavy,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Amount Text Field
                    OutlinedTextField(
                        value = uiState.totalAmountText,
                        onValueChange = { viewModel.onTotalAmountChanged(it) },
                        leadingIcon = {
                            Text(
                                text = "₹",
                                style = HeadingStyle.copy(color = DeepNavy, fontSize = 22.sp),
                                modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                            )
                        },
                        trailingIcon = {
                            if (uiState.totalAmountText.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onTotalAmountChanged("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear Amount",
                                        tint = TextSecondary
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        placeholder = {
                            Text(
                                text = "Enter amount",
                                style = LargeAmountStyle.copy(color = TextMuted)
                            )
                        },
                        textStyle = LargeAmountStyle.copy(color = TextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputBackground,
                            unfocusedContainerColor = InputBackground,
                            focusedBorderColor = DeepNavy,
                            unfocusedBorderColor = BorderColor,
                            errorBorderColor = DangerRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        isError = uiState.errorMessage != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("free_mdr_amount_input")
                    )

                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = CaptionStyle.copy(color = DangerRed)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Results Section: Only when parts exist
            if (uiState.parts.isNotEmpty()) {
                val currentPart = uiState.parts.getOrNull(uiState.selectedPartIndex) ?: uiState.parts.first()
                val totalPartsCount = uiState.parts.size

                // Summary of split and savings
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("savings_summary_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Generated Split Breakdown",
                                    style = SectionHeadingStyle.copy(fontSize = 14.sp, color = DeepNavy)
                                )
                                Text(
                                    text = "$totalPartsCount Free Limit QR Code${if (totalPartsCount > 1) "s" else ""}",
                                    style = CaptionStyle.copy(color = TextSecondary, fontSize = 11.sp)
                                )
                            }

                            // Savings Pill
                            Surface(
                                color = SuccessBackground,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Saved ₹${String.format(Locale.US, "%.2f", uiState.estimatedTotalSaved)}",
                                        style = CaptionStyle.copy(
                                            color = SuccessGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Part Distribution Summary row (e.g. 1999 + 1999 + 1502 = 5500)
                        Surface(
                            color = InputBackground,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Distribution Formula:",
                                    style = CaptionStyle.copy(
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val formula = uiState.parts.joinToString(" + ") { "₹${it.amountFormatted}" }
                                Text(
                                    text = "$formula = ₹${String.format(Locale.US, "%,d", uiState.parsedAmount.toLong())}",
                                    style = BodyMediumStyle.copy(
                                        color = DeepNavy,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // If multiple parts, display Segmented Tab Selector
                if (totalPartsCount > 1) {
                    Text(
                        text = "Select QR Code to Present (${uiState.selectedPartIndex + 1} of $totalPartsCount):",
                        style = SectionHeadingStyle.copy(fontSize = 13.sp, color = DeepNavy)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.parts.forEachIndexed { idx, part ->
                            val isSelected = uiState.selectedPartIndex == idx
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = when {
                                    isSelected -> DeepNavy
                                    part.isPaid -> SuccessBackground
                                    else -> CardBackground
                                },
                                border = BorderStroke(
                                    1.dp,
                                    when {
                                        isSelected -> DeepNavy
                                        part.isPaid -> SuccessGreen
                                        else -> BorderColor
                                    }
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.onSelectPart(idx) }
                                    .testTag("select_part_tab_$idx")
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "QR Code ${part.partIndex}",
                                        style = CaptionStyle.copy(
                                            color = when {
                                                isSelected -> Color.White
                                                part.isPaid -> SuccessGreen
                                                else -> DeepNavy
                                            },
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "₹${part.amountFormatted}",
                                        style = BodyMediumStyle.copy(
                                            color = when {
                                                isSelected -> Color.White
                                                part.isPaid -> SuccessGreen
                                                else -> TextPrimary
                                            },
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp
                                        )
                                    )
                                    if (part.isPaid) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "PAID",
                                            style = CaptionStyle.copy(
                                                color = if (isSelected) Color.White else SuccessGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // PRIMARY QR CODE DISPLAY CARD
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, BorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_qr_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Part badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                color = if (currentPart.isPaid) SuccessBackground else PrimaryLight,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (currentPart.isPaid) Icons.Default.CheckCircle else Icons.Outlined.QrCodeScanner,
                                        contentDescription = null,
                                        tint = if (currentPart.isPaid) SuccessGreen else DeepNavy,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "QR Code ${currentPart.partIndex} of $totalPartsCount",
                                        style = CaptionStyle.copy(
                                            color = if (currentPart.isPaid) SuccessGreen else DeepNavy,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Amount to pay for this specific QR
                        Text(
                            text = "₹${currentPart.amountFormatted}",
                            style = DisplayStyle.copy(
                                color = DeepNavy,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            modifier = Modifier.testTag("active_qr_amount")
                        )
                        Text(
                            text = "Zero MDR Threshold Compliant",
                            style = CaptionStyle.copy(
                                color = SuccessGreen,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // QR Code Image Container
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(240.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .border(2.dp, BorderColor, RoundedCornerShape(16.dp))
                                .padding(12.dp)
                                .testTag("qr_code_image_container")
                        ) {
                            if (currentPart.qrBitmap != null) {
                                Image(
                                    bitmap = currentPart.qrBitmap.asImageBitmap(),
                                    contentDescription = "UPI Payment QR Code for ₹${currentPart.amountFormatted}",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                CircularProgressIndicator(
                                    color = DeepNavy,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Merchant VPA & Instruction
                        Text(
                            text = "Pay to: ${uiState.merchantUpiId}",
                            style = BodyMediumStyle.copy(
                                color = DeepNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = "Scan using Google Pay, PhonePe, Paytm, or BHIM",
                            style = CaptionStyle.copy(color = TextSecondary, fontSize = 11.sp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Actions Row: Mark as Paid & Copy Link & Share
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Mark Paid Toggle Button
                            Button(
                                onClick = { viewModel.togglePartPaid(currentPart.partIndex) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentPart.isPaid) SuccessBackground else DeepNavy,
                                    contentColor = if (currentPart.isPaid) SuccessGreen else Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("mark_paid_button")
                            ) {
                                Icon(
                                    imageVector = if (currentPart.isPaid) Icons.Default.CheckCircle else Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentPart.isPaid) "Marked Paid ✓" else "Mark Paid",
                                    style = CaptionStyle.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                )
                            }

                            // Copy UPI URI Link
                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(currentPart.upiUri))
                                    viewModel.setCopyFeedback("UPI Link for Part ${currentPart.partIndex} (₹${currentPart.amountFormatted}) copied!")
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, BorderColor),
                                modifier = Modifier.testTag("copy_upi_link_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy UPI URI Link",
                                    tint = DeepNavy,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Share Intent
                            OutlinedButton(
                                onClick = {
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "UPI Payment request for ₹${currentPart.amountFormatted} (Part ${currentPart.partIndex} of $totalPartsCount):\n${currentPart.upiUri}"
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Payment Link"))
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, BorderColor),
                                modifier = Modifier.testTag("share_upi_link_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = DeepNavy,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Next QR button if multiple parts
                        if (totalPartsCount > 1) {
                            Spacer(modifier = Modifier.height(10.dp))
                            val nextIndex = (uiState.selectedPartIndex + 1) % totalPartsCount
                            TextButton(
                                onClick = { viewModel.onSelectPart(nextIndex) },
                                modifier = Modifier.testTag("switch_to_next_part_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (uiState.selectedPartIndex < totalPartsCount - 1)
                                            "Proceed to QR Code ${uiState.selectedPartIndex + 2} (₹${uiState.parts[nextIndex].amountFormatted}) →"
                                        else
                                            "Back to QR Code 1 (₹${uiState.parts[0].amountFormatted}) ↺",
                                        style = CaptionStyle.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DeepNavy,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ALL PARTS LIST CARD
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("all_parts_overview_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "All QR Codes Checklist",
                            style = SectionHeadingStyle.copy(fontSize = 14.sp, color = DeepNavy)
                        )
                        Text(
                            text = "Track payment receipt for each split code",
                            style = CaptionStyle.copy(color = TextSecondary, fontSize = 11.sp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        uiState.parts.forEach { part ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (part.isPaid) SuccessBackground.copy(alpha = 0.5f) else InputBackground,
                                border = BorderStroke(1.dp, if (part.isPaid) SuccessGreen.copy(alpha = 0.4f) else BorderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { viewModel.onSelectPart(part.partIndex - 1) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = part.isPaid,
                                            onCheckedChange = { viewModel.togglePartPaid(part.partIndex) },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = SuccessGreen,
                                                uncheckedColor = TextSecondary
                                            ),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "QR Code ${part.partIndex} of $totalPartsCount",
                                                style = CaptionStyle.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = DeepNavy,
                                                    fontSize = 12.sp
                                                )
                                            )
                                            Text(
                                                text = if (part.isPaid) "Payment Received" else "Pending customer scan",
                                                style = CaptionStyle.copy(
                                                    color = if (part.isPaid) SuccessGreen else TextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "₹${part.amountFormatted}",
                                            style = BodyMediumStyle.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = DeepNavy,
                                                fontSize = 14.sp
                                            )
                                        )
                                        Text(
                                            text = "0% MDR",
                                            style = CaptionStyle.copy(
                                                color = SuccessGreen,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .testTag("empty_amount_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(56.dp)
                                .background(PrimaryLight, shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "Enter Amount",
                                tint = DeepNavy,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Enter Customer Bill Amount",
                            style = SectionHeadingStyle.copy(fontSize = 16.sp, color = DeepNavy)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Type any transaction amount above to generate zero-MDR QR codes split under ₹2,000 each.",
                            style = CaptionStyle.copy(color = TextSecondary, textAlign = TextAlign.Center)
                        )
                    }
                }
            }
        }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
