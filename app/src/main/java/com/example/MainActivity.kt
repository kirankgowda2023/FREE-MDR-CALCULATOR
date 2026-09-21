package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.DesignSystemScreen
import com.example.ui.screens.FreeMdrScreen
import com.example.ui.screens.MdrCalculatorScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.*

enum class AppScreen {
    CALCULATOR,
    FREE_MDR,
    PROFILE,
    DESIGN_SYSTEM
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MdrApp()
            }
        }
    }
}

@Composable
fun MdrApp() {
    var currentScreen by remember { mutableStateOf(AppScreen.CALCULATOR) }

    // Handle back button when not on main Calculator screen
    BackHandler(enabled = currentScreen != AppScreen.CALCULATOR) {
        currentScreen = AppScreen.CALCULATOR
    }

    Scaffold(
        bottomBar = {
            if (currentScreen != AppScreen.DESIGN_SYSTEM) {
                NavigationBar(
                    containerColor = CardBackground,
                    contentColor = DeepNavy,
                    tonalElevation = 4.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.CALCULATOR,
                        onClick = { currentScreen = AppScreen.CALCULATOR },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Home,
                                contentDescription = "Calculator",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Calculator",
                                style = CaptionStyle.copy(fontSize = 12.sp)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepNavy,
                            selectedTextColor = DeepNavy,
                            indicatorColor = PrimaryLight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_calculator")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.FREE_MDR,
                        onClick = { currentScreen = AppScreen.FREE_MDR },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.QrCodeScanner,
                                contentDescription = "Free MDR Splitter",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Free MDR",
                                style = CaptionStyle.copy(fontSize = 12.sp)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepNavy,
                            selectedTextColor = DeepNavy,
                            indicatorColor = PrimaryLight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_free_mdr")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.PROFILE,
                        onClick = { currentScreen = AppScreen.PROFILE },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = "Business Profile",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Profile",
                                style = CaptionStyle.copy(fontSize = 12.sp)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepNavy,
                            selectedTextColor = DeepNavy,
                            indicatorColor = PrimaryLight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_profile")
                    )
                }
            }
        },
        containerColor = AppBackground,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = AppBackground
        ) {
            Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                when (screen) {
                    AppScreen.CALCULATOR -> {
                        MdrCalculatorScreen(
                            onOpenProfile = { currentScreen = AppScreen.PROFILE },
                            onOpenFreeMdr = { currentScreen = AppScreen.FREE_MDR },
                            onOpenDesignSystem = { currentScreen = AppScreen.DESIGN_SYSTEM }
                        )
                    }
                    AppScreen.FREE_MDR -> {
                        FreeMdrScreen(
                            onNavigateToCalculator = { currentScreen = AppScreen.CALCULATOR }
                        )
                    }
                    AppScreen.PROFILE -> {
                        ProfileScreen(
                            onBack = { currentScreen = AppScreen.CALCULATOR },
                            onNavigateToMdrSplitter = { currentScreen = AppScreen.FREE_MDR }
                        )
                    }
                    AppScreen.DESIGN_SYSTEM -> {
                        DesignSystemScreen(
                            onBack = { currentScreen = AppScreen.CALCULATOR }
                        )
                    }
                }
            }
        }
    }
}
