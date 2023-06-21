package com.ilustris.motivcompose.features.radio.ui

import ai.atick.material.MaterialColor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.motivBrushes
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.ImageState
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState
import com.skydoves.landscapist.glide.GlideRequestType
import java.net.URL

@Composable
fun RadioView(modifier: Modifier) {
    val expanded = remember {
        mutableStateOf(false)
    }
    val backColor by animateColorAsState(targetValue = if (expanded.value) MaterialTheme.colorScheme.background else Color.Transparent, tween(500, easing = LinearOutSlowInEasing))
    Column(
        modifier = modifier
            .background(backColor, RoundedCornerShape(defaultRadius * 2))
            .border(
                if (expanded.value) 1.dp else 0.dp,
                MaterialTheme.colorScheme.onBackground.copy(alpha = if (expanded.value) 0.1f else 0f),
                RoundedCornerShape(defaultRadius * 2)
            )
            .padding(4.dp)
            .animateContentSize(tween(1500, easing = FastOutSlowInEasing)), horizontalAlignment = Alignment.CenterHorizontally
    ) {


        val gifUrl = URL("https://media.giphy.com/media/klZBxHoFLN44M/giphy.gif")

        val infiniteTransition = rememberInfiniteTransition()
        val rotationAnimation = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = if (!expanded.value) 360f else 0f,
            animationSpec = infiniteRepeatable(
                tween(5000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val blurAnimation by animateDpAsState(
            targetValue = if (expanded.value) 0.dp else 15.dp,
            tween(1000, easing = FastOutSlowInEasing)
        )

        val rowModifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()



        Row(
            modifier = rowModifier,
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
                    .blur(blurAnimation)
                    .clickable {
                        expanded.value = !expanded.value
                    }
            )

            AnimatedVisibility(
                visible = expanded.value,
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
                val brushes = motivBrushes()
                Text(
                    text = "NightWave Plaza",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(
                                    brush = Brush.linearGradient(brushes),
                                    blendMode = BlendMode.SrcAtop
                                )
                            }
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

