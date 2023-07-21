@file:OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)

package com.ilustris.motivcompose.features.radio.ui

import android.graphics.Bitmap
import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.GlideBuilder
import com.ilustris.motiv.foundation.R
import com.ilustris.motiv.foundation.model.Radio
import com.ilustris.motiv.foundation.ui.theme.brushsFromPalette
import com.ilustris.motiv.foundation.ui.theme.colorsFromPalette
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.gradientAnimation
import com.ilustris.motiv.foundation.ui.theme.gradientFill
import com.ilustris.motiv.foundation.ui.theme.gradientOverlay
import com.ilustris.motiv.foundation.ui.theme.motivBrushes
import com.ilustris.motiv.foundation.ui.theme.motivGradient
import com.ilustris.motiv.foundation.ui.theme.paletteFromBitMap
import com.ilustris.motiv.foundation.ui.theme.radioIconModifier
import com.ilustris.motiv.foundation.ui.theme.radioRadius
import com.ilustris.motivcompose.features.radio.presentation.RadioViewModel
import com.ilustris.motivcompose.features.radio.ui.component.RadioListItem
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState
import com.skydoves.landscapist.glide.GlideRequestType
import com.skydoves.landscapist.glide.LocalGlideRequestBuilder
import java.net.URL
import kotlin.random.Random

@Composable
fun RadioView(
    modifier: Modifier,
    expanded: Boolean = false,
    swipeEnabled: Boolean = true,
    playingRadio: Radio? = null,
    onSelectRadio: (Radio) -> Unit,
    onExpand: () -> Unit = {}
) {


    var coroutineScope = rememberCoroutineScope()
    var visualizarBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var speed by remember {
        mutableFloatStateOf(1f)
    }

    val radioViewModel = hiltViewModel<RadioViewModel>()
    val radios = radioViewModel.radioList.observeAsState(initial = emptyList()).value


    LaunchedEffect(Unit) {
        radioViewModel.getAllData()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .wrapContentSize()
            .animateContentSize(tween(500, easing = LinearEasing))
            .padding(vertical = 4.dp)
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(radioRadius))
            .clip(RoundedCornerShape(radioRadius))
    ) {

        val pagerState = rememberPagerState() {
            radios.size
        }

        val borderBrush = gradientAnimation(
            visualizarBitmap?.paletteFromBitMap()?.colorsFromPalette() ?: motivBrushes()
        )

        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier.padding(8.dp),
            enter = fadeIn(tween(1000)),
            exit = fadeOut()
        ) {
            Row {
                GlideImage(
                    imageModel = { playingRadio?.visualizer },
                    glideRequestType = GlideRequestType.GIF,
                    onImageStateChanged = { state ->
                        if (state is GlideImageState.Success && visualizarBitmap == null) {
                            visualizarBitmap = state.imageBitmap?.asAndroidBitmap()
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
                    userScrollEnabled = swipeEnabled,
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
            }

        }


        val wavesAnimation by rememberLottieComposition(
            LottieCompositionSpec.RawRes(com.ilustris.motivcompose.R.raw.waves)
        )

        val waveProgress by animateLottieCompositionAsState(
            wavesAnimation,
            speed = speed * 0.4f,
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
                .height(36.dp)
                .clickable {
                    onExpand()
                }
                .gradientFill(brush = borderBrush)

        )



        LaunchedEffect(radios) {
            if (radios.isNotEmpty() && playingRadio == null) {
                delayedFunction(500) {
                }
                pagerState.animateScrollToPage(Random.nextInt(0, radios.size))

            }
        }

        LaunchedEffect(pagerState.currentPage) {
            if (radios.isNotEmpty()) {
                val radio = radios[pagerState.currentPage]
                onSelectRadio(radio)
                speed = Random.nextInt(1, 3).toFloat()
            }
        }

        LaunchedEffect(playingRadio) {
            visualizarBitmap = null
        }
    }
}

@Preview()
@Composable
fun RadioViewPreview() {
    RadioView(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .animateContentSize(), onSelectRadio = {}, onExpand = {})
}

