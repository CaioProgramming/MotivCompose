package com.ilustris.motiv.foundation.utils

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.ilustris.motiv.foundation.model.FontStyle
import com.ilustris.motiv.foundation.model.Style
import com.ilustris.motiv.foundation.model.TextAlignment

fun FontStyle?.getFontStyle(): androidx.compose.ui.text.font.FontStyle {
    if (this == null) return androidx.compose.ui.text.font.FontStyle.Normal
    return when (this) {
        FontStyle.ITALIC -> androidx.compose.ui.text.font.FontStyle.Italic
        else -> androidx.compose.ui.text.font.FontStyle.Normal
    }
}

fun FontStyle?.getFontWeight(): FontWeight {
    if (this == null) return FontWeight.Normal
    return when (this) {
        FontStyle.ITALIC, FontStyle.NORMAL -> FontWeight.Normal
        FontStyle.BOLD -> FontWeight.Bold
        FontStyle.BLACK -> FontWeight.Black
    }
}

fun TextAlignment?.getTextAlign(): TextAlign {
    if (this == null) return TextAlign.Center
    return when (this) {
        TextAlignment.JUSTIFY -> TextAlign.Justify
        TextAlignment.CENTER -> TextAlign.Center
        TextAlignment.START -> TextAlign.Start
        TextAlignment.END -> TextAlign.End
    }
}

fun Style?.buildStyleShadow() = if (this != null) Shadow(
    color = Color(
        android.graphics.Color.parseColor(shadowStyle?.shadowColor ?: "#000000")
    ),
    offset = Offset(shadowStyle?.dx ?: 0f, shadowStyle?.dy ?: 0f),
    blurRadius = shadowStyle?.radius ?: 0f
) else {
    Shadow()
}

@Composable
fun String?.buildTextColor() = if (this == null) MaterialTheme.colorScheme.onBackground else Color(
    android.graphics.Color.parseColor(this)
)

fun Style.getFontPosition(): Int? {
    return try {
        font.toInt()
    } catch (e: Exception) {
        null
    }
}

fun Style.buildFont(context: Context): FontFamily {
    return FontUtils.getFont(context, font)
}