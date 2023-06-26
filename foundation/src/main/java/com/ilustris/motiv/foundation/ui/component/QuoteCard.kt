package com.ilustris.motiv.foundation.ui.component

import ai.atick.material.MaterialColor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutBounce
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.palette.graphics.Palette
import com.google.android.play.integrity.internal.t
import com.ilustris.motiv.foundation.model.QuoteDataModel
import com.ilustris.motiv.foundation.model.Style
import com.ilustris.motiv.foundation.model.TextAlignment
import com.ilustris.motiv.foundation.ui.theme.brushsFromPalette
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.getDeviceHeight
import com.ilustris.motiv.foundation.ui.theme.getDeviceWidth
import com.ilustris.motiv.foundation.ui.theme.motivGradient
import com.ilustris.motiv.foundation.ui.theme.radioRadius
import com.ilustris.motiv.foundation.utils.FontUtils
import com.silent.ilustriscore.core.utilities.DateFormats
import com.silent.ilustriscore.core.utilities.format
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState

@Composable
fun QuoteCard(quoteDataModel: QuoteDataModel, modifier: Modifier) {
    val quote = quoteDataModel.quoteBean
    val context = LocalContext.current
    val defaultFont =
        FontUtils.getFontFamily(FontUtils.getFamily(context, quoteDataModel.style?.font ?: 0))

    var backgroundBitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    var showInfo by remember {
        mutableStateOf(false)
    }

    var palette by remember {
        mutableStateOf<Palette?>(null)
    }

    var animationCompleted by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(backgroundBitmap) {
        backgroundBitmap?.let {
            if (palette == null) palette = Palette.Builder(it.asAndroidBitmap()).generate()
        }
    }

    val brush = if (animationCompleted) palette?.brushsFromPalette()
        ?: motivGradient() else Brush.linearGradient(
        listOf(MaterialTheme.colorScheme.background, Color.Transparent)
    )


    val borderAnimation by animateDpAsState(
        targetValue = if (animationCompleted) 3.dp else 0.dp,
        tween(500)
    )

    ConstraintLayout(
        modifier = modifier.border(
            width = borderAnimation,
            brush = brush,
            shape = RoundedCornerShape(
                defaultRadius
            )
        )
    ) {

        val (
            background,
            quoteInfo,
            quoteText,
            quoteAuthor,
            likeButton,
            actionsRow) = createRefs()

        val style = quoteDataModel.style
        val shadowStyle = style.buildStyleShadow()
        val textAlign = style.getTextAlign()
        val textColor = style?.textColor.buildTextColor()
        var imageLoaded by remember {
            mutableStateOf(false)
        }


        val imageBlur by animateDpAsState(
            targetValue = if (imageLoaded) 0.dp else 50.dp,
            tween(1500)
        )

        val imageAlpha by animateFloatAsState(
            targetValue = if (imageLoaded && animationCompleted) 1f else 0f,
            tween(1500)
        )

        GlideImage(
            imageModel = { style?.backgroundURL },
            modifier = Modifier
                .constrainAs(
                    background
                ) {
                    top.linkTo(parent.top)
                    bottom.linkTo(actionsRow.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .alpha(imageAlpha)
                .blur(imageBlur),
            onImageStateChanged = {
                imageLoaded = it is GlideImageState.Success
                if (it is GlideImageState.Success) {
                    backgroundBitmap = it.imageBitmap
                }
            },
        )

        Column(
            modifier = Modifier
                .constrainAs(quoteText) {
                    top.linkTo(quoteInfo.bottom)
                    bottom.linkTo(quoteAuthor.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.matchParent
                    height = Dimension.fillToConstraints
                }
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TypeWriterText(
                text = quote.quote,
                shadow = shadowStyle,
                color = textColor,
                textAlign = textAlign,
                fontFamily = defaultFont,
                textStyle = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                animationCompleted = true
            }
        }


        Text(text = quote.author, modifier = Modifier
            .constrainAs(quoteAuthor) {
                bottom.linkTo(actionsRow.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            }
            .padding(16.dp)
            .graphicsLayer(alpha = imageAlpha),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(
                shadow = shadowStyle,
                color = textColor,
                textAlign = textAlign,
                fontFamily = defaultFont
            ),
            fontStyle = FontStyle.Italic,
            softWrap = true
        )

        AnimatedVisibility(
            visible = quoteDataModel.user != null,
            modifier = Modifier
                .constrainAs(quoteInfo) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
                .padding(16.dp)
                .graphicsLayer(alpha = imageAlpha)
                .animateContentSize(tween(1000))) {

            var infoExpanded by remember {
                mutableStateOf(false)
            }

            val blurInfoAnimation by animateDpAsState(
                targetValue = if (infoExpanded) 0.dp else defaultRadius,
                tween(500)
            )

            val iconAlphaAnimation by animateFloatAsState(
                targetValue = if (infoExpanded) 1f else 0.7f,
                tween(1500)
            )


            Row(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
                        RoundedCornerShape(radioRadius)
                    )
                    .clip(RoundedCornerShape(radioRadius))
                    .animateContentSize(tween(250, easing = LinearEasing)),
                verticalAlignment = Alignment.CenterVertically
            ) {


                GlideImage(
                    imageModel = {
                        quoteDataModel.user?.picurl ?: ""
                    },
                    imageOptions = ImageOptions(
                        requestSize = IntSize(52, 52),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                    ),
                    modifier = Modifier
                        .size(50.dp)
                        .blur(blurInfoAnimation, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                        .clip(CircleShape)
                        .border(2.dp, brush = brush, CircleShape)
                        .clickable {
                            infoExpanded = !infoExpanded
                            showInfo = infoExpanded
                        }
                )



                AnimatedVisibility(
                    visible = showInfo && infoExpanded,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(modifier = Modifier
                        .padding(8.dp)
                        .clickable {

                        }) {
                        Text(
                            text = (quoteDataModel.user?.name) ?: "".trimEnd(),
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = quote.data.format(DateFormats.DD_OF_MM_FROM_YYYY),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }


        IconButton(onClick = { /*TODO*/ }, modifier = Modifier
            .padding(8.dp)
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                CircleShape
            )
            .constrainAs(likeButton) {
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
                top.linkTo(actionsRow.top)
                bottom.linkTo(actionsRow.bottom)
            }
            .graphicsLayer(alpha = imageAlpha)) {
            val isFavorite = quoteDataModel.isFavorite
            val color =
                if (isFavorite) MaterialColor.Red500 else MaterialTheme.colorScheme.onBackground
            val icon = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder
            Icon(
                icon,
                contentDescription = "Curtir",
                tint = color,
            )
        }

        Row(modifier = Modifier
            .constrainAs(actionsRow) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            }
            .graphicsLayer(alpha = imageAlpha)) {


            AnimatedVisibility(
                visible = quoteDataModel.isUserQuote,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
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

fun Style?.getTextAlign(): TextAlign {
    if (this == null) return TextAlign.Center
    return when (textAlignment) {
        TextAlignment.JUSTIFY -> TextAlign.Justify
        TextAlignment.CENTER -> TextAlign.Center
        TextAlignment.START -> TextAlign.Start
        TextAlignment.END -> TextAlign.End
    }
}

fun Style?.buildStyleShadow() = if (this != null) Shadow(
    color = Color(
        android.graphics.Color.parseColor(shadowStyle.shadowColor)
    ),
    offset = Offset(shadowStyle.dx, shadowStyle.dy),
    blurRadius = shadowStyle.radius
) else {
    Shadow()
}

@Composable
fun String?.buildTextColor() = if (this == null) MaterialTheme.colorScheme.onBackground else Color(
    android.graphics.Color.parseColor(this)
)