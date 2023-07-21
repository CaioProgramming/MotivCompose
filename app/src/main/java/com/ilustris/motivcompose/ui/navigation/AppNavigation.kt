package com.ilustris.motivcompose.ui.navigation

import androidx.annotation.DrawableRes
import com.ilustris.motivcompose.R

private const val HOME_ROUTE = "home"
private const val MANAGER_ROUTE = "manager"
private const val PROFILE_ROUTE = "profile/{userId}"
private const val POST_ROUTE = "post/{quoteId}"

enum class AppNavigation(
    val title: String,
    val route: String,
    @DrawableRes val icon: Int,
    val arguments: List<String> = emptyList(),
    val showOnNavigation: Boolean = true,
    val showBottomBar: Boolean = true
) {
    HOME("Home", HOME_ROUTE, R.drawable.round_home_24),
    POST(
        "Publicar",
        POST_ROUTE,
        R.drawable.round_add_24,
        arguments = listOf("quoteId"),
        showBottomBar = false
    ),
    PROFILE("Eu", PROFILE_ROUTE, R.drawable.round_person_24, arguments = listOf("userId")),
    SETTINGS("Configurações", "settings", R.drawable.round_settings_24, showOnNavigation = false),
    MANAGER(
        "Gerenciar",
        MANAGER_ROUTE,
        com.ilustris.motiv.foundation.R.drawable.ic_saturn_and_other_planets_primary,
        showOnNavigation = false,
        showBottomBar = false
    ),
}