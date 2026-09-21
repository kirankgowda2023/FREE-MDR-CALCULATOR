package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PaymentMethod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignSystemScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var interactiveAmount by remember { mutableStateOf("10000") }
    var interactiveMdr by remember { mutableStateOf("150") } // Error demo value
    var interactiveMethod by remember { mutableStateOf(PaymentMethod.UPI) }
    var interactiveToggle by remember { mutableStateOf(true) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedDropdownOption by remember { mutableStateOf("POS Terminal (Standard)") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Design System",
                            style = SectionHeadingStyle.copy(fontWeight = FontWeight.Bold),
                            color = DeepNavy
                        )
                        Text(
                            text = "MDR Calculator Component & Token Library",
                            style = CaptionStyle,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("ds_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Calculator",
                            tint = DeepNavy
                        )
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
            .testTag("design_system_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Color Palette Section
            DsSection(title = "1. Color Palette") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ColorSwatchRow(name = "Primary Deep Navy", hex = "#172554", color = DeepNavy, isDark = true)
                    ColorSwatchRow(name = "Primary Dark", hex = "#0F172A", color = PrimaryDark, isDark = true)
                    ColorSwatchRow(name = "Primary Light", hex = "#E8EEF9", color = PrimaryLight, isDark = false)
                    ColorSwatchRow(name = "Success Green", hex = "#15803D", color = SuccessGreen, isDark = true)
                    ColorSwatchRow(name = "Success Background", hex = "#DCFCE7", color = SuccessBackground, isDark = false)
                    ColorSwatchRow(name = "Warning Orange", hex = "#D97706", color = WarningOrange, isDark = true)
                    ColorSwatchRow(name = "Warning Background", hex = "#FEF3C7", color = WarningBackground, isDark = false)
                    ColorSwatchRow(name = "Danger Red", hex = "#DC2626", color = DangerRed, isDark = true)
                    ColorSwatchRow(name = "Danger Background", hex = "#FEE2E2", color = DangerBackground, isDark = false)
                    ColorSwatchRow(name = "App Background", hex = "#F8FAFC", color = AppBackground, isDark = false)
                    ColorSwatchRow(name = "Card Surface", hex = "#FFFFFF", color = CardBackground, isDark = false)
                    ColorSwatchRow(name = "Border", hex = "#E2E8F0", color = BorderColor, isDark = false)
                    ColorSwatchRow(name = "Primary Text", hex = "#0F172A", color = TextPrimary, isDark = true)
                    ColorSwatchRow(name = "Secondary Text", hex = "#64748B", color = TextSecondary, isDark = true)
                    ColorSwatchRow(name = "Muted Text", hex = "#94A3B8", color = TextMuted, isDark = false)
                }
            }

            // 2. Typography Section
            DsSection(title = "2. Typography Scale (Inter / SansSerif)") {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    TypographySpecimen(label = "Display: 32px / Bold", text = "₹9,823.00", style = DisplayStyle)
                    TypographySpecimen(label = "Large Amount: 28px / Bold", text = "₹10,000.00", style = LargeAmountStyle)
                    TypographySpecimen(label = "Heading: 22px / Bold", text = "MDR Calculator", style = HeadingStyle)
                    TypographySpecimen(label = "Section Heading: 17px / Semi Bold", text = "Transaction Details", style = SectionHeadingStyle)
                    TypographySpecimen(label = "Body: 14px / Regular", text = "GST is calculated on the MDR amount.", style = BodyStyle)
                    TypographySpecimen(label = "Body Medium: 14px / Medium", text = "Net amount you receive", style = BodyMediumStyle)
                    TypographySpecimen(label = "Caption: 12px / Regular", text = "Optional fixed fee per transaction.", style = CaptionStyle)
                    TypographySpecimen(label = "Button: 15px / Semi Bold", text = "Calculate Settlement", style = ButtonTextStyle.copy(color = DeepNavy))
                }
            }

            // 3. Spacing System
            DsSection(title = "3. Spacing & Touch Metrics") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SpacingTokenRow(label = "4px (Compact Micro)", size = 4.dp)
                    SpacingTokenRow(label = "8px (Base Unit / Item Gap)", size = 8.dp)
                    SpacingTokenRow(label = "12px (Input Corner / Medium Gap)", size = 12.dp)
                    SpacingTokenRow(label = "16px (Card Internal Padding)", size = 16.dp)
                    SpacingTokenRow(label = "20px (Screen Horizontal Margin)", size = 20.dp)
                    SpacingTokenRow(label = "24px (Section Spacing)", size = 24.dp)
                    SpacingTokenRow(label = "32px (Large Break)", size = 32.dp)
                    SpacingTokenRow(label = "40px (Hero / Empty Margin)", size = 40.dp)
                    SpacingTokenRow(label = "54px (Standard Input & Button Height)", size = 54.dp)
                }
            }

            // 4. Buttons & States
            DsSection(title = "4. Button Library & States") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Primary Button (Default)", style = CaptionStyle)
                    MdrPrimaryButton(
                        text = "Calculate Settlement",
                        onClick = {}
                    )

                    Text(text = "Primary Button (Disabled)", style = CaptionStyle)
                    MdrPrimaryButton(
                        text = "Calculate Settlement",
                        onClick = {},
                        enabled = false
                    )

                    Text(text = "Secondary Outlined Button", style = CaptionStyle)
                    MdrSecondaryButton(
                        text = "Export Breakdown",
                        onClick = {}
                    )

                    Text(text = "Muted Reset Button", style = CaptionStyle)
                    MdrResetButton(onClick = {})
                }
            }

            // 5. Input Fields & Error State
            DsSection(title = "5. Input Fields & Validation States") {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(text = "Amount Input (Default / Filled)", style = CaptionStyle)
                    MdrAmountInput(
                        value = interactiveAmount,
                        onValueChange = { interactiveAmount = it },
                        label = "Transaction Amount",
                        supportingText = "Gross payment made by customer."
                    )

                    Text(text = "Percentage Input with Error State (Invalid 150%)", style = CaptionStyle)
                    MdrPercentageInput(
                        value = interactiveMdr,
                        onValueChange = { interactiveMdr = it },
                        label = "MDR Rate",
                        supportingText = "Enter the MDR charged by your payment provider.",
                        errorMessage = "Enter an MDR rate between 0% and 100%."
                    )

                    Text(text = "Fixed Fee Input", style = CaptionStyle)
                    MdrFixedFeeInput(
                        value = "0",
                        onValueChange = {}
                    )
                }
            }

            // 6. Selectors, Dropdowns & Toggles
            DsSection(title = "6. Controls & Toggles") {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(text = "Payment Method Selector (UPI)", style = CaptionStyle)
                    MdrPaymentMethodSelector(
                        selectedMethod = interactiveMethod,
                        onMethodSelected = { interactiveMethod = it }
                    )

                    Text(text = "Dropdown Select (Terminal Type)", style = CaptionStyle)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            onClick = { dropdownExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            color = InputBackground,
                            border = BorderStroke(1.dp, BorderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedDropdownOption,
                                    style = SectionHeadingStyle.copy(fontWeight = FontWeight.Medium),
                                    color = TextPrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = DeepNavy
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            modifier = Modifier.background(CardBackground)
                        ) {
                            listOf(
                                "POS Terminal (Standard)",
                                "Payment Gateway (Online)",
                                "QR Soundbox / Static",
                                "mPOS Mobile Reader"
                            ).forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option,
                                            style = BodyMediumStyle,
                                            color = TextPrimary
                                        )
                                    },
                                    onClick = {
                                        selectedDropdownOption = option
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Text(text = "GST on MDR Switch Toggle", style = CaptionStyle)
                    MdrGstToggle(
                        checked = interactiveToggle,
                        onCheckedChange = { interactiveToggle = it }
                    )
                }
            }

            // 7. Results, Breakdown & Insight Cards
            DsSection(title = "7. Result & Insight Cards") {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(text = "Settlement Result Card", style = CaptionStyle)
                    MdrResultCard(
                        grossAmount = 10000.0,
                        mdrRate = 1.50,
                        mdrAmount = 150.0,
                        gstAmount = 27.0,
                        fixedFee = 0.0,
                        totalCharges = 177.0,
                        netSettlement = 9823.0,
                        includeGst = true
                    )

                    Text(text = "Quick Insight Card", style = CaptionStyle)
                    MdrInsightCard(costPercentage = 1.77)

                    Text(text = "Empty / Initial State Card", style = CaptionStyle)
                    MdrEmptyStateCard()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DsSection(
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
            HorizontalDivider(color = BorderColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun ColorSwatchRow(
    name: String,
    hex: String,
    color: Color,
    isDark: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(InputBackground)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color)
                    .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = name,
                    style = BodyMediumStyle.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary
                )
                Text(
                    text = hex,
                    style = CaptionStyle.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                    color = TextSecondary
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isDark) DeepNavy.copy(alpha = 0.1f) else Color.Transparent)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isDark) "Dark Text/Bg" else "Light Surface",
                style = CaptionStyle.copy(fontSize = 10.sp),
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun TypographySpecimen(
    label: String,
    text: String,
    style: androidx.compose.ui.text.TextStyle
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(InputBackground)
            .padding(10.dp)
    ) {
        Text(
            text = label,
            style = CaptionStyle.copy(fontWeight = FontWeight.Bold),
            color = DeepNavy
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = style
        )
    }
}

@Composable
private fun SpacingTokenRow(
    label: String,
    size: androidx.compose.ui.unit.Dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(InputBackground)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = BodyMediumStyle,
            color = TextPrimary
        )
        Box(
            modifier = Modifier
                .width(size)
                .height(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(DeepNavy)
        )
    }
}
