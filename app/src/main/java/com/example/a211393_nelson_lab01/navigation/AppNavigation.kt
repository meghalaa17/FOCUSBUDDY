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
import androidx.compose.ui.unit.sp



//all screens with title
enum class AppScreen(val title: String) {
    Home("Focus Buddy \uD83D\uDC3E"),
    Login("Login"),
    Dashboard("Dashboard"),
    StudySession("Study Session"),
    Timer("Focus Timer"),          // now uses TimerWithSensorScreen
    PetGrowth("Your Pet"),
    Stats("Stats & Summary"),
    DailyTip("Daily Motivation"),   // NEW — Screen 6
    Community("Community Goals")   // NEW — Screen 7
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
    val screensWithBottomBar = listOf(
        AppScreen.Dashboard,
        AppScreen.StudySession,
        AppScreen.Timer,
        AppScreen.PetGrowth,
        AppScreen.Stats
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
                        Triple(AppScreen.Stats,        "📊", "Stats")
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
                            label = { Text(label, fontSize = 10.sp) },
                            icon  = { Text(icon,  fontSize = 16.sp) }
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

            composable(AppScreen.Timer.name) {
                TimerScreen(
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
        }
    }
}

@Composable
fun TimerScreen(viewModel: AppViewModel, onBack: () -> Boolean) {
    TODO("Not yet implemented")
}