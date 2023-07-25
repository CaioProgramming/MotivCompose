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
import com.ilustris.motiv.foundation.model.FontStyle
import com.ilustris.motiv.foundation.model.Style
import com.ilustris.motiv.foundation.model.TextAlignment
import com.ilustris.motiv.foundation.model.Window
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.motivBrushes

fun Style?.buildStyleShadow() = if (this != null && shadowStyle != null)
    shadowStyle!!.buildShadow() else {
    Shadow()
}

@Composable
fun String?.buildTextColor() = if (this == null) MaterialTheme.colorScheme.onBackground else Color(
    android.graphics.Color.parseColor(this)
)


fun Style.buildFont(context: Context): FontFamily {
    if (textProperties != null) {
        return FontUtils.getFontFamily(textProperties!!.fontFamily)
    }
    return FontUtils.getFont(context, font)
}

