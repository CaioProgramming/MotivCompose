package com.ilustris.motivcompose.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ilustris.motivcompose.features.home.ui.HomeView

@Composable
fun MotivNavigationGraph(navHostController: NavHostController, padding: Dp) {
    NavHost(
        navController = navHostController,
        startDestination = AppNavigation.HOME.route,
        modifier = Modifier.padding(bottom = padding)
    ) {
        AppNavigation.values().forEach { item ->
            composable(route = item.route) {
                getRouteScreen(navigationItem = item)
            }
        }
    }
}

@Composable
fun MotivBottomNavigation(navController: NavController) {

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
        AppNavigation.values().forEach { item ->
            val isSelected = currentRoute?.hierarchy?.any { it.route == item.route } == true
            val itemColor =
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.5f
                )

            BottomNavigationItem(
                selected = true,
                selectedContentColor = MaterialTheme.colorScheme.onBackground,
                unselectedContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                onClick = { navigateToScreen(item.route) },
                icon = {
                    Image(
                        painterResource(item.icon),
                        contentDescription = item.title,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                        colorFilter = ColorFilter.tint(itemColor)
                    )
                })
        }
    }
}


@Composable
fun getRouteScreen(navigationItem: AppNavigation) {
    when (navigationItem) {
        AppNavigation.HOME -> {
           HomeView()
        }

        AppNavigation.PROFILE -> {
            Text(text = "Profile")
        }

        AppNavigation.POST -> {
            Text(text = "Post")
        }
    }
}