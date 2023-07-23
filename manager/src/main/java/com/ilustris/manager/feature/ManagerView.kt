@file:OptIn(ExperimentalMaterial3Api::class)

package com.ilustris.manager.feature

import ai.atick.material.MaterialColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ilustris.manager.R
import com.ilustris.manager.feature.home.ui.ManagerHomeView
import com.ilustris.manager.feature.icons.ui.IconsView
import com.ilustris.manager.feature.covers.ui.CoversView
import com.ilustris.motiv.foundation.ui.theme.MotivTitle
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ManagerView(navController: NavController) {


    val currentFeature = remember {
        mutableStateOf(ManagerFeatures.HOME)
    }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    @Composable
    fun getFeatureView() {
        when (currentFeature.value) {
            ManagerFeatures.HOME -> {
                ManagerHomeView()
            }

            ManagerFeatures.STYLES -> {
                Text(text = "Estilos")
            }

            ManagerFeatures.ICONS -> {
                IconsView()
            }

            ManagerFeatures.COVERS -> {
                CoversView()
            }
        }
    }

    fun selectFeature(features: ManagerFeatures) {
        currentFeature.value = features
        scope.launch {
            delay(1000)
            drawerState.close()
        }
    }

    fun openDrawer() {
        scope.launch {
            drawerState.open()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight(),
                drawerShape = RoundedCornerShape(0.dp),
                drawerContainerColor = MaterialTheme.colorScheme.background
            ) {
                MotivTitle()
                ManagerFeatures.values().forEach {
                    val isSelected = it == currentFeature.value
                    val textColor =
                        MaterialTheme.colorScheme.onBackground.copy(alpha = if (isSelected) 1f else 0.5f)
                    val weight = if (isSelected) FontWeight.Bold else FontWeight.Light
                    val sizeFactor = if (isSelected) 1.3f else 1f
                    val fontSizeAnimation =
                        animateFloatAsState(targetValue = sizeFactor, tween(1000))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .clip(RoundedCornerShape(defaultRadius))
                        .clickable {
                            selectFeature(it)
                        }
                        .padding(16.dp)) {
                        Icon(
                            painter = painterResource(id = it.icon),
                            contentDescription = it.title,
                            tint = textColor,
                            modifier = Modifier.size(24.dp * fontSizeAnimation.value)
                        )
                        Text(
                            text = it.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor,
                            fontWeight = weight,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize * fontSizeAnimation.value,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }


                }
            }

        }) {
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = { openDrawer() }) {
                Icon(
                    painter = painterResource(id = R.drawable.burger_menu_left_svgrepo_com),
                    contentDescription = "Abrir menu",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            getFeatureView()
        }
    }
}

enum class ManagerFeatures(
    val title: String,
    val icon: Int
) {
    HOME(
        "Home",
        com.ilustris.motiv.foundation.R.drawable.ic_saturn_and_other_planets_primary
    ),
    STYLES(
        "Estilos",
        R.drawable.ic_baseline_architecture_24
    ),
    ICONS("Ícones", R.drawable.ic_outline_theater_comedy_24), COVERS(
        "Capas",
        R.drawable.ic_baseline_imagesearch_roller_24
    )
}