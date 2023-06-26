package com.ilustris.motivcompose.features.radio.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ilustris.motiv.foundation.model.Radio
import com.ilustris.motiv.foundation.ui.theme.radioIconModifier
import com.ilustris.motiv.foundation.ui.theme.radioRadius
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideRequestType

@Composable
fun RadioListItem(radio: Radio, onClickRadio: (Radio) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(radioRadius))
            .clickable {
                onClickRadio(radio)
            },
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            imageModel = { radio.visualizer },
            glideRequestType = GlideRequestType.BITMAP,
            imageOptions = ImageOptions(
                Alignment.Center,
                contentScale = ContentScale.Crop,
            ),
            modifier = Modifier
                .radioIconModifier(
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.onBackground,
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.background
                        )
                    ),
                    rotationValue = 0f,
                    sizeValue = 64.dp
                )

        )

        Text(
            text = radio.name,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
    }

}