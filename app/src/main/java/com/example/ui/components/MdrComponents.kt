package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.PaymentMethod
import com.example.ui.viewmodel.UpiCategory

/**
 * Primary Button with default, pressed, and disabled states.
 * Height 54dp with 12dp rounded corners and calculator icon.
 */
@Composable
fun MdrPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = Icons.Default.Calculate,
    testTag: String = "calculate_settlement_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = when {
        !enabled -> BorderColor
        isPressed -> PrimaryDark
        else -> DeepNavy
    }

    val contentColor = when {
        !enabled -> TextMuted
        else -> Color.White
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        shadowElevation = if (enabled && !isPressed) 2.dp else 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = ButtonTextStyle,
                color = contentColor
            )
        }
    }
}

/**
 * Secondary Button (Bordered style)
 */
@Composable
fun MdrSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    testTag: String = "secondary_button"
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = DeepNavy
        ),
        border = BorderStroke(1.5.dp, if (enabled) BorderColor else BorderColor.copy(alpha = 0.5f)),
        modifier = modifier
            .height(50.dp)
            .testTag(testTag)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = DeepNavy
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = ButtonTextStyle.copy(fontSize = 14.sp),
            color = DeepNavy
        )
    }
}

/**
 * Muted Reset Button
 */
@Composable
fun MdrResetButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Reset Calculator",
    testTag: String = "reset_calculator_button"
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = Icons.Outlined.RestartAlt,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = BodyMediumStyle,
            color = TextSecondary
        )
    }
}

/**
 * Currency/Amount Input field with ₹ prefix and state indicators
 */
@Composable
fun MdrAmountInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Transaction Amount",
    placeholder: String = "Enter amount",
    supportingText: String? = null,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
    testTag: String = "transaction_amount_input"
) {
    var isFocused by remember { mutableStateOf(false) }
    val isError = errorMessage != null
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = BodyMediumStyle.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = InputBackground,
            border = BorderStroke(
                width = if (isFocused || isError) 1.5.dp else 1.dp,
                color = when {
                    isError -> BorderError
                    isFocused -> BorderFocused
                    else -> BorderColor
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Currency Prefix Badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryLight)
                ) {
                    Text(
                        text = "₹",
                        style = SectionHeadingStyle.copy(fontWeight = FontWeight.Bold),
                        color = DeepNavy
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = placeholder,
                            style = SectionHeadingStyle.copy(color = TextMuted)
                        )
                    },
                    textStyle = SectionHeadingStyle.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus() }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { isFocused = it.isFocused }
                        .testTag(testTag)
                )

                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = { onValueChange("") },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear amount",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = CaptionStyle,
                color = DangerRed
            )
        } else if (supportingText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = supportingText,
                style = CaptionStyle,
                color = TextSecondary
            )
        }
    }
}

/**
 * Percentage Input field with % suffix and optional Info dialog trigger
 */
@Composable
fun MdrPercentageInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "MDR Rate",
    supportingText: String = "Enter the MDR charged by your payment provider.",
    errorMessage: String? = null,
    onInfoClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    testTag: String = "mdr_rate_input"
) {
    var isFocused by remember { mutableStateOf(false) }
    val isError = errorMessage != null
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = BodyMediumStyle.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary
            )
            if (onInfoClick != null) {
                IconButton(
                    onClick = onInfoClick,
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("mdr_rate_info_icon")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "MDR Rate Info",
                        tint = DeepNavy,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = InputBackground,
            border = BorderStroke(
                width = if (isFocused || isError) 1.5.dp else 1.dp,
                color = when {
                    isError -> BorderError
                    isFocused -> BorderFocused
                    else -> BorderColor
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = "1.50",
                            style = SectionHeadingStyle.copy(color = TextMuted)
                        )
                    },
                    textStyle = SectionHeadingStyle.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus() }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { isFocused = it.isFocused }
                        .testTag(testTag)
                )

                // Percent Suffix Pill
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(PrimaryLight)
                        .padding(horizontal = 10.dp)
                ) {
                    Text(
                        text = "%",
                        style = SectionHeadingStyle.copy(fontWeight = FontWeight.Bold),
                        color = DeepNavy
                    )
                }
            }
        }

        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = DangerRed,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = errorMessage,
                    style = CaptionStyle,
                    color = DangerRed
                )
            }
        } else {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = supportingText,
                style = CaptionStyle,
                color = TextSecondary
            )
        }
    }
}

