package com.idchan.prompt.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.idchan.prompt.domain.model.AnalysisResult
import com.idchan.prompt.ui.components.IDChanBottomNavigation
import com.idchan.prompt.ui.favorites.FavoritesScreen
import com.idchan.prompt.ui.favorites.FavoritesViewModel
import com.idchan.prompt.ui.history.HistoryScreen
import com.idchan.prompt.ui.history.HistoryViewModel
import com.idchan.prompt.ui.home.HomeScreen
import com.idchan.prompt.ui.home.HomeViewModel
import com.idchan.prompt.ui.prompt.PromptResultScreen
import com.idchan.prompt.ui.prompt.PromptResultViewModel
import com.idchan.prompt.ui.settings.SettingsScreen
import com.idchan.prompt.ui.settings.SettingsViewModel

import com.idchan.prompt.ui.auth.LoginScreen
import com.idchan.prompt.ui.auth.LoginViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    homeViewModel: HomeViewModel,
    promptResultViewModel: PromptResultViewModel,
    historyViewModel: HistoryViewModel,
    favoritesViewModel: FavoritesViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val loginState by loginViewModel.uiState.collectAsState()

    var activeAnalysisResult by remember { mutableStateOf<AnalysisResult?>(null) }

    val startDestination = if (loginState.isLoggedIn) Screen.Home.route else Screen.Login.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.PromptResult.route && currentRoute != Screen.Login.route) {
                IDChanBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToResult = { result ->
                        activeAnalysisResult = result
                        navController.navigate(Screen.PromptResult.route)
                    }
                )
            }

            composable(Screen.PromptResult.route) {
                activeAnalysisResult?.let { result ->
                    PromptResultScreen(
                        analysisResult = result,
                        viewModel = promptResultViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = historyViewModel,
                    onOpenItem = { result ->
                        activeAnalysisResult = result
                        navController.navigate(Screen.PromptResult.route)
                    }
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = favoritesViewModel,
                    onOpenItem = { result ->
                        activeAnalysisResult = result
                        navController.navigate(Screen.PromptResult.route)
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = settingsViewModel)
            }
        }
    }
}
