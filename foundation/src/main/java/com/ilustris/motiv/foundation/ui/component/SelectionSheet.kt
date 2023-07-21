@file:OptIn(ExperimentalFoundationApi::class)

package com.ilustris.motiv.foundation.ui.component

import android.provider.SyncStateContract.Columns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ilustris.motiv.foundation.model.Cover
import com.ilustris.motiv.foundation.model.Icon
import com.ilustris.motiv.foundation.ui.presentation.CoversViewModel
import com.ilustris.motiv.foundation.ui.presentation.IconsViewModel
import com.ilustris.motiv.foundation.ui.theme.gradientAnimation
import com.ilustris.motiv.foundation.ui.theme.gradientFill
import com.ilustris.motiv.foundation.ui.theme.grayGradients
import com.ilustris.motiv.foundation.ui.theme.motivGradient
import com.ilustris.motiv.foundation.ui.theme.radioIconModifier
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState
import com.skydoves.landscapist.glide.GlideRequestType

@Composable
fun IconSheet(onSelection: (Icon) -> Unit) {

    val viewModel = hiltViewModel<IconsViewModel>()
    val viewModelState = viewModel.viewModelState.observeAsState()


    AnimatedVisibility(
        visible = viewModelState.value is ViewModelBaseState.LoadingState,
        modifier = Modifier.fillMaxSize(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                strokeWidth = 5.dp,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.Center)
                    .gradientFill(gradientAnimation())
            )
        }

    }

    AnimatedVisibility(
        visible = viewModelState.value is ViewModelBaseState.ErrorState,
        modifier = Modifier.fillMaxSize(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Text(
            text = "Ocorreu um erro inesperado ao carregar os ícones.",
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light)
        )

    }

    if (viewModelState.value is ViewModelBaseState.DataListRetrievedState) {
        val icons =
            (viewModelState.value as ViewModelBaseState.DataListRetrievedState).dataList as List<Icon>

        val gridCells = 2

        AnimatedVisibility(visible = icons.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(gridCells),
                content = {
                    item(span = { GridItemSpan(gridCells) }) {
                        Text(
                            text = "Alterar avatar",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }

                    items(icons.size, key = {
                        icons[it].id
                    }) { index ->


                        val icon = icons[index]
                        val loadedImage = remember { mutableStateOf(false) }
                        val alphaAnimation = animateFloatAsState(
                            targetValue = if (loadedImage.value) 1f else 0f,
                            tween(1000)
                        )
                        GlideImage(
                            imageModel = { icon.uri },
                            glideRequestType = GlideRequestType.BITMAP,
                            onImageStateChanged = {
                                loadedImage.value = it is GlideImageState.Success
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .alpha(alphaAnimation.value)
                                .radioIconModifier(
                                    0f,
                                    170.dp,
                                    motivGradient(),
                                    2.dp
                                )
                                .clickable {
                                    onSelection(icon)
                                }
                        )
                    }

                })
        }
    }



    LaunchedEffect(Unit) {
        viewModel.getAllData()
    }

}

@Composable
fun CoverSheet(onSelection: (Cover) -> Unit) {

    val viewModel = hiltViewModel<CoversViewModel>()
    val viewModelState = viewModel.viewModelState.observeAsState()


    AnimatedVisibility(
        visible = viewModelState.value is ViewModelBaseState.LoadingState,
        modifier = Modifier.fillMaxSize(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                strokeWidth = 5.dp,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.Center)
                    .gradientFill(gradientAnimation())
            )
        }

    }

    AnimatedVisibility(
        visible = viewModelState.value is ViewModelBaseState.ErrorState,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Text(
            text = "Ocorreu um erro inesperado ao carregar os ícones.",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light)
        )

    }

    AnimatedVisibility(
        visible = viewModelState.value is ViewModelBaseState.DataListRetrievedState,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val covers =
            (viewModelState.value as ViewModelBaseState.DataListRetrievedState).dataList as List<Cover>

        LazyVerticalGrid(columns = GridCells.Fixed(2), content = {
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "Alterar capa",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(), style = MaterialTheme.typography.headlineSmall
                )
            }

            items(covers.size) { index ->
                val cover = covers[index]
                GlideImage(
                    imageModel = { cover.url },
                    glideRequestType = GlideRequestType.GIF,
                    modifier = Modifier
                        .size(200.dp)
                        .border(color = MaterialTheme.colorScheme.onBackground, width = 2.dp)
                        .clickable {
                            onSelection(cover)
                        }
                )
            }

        })

    }

    LaunchedEffect(Unit) {
        viewModel.getAllData()
    }

}