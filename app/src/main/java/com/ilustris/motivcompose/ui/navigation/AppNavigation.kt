package com.ilustris.motivcompose.ui.navigation

import androidx.annotation.DrawableRes
import com.ilustris.motivcompose.R

private const val HOME_ROUTE = "home"
private const val PROFILE_ROUTE = "profile/{userId}"
private const val POST_ROUTE = "post"

enum class AppNavigation(val title: String,val route:String, @DrawableRes val icon:  Int) {
    HOME("Home", HOME_ROUTE, R.drawable.round_home_24),
    POST("Publicar", POST_ROUTE, R.drawable.round_add_24),
    PROFILE("Eu", PROFILE_ROUTE, R.drawable.round_person_24),
}