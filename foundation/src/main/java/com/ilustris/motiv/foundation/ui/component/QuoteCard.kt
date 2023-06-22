package com.ilustris.motiv.foundation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ilustris.motiv.foundation.model.Quote
import com.ilustris.motiv.foundation.ui.theme.MotivTheme
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.quoteCardModifier
import com.ilustris.motiv.foundation.ui.theme.motivBrushes
import com.ilustris.motiv.foundation.ui.theme.radioRadius
import com.silent.ilustriscore.core.utilities.DateFormats
import com.silent.ilustriscore.core.utilities.format
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun QuoteCard(quote: Quote, onClick: (Quote) -> Unit, modifier: Modifier) {

    ConstraintLayout(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .padding(vertical = 8.dp)
    ) {

        val brush = motivBrushes()
        val (quoteInfo,
            quoteText,
            quoteAuthor,
            likeButton,
            actionsRow) = createRefs()


        Text(
            text = quote.quote,
            modifier = Modifier
                .constrainAs(quoteText) {
                    top.linkTo(quoteInfo.bottom)
                    bottom.linkTo(quoteAuthor.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge
        )

        Text(text = quote.author, modifier = Modifier
            .constrainAs(quoteAuthor) {
                bottom.linkTo(actionsRow.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            }
            .padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            softWrap = true
        )

        Row(modifier = Modifier
            .constrainAs(quoteInfo) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.5f), RoundedCornerShape(
                    radioRadius
                )
            )
            .padding(8.dp)
            .clip(RoundedCornerShape(radioRadius))
            .clickable {

            }, verticalAlignment = Alignment.CenterVertically
        ) {

            MotivLoader(
                showText = false,
                modifier = Modifier.size(40.dp)
            )

            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(text = quote.author.trimEnd(), style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = quote.data.format(DateFormats.DD_OF_MM_FROM_YYYY),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        IconButton(onClick = { /*TODO*/ }, modifier = Modifier
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                CircleShape
            )
            .constrainAs(likeButton) {
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
                top.linkTo(actionsRow.top)
                bottom.linkTo(actionsRow.bottom)
            }) {
            Icon(
                Icons.Rounded.FavoriteBorder,
                contentDescription = "Curtir",
                tint = MaterialTheme.colorScheme.onBackground,

                )
        }

        Row(modifier = Modifier.constrainAs(actionsRow) {
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
        }) {

            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Rounded.Edit,
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(
                onClick = { /*TODO*/ }, modifier = Modifier
                    .padding(8.dp)
                    .background(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Rounded.Send,
                    contentDescription = "Compartilhar",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

        }

    }

}

@Preview
@Composable
fun QuoteCardPreview() {
    MotivTheme() {
        QuoteCard(
            Quote(quote = "Love is the only force capable of transforming an enemy into a friend."),
            onClick = {},
            modifier = Modifier.quoteCardModifier()
        )
    }
}