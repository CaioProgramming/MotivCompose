@file:OptIn(ExperimentalAnimationApi::class)

package com.ilustris.motiv.foundation.ui.component

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutBounce
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.with
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.silent.ilustriscore.core.utilities.delayedFunction
import kotlin.random.Random

@Composable
fun TypeWriterText(
    text: String,
    shadow: Shadow?,
    color: Color,
    textAlign: TextAlign,
    fontFamily: FontFamily,
    modifier: Modifier,
    onCompleteType: () -> Unit
) {


    var textIndex by remember { mutableIntStateOf(0) }
    val textPart = text.substring(0, textIndex)
    val headlineMedium = MaterialTheme.typography.headlineMedium.copy(shadow = shadow)
    var textStyle by remember { mutableStateOf(headlineMedium) }

    LaunchedEffect(textIndex) {
        val delay = (50 * Random.nextInt(1, 2)).toLong()
        delayedFunction(delay) {
            if (textIndex < text.length) {
                textIndex++
            } else {
                onCompleteType()
            }
        }

    }

    LaunchedEffect(Unit) {
        delayedFunction(500) {
            textIndex++
        }
    }

    Text(
        text = textPart,
        style = textStyle,
        textAlign = textAlign,
        modifier = modifier.animateContentSize(tween(500, easing = EaseInOutBounce)),
        color = color,
        fontFamily = fontFamily,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowHeight && textPart == text) {
                textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.8)
            }
        }
    )


}