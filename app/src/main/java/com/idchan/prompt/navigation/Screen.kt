package com.idchan.prompt.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object PromptResult : Screen("prompt_result")
    object History : Screen("history")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
}
