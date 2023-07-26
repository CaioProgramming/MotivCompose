package com.ilustris.motiv.foundation.utils

import android.content.Context
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilustris.motiv.foundation.R
import com.ilustris.motiv.foundation.model.BlendMode
import com.ilustris.motiv.foundation.model.DEFAULT_FONT_FAMILY
import com.ilustris.motiv.foundation.model.FontStyle
import com.ilustris.motiv.foundation.model.Style
import com.ilustris.motiv.foundation.model.TextAlignment
import com.ilustris.motiv.foundation.model.Window
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.motivBrushes

fun Style?.buildStyleShadow() = if (this != null && shadowStyle != null) {
    shadowStyle!!.buildShadow()
} else {
    Shadow()
}

@Composable
fun Style?.getTextColor() = if (this == null) {
    MaterialTheme.colorScheme.onBackground
} else {
    textProperties?.let { textColor.buildTextColor() } ?: textColor.buildTextColor()
}

fun Style?.getTextAlign() = if (this == null) {
    TextAlign.Center
} else {
    textProperties?.let {
        it.textAlignment.getTextAlign()
    } ?: textAlignment.getTextAlign()
}

@Composable
fun String?.buildTextColor() = if (this == null) {
    MaterialTheme.colorScheme.onBackground
} else {
    Color(android.graphics.Color.parseColor(this))
}

fun Style?.buildFont(context: Context): FontFamily {
    return if (this == null) FontUtils.getFontFamily(DEFAULT_FONT_FAMILY) else {
        if (textProperties != null) {
            FontUtils.getFontFamily(textProperties!!.fontFamily)
        } else {
            FontUtils.getFont(
                context,
                font
            )
        }
    }
}

fun Style?.getFontStyle() = if (this == null) androidx.compose.ui.text.font.FontStyle.Normal else {
    textProperties?.let { it.fontStyle.getFontStyle() } ?: fontStyle.getFontStyle()
}

fun Style?.getFontWeight() = if (this == null) FontWeight.Normal else {
    textProperties?.let { it.fontStyle.getFontWeight() } ?: fontStyle.getFontWeight()
}


