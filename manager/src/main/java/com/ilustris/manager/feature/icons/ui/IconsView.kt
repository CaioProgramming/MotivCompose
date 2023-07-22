@file:OptIn(ExperimentalMaterialApi::class)

package com.ilustris.manager.feature.icons.ui

import ai.atick.material.MaterialColor
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.ilustris.manager.R
import com.ilustris.manager.feature.icons.presentation.IconsManagerViewModel
import com.ilustris.motiv.foundation.model.Icon
import com.ilustris.motiv.foundation.ui.component.IconView
import com.ilustris.motiv.foundation.ui.component.MotivLoader
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.grayGradients
import com.ilustris.motiv.foundation.ui.theme.radioIconModifier
import com.silent.ilustriscore.core.model.ViewModelBaseState
import kotlinx.coroutines.launch

@Composable
fun IconsView() {
    val iconsManagerViewModel = hiltViewModel<IconsManagerViewModel>()
    val state = iconsManagerViewModel.viewModelState.observeAsState().value

    val dialogVisibility = remember {
        mutableStateOf(false)
    }
    var selectedIcon by remember {
        mutableStateOf<Icon?>(null)
    }
    val columns = 3
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    fun enableBottomSheet(enabled: Boolean) {
        coroutineScope.launch {
            if (enabled) {
                bottomSheetState.show()
            } else {
                bottomSheetState.hide()
            }
        }
    }

    fun dismissRequest() {
        dialogVisibility.value = false
    }

    fun deleteIcon() {
        selectedIcon?.let {
            iconsManagerViewModel.deleteData(it.id)
        }
        dismissRequest()
    }


    ModalBottomSheetLayout(
        sheetContent = {
            IconUploadSheet(saveIcon = {
                iconsManagerViewModel.saveIcon(it)
                enableBottomSheet(false)
            })
        },
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(defaultRadius),
        sheetBackgroundColor = MaterialTheme.colorScheme.surface
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize(tween(500)),
            columns = GridCells.Fixed(columns)
        ) {
            item(span = { GridItemSpan(columns) }) {
                AnimatedVisibility(
                    visible = state == ViewModelBaseState.LoadingState,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    MotivLoader(modifier = Modifier.size(150.dp))
                }
            }

            if (state is ViewModelBaseState.DataListRetrievedState) {

                item {
                    IconButton(
                        onClick = { enableBottomSheet(true) },
                        modifier = Modifier
                            .padding(8.dp)
                            .radioIconModifier(0f, 100.dp, grayGradients())
                    ) {
                        Icon(
                            Icons.Rounded.KeyboardArrowUp,
                            contentDescription = "enviar novo icone",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                items((state.dataList as List<Icon>).size) { index ->
                    val icon = state.dataList[index] as Icon
                    IconView(icon = icon, iconSize = 100.dp, onSelectIcon = {
                        selectedIcon = it
                    })
                }
            }
        }
    }


    AnimatedVisibility(
        visible = dialogVisibility.value,
        enter = fadeIn(),
        exit = scaleOut()
    ) {
        AlertDialog(modifier = Modifier
            .wrapContentSize()
            .background(
                MaterialTheme.colorScheme.surface, RoundedCornerShape(
                    defaultRadius
                )
            ),
            shape = RoundedCornerShape(defaultRadius),
            title = {
                Text(
                    text = "Tem certeza?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )

            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    selectedIcon?.let {
                        IconView(icon = it, isSelected = true, onSelectIcon = { })
                    }
                    Text(text = stringResource(R.string.delete_icon_message))

                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        dismissRequest()
                    }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(defaultRadius),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Text(
                        text = "Cancelar", textAlign = TextAlign.Center, modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(defaultRadius))
                            .padding(8.dp)
                    )
                }

            },
            confirmButton = {
                Button(
                    onClick = {
                        deleteIcon()
                        dismissRequest()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(defaultRadius),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialColor.Red500,
                        contentColor = MaterialColor.White
                    )
                ) {
                    Text(
                        text = "Excluir", textAlign = TextAlign.Center, modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            onDismissRequest = {
                dismissRequest()
            })
    }


    LaunchedEffect(Unit) {
        iconsManagerViewModel.getAllData()
    }

    LaunchedEffect(state) {
        if (state is ViewModelBaseState.DataSavedState || state == ViewModelBaseState.DataDeletedState || state is ViewModelBaseState.DataUpdateState) {
            iconsManagerViewModel.getAllData()
        }
    }

    LaunchedEffect(selectedIcon) {
        Log.i("IconsView", "IconsView: Icon selected $selectedIcon")
        dialogVisibility.value = selectedIcon != null
    }

}