/**
 * Fixed Fee Input Field
 */
@Composable
fun MdrFixedFeeInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Fixed Fee",
    supportingText: String = "Optional fixed fee per transaction.",
    modifier: Modifier = Modifier,
    testTag: String = "fixed_fee_input"
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = BodyMediumStyle.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = InputBackground,
            border = BorderStroke(
                width = if (isFocused) 1.5.dp else 1.dp,
                color = if (isFocused) BorderFocused else BorderColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹",
                    style = SectionHeadingStyle.copy(fontWeight = FontWeight.Bold, color = TextSecondary)
                )
                Spacer(modifier = Modifier.width(10.dp))
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = "0",
                            style = SectionHeadingStyle.copy(color = TextMuted)
                        )
                    },
                    textStyle = SectionHeadingStyle.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { isFocused = it.isFocused }
                        .testTag(testTag)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = supportingText,
            style = CaptionStyle,
            color = TextSecondary
        )
    }
}

/**
 * Payment Method Selector - UPI only
 */
@Composable
fun MdrPaymentMethodSelector(
    selectedMethod: PaymentMethod = PaymentMethod.UPI,
    onMethodSelected: (PaymentMethod) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Payment Method",
            style = BodyMediumStyle.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            onClick = { onMethodSelected(PaymentMethod.UPI) },
            shape = RoundedCornerShape(12.dp),
            color = DeepNavy,
            border = BorderStroke(1.dp, DeepNavy),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .testTag("payment_method_upi")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "UPI (Unified Payments Interface)",
                            style = SectionHeadingStyle.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "P2M, QR Code & Soundbox payments",
                            style = CaptionStyle.copy(fontSize = 11.sp),
                            color = PrimaryLight
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SuccessBackground)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Active",
                        style = CaptionStyle.copy(
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    )
                }
            }
        }
    }
}

/**
 * Official New UPI Fee Structure Announcement & Policy Banner
 */
@Composable
fun UpiPolicyAnnouncementBanner(
    onLearnMoreClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DeepNavy,
        border = BorderStroke(1.dp, PrimaryLight.copy(alpha = 0.2f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "New UPI Fee Structure",
                        style = BodyMediumStyle.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(WarningOrange)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Effective 15 Oct 2026",
                        style = CaptionStyle.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "• Everyday payments up to ₹2,000 remain 100% FREE\n" +
                        "• All Person-to-Person (P2P) transfers remain FREE\n" +
                        "• Applies to merchant payments only (No charges to customers)\n" +
                        "• Maximum ₹300 fee cap for transactions ≥ ₹75,000",
                style = CaptionStyle.copy(
                    color = PrimaryLight,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onLearnMoreClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = "View Circular Rules →",
                        style = CaptionStyle.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

/**
 * Category Selector for the New UPI Merchant Categories
 */
@Composable
fun UpiCategorySelector(
    selectedCategory: UpiCategory,
    onCategorySelected: (UpiCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Merchant / Transaction Category",
                style = BodyMediumStyle.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary
            )
            Text(
                text = "Official 2026 Rules",
                style = CaptionStyle.copy(
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy,
                    fontSize = 10.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                onClick = { expanded = true },
                shape = RoundedCornerShape(12.dp),
                color = InputBackground,
                border = BorderStroke(1.dp, if (expanded) DeepNavy else BorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upi_category_dropdown")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (selectedCategory) {
                                    UpiCategory.REGULAR_P2M -> Icons.Outlined.Storefront
                                    UpiCategory.FUEL -> Icons.Outlined.LocalGasStation
                                    UpiCategory.RAILWAYS -> Icons.Outlined.Train
                                    UpiCategory.TELECOM -> Icons.Outlined.PhoneAndroid
                                    UpiCategory.INSURANCE -> Icons.Outlined.Security
                                    UpiCategory.CAPITAL_MARKETS -> Icons.Outlined.TrendingUp
                                    UpiCategory.SMALL_MERCHANT -> Icons.Outlined.ShoppingBag
                                    UpiCategory.P2P -> Icons.Outlined.Person
                                    UpiCategory.CUSTOM -> Icons.Outlined.Tune
                                },
                                contentDescription = null,
                                tint = DeepNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = selectedCategory.title,
                                style = BodyMediumStyle.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = selectedCategory.ruleSummary,
                                style = CaptionStyle.copy(
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Category",
                        tint = DeepNavy,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(CardBackground)
            ) {
                UpiCategory.values().forEach { category ->
                    val isSelected = category == selectedCategory
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = category.title,
                                        style = BodyMediumStyle.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) DeepNavy else TextPrimary
                                        )
                                    )
                                    Text(
                                        text = category.ruleSummary,
                                        style = CaptionStyle.copy(
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = DeepNavy,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * GST on MDR Toggle Row
 */
@Composable
fun MdrGstToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "gst_toggle"
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = InputBackground,
        border = BorderStroke(1.dp, BorderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "GST on MDR",
                        style = BodyMediumStyle.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(PrimaryLight)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "18%",
                            style = CaptionStyle.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "GST is calculated on the MDR amount.",
                    style = CaptionStyle,
                    color = TextSecondary
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = DeepNavy,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = BorderColor
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}

/**
 * Single Breakdown Row inside result card
 */
@Composable
fun FeeBreakdownRow(
    label: String,
    amount: String,
    isDeduction: Boolean = false,
    isHighlighted: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isHighlighted) BodyMediumStyle.copy(fontWeight = FontWeight.SemiBold) else BodyStyle,
            color = if (isHighlighted) TextPrimary else TextSecondary
        )
        Text(
            text = if (isDeduction && amount != "₹0.00" && amount != "₹0") "− $amount" else amount,
            style = if (isHighlighted) SectionHeadingStyle.copy(fontWeight = FontWeight.Bold) else BodyMediumStyle.copy(fontWeight = FontWeight.SemiBold),
            color = if (isHighlighted) TextPrimary else if (isDeduction && amount != "₹0.00") DangerRed else TextPrimary
        )
    }
}

/**
 * Result Card showing Settlement Results
 */
@Composable
fun MdrResultCard(
    grossAmount: Double,
    mdrRate: Double,
    mdrAmount: Double,
    gstAmount: Double,
    fixedFee: Double,
    totalCharges: Double,
    netSettlement: Double,
    includeGst: Boolean,
    modifier: Modifier = Modifier,
    mdrRateDisplay: String? = null,
    ruleAppliedText: String? = null,
    isCapApplied: Boolean = false,
    isFreeEverydayPayment: Boolean = false,
    categoryName: String? = null
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, BorderColor),
        shadowElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("result_settlement_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Card Title & Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Settlement",
                        style = SectionHeadingStyle.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    if (categoryName != null) {
                        Text(
                            text = categoryName,
                            style = CaptionStyle.copy(
                                fontWeight = FontWeight.Medium,
                                color = DeepNavy,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (isFreeEverydayPayment) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SuccessBackground)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "100% FREE",
                                style = CaptionStyle.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            )
                        }
                    } else if (isCapApplied) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(WarningBackground)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Cap Applied",
                                style = CaptionStyle.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WarningOrange
                                )
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SuccessBackground)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Calculated",
                                    style = CaptionStyle.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = SuccessGreen
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Highlighted Net Settlement Amount Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SuccessBackground.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Net amount you receive",
                        style = BodyMediumStyle,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹%,.2f".format(netSettlement),
                        style = LargeAmountStyle.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SuccessGreen
                        ),
                        textAlign = TextAlign.Center
                    )

                    if (ruleAppliedText != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = ruleAppliedText,
                            style = CaptionStyle.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isFreeEverydayPayment) SuccessGreen else DeepNavy
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Breakdown Rows
            FeeBreakdownRow(
                label = "Transaction Amount",
                amount = "₹%,.2f".format(grossAmount)
            )

            val mdrLabel = if (mdrRateDisplay != null) {
                "MDR — $mdrRateDisplay"
            } else {
                "MDR — %.2f%%".format(mdrRate)
            }
            FeeBreakdownRow(
                label = mdrLabel,
                amount = "₹%,.2f".format(mdrAmount),
                isDeduction = mdrAmount > 0.0
            )

            if (includeGst) {
                FeeBreakdownRow(
                    label = "GST on MDR — 18%",
                    amount = "₹%,.2f".format(gstAmount),
                    isDeduction = gstAmount > 0.0
                )
            }

            if (fixedFee > 0.0) {
                FeeBreakdownRow(
                    label = "Fixed Fee",
                    amount = "₹%,.2f".format(fixedFee),
                    isDeduction = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BorderColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            FeeBreakdownRow(
                label = "Total Charges",
                amount = "₹%,.2f".format(totalCharges),
                isHighlighted = false
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Net Settlement",
                    style = SectionHeadingStyle.copy(fontWeight = FontWeight.Bold),
                    color = DeepNavy
                )
                Text(
                    text = "₹%,.2f".format(netSettlement),
                    style = SectionHeadingStyle.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SuccessGreen
                    )
                )
            }
        }
    }
}

