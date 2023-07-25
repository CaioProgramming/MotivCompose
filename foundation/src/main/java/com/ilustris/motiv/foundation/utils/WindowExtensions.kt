package com.ilustris.motiv.foundation.utils

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilustris.motiv.foundation.R
import com.ilustris.motiv.foundation.model.Window
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.motivBrushes

@Composable
fun Window.getWindowView(modifier: Modifier, showTitle: Boolean = true) {
    val arrangement = when (this) {
        Window.CLASSIC -> Arrangement.SpaceBetween
        Window.MODERN -> Arrangement.Start
    }
    Column(
        modifier
            .fillMaxWidth()
            .animateContentSize(tween(1000, easing = EaseIn))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = arrangement
        ) {
            when (this@getWindowView) {
                Window.MODERN -> {
                    motivBrushes().forEach {
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(12.dp)
                                .background(it, CircleShape)
                                .border(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    width = 1.dp,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Window.CLASSIC -> {
                    val fontFamily = FontUtils.getFontFamily("VT323")

                    Text(
                        text = stringResource(id = R.string.app_name),
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = fontFamily,
                    )

                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = stringResource(id = R.string.app_name),
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

        }

        this@getWindowView.dividerForWindow()
    }

}

@Preview
@Composable
fun PreviewWindow() {
    Column() {
        Window.values().forEach {
            it.getWindowView(modifier = Modifier.fillMaxWidth())
        }
    }

}

@Composable
fun Window.dividerForWindow() {

    val targetColor = when (this) {
        Window.CLASSIC -> MaterialTheme.colorScheme.onBackground
        Window.MODERN -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    }
    val targetThickness = when (this) {
        Window.CLASSIC -> 2.dp
        Window.MODERN -> 1.dp
    }
    val borderThick = animateDpAsState(targetValue = targetThickness, tween(1000, easing = EaseIn))
    val color = animateColorAsState(targetValue = targetColor, tween(1000, easing = EaseIn))
    Divider(modifier = Modifier.fillMaxWidth(), color = color.value, thickness = borderThick.value)
}

fun Modifier.borderForWindow(window: Window?): Modifier = composed {

    val shapeForWindows = RoundedCornerShape(defaultRadius)

    when (window) {
        Window.CLASSIC -> background(
            MaterialTheme.colorScheme.background, RoundedCornerShape(
                defaultRadius
            )
        ).border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.onBackground,
            shape = shapeForWindows
        )

        Window.MODERN, null -> background(
            MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(
                defaultRadius
            )
        ).border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            shape = shapeForWindows
        )
    }
}