package com.example.a211393_nelson_lab01.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.a211393_nelson_lab01.AppViewModel
import com.example.a211393_nelson_lab01.ui.screens.DashboardScreen
import com.example.a211393_nelson_lab01.ui.screens.HomeScreen
import com.example.a211393_nelson_lab01.ui.screens.LoginScreen
import com.example.a211393_nelson_lab01.ui.screens.PetGrowthScreen
import com.example.a211393_nelson_lab01.ui.screens.StatsScreen
import com.example.a211393_nelson_lab01.ui.screens.StudySessionScreen
// --- ADDED: imports for the three screens that were missing ---
import com.example.a211393_nelson_lab01.ui.screens.DailyTipScreen
import com.example.a211393_nelson_lab01.ui.screens.CommunityScreen
import com.example.a211393_nelson_lab01.ui.screens.TimerWithSensorScreen
import androidx.compose.ui.unit.sp


// ================================================================
// CHANGES MADE IN THIS FILE:
//
// 1. Added imports for DailyTipScreen, CommunityScreen,
//    TimerWithSensorScreen (these existed as files but were
//    never imported or wired into the NavHost).
//
// 2. Removed the broken placeholder `TimerScreen` composable
//    that had `TODO("Not yet implemented")` and the wrong
//    `onBack: () -> Boolean` signature.
//
// 3. AppScreen.Timer route now calls TimerWithSensorScreen
//    (your actual Pillar 4 sensor screen) instead of the
//    broken placeholder.
//
// 4. Added composable(AppScreen.DailyTip.name) and
//    composable(AppScreen.Community.name) blocks to the
//    NavHost — these routes existed in the enum but had
//    no matching destination, which would crash on navigate().
//
// 5. Added DailyTip and Community as menu options in
//    DashboardMenuGrid (inside DashboardScreen.kt — see that
//    file's notes). For now, the simplest fix is to add two
//    more buttons there. If you'd rather not touch
//    DashboardScreen, you can navigate to them via
//    navController.navigate(AppScreen.DailyTip.name) from
//    anywhere you like.
// ================================================================

//all screens with title
enum class AppScreen(val title: String) {
    Home("Focus Buddy \uD83D\uDC3E"),
    Login("Login"),
    Dashboard("Dashboard"),
    StudySession("Study Session"),
    Timer("Focus Timer"),
    PetGrowth("Your Pet"),
    Stats("Stats & Summary"),
    DailyTip("Daily Motivation"),
    Community("Community Goals")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
) {
    TopAppBar(
        title = { Text(currentScreen.title) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        }
    )
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: AppViewModel = viewModel() //create viewmodel

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Home.name
    )

    // Screens that should show the bottom bar
    // --- ADDED: DailyTip and Community so they're reachable from
    //     the bottom bar too (otherwise they'd be unreachable
    //     unless wired up elsewhere) ---
    val screensWithBottomBar = listOf(
        AppScreen.Dashboard,
        AppScreen.StudySession,
        AppScreen.Timer,
        AppScreen.PetGrowth,
        AppScreen.Stats,
        AppScreen.DailyTip,
        AppScreen.Community
    )

    val showBottomBar = currentScreen in screensWithBottomBar

    Scaffold(
        topBar = {
            AppTopBar(
                currentScreen   = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp      = { navController.navigateUp() }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    listOf(
                        Triple(AppScreen.Dashboard,    "🏠", "Menu"),
                        Triple(AppScreen.StudySession, "📚", "Study"),
                        Triple(AppScreen.Timer,        "⏱️", "Timer"),
                        Triple(AppScreen.PetGrowth,    "🐾", "Pet"),
                        Triple(AppScreen.Stats,        "📊", "Stats"),
                        // --- ADDED: two new bottom bar items ---
                        Triple(AppScreen.DailyTip,     "🌟", "Tips"),
                        Triple(AppScreen.Community,    "🌍", "Community")
                    ).forEach { (screen, icon, label) ->
                        NavigationBarItem(
                            selected = currentScreen == screen,
                            onClick  = {
                                navController.navigate(screen.name) {
                                    popUpTo(AppScreen.Dashboard.name) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            label = { Text(label, fontSize = 9.sp) },
                            icon  = { Text(icon,  fontSize = 14.sp) }
                        )
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = AppScreen.Home.name,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(AppScreen.Home.name) {
                HomeScreen(
                    onLoginClick     = { navController.navigate(AppScreen.Login.name) },
                    onEnterDashboard = { name ->
                        viewModel.setUserName(name)
                        navController.navigate(AppScreen.Dashboard.name) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(AppScreen.Login.name) {
                LoginScreen(onBackClick = { navController.navigateUp() })
            }

            composable(AppScreen.Dashboard.name) {
                DashboardScreen(
                    viewModel         = viewModel,
                    onNavigateToStudy = { navController.navigate(AppScreen.StudySession.name) },
                    onNavigateToTimer = { navController.navigate(AppScreen.Timer.name) },
                    onNavigateToPet   = { navController.navigate(AppScreen.PetGrowth.name) },
                    onNavigateToStats = { navController.navigate(AppScreen.Stats.name) },
                    onExit = {
                        navController.popBackStack(AppScreen.Home.name, inclusive = false)
                    }
                )
            }

            composable(AppScreen.StudySession.name) {
                StudySessionScreen(
                    viewModel = viewModel,
                    onBack    = { navController.navigateUp() }
                )
            }

            // --- FIXED: now points to TimerWithSensorScreen
            //     (was: broken placeholder TimerScreen) ---
            composable(AppScreen.Timer.name) {
                TimerWithSensorScreen(
                    viewModel = viewModel,
                    onBack    = { navController.navigateUp() }
                )
            }

            composable(AppScreen.PetGrowth.name) {
                PetGrowthScreen(
                    viewModel = viewModel,
                    onBack    = { navController.navigateUp() }
                )
            }

            composable(AppScreen.Stats.name) {
                StatsScreen(
                    viewModel = viewModel,
                    onBack    = { navController.navigateUp() }
                )
            }

            // --- ADDED: previously missing route, screen 6 of 7 ---
            composable(AppScreen.DailyTip.name) {
                DailyTipScreen(
                    viewModel = viewModel,
                    onBack    = { navController.navigateUp() }
                )
            }

            // --- ADDED: previously missing route, screen 7 of 7 ---
            composable(AppScreen.Community.name) {
                CommunityScreen(
                    viewModel = viewModel,
                    onBack    = { navController.navigateUp() }
                )
            }
        }
    }
}

// --- REMOVED: the broken placeholder composable that used to be here:
//
// @Composable
// fun TimerScreen(viewModel: AppViewModel, onBack: () -> Boolean) {
//     TODO("Not yet implemented")
// }
//
// It's gone because AppScreen.Timer now routes directly to
// TimerWithSensorScreen, which already exists in
// ui/screens/TimerWithSensorScreen.kt