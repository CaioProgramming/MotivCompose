package com.ilustris.motivcompose.ui.navigation

import android.os.Bundle
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.ilustris.motiv.foundation.ui.theme.gradientFill
import com.ilustris.motiv.foundation.ui.theme.grayGradients
import com.ilustris.motiv.foundation.ui.theme.motivGradient
import com.ilustris.motiv.foundation.ui.theme.radioIconModifier
import com.ilustris.motivcompose.features.home.ui.HomeView
import com.ilustris.motivcompose.features.post.ui.QuotePostView
import com.ilustris.motivcompose.features.profile.ui.ProfileView
import com.ilustris.motivcompose.features.settings.ui.SettingsView
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideRequestType

@Composable
fun MotivNavigationGraph(
    navHostController: NavHostController,
    padding: PaddingValues,
    modifier: Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = AppNavigation.HOME.route,
        modifier = modifier.padding(padding)
    ) {
        AppNavigation.values().forEach { item ->
            val args = item.arguments.map { navArgument(it) { type = NavType.StringType } }
            composable(route = item.route, arguments = args, enterTransition = { fadeIn() }) {
                GetRouteScreen(navigationItem = item, navHostController, it.arguments)
            }
        }
    }
}

@Composable
fun MotivBottomNavigation(navController: NavController, userProfilePic: String? = null) {

    fun navigateToScreen(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination

    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        AppNavigation.values().filter { it.showOnNavigation }.forEach { item ->
            val isSelected = currentRoute?.hierarchy?.any { it.route == item.route } == true
            val itemColor =
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.5f
                )

            val selectedBrush = if (isSelected) motivGradient() else grayGradients()

            BottomNavigationItem(
                selected = isSelected,
                onClick = { navigateToScreen(item.route) },
                icon = {
                    if (item == AppNavigation.PROFILE && userProfilePic != null) {
                        GlideImage(
                            imageModel = { userProfilePic },
                            glideRequestType = GlideRequestType.BITMAP,
                            modifier = Modifier
                                .size(32.dp)
                                .radioIconModifier(
                                    0f,
                                    borderWidth = 1.dp,
                                    sizeValue = 24.dp,
                                    brush = selectedBrush
                                ),
                            imageOptions = ImageOptions(
                                Alignment.Center,
                                contentScale = ContentScale.Crop
                            )
                        )
                    } else {
                        Image(
                            painterResource(item.icon),
                            contentDescription = item.title,
                            modifier = Modifier
                                .size(24.dp)
                                .gradientFill(brush = selectedBrush)
                                .clip(CircleShape),

                            )
                    }

                })
        }
    }
}


@Composable
fun GetRouteScreen(
    navigationItem: AppNavigation,
    navController: NavController,
    arguments: Bundle?
) {
    when (navigationItem) {
        AppNavigation.HOME -> {
            HomeView(navController)
        }

        AppNavigation.PROFILE -> {
            val userID = arguments?.getString("userId")
            ProfileView(userID, navController = navController)
        }

        AppNavigation.POST -> {
            QuotePostView(navController)
        }

        AppNavigation.SETTINGS -> {
            SettingsView(navController)
        }
    }
}