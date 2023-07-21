package com.ilustris.motiv.foundation.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ilustris.motiv.foundation.R
import com.ilustris.motiv.foundation.ui.theme.motivBrushes


@Composable
fun MotivLoader(
    showText: Boolean = true,
    customIcon: Int = R.drawable.ic_saturn_and_other_planets_primary,
    modifier: Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val offsetAnimation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        )
    )
    val brush = Brush.linearGradient(
        motivBrushes(),
        start = Offset(offsetAnimation.value, offsetAnimation.value),
        end = Offset(x = offsetAnimation.value * 10, y = offsetAnimation.value * 5)
    )
    Column(
        modifier = modifier
            .graphicsLayer(alpha = 0.99f)
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    drawRect(brush, blendMode = BlendMode.SrcAtop)
                }
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val appname = stringResource(id = R.string.app_name)
        Icon(
            imageVector = ImageVector.vectorResource(id = customIcon),
            contentDescription = appname,
            modifier = Modifier
                .size(100.dp)
        )

        AnimatedVisibility(visible = showText, enter = fadeIn(), exit = fadeOut()) {
            Text(
                text = appname,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }


    }
}