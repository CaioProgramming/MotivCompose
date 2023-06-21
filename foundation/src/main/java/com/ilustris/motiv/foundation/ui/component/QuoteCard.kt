package com.ilustris.motiv.foundation.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilustris.motiv.foundation.ui.theme.MotivTheme
import com.ilustris.motiv.foundation.ui.theme.quoteCardModifier
import com.ilustris.motiv.foundation.ui.theme.motivBrushes

@Composable
fun QuoteCard(quote: String, onClick: (String) -> Unit, modifier: Modifier) {

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .clickable {
                onClick(quote)
            }
            .padding(8.dp)
    ) {

        val brush = motivBrushes()
        Text(
            text = quote,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.CenterVertically)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(brush = Brush.linearGradient(brush), blendMode = BlendMode.SrcAtop)
                    }
                },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge
        )

    }

}

@Preview
@Composable
fun QuoteCardPreview() {
    MotivTheme() {
        QuoteCard(
            "Love is the only force capable of transforming an enemy into a friend.",
            onClick = {},
            modifier = quoteCardModifier()
        )
    }
}