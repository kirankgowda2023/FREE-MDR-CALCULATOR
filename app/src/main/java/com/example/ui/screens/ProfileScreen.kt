package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.BusinessProfileUiState
import com.example.ui.viewmodel.BusinessProfileViewModel
import com.example.ui.viewmodel.UpiCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onNavigateToMdrSplitter: () -> Unit = {},
    viewModel: BusinessProfileViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    // Automatically redirect back to MDR Splitter screen after saving
    LaunchedEffect(uiState.saveSuccessMessage) {
        if (uiState.saveSuccessMessage != null) {
            kotlinx.coroutines.delay(600)
            onNavigateToMdrSplitter()
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Business Profile",
                        style = SectionHeadingStyle.copy(fontWeight = FontWeight.Bold),
                        color = DeepNavy
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("profile_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Calculator",
                            tint = DeepNavy
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.saveBusinessProfile()
                        },
                        modifier = Modifier.testTag("profile_save_top_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = DeepNavy,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Save",
                                style = ButtonTextStyle.copy(color = DeepNavy)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBackground
                )
            )
        },
        containerColor = AppBackground,
        modifier = modifier
            .fillMaxSize()
            .testTag("business_profile_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Success Feedback Banner
            AnimatedVisibility(
                visible = uiState.saveSuccessMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SuccessBackground,
                    border = BorderStroke(1.dp, SuccessGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
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
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${uiState.saveSuccessMessage} Redirecting to MDR Splitter...",
                                style = BodyMediumStyle.copy(
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        IconButton(
                            onClick = { viewModel.clearSuccessMessage() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = SuccessGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Merchant Business Header Card
            MerchantHeaderCard(uiState = uiState)

            // Monthly UPI Fee & Turnover Projection Card
            MonthlyVolumeInsightCard(uiState = uiState)

            // Section 1: Business Details Form
            ProfileSectionCard(title = "Primary Business Details") {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Business Name
                    ProfileInputField(
                        value = uiState.businessName,
                        onValueChange = { viewModel.onBusinessNameChanged(it) },
                        label = "Business / Store Name *",
                        placeholder = "e.g. Kiran Retail & Electronics",
                        leadingIcon = Icons.Default.Home,
                        errorMessage = uiState.businessNameError,
                        testTag = "input_business_name"
                    )

                    // Owner Name
                    ProfileInputField(
                        value = uiState.ownerName,
                        onValueChange = { viewModel.onOwnerNameChanged(it) },
                        label = "Owner / Merchant Name",
                        placeholder = "e.g. Kiran Gowda",
                        leadingIcon = Icons.Default.Person,
                        testTag = "input_owner_name"
                    )

                    // Business Entity Type
                    Column {
                        Text(
                            text = "Business Entity Type",
                            style = CaptionStyle.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val types = listOf(
                            "Retail Store",
                            "Sole Proprietor",
                            "Partnership",
                            "Private Ltd",
                            "Freelancer / Service"
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            types.take(3).forEach { type ->
                                val isSelected = uiState.businessType == type
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.onBusinessTypeChanged(type) },
                                    label = {
                                        Text(
                                            text = type,
                                            style = CaptionStyle.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 11.sp
                                            )
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = DeepNavy,
                                        selectedLabelColor = Color.White,
                                        containerColor = InputBackground,
                                        labelColor = TextPrimary
                                    ),
                                    border = BorderStroke(1.dp, if (isSelected) DeepNavy else BorderColor)
                                )
                            }
                        }
                    }

                    // Merchant Category under Circular
                    Column {
                        Text(
                            text = "UPI Merchant Category (15 Oct 2026 Circular)",
                            style = CaptionStyle.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        UpiCategorySelector(
                            selectedCategory = uiState.selectedCategory,
                            onCategorySelected = { viewModel.onCategoryChanged(it) }
                        )
                    }
                }
            }

            // Section 2: UPI & Banking Identifiers
            ProfileSectionCard(title = "Payment & Tax Identifiers") {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // UPI ID / VPA
                    ProfileInputField(
                        value = uiState.upiVpa,
                        onValueChange = { viewModel.onUpiVpaChanged(it) },
                        label = "Merchant UPI VPA / ID",
                        placeholder = "e.g. your_business_upi_id@bankid",
                        leadingIcon = Icons.Default.AccountBox,
                        errorMessage = uiState.upiVpaError,
                        keyboardType = KeyboardType.Email,
                        testTag = "input_upi_vpa"
                    )

                    // UPI Caution Warning
                    Surface(
                        color = WarningOrange.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, WarningOrange.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
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

                    // GSTIN
                    ProfileInputField(
                        value = uiState.gstin,
                        onValueChange = { viewModel.onGstinChanged(it) },
                        label = "GSTIN Number (Optional)",
                        placeholder = "e.g. 29AABCU9603R1ZM",
                        leadingIcon = Icons.Default.Edit,
                        errorMessage = uiState.gstinError,
                        capitalization = KeyboardCapitalization.Characters,
                        testTag = "input_gstin"
                    )
                }
            }

            // Section 3: Contact & Store Location
            ProfileSectionCard(title = "Contact & Location") {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Phone
                    ProfileInputField(
                        value = uiState.phoneNumber,
                        onValueChange = { viewModel.onPhoneNumberChanged(it) },
                        label = "Contact Phone Number",
                        placeholder = "+91 98765 43210",
                        leadingIcon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone,
                        testTag = "input_phone"
                    )

                    // Email
                    ProfileInputField(
                        value = uiState.email,
                        onValueChange = { viewModel.onEmailChanged(it) },
                        label = "Email Address",
                        placeholder = "contact@business.com",
                        leadingIcon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email,
                        testTag = "input_email"
                    )

                    // Address
                    ProfileInputField(
                        value = uiState.address,
                        onValueChange = { viewModel.onAddressChanged(it) },
                        label = "Business Store Address",
                        placeholder = "Shop No., Street, City, State, PIN",
                        leadingIcon = Icons.Default.LocationOn,
                        singleLine = false,
                        testTag = "input_address"
                    )
                }
            }

            // Section 4: Monthly Volume & Estimation
            ProfileSectionCard(title = "Transaction Volume Estimate") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProfileInputField(
                        value = uiState.monthlyVolumeText,
                        onValueChange = { viewModel.onMonthlyVolumeChanged(it) },
                        label = "Estimated Monthly UPI Turnover (₹)",
                        placeholder = "e.g. 250000",
                        leadingIcon = Icons.Default.ShoppingCart,
                        keyboardType = KeyboardType.Number,
                        testTag = "input_monthly_volume"
                    )
                    Text(
                        text = "Used to estimate total monthly payment processing charges under your selected category rules.",
                        style = CaptionStyle.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                }
            }

            // Save Profile Button
            MdrPrimaryButton(
                text = if (uiState.isSaving) "Saving Information..." else "Save Business Profile",
                onClick = {
                    focusManager.clearFocus()
                    viewModel.saveBusinessProfile()
                },
                enabled = !uiState.isSaving && uiState.businessName.isNotBlank(),
                testTag = "save_business_profile_button",
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Header card displaying merchant identity and verified status
 */
@Composable
private fun MerchantHeaderCard(uiState: BusinessProfileUiState) {
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
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with business initials
                val initials = uiState.businessName
                    .split(" ")
                    .filter { it.isNotEmpty() }
                    .take(2)
                    .map { it.first().uppercase() }
                    .joinToString("")
                    .ifEmpty { "MB" }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(DeepNavy)
                ) {
                    Text(
                        text = initials,
                        style = SectionHeadingStyle.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.businessName.ifEmpty { "My Business" },
                        style = SectionHeadingStyle.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = TextPrimary
                    )
                    if (uiState.ownerName.isNotEmpty()) {
                        Text(
                            text = "Prop: ${uiState.ownerName}",
                            style = CaptionStyle.copy(color = TextSecondary)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SuccessBackground)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Verified Merchant",
                                    style = CaptionStyle.copy(
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PrimaryLight)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "P2M Enabled",
                                style = CaptionStyle.copy(
                                    color = DeepNavy,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            if (uiState.upiVpa.isNotEmpty() || uiState.gstin.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderColor, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (uiState.upiVpa.isNotEmpty()) {
                        Column {
                            Text(
                                text = "UPI VPA",
                                style = CaptionStyle.copy(fontSize = 10.sp, color = TextSecondary)
                            )
                            Text(
                                text = uiState.upiVpa,
                                style = CaptionStyle.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepNavy
                                )
                            )
                        }
                    }
                    if (uiState.gstin.isNotEmpty()) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "GSTIN",
                                style = CaptionStyle.copy(fontSize = 10.sp, color = TextSecondary)
                            )
                            Text(
                                text = uiState.gstin,
                                style = CaptionStyle.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepNavy
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Monthly Volume & Projected Fee Insight
 */
@Composable
private fun MonthlyVolumeInsightCard(uiState: BusinessProfileUiState) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Projected Monthly UPI Cost",
                    style = CaptionStyle.copy(fontWeight = FontWeight.Bold),
                    color = DeepNavy
                )
                Text(
                    text = "Rule: ${uiState.selectedCategory.title}",
                    style = CaptionStyle.copy(fontSize = 11.sp, color = TextSecondary)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Monthly Turnover",
                        style = CaptionStyle.copy(fontSize = 11.sp, color = TextSecondary)
                    )
                    Text(
                        text = "₹%,.0f".format(uiState.estimatedMonthlyVolume),
                        style = SectionHeadingStyle.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Est. MDR Fee",
                        style = CaptionStyle.copy(fontSize = 11.sp, color = TextSecondary)
                    )
                    Text(
                        text = "₹%,.2f".format(uiState.estimatedMonthlyMdr),
                        style = SectionHeadingStyle.copy(
                            fontWeight = FontWeight.Bold,
                            color = WarningOrange
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total Charges (incl. GST)",
                        style = CaptionStyle.copy(fontSize = 11.sp, color = TextSecondary)
                    )
                    Text(
                        text = "₹%,.2f".format(uiState.estimatedTotalMonthlyCharges),
                        style = SectionHeadingStyle.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = SuccessGreen
                        )
                    )
                }
            }
        }
    }
}

/**
 * Reusable Section Card
 */
@Composable
private fun ProfileSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = SectionHeadingStyle.copy(fontWeight = FontWeight.Bold),
                color = DeepNavy
            )
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

/**
 * Form Input Field for Profile Screen
 */
@Composable
private fun ProfileInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Words,
    testTag: String = ""
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = CaptionStyle.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, style = BodyStyle, color = TextSecondary.copy(alpha = 0.6f)) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = DeepNavy,
                    modifier = Modifier.size(20.dp)
                )
            },
            isError = errorMessage != null,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                capitalization = capitalization,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                focusedBorderColor = DeepNavy,
                unfocusedBorderColor = BorderColor,
                errorBorderColor = DangerRed,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = errorMessage,
                style = CaptionStyle.copy(color = DangerRed),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