/**
 * Visual Flow representation of the calculation
 */
@Composable
fun MdrVisualFlow(
    grossAmount: Double,
    mdrAmount: Double,
    gstAmount: Double,
    fixedFee: Double,
    netSettlement: Double,
    includeGst: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, BorderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Calculation Flow",
                style = SectionHeadingStyle.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Gross Transaction
            FlowStepItem(
                badge = "Gross",
                badgeColor = DeepNavy,
                badgeBg = PrimaryLight,
                title = "Gross Transaction",
                value = "₹%,.2f".format(grossAmount),
                valueColor = TextPrimary
            )

            FlowArrow()

            // MDR
            FlowStepItem(
                badge = "MDR",
                badgeColor = DangerRed,
                badgeBg = DangerBackground,
                title = "Payment Provider MDR",
                value = "− ₹%,.2f".format(mdrAmount),
                valueColor = DangerRed
            )

            if (includeGst) {
                FlowArrow()
                FlowStepItem(
                    badge = "GST",
                    badgeColor = WarningOrange,
                    badgeBg = WarningBackground,
                    title = "18% GST on MDR",
                    value = "− ₹%,.2f".format(gstAmount),
                    valueColor = DangerRed
                )
            }

            if (fixedFee > 0) {
                FlowArrow()
                FlowStepItem(
                    badge = "Fee",
                    badgeColor = TextSecondary,
                    badgeBg = BorderColor,
                    title = "Fixed Transaction Fee",
                    value = "− ₹%,.2f".format(fixedFee),
                    valueColor = DangerRed
                )
            }

            FlowArrow()

            // Net Settlement
            FlowStepItem(
                badge = "Net",
                badgeColor = SuccessGreen,
                badgeBg = SuccessBackground,
                title = "Net Settlement Received",
                value = "₹%,.2f".format(netSettlement),
                valueColor = SuccessGreen,
                isFinal = true
            )
        }
    }
}

