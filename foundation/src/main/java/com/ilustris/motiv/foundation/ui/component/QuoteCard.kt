package com.ilustris.motiv.foundation.ui.component

import ai.atick.material.MaterialColor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInExpo
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.palette.graphics.Palette
import com.ilustris.motiv.foundation.R
import com.ilustris.motiv.foundation.model.QuoteDataModel
import com.ilustris.motiv.foundation.model.Style
import com.ilustris.motiv.foundation.model.TextAlignment
import com.ilustris.motiv.foundation.ui.presentation.QuoteActions
import com.ilustris.motiv.foundation.ui.theme.brushsFromPalette
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.motivGradient
import com.ilustris.motiv.foundation.ui.theme.radioRadius
import com.ilustris.motiv.foundation.utils.FontUtils
import com.silent.ilustriscore.core.utilities.DateFormats
import com.silent.ilustriscore.core.utilities.format
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState
import com.skydoves.landscapist.glide.GlideRequestType
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController

@Composable
fun QuoteCard(
    quoteDataModel: QuoteDataModel,
    modifier: Modifier,
    loadAsGif: Boolean = true,
    animationEnabled: Boolean = true,
    quoteActions: QuoteActions? = null
) {
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

    val style = quoteDataModel.style
    val shadowStyle = style.buildStyleShadow()
    val textAlign = style.getTextAlign()
    val textColor = style?.textColor.buildTextColor()


    LaunchedEffect(backgroundBitmap) {
        backgroundBitmap?.let {
            if (palette == null) palette = Palette.Builder(it.asAndroidBitmap()).generate()
        }
    }

    LaunchedEffect(Unit) {
        if (!animationEnabled) animationCompleted = true

    }

    val brush = if (animationCompleted) palette?.brushsFromPalette()
        ?: motivGradient() else Brush.linearGradient(
        listOf(MaterialTheme.colorScheme.background, Color.Transparent)
    )

    val captureController = rememberCaptureController()

    val borderAnimation by animateDpAsState(
        targetValue = if (animationCompleted) 2.dp else 0.dp,
        tween(1500, easing = EaseInExpo)
    )

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

    var dropDownState by remember {
        mutableStateOf(false)
    }
    val dropDownOptions =
        if (quoteDataModel.isUserQuote) listOf("Excluir", "Editar") else listOf("Denunciar")

    Column(
        modifier = modifier
    ) {


        AnimatedVisibility(
            visible = quoteDataModel.user != null,
            modifier = Modifier
                .padding(8.dp)
                .graphicsLayer(alpha = imageAlpha)
                .animateContentSize(tween(1000))
        ) {


            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .animateContentSize(tween(250, easing = LinearEasing)),
            ) {

                val (userInfo, options) = createRefs()


                Row(modifier = Modifier.constrainAs(userInfo) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(options.start)
                    width = Dimension.fillToConstraints
                }, horizontalArrangement = Arrangement.Start) {
                    GlideImage(
                        imageModel = {
                            quoteDataModel.user?.picurl ?: ""
                        },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                        ),
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .clickable {
                                quoteDataModel.user?.id?.let { quoteActions?.onClickUser(it) }
                            }
                            .border(
                                1.dp,
                                color = MaterialTheme.colorScheme.onBackground,
                                CircleShape
                            )

                    )
                    Column(modifier = Modifier

                        .padding(8.dp)
                    ) {
                        Text(
                            text = (quoteDataModel.user?.name) ?: "".trimEnd(),
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                quoteDataModel.user?.id?.let { quoteActions?.onClickUser(it) }
                            }
                        )
                        Text(
                            text = quote.data?.toDate()?.format(DateFormats.DD_OF_MM_FROM_YYYY)
                                ?: "-",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Light
                        )
                    }
                }



                IconButton(modifier = Modifier.constrainAs(options) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }, onClick = {
                    dropDownState = !dropDownState
                }) {
                    Icon(
                        Icons.Rounded.MoreVert,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Opções"
                    )

                    DropdownMenu(
                        expanded = dropDownState,
                        onDismissRequest = {
                            dropDownState = false
                        }, modifier = Modifier.background(
                            MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(
                                defaultRadius
                            )
                        )
                    ) {
                        dropDownOptions.forEach { option ->
                            DropdownMenuItem(text = {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(8.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }, onClick = {
                                when (option) {
                                    "Excluir" -> quoteActions?.onDelete(quoteDataModel)
                                    "Editar" -> {
                                        quoteActions?.onEdit(quoteDataModel)
                                    }

                                    "Denunciar" -> {
                                        quoteActions?.onReport(quoteDataModel)
                                    }
                                }
                                dropDownState = false

                            })
                        }
                    }
                }
            }
        }

        Capturable(controller = captureController, onCaptured = { bitmap, error ->
            bitmap?.let {
                quoteActions?.onShare(quoteDataModel, it.asAndroidBitmap())
            }
        }) {
            Box {

                GlideImage(
                    imageModel = { style?.backgroundURL },
                    glideRequestType = if (!loadAsGif) GlideRequestType.BITMAP else GlideRequestType.GIF,
                    modifier = Modifier
                        .border(
                            width = borderAnimation,
                            brush = brush,
                            shape = RoundedCornerShape(defaultRadius)
                        )
                        .clip(RoundedCornerShape(defaultRadius))
                        .matchParentSize()
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
                        .fillMaxWidth()
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedText(
                        text = quote.quote,
                        animationEnabled = animationEnabled,
                        shadow = shadowStyle,
                        fontFamily = defaultFont,
                        textStyle = MaterialTheme.typography.headlineLarge.copy(
                            shadow = shadowStyle,
                            color = textColor,
                            textAlign = textAlign,
                            fontFamily = defaultFont
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        animationCompleted = true
                    }

                    Text(
                        text = quote.author, modifier = Modifier
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
                }

            }
        }


        Row(
            modifier = Modifier
                .padding(8.dp)
                .graphicsLayer(alpha = imageAlpha)
        ) {


            IconButton(
                onClick = { quoteActions?.onLike(quoteDataModel) },
                modifier = Modifier.padding(4.dp)
            ) {
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

            IconButton(
                onClick = { captureController.capture() }, modifier = Modifier.padding(4.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.share),
                    contentDescription = "Compartilhar",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
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