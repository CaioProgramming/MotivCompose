@file:OptIn(ExperimentalFoundationApi::class)

package com.ilustris.manager.feature.styles.ui.form.ui

import ai.atick.material.MaterialColor
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.ilustris.manager.R
import com.ilustris.manager.feature.styles.ui.form.presentation.NewStyleViewModel
import com.ilustris.manager.feature.styles.utils.StyleUtils
import com.ilustris.motiv.foundation.data.model.AnimationOptions
import com.ilustris.motiv.foundation.data.model.AnimationProperties
import com.ilustris.motiv.foundation.data.model.AnimationTransition
import com.ilustris.motiv.foundation.data.model.BlendMode
import com.ilustris.motiv.foundation.data.model.FontStyle
import com.ilustris.motiv.foundation.data.model.ShadowStyle
import com.ilustris.motiv.foundation.data.model.StyleProperties
import com.ilustris.motiv.foundation.data.model.TextAlignment
import com.ilustris.motiv.foundation.data.model.TextProperties
import com.ilustris.motiv.foundation.data.model.Window
import com.ilustris.motiv.foundation.ui.component.AnimatedText
import com.ilustris.motiv.foundation.ui.theme.brushsFromPalette
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.gradientFill
import com.ilustris.motiv.foundation.ui.theme.grayGradients
import com.ilustris.motiv.foundation.ui.theme.managerGradient
import com.ilustris.motiv.foundation.ui.theme.paletteFromBitMap
import com.ilustris.motiv.foundation.ui.theme.textColorGradient
import com.ilustris.motiv.foundation.utils.ColorUtils
import com.ilustris.motiv.foundation.utils.FontUtils
import com.ilustris.motiv.foundation.utils.borderForWindow
import com.ilustris.motiv.foundation.utils.buildFont
import com.ilustris.motiv.foundation.utils.buildStyleShadow
import com.ilustris.motiv.foundation.utils.getFontStyle
import com.ilustris.motiv.foundation.utils.getFontWeight
import com.ilustris.motiv.foundation.utils.getTextAlign
import com.ilustris.motiv.foundation.utils.getTextColor
import com.ilustris.motiv.foundation.utils.CustomWindow
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState
import com.skydoves.landscapist.glide.GlideRequestType

