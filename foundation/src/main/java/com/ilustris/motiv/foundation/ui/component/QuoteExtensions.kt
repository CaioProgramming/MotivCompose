@file:OptIn(ExperimentalAnimationApi::class)

package com.ilustris.motiv.foundation.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseInOutBounce
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState
import com.skydoves.landscapist.glide.GlideRequestType
import kotlin.random.Random

@Composable
fun CardBackground(
    modifier: Modifier,
    backgroundImage: String?,
    colorFilter: Color? = Color.Transparent,
    loadedBitmap: (ImageBitmap) -> Unit
) {


    var imageLoaded by remember {
        mutableStateOf(false)
    }

    AnimatedContent(targetState = backgroundImage, transitionSpec = {
        fadeIn(tween(1000)) with fadeOut(tween(500))
    }) {
        val imageBlur by animateDpAsState(
            targetValue = if (imageLoaded) 0.dp else defaultRadius,
            tween(1500)
        )

        val imageAlpha by animateFloatAsState(
            targetValue = if (imageLoaded) 1f else 0f,
            tween(1500)
        )
        GlideImage(
            imageModel = { it },
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                colorFilter = ColorFilter.tint(
                    colorFilter ?: Color.Transparent,
                    blendMode = BlendMode.SrcAtop
                )
            ),
            glideRequestType = GlideRequestType.GIF,
            modifier = modifier
                .alpha(imageAlpha)
                .blur(imageBlur),
            onImageStateChanged = {
                imageLoaded = it is GlideImageState.Success
                if (it is GlideImageState.Success) {
                    it.imageBitmap?.let(loadedBitmap)
                }
            },
        )
    }

}