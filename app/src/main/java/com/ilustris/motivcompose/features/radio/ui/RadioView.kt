@file:OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)

package com.ilustris.motivcompose.features.radio.ui

import android.graphics.Bitmap
import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
import com.ilustris.motiv.foundation.model.Radio
import com.ilustris.motiv.foundation.ui.theme.brushsFromPalette
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.motivGradient
import com.ilustris.motiv.foundation.ui.theme.paletteFromBitMap
import com.ilustris.motiv.foundation.ui.theme.radioIconModifier
import com.ilustris.motiv.foundation.ui.theme.radioRadius
import com.ilustris.motivcompose.features.radio.presentation.RadioViewModel
import com.ilustris.motivcompose.features.radio.ui.component.RadioListItem
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState
import com.skydoves.landscapist.glide.GlideRequestType
import java.net.URL

@Composable
fun RadioView(
    modifier: Modifier,
    expanded: Boolean = false,
    playingRadio: Radio? = null,
    onSelectRadio: (Radio) -> Unit,
    onExpand: () -> Unit = {}
) {

    val backColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.background,
        tween(500, easing = FastOutSlowInEasing)
    )

    val blurAnimation by animateDpAsState(
        targetValue = if (expanded) 0.dp else defaultRadius,
        tween(5000, easing = EaseInBounce)
    )

    var visualizarBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    val radioViewModel = hiltViewModel<RadioViewModel>()
    val radios = radioViewModel.radioList.observeAsState(initial = emptyList()).value


    LaunchedEffect(Unit) {
        radioViewModel.getAllData()
    }



    AnimatedVisibility(
        visible = radios.isNotEmpty(),
        enter = scaleIn(),
        exit = scaleOut(),
        modifier = modifier.fillMaxWidth()
    ) {

        LazyColumn(
            modifier = Modifier
                .background(backColor, RoundedCornerShape(radioRadius))
                .border(
                    if (expanded) 2.dp else 1.dp,
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    RoundedCornerShape(radioRadius)
                )
                .animateContentSize(tween(1000, easing = FastOutSlowInEasing)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            playingRadio?.run {
                stickyHeader {

                    val firstRadio = radios.find { it.id == id }
                    val gifUrl = URL(firstRadio?.visualizer)

                    val infiniteTransition = rememberInfiniteTransition()
                    val rotationAnimation = infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = if (!expanded) 360f else 0f,
                        animationSpec = infiniteRepeatable(
                            tween(2500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                    val scaleAnimation = animateDpAsState(
                        targetValue = if (!expanded) 32.dp else 64.dp,
                        tween(1500, easing = FastOutSlowInEasing)
                    )
                    val borderAnimation = animateDpAsState(
                        targetValue = if (!expanded) 2.dp else 4.dp,
                        tween(1500, easing = EaseInBounce)
                    )


                    val borderBrush = visualizarBitmap?.paletteFromBitMap()?.brushsFromPalette()
                        ?: motivGradient()

                    if (expanded) {
                        Divider(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(0.2f)
                                .clip(RoundedCornerShape(defaultRadius))
                                .wrapContentHeight()
                                .clickable { onExpand() },
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            thickness = 5.dp
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(radioRadius))
                            .clickable {
                                onExpand()
                            }
                            .animateContentSize(tween(500, easing = LinearOutSlowInEasing)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlideImage(
                            imageModel = { gifUrl },
                            glideRequestType = GlideRequestType.GIF,
                            onImageStateChanged = { state ->
                                if (state is GlideImageState.Success) {
                                    state.imageBitmap?.let {
                                        visualizarBitmap = it.asAndroidBitmap()
                                    }
                                }
                            },
                            imageOptions = ImageOptions(
                                Alignment.Center,
                                contentScale = ContentScale.Crop
                            ),
                            modifier = Modifier
                                .padding(8.dp)
                                .radioIconModifier(
                                    brush = borderBrush,
                                    rotationValue = rotationAnimation.value,
                                    sizeValue = scaleAnimation.value,
                                    borderWidth = borderAnimation.value
                                )
                                .background(
                                    borderBrush,
                                    CircleShape
                                )
                                .clip(CircleShape)
                                .animateContentSize(tween(100))
                        )

                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontStyle = FontStyle.Italic
                            ),
                            modifier = Modifier
                                .graphicsLayer(alpha = 0.99f)
                                .drawWithCache {
                                    onDrawWithContent {
                                        drawContent()
                                        drawRect(
                                            brush = borderBrush,
                                            blendMode = BlendMode.SrcAtop
                                        )
                                    }
                                })
                    }
                }

            }
            if (expanded) {
                val currentRadios =
                    if (playingRadio == null) radios else radios.filter { it.id != playingRadio.id }


                items(currentRadios) {
                    RadioListItem(radio = it, onClickRadio = { radio ->
                        onSelectRadio(radio)
                    })
                }
            }

        }
        if (playingRadio == null) {
            onSelectRadio(radios.random())
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