@Composable
fun NewStyleScreen(navController: NavController) {
    val viewModel = hiltViewModel<NewStyleViewModel>()
    val state = viewModel.viewModelState.observeAsState().value
    val style = viewModel.newStyle.observeAsState().value

    var gifBitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    var currentOption by remember {
        mutableStateOf(FormOptions.values().first())
    }

    val brush =
        gifBitmap?.asAndroidBitmap()?.paletteFromBitMap()?.brushsFromPalette() ?: managerGradient()
    val context = LocalContext.current
    var exampleText by remember {
        mutableStateOf(StyleUtils.getExampleQuote())
    }
    var textyStyle = MaterialTheme.typography.headlineLarge.copy(
        color = style.getTextColor(),
        shadow = style.buildStyleShadow(),
        textAlign = style.getTextAlign(),
        fontFamily = style.buildFont(context),
        fontStyle = style.getFontStyle(),
        fontWeight = style.getFontWeight()
    )

    fun showGifDialog() {
        val activity = context as AppCompatActivity
        val fragmentManager = activity.supportFragmentManager
        val giphyKey = context.getString(R.string.giphy_api)
        Giphy.configure(context, context.getString(R.string.giphy_api), true)
        val settings = GPHSettings(
            theme = GPHTheme.Automatic,
            mediaTypeConfig = arrayOf(GPHContentType.gif),
            stickerColumnCount = 2,
            selectedContentType = GPHContentType.gif
        )
        GiphyDialogFragment.newInstance(settings, giphyKey)
            .apply {
                gifSelectionListener = object : GiphyDialogFragment.GifSelectionListener {
                    override fun didSearchTerm(term: String) {}

                    override fun onDismissed(selectedContentType: GPHContentType) {}

                    override fun onGifSelected(
                        media: Media,
                        searchTerm: String?,
                        selectedContentType: GPHContentType
                    ) {
                        media.images.downsizedMedium?.gifUrl?.let { url ->
                            viewModel.updateStyleBackground(url)
                            gifBitmap = null
                        }
                    }

                }
            }
            .show(fragmentManager, GiphyDialogFragment::class.java.simpleName)
    }

    @Composable
    fun getOptionsView() {
        when (currentOption) {
            FormOptions.COLORS -> {
                ColorsOptions(
                    textColor = style?.textColor,
                    backgroundColor = style?.styleProperties?.backgroundColor,
                    updateTextColor = { viewModel.updateStyleColor(it) },
                    updateBackColor = { viewModel.updateStyleBackColor(it) }
                )
            }

            FormOptions.SHADOW -> {
                ShadowOptions(shadowStyle = style?.shadowStyle) {
                    viewModel.updateShadowStyle(it)
                }
            }

            FormOptions.TEXT -> {
                TextOptions(textProperties = style?.textProperties) {
                    viewModel.updateTextProperties(it)
                }
            }

            FormOptions.STYLE -> {
                StyleOptions(
                    animationProperties = style?.animationProperties,
                    styleProperties = style?.styleProperties,
                    {
                        viewModel.updateAnimationProperties(it)
                    }) {
                    viewModel.updateStyleProperties(it)
                }
            }
        }
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (topBar, card, actions, sheetMenu) = createRefs()
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Rounded.KeyboardArrowLeft,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "Novo estilo",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { style?.let { viewModel.saveData(it) } },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialColor.Blue500)
            ) {
                Text(text = "Salvar", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Column(
            modifier = Modifier
                .constrainAs(card) {
                    top.linkTo(topBar.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(actions.top)
                    height = Dimension.fillToConstraints
                }
                .padding(16.dp)
                .fillMaxWidth()
                .borderForWindow(style?.styleProperties?.customWindow, brush = brush)
                .animateContentSize(tween(1000, easing = LinearEasing))
        ) {
            style?.styleProperties?.customWindow?.CustomWindow(
                modifier = Modifier.fillMaxWidth(),
                brush = brush
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                GlideImage(
                    imageModel = { style?.backgroundURL },
                    glideRequestType = GlideRequestType.GIF,
                    onImageStateChanged = {
                        if (it is GlideImageState.Success && gifBitmap == null) {
                            gifBitmap = it.imageBitmap
                        }
                    },
                    modifier = Modifier
                        .matchParentSize()
                        .clickable {
                            showGifDialog()
                        }
                        .clip(
                            RoundedCornerShape(
                                bottomEnd = defaultRadius,
                                bottomStart = defaultRadius
                            )
                        )
                        .border(
                            brush = brush,
                            width = 2.dp,
                            shape = RoundedCornerShape(
                                bottomEnd = defaultRadius,
                                bottomStart = defaultRadius
                            )
                        )
                )


                AnimatedText(
                    text = exampleText,
                    animationEnabled = currentOption == FormOptions.STYLE,
                    animation = style?.animationProperties?.animation ?: AnimationOptions.TYPE,
                    transitionMethod = style?.animationProperties?.transition
                        ?: AnimationTransition.LETTERS,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .animateContentSize(tween(1500, easing = EaseIn))
                        .graphicsLayer(alpha = 0.99f),
                    textStyle = textyStyle
                )
            }
        }


        LazyColumn(modifier = Modifier
            .constrainAs(actions) {
                bottom.linkTo(sheetMenu.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .heightIn(min = 0.dp, max = 300.dp)
            .animateContentSize(tween(1500, easing = EaseIn))
            .fillMaxWidth()) {
            item {
                getOptionsView()
            }
        }

        LazyRow(modifier = Modifier
            .constrainAs(sheetMenu) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .padding(16.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            items(FormOptions.values().size) {
                val option = FormOptions.values()[it]
                val isSelected = option == currentOption

                IconButton(
                    onClick = {
                        currentOption = option
                    }, modifier = Modifier
                        .padding(8.dp)
                        .background(
                            brush = if (isSelected) managerGradient() else grayGradients(),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = CircleShape
                        )
                ) {
                    val itemColor =
                        animateColorAsState(targetValue = if (isSelected) MaterialColor.White else MaterialTheme.colorScheme.onBackground)
                    Icon(
                        painter = painterResource(id = option.icon),
                        contentDescription = null,
                        tint = itemColor.value
                    )
                }
            }
        }
    }

    LaunchedEffect(style?.animationProperties) {
        exampleText = StyleUtils.getExampleQuote(exampleText)
    }

    LaunchedEffect(state) {
        if (state is ViewModelBaseState.DataSavedState) {
            navController.popBackStack()
        }
    }

}


@Composable
fun ColorsOptions(
    textColor: String?,
    backgroundColor: String?, updateTextColor: (String) -> Unit, updateBackColor: (String) -> Unit
) {
    val context = LocalContext.current
    val colors = ColorUtils.getMaterialColors(context)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Ajustes de cor", style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        Text(
            text = "Cor do texto",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        ColorList(
            colors = colors.filter { it != backgroundColor },
            currentColor = textColor,
            onPickColor = updateTextColor
        )
        Text(
            text = "Cor de fundo",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        ColorList(
            colors = colors.filter { it != textColor },
            currentColor = backgroundColor,
            onPickColor = updateBackColor
        )

    }

}

@Composable
fun ColorList(colors: List<String>, currentColor: String?, onPickColor: (String) -> Unit) {
    LazyRow(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .animateContentSize(tween(1000, delayMillis = 500, easing = EaseIn))
    ) {
        items(colors.size) {
            ColorIcon(
                color = colors[it],
                isSelected = currentColor == colors[it],
                onSelectColor = { selectedColor ->
                    onPickColor(selectedColor)
                })
        }
    }
}

@Composable
fun ColorIcon(color: String, isSelected: Boolean, onSelectColor: (String) -> Unit) {
    val borderColor =
        animateColorAsState(
            animationSpec = tween(1000, easing = EaseIn), targetValue =
            if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground
        )
    val intColor = Color(android.graphics.Color.parseColor(color))
    Box(
        modifier = Modifier
            .padding(4.dp)
            .border(width = 1.dp, shape = CircleShape, color = borderColor.value)
            .clickable {
                onSelectColor(color)
            }
            .padding(4.dp)
            .background(intColor, shape = CircleShape)
            .size(24.dp)
            .padding(4.dp)
    )
}

@Composable
fun ShadowOptions(shadowStyle: ShadowStyle?, updateShadowStyle: (ShadowStyle) -> Unit) {
    val context = LocalContext.current
    val colors = ColorUtils.getMaterialColors(context)
    var sliderX by remember { mutableFloatStateOf(shadowStyle?.dx ?: 0f) }
    var sliderY by remember { mutableFloatStateOf(shadowStyle?.dy ?: 0f) }
    var sliderBlur by remember { mutableFloatStateOf(shadowStyle?.radius ?: 0f) }
    val positionRange = -100f..100f
    val blurRange = 0f..50f

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Ajustes de sombra",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Text(
            text = "Cor da sombra",
            modifier = Modifier.padding(vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Light
        )

        ColorList(colors = colors, currentColor = shadowStyle?.shadowColor, onPickColor = {
            updateShadowStyle(shadowStyle?.copy(shadowColor = it) ?: ShadowStyle(shadowColor = it))
        })

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Horizontal",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Light
            )

            Slider(
                value = sliderX,
                onValueChange = {
                    sliderX = it
                },
                onValueChangeFinished = {
                    updateShadowStyle(shadowStyle?.copy(dx = sliderX) ?: ShadowStyle(dx = sliderX))
                },
                valueRange = positionRange,
                steps = 100,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .gradientFill(managerGradient())
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Vertical",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Light
            )

            Slider(
                value = sliderY,
                onValueChange = {
                    sliderY = it
                },
                onValueChangeFinished = {
                    updateShadowStyle(shadowStyle?.copy(dy = sliderY) ?: ShadowStyle(dy = sliderY))
                },
                valueRange = positionRange,
                steps = 100,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .gradientFill(managerGradient())
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Desfoque",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Light
            )

            Slider(
                value = sliderBlur,
                onValueChange = {
                    sliderBlur = it
                },
                onValueChangeFinished = {
                    updateShadowStyle(
                        shadowStyle?.copy(radius = sliderBlur) ?: ShadowStyle(radius = sliderBlur)
                    )
                },
                valueRange = blurRange,
                steps = 100,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .gradientFill(managerGradient())
            )
        }
    }
}

@Composable
fun TextOptions(textProperties: TextProperties?, updateTextProperties: (TextProperties) -> Unit) {
    val context = LocalContext.current
    val fonts = FontUtils.getFamilies(context)
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Configurações de texto", style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Text(
            text = "Ajustes de fonte", style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyRow(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            val styles = FontStyle.values()
            items(styles.size) {
                val isSelected = textProperties?.fontStyle == styles[it]
                IconButton(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape),
                    onClick = {
                        updateTextProperties(
                            textProperties?.copy(fontStyle = styles[it]) ?: TextProperties(
                                fontStyle = styles[it]
                            )
                        )
                    }) {

                    Icon(
                        tint = MaterialTheme.colorScheme.onBackground,
                        painter = painterResource(id = styles[it].getIconForFontStyle()),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .gradientFill(
                                if (isSelected) managerGradient() else textColorGradient()
                            )
                    )

                }
            }
        }

        Text(
            text = "Ajustes de alinhamento", style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyRow(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            val alignments = TextAlignment.values()
            items(alignments.size) {
                val isSelected = textProperties?.textAlignment == alignments[it]
                IconButton(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape),
                    onClick = {
                        updateTextProperties(
                            textProperties?.copy(textAlignment = alignments[it]) ?: TextProperties(
                                textAlignment = alignments[it]
                            )
                        )
                    }) {

                    Icon(
                        tint = MaterialTheme.colorScheme.onBackground,
                        painter = painterResource(id = alignments[it].getIconForAlignment()),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .gradientFill(
                                if (isSelected) managerGradient() else textColorGradient()
                            )
                    )

                }
            }
        }

        Text(
            "Fontes",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {
            items(fonts.size) {
                val font = fonts[it]
                FontCard(fontName = font, textProperties?.fontFamily == font) {
                    updateTextProperties(
                        textProperties?.copy(fontFamily = font) ?: TextProperties(fontFamily = font)
                    )
                }
            }
        }
    }

}

