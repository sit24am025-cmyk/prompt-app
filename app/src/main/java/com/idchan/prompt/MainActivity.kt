package com.idchan.prompt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.idchan.prompt.ui.favorites.FavoritesViewModel
import com.idchan.prompt.ui.history.HistoryViewModel
import com.idchan.prompt.ui.home.HomeViewModel
import com.idchan.prompt.ui.prompt.PromptResultViewModel
import com.idchan.prompt.ui.settings.SettingsViewModel
import com.idchan.prompt.ui.theme.IDChanPromptTheme
import com.idchan.prompt.navigation.NavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as IDChanPromptApp

        val loginViewModel = com.idchan.prompt.ui.auth.LoginViewModel(app.userPreferences)
        val homeViewModel = HomeViewModel(app.analyzeImageUseCase)
        val promptResultViewModel = PromptResultViewModel(app.enhancePromptUseCase, app.manageHistoryUseCase)
        val historyViewModel = HistoryViewModel(app.manageHistoryUseCase)
        val favoritesViewModel = FavoritesViewModel(app.manageHistoryUseCase)
        val settingsViewModel = SettingsViewModel(app.settingsRepository, app.manageHistoryUseCase)

        setContent {
            val settingsState by settingsViewModel.uiState.collectAsState()
            val useDarkTheme = when (settingsState.theme) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            IDChanPromptTheme(darkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        homeViewModel = homeViewModel,
                        promptResultViewModel = promptResultViewModel,
                        historyViewModel = historyViewModel,
                        favoritesViewModel = favoritesViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}
