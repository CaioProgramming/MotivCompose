package com.ilustris.motiv.foundation.ui.theme

import ai.atick.material.MaterialColor
import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.palette.graphics.Palette
import com.ilustris.motiv.foundation.R

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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
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
    MaterialColor.Purple300,
    MaterialColor.Purple600,
    MaterialColor.Purple900
)

@Composable
fun motivGradient() = Brush.linearGradient(colors = motivBrushes())

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
        MaterialColor.Purple800
    }

    val vibrantSwatch = try {
        Color(this.vibrantSwatch!!.rgb)
    } catch (e: Exception) {
        MaterialColor.PurpleA700
    }

    val mutedSwatch = try {
        Color(this.mutedSwatch!!.rgb)
    } catch (e: Exception) {
        MaterialColor.DeepPurpleA200
    }

    return Brush.linearGradient(listOf(dominantSwatch, vibrantSwatch, mutedSwatch))
}


fun Modifier.quoteCardModifier() = composed {
    padding(16.dp)
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(defaultRadius))
        .clip(RoundedCornerShape(defaultRadius))

}


