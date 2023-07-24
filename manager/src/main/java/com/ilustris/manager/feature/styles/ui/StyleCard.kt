package com.ilustris.manager.feature.styles.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ilustris.motiv.foundation.model.Style
import com.ilustris.motiv.foundation.ui.component.buildStyleShadow
import com.ilustris.motiv.foundation.ui.component.buildTextColor
import com.ilustris.motiv.foundation.ui.component.getTextAlign
import com.ilustris.motiv.foundation.ui.theme.brushsFromPalette
import com.ilustris.motiv.foundation.ui.theme.grayGradients
import com.ilustris.motiv.foundation.ui.theme.paletteFromBitMap
import com.ilustris.motiv.foundation.utils.FontUtils
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState
import com.skydoves.landscapist.glide.GlideRequestType

@Composable
fun StyleCard(style: Style, onSelectStyle: (Style) -> Unit) {
    val context = LocalContext.current
    val font = FontUtils.getFont(context, style.font)
    val shadowStyle = style.buildStyleShadow()
    val textAlign = style.getTextAlign()
    val textColor = style.textColor.buildTextColor()
    var styleBitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    val brush =
        styleBitmap?.asAndroidBitmap()?.paletteFromBitMap()?.brushsFromPalette() ?: grayGradients()

    Box {
        GlideImage(
            imageModel = { style.backgroundURL },
            glideRequestType = GlideRequestType.GIF,
            onImageStateChanged = {
                if (it is GlideImageState.Success && styleBitmap == null) {
                    styleBitmap = it.imageBitmap
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(
                    brush = brush,
                    shape = RoundedCornerShape(0.dp),
                    width = 2.dp,
                )
                .clickable {
                    onSelectStyle(style)
                }
        )

        Text(
            text = "Motiv",
            style = MaterialTheme.typography.headlineMedium.copy(
                shadow = shadowStyle,
                color = textColor,
                textAlign = textAlign,
                fontFamily = font
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.Center)
        )
    }
}