@Composable
fun StyleOptions(
    animationProperties: AnimationProperties?,
    styleProperties: StyleProperties?,
    updateAnimationProperties: (AnimationProperties) -> Unit,
    updateStyleProperties: (StyleProperties) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Configurações de estilo", style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Text(
            text = "Animação do texto", modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )

        LazyRow() {
            items(AnimationOptions.values().size) {
                val animation = AnimationOptions.values()[it]
                val isSelected = animationProperties?.animation == animation
                IconButton(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape),
                    onClick = {
                        updateAnimationProperties(
                            animationProperties?.copy(animation = animation) ?: AnimationProperties(
                                animation = animation
                            )
                        )
                    }) {

                    Icon(
                        tint = MaterialTheme.colorScheme.onBackground,
                        painter = painterResource(id = animation.getIconForAnimation()),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .gradientFill(
                                if (isSelected) managerGradient() else textColorGradient()
                            )
                    )
                }
            }
        }

        Text(
            text = "Tipo de animação", modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )

        LazyRow() {
            items(AnimationTransition.values().size) {
                val transition = AnimationTransition.values()[it]
                val isSelected = animationProperties?.transition == transition
                Text(
                    text = transition.title,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .background(
                            MaterialTheme.colorScheme.surface, RoundedCornerShape(
                                defaultRadius
                            )
                        )
                        .padding(8.dp)
                        .gradientFill(if (isSelected) managerGradient() else textColorGradient())
                        .clickable {
                            updateAnimationProperties(
                                animationProperties?.copy(transition = transition)
                                    ?: AnimationProperties(
                                        transition = transition
                                    )
                            )
                        }
                )
            }
        }


        Text(
            text = "Ajuste de bordas", modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )

        LazyRow() {
            items(Window.values().size) {
                val window = Window.values()[it]
                val isSelected = styleProperties?.customWindow == window
                IconButton(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape),
                    onClick = {
                        updateStyleProperties(
                            styleProperties?.copy(customWindow = window) ?: StyleProperties(
                                customWindow = window
                            )
                        )
                    }) {

                    Icon(
                        tint = MaterialTheme.colorScheme.onBackground,
                        painter = painterResource(id = window.getIconForWindow()),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .gradientFill(
                                if (isSelected) managerGradient() else textColorGradient()
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun FontCard(fontName: String, isSelected: Boolean, onSelectFont: (String) -> Unit) {
    val font = FontUtils.getFontFamily(fontName)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(color = MaterialTheme.colorScheme.surface)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(0.dp)
            )
            .gradientFill(if (isSelected) managerGradient() else textColorGradient())
            .padding(8.dp)
            .clickable {
                onSelectFont(fontName)
            }) {
        Text(
            text = "Mm",
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = font,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = fontName,
            modifier = Modifier.padding(vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Light
        )
    }

}

enum class FormOptions(val icon: Int = com.ilustris.motiv.foundation.R.drawable.ic_saturn_and_other_planets_primary) {
    COLORS(R.drawable.ic_color_pallete_24),
    SHADOW(R.drawable.ic_shadow_24),
    TEXT(R.drawable.ic_text_selection_24),
    STYLE(R.drawable.ic_style_24)
}

fun FontStyle.getIconForFontStyle(): Int {
    return when (this) {
        FontStyle.REGULAR -> R.drawable.ic_text_24
        FontStyle.ITALIC -> R.drawable.ic_italic_24
        FontStyle.BOLD -> R.drawable.ic_bold_24
        FontStyle.BLACK -> R.drawable.ic_extra_bold_24
    }
}

fun TextAlignment.getIconForAlignment(): Int {
    return when (this) {
        TextAlignment.CENTER -> R.drawable.ic_round_format_align_center_24
        TextAlignment.START -> R.drawable.ic_round_format_align_left_24
        TextAlignment.END -> R.drawable.ic_round_format_align_right_24
        TextAlignment.JUSTIFY -> R.drawable.ic_round_format_align_justify_24
    }
}

fun AnimationOptions.getIconForAnimation(): Int {
    return when (this) {
        AnimationOptions.TYPE -> R.drawable.ic_text_input
        AnimationOptions.FADE -> R.drawable.ic_shadows
        AnimationOptions.SCALE -> R.drawable.ic_scale_24
    }
}

fun Window.getIconForWindow(): Int {
    return when (this) {
        Window.CLASSIC -> R.drawable.ic_retro
        Window.MODERN -> R.drawable.ic_modern
    }
}

fun BlendMode.getIconForBlend(): Int {
    return when (this) {
        BlendMode.NORMAL -> R.drawable.ic_normal_24
        BlendMode.DARKEN -> R.drawable.ic_darken_24
        BlendMode.LIGHTEN -> R.drawable.ic_lighten_24
        BlendMode.OVERLAY -> R.drawable.ic_overlay_24
        BlendMode.SCREEN -> R.drawable.ic_screen_24
    }
}

