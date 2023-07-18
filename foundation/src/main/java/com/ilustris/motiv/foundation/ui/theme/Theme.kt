package com.ilustris.motiv.foundation.ui.theme

import ai.atick.material.MaterialColor
import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.palette.graphics.Palette
import com.ilustris.motiv.foundation.R
import java.lang.Math.PI
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt

private val DarkColorScheme = darkColorScheme(
    primary = MaterialColor.DeepPurple300,
    secondary = MaterialColor.DeepPurple500,
    tertiary = MaterialColor.DeepPurpleA200,
    background = MaterialColor.Black,
    surface = MaterialColor.Gray900
)

private val LightColorScheme = lightColorScheme(
    primary = MaterialColor.Purple500,
    secondary = MaterialColor.Purple800,
    tertiary = MaterialColor.PurpleA700,
    background = MaterialColor.White,
    surface = MaterialColor.Gray300

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun MotivTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor =
                if (darkTheme) MaterialColor.Black.toArgb() else MaterialColor.White.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

val defaultRadius = 15.dp
val radioRadius = 30.dp

val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

@Composable
fun motivBrushes() = listOf(
    MaterialColor.DeepPurple500,
    MaterialColor.PinkA200,
    MaterialColor.Purple500,
    MaterialColor.PurpleA700,
    MaterialColor.Pink500,
)

@Composable
fun motivGradient() = Brush.linearGradient(colors = motivBrushes())

@Composable
fun grayGradients() = Brush.linearGradient(
    colors = listOf(
        MaterialColor.Gray300,
        MaterialColor.Gray500,
        MaterialColor.Gray800,
        Color.Transparent
    )
)

@Composable
fun transparentFadeGradient(): Brush {


    val mainColor = MaterialTheme.colorScheme.background

    val blackColors = listOf(
        Color.Transparent,
        mainColor.copy(alpha = 1f),
    )


    return Brush.verticalGradient(
        1f to Color.Transparent,
        0.5f to mainColor,
        endY = 750f
    )
}

@Composable
fun getDeviceWidth() = LocalConfiguration.current.screenWidthDp

@Composable
fun getDeviceHeight() = LocalConfiguration.current.screenHeightDp

@Composable
fun Bitmap.paletteFromBitMap() = Palette.from(this).generate()

fun Palette.brushsFromPalette(): Brush {
    val dominantSwatch = try {
        Color(this.dominantSwatch!!.rgb)
    } catch (e: Exception) {
        MaterialColor.Gray900
    }

    val vibrantSwatch = try {
        Color(this.vibrantSwatch!!.rgb)
    } catch (e: Exception) {
        MaterialColor.Gray500
    }

    val mutedSwatch = try {
        Color(this.mutedSwatch!!.rgb)
    } catch (e: Exception) {
        MaterialColor.Gray300
    }

    return Brush.linearGradient(listOf(dominantSwatch, vibrantSwatch, mutedSwatch))
}

fun Palette.colorsFromPalette(): List<Color> {
    val dominantSwatch = try {
        Color(this.dominantSwatch!!.rgb)
    } catch (e: Exception) {
        MaterialColor.Gray900
    }

    val vibrantSwatch = try {
        Color(this.vibrantSwatch!!.rgb)
    } catch (e: Exception) {
        MaterialColor.Gray500
    }

    val mutedSwatch = try {
        Color(this.mutedSwatch!!.rgb)
    } catch (e: Exception) {
        MaterialColor.Gray300
    }

    return listOf(dominantSwatch, vibrantSwatch, mutedSwatch)
}


fun Modifier.quoteCardModifier() = clip(RoundedCornerShape(defaultRadius))


fun Modifier.gradientFill(brush: Brush) =
    graphicsLayer(alpha = 0.99f)
        .drawWithCache {
            onDrawWithContent {
                drawContent()
                drawRect(brush, blendMode = BlendMode.SrcAtop)
            }
        }

fun Modifier.gradientOverlay(brush: Brush) =
    graphicsLayer()
        .drawWithCache {
            onDrawWithContent {
                drawContent()
                drawRect(brush, blendMode = BlendMode.Darken)
            }
        }

fun Modifier.radioIconModifier(
    rotationValue: Float,
    sizeValue: Dp,
    brush: Brush,
    borderWidth: Dp = 3.dp,
) =
    border(
        borderWidth,
        brush = brush,
        CircleShape
    )
        .padding(4.dp)
        .clip(CircleShape)
        .size(sizeValue)
        .rotate(rotationValue)




