@file:OptIn(ExperimentalMaterial3Api::class)

package com.ilustris.manager.feature

import ai.atick.material.MaterialColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ilustris.manager.R
import com.ilustris.manager.feature.home.ui.ManagerHomeView
import com.ilustris.motiv.foundation.ui.theme.MotivTitle
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
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
                Text(text = "Icones")
            }

            ManagerFeatures.COVERS -> {
                Text(text = "Capas")
            }
        }
    }

    fun selectFeature(features: ManagerFeatures) {
        currentFeature.value = features
        scope.launch {
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
            ModalDrawerSheet() {
                MotivTitle()
                ManagerFeatures.values().sortedBy { it.name }.forEach {
                    val isSelected = it == currentFeature.value
                    val textColor =
                        if (isSelected) MaterialColor.White else MaterialTheme.colorScheme.onSurface
                    val backColor =
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f) else Color.Transparent

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .clickable {
                            selectFeature(it)
                        }
                        .background(
                            backColor,
                            RoundedCornerShape(
                                bottomEnd = defaultRadius,
                                topEnd = defaultRadius
                            )
                        )
                        .padding(vertical = 8.dp, horizontal = 32.dp)) {
                        Icon(painter = painterResource(id = it.icon), contentDescription = it.title)
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = textColor,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(16.dp)
                        )
                    }


                }
            }

        }) {
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = { openDrawer() }) {
                Icon(
                    Icons.Rounded.Menu,
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