package com.darkz.live

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darkz.live.events.LiveEventManager
import com.darkz.live.settings.SettingsViewModel
import com.darkz.live.ui.screens.DashboardScreen
import com.darkz.live.ui.screens.EventHubScreen
import com.darkz.live.ui.screens.GameScreen
import com.darkz.live.ui.screens.GameSelectScreen
import com.darkz.live.ui.screens.SettingsScreen
import com.darkz.live.ui.theme.DarkzLiveTheme
import com.darkz.live.ui.theme.DarkzVoid

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Game : Screen("game")
    object GameSelect : Screen("game_select")
    object Events : Screen("events")
    object Settings : Screen("settings")
}

@Composable
fun DarkzLiveApp(
    eventManager: LiveEventManager,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()

    DarkzLiveTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DarkzVoid
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route
            ) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        eventManager = eventManager,
                        onNavigateToGame = { navController.navigate(Screen.Game.route) },
                        onNavigateToGameSelect = { navController.navigate(Screen.GameSelect.route) },
                        onNavigateToEvents = { navController.navigate(Screen.Events.route) },
                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                    )
                }

                composable(Screen.Game.route) {
                    GameScreen(
                        eventManager = eventManager,
                        onBackToDashboard = { navController.popBackStack() }
                    )
                }

                composable(Screen.GameSelect.route) {
                    GameSelectScreen(
                        onSelectGame = { gameId ->
                            if (gameId == "darkz_battle") {
                                navController.navigate(Screen.Game.route)
                            }
                        },
                        onBackToDashboard = { navController.popBackStack() }
                    )
                }

                composable(Screen.Events.route) {
                    EventHubScreen(
                        eventManager = eventManager,
                        onBackToDashboard = { navController.popBackStack() }
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onBackToDashboard = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
