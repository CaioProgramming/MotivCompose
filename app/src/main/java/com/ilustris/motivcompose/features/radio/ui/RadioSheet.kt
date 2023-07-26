@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalAnimationApi::class
)

package com.ilustris.motivcompose.features.radio.ui

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ilustris.motiv.foundation.model.Radio
import com.ilustris.motiv.foundation.ui.component.MotivLoader
import com.ilustris.motiv.foundation.ui.theme.colorsFromPalette
import com.ilustris.motiv.foundation.ui.theme.gradientAnimation
import com.ilustris.motiv.foundation.ui.theme.gradientFill
import com.ilustris.motiv.foundation.ui.theme.motivBrushes
import com.ilustris.motiv.foundation.ui.theme.paletteFromBitMap
import com.ilustris.motiv.foundation.ui.theme.radioIconModifier
import com.ilustris.motiv.foundation.ui.theme.radioRadius
import com.ilustris.motivcompose.features.radio.presentation.RadioViewModel
import com.ilustris.motivcompose.features.radio.ui.component.RadioListItem
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState
import com.skydoves.landscapist.glide.GlideRequestType
import kotlin.random.Random

@Composable
fun RadioSheet(
    modifier: Modifier,
    enabled: Boolean = true,
    playingRadio: Radio? = null,
    onSelectRadio: (Radio) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val radioViewModel = hiltViewModel<RadioViewModel>()
    val state = radioViewModel.viewModelState.observeAsState().value


    AnimatedVisibility(visible = state == ViewModelBaseState.LoadingState) {
        Box(modifier = Modifier.fillMaxWidth()) {
            MotivLoader(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
            )

        }
    }

    AnimatedVisibility(
        visible = state is ViewModelBaseState.DataListRetrievedState,
        enter = slideInVertically() + fadeIn(
            tween(1000)
        ),
        exit = slideOutVertically()
    ) {
        val radios = if (state is ViewModelBaseState.DataListRetrievedState) {
            state.dataList as List<Radio>
        } else emptyList()

        var visualizerBitmap by remember {
            mutableStateOf<Bitmap?>(null)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .wrapContentSize()
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(radioRadius))
                .clip(RoundedCornerShape(radioRadius))
                .animateContentSize(tween(2000, easing = EaseInElastic))
        ) {

            val page = Random.nextInt(radios.size - 1)
            val pagerState = rememberPagerState(initialPage = page) {
                radios.size
            }

            var speed by remember {
                mutableFloatStateOf(1f)
            }

            val borderBrush = gradientAnimation(
                visualizerBitmap?.paletteFromBitMap()?.colorsFromPalette() ?: motivBrushes()
            )

            val wavesAnimation by rememberLottieComposition(
                LottieCompositionSpec.RawRes(com.ilustris.motivcompose.R.raw.waves)
            )

            val waveProgress by animateLottieCompositionAsState(
                wavesAnimation,
                speed = speed * 0.5f,
                isPlaying = true,
                restartOnPlay = false,
                iterations = LottieConstants.IterateForever
            )


            LottieAnimation(
                wavesAnimation,
                waveProgress,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .gradientFill(brush = borderBrush)
            )


            Row(modifier = Modifier.padding(16.dp)) {
                GlideImage(
                    imageModel = { playingRadio?.visualizer },
                    glideRequestType = GlideRequestType.GIF,
                    onImageStateChanged = { state ->
                        if (state is GlideImageState.Success && visualizerBitmap == null) {
                            visualizerBitmap = state.imageBitmap?.asAndroidBitmap()
                        }
                    },
                    imageOptions = ImageOptions(
                        Alignment.Center,
                        contentScale = ContentScale.Crop,
                    ),
                    modifier = Modifier
                        .radioIconModifier(
                            brush = borderBrush,
                            rotationValue = 0f,
                            sizeValue = 64.dp
                        )

                )
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .gradientFill(brush = borderBrush)
                ) {
                    val radio = radios[it]
                    RadioListItem(radio = radio, onClickRadio = { radio ->
                        onSelectRadio(radio)
                    })
                }

                LaunchedEffect(pagerState.currentPage) {
                    if (radios.isNotEmpty()) {
                        val pagerRadio = radios[pagerState.currentPage]
                        onSelectRadio(pagerRadio)
                        speed = Random.nextInt(1, 3).toFloat()
                    }
                }

                LaunchedEffect(playingRadio) {
                    visualizerBitmap = null
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        radioViewModel.getAllData()
    }
}


