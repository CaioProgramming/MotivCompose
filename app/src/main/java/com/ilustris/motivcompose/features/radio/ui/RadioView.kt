@file:OptIn(ExperimentalFoundationApi::class)

package com.ilustris.motivcompose.features.radio.ui

import ai.atick.material.MaterialColor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.ilustris.motiv.foundation.model.Radio
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.motivBrushes
import com.ilustris.motiv.foundation.ui.theme.motivGradient
import com.ilustris.motiv.foundation.ui.theme.radioRadius
import com.ilustris.motivcompose.features.radio.ui.component.RadioListItem
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.ImageState
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState
import com.skydoves.landscapist.glide.GlideRequestType
import java.net.URL

@Composable
fun RadioView(modifier: Modifier, expanded: Boolean = false, onExpand: () -> Unit = {}) {

    val backColor by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colorScheme.background else Color.Transparent,
        tween(500, easing = FastOutSlowInEasing)
    )

    val radios = listOf(
        Radio(name = "NightDrive"),
        Radio(name = "NightwavePlaza"),
        Radio(name = "Vaporwave and synthwave")
    )

    LazyColumn(
        modifier = modifier
            .background(backColor, RoundedCornerShape(radioRadius))
            .border(
                if (expanded) 1.dp else 0.dp,
                MaterialTheme.colorScheme.onBackground.copy(alpha = if (expanded) 0.1f else 0f),
                RoundedCornerShape(radioRadius)
            )
            .padding(4.dp)
            .animateContentSize(tween(1500, easing = FastOutSlowInEasing)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        stickyHeader {

            val firstRadio = radios.first()
            val gifUrl = URL(firstRadio.visualizer)

            val infiniteTransition = rememberInfiniteTransition()
            val rotationAnimation = infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = if (!expanded) 360f else 0f,
                animationSpec = infiniteRepeatable(
                    tween(5000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            val blurAnimation by animateDpAsState(
                targetValue = if (expanded) 0.dp else 15.dp,
                tween(1000, easing = FastOutSlowInEasing)
            )

            val offsetAnimation = infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = (128 * 2).toFloat(),
                animationSpec = infiniteRepeatable(
                    tween(3500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                )
            )
            val brushes = motivBrushes()

            val borderBrush = Brush.linearGradient(
                brushes,
                tileMode = TileMode.Repeated,
                start = Offset(offsetAnimation.value, offsetAnimation.value),
                end = Offset(x = offsetAnimation.value * 3, y = offsetAnimation.value),
            )

            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .animateContentSize(tween(1000, easing = FastOutSlowInEasing)),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlideImage(
                    imageModel = { gifUrl },
                    glideRequestType = GlideRequestType.GIF,
                    imageOptions = ImageOptions(
                        Alignment.Center,
                        contentScale = ContentScale.Crop,
                    ),
                    modifier = Modifier
                        .radioIconModifier(
                            rotationValue = rotationAnimation.value,
                            sizeValue = 64.dp
                        )
                        .animateContentSize(tween(1000))
                        .blur(blurAnimation)
                        .clickable {
                            onExpand()
                        }
                )

                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(tween(1500)) + slideInHorizontally(
                        tween(
                            500,
                            easing = LinearOutSlowInEasing
                        )
                    ),
                    exit = fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    val gradient = motivGradient()
                    Text(
                        text = firstRadio.name,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier
                            .graphicsLayer(alpha = 0.99f)
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = gradient,
                                        blendMode = BlendMode.SrcAtop
                                    )
                                }
                            })
                }
            }
        }
        if (expanded) {
            items(radios.filter { it.name != radios.first().name }) {
                RadioListItem(radio = it, onClickRadio = {
                    Log.i(javaClass.simpleName, "RadioView: radio selected -> $it")
                })
            }
        }

    }
}

fun Modifier.radioIconModifier(rotationValue: Float, sizeValue: Dp) = composed {
    size(sizeValue)
        .padding(4.dp)
        .clip(CircleShape)
        .border(2.dp, brush = Brush.linearGradient(motivBrushes()), CircleShape)
        .rotate(rotationValue)
}


@Preview()
@Composable
fun RadioViewPreview() {
    RadioView(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .animateContentSize()
    )
}