@Composable
private fun FlowStepItem(
    badge: String,
    badgeColor: Color,
    badgeBg: Color,
    title: String,
    value: String,
    valueColor: Color,
    isFinal: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFinal) SuccessBackground.copy(alpha = 0.3f) else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badge,
                    style = CaptionStyle.copy(fontWeight = FontWeight.Bold),
                    color = badgeColor
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = if (isFinal) BodyMediumStyle.copy(fontWeight = FontWeight.Bold) else BodyMediumStyle,
                color = TextPrimary
            )
        }

        Text(
            text = value,
            style = if (isFinal) SectionHeadingStyle.copy(fontWeight = FontWeight.ExtraBold) else BodyMediumStyle.copy(fontWeight = FontWeight.Bold),
            color = valueColor
        )
    }
}

@Composable
private fun FlowArrow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowDownward,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * Quick Insight Card
 */
@Composable
fun MdrInsightCard(
    costPercentage: Double,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = PrimaryLight,
        border = BorderStroke(1.dp, DeepNavy.copy(alpha = 0.15f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = DeepNavy,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Your total payment processing cost is %.2f%% of the transaction.".format(costPercentage),
                style = BodyMediumStyle.copy(color = DeepNavy, fontWeight = FontWeight.Medium)
            )
        }
    }
}

/**
 * Empty / Initial State Card
 */
@Composable
fun MdrEmptyStateCard(
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, BorderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(PrimaryLight)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ReceiptLong,
                    contentDescription = null,
                    tint = DeepNavy,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Your settlement will appear here.",
                style = SectionHeadingStyle.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Enter your transaction details and calculate the charges.",
                style = BodyStyle,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Merchant Guidance Dialog
 */
@Composable
fun MdrInfoDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = DeepNavy
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "New UPI Fee Structure",
                        style = SectionHeadingStyle.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "Effective 15 October 2026",
                        style = CaptionStyle.copy(color = WarningOrange, fontWeight = FontWeight.Bold)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoTopic(
                    title = "Key Policy Rules",
                    description = "• Everyday payments up to ₹2,000 remain FREE\n" +
                            "• All person-to-person (P2P) transfers remain FREE\n" +
                            "• Applies to merchant payments only (P2M)\n" +
                            "• No charges to customers; fees cannot be passed on"
                )

                HorizontalDivider(color = BorderColor, thickness = 1.dp)

                InfoTopic(
                    title = "Regular Merchants (P2M)",
                    description = "• Up to ₹2,000: 0% (FREE)\n" +
                            "• > ₹2,000 and < ₹75,000: 0.40%\n" +
                            "• ≥ ₹75,000: 0.40% capped at Maximum Fee of ₹300"
                )

                InfoTopic(
                    title = "Railways, Fuel, Telecom & Insurance",
                    description = "• Up to ₹2,000: 0% (FREE)\n" +
                            "• > ₹2,000: ₹5 flat fee (Maximum Fee: ₹5)"
                )

                InfoTopic(
                    title = "Capital Markets",
                    description = "• Up to ₹2,000: 0% (FREE)\n" +
                            "• > ₹2,000: 0.02% capped at Maximum Fee of ₹300"
                )

                InfoTopic(
                    title = "Small Merchants & P2PM",
                    description = "• Eligible transactions: 0% FREE"
                )

                InfoTopic(
                    title = "GST on MDR",
                    description = "18% GST applies strictly to the MDR fee amount."
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Close",
                    style = ButtonTextStyle,
                    color = DeepNavy
                )
            }
        },
        containerColor = CardBackground,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun InfoTopic(title: String, description: String) {
    Column {
        Text(
            text = title,
            style = BodyMediumStyle.copy(fontWeight = FontWeight.Bold),
            color = DeepNavy
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = description,
            style = CaptionStyle.copy(fontSize = 12.sp, lineHeight = 16.sp),
            color = TextSecondary
        )
    }
}
