@file:OptIn(ExperimentalFoundationApi::class)

package com.ilustris.manager.feature.styles.ui.form.ui

import ai.atick.material.MaterialColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ilustris.manager.R
import com.ilustris.manager.feature.styles.ui.form.presentation.NewStyleViewModel
import com.ilustris.motiv.foundation.model.DEFAULT_FONT_FAMILY
import com.ilustris.motiv.foundation.model.FontStyle
import com.ilustris.motiv.foundation.model.NEW_STYLE_BACKGROUND
import com.ilustris.motiv.foundation.model.ShadowStyle
import com.ilustris.motiv.foundation.model.TextAlignment
import com.ilustris.motiv.foundation.model.TextProperties
import com.ilustris.motiv.foundation.ui.theme.colorsFromPalette
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.gradientAnimation
import com.ilustris.motiv.foundation.ui.theme.gradientFill
import com.ilustris.motiv.foundation.ui.theme.grayGradients
import com.ilustris.motiv.foundation.ui.theme.managerBrushes
import com.ilustris.motiv.foundation.ui.theme.managerGradient
import com.ilustris.motiv.foundation.ui.theme.paletteFromBitMap
import com.ilustris.motiv.foundation.ui.theme.textColorGradient
import com.ilustris.motiv.foundation.utils.ColorUtils
import com.ilustris.motiv.foundation.utils.FontUtils
import com.ilustris.motiv.foundation.utils.buildStyleShadow
import com.ilustris.motiv.foundation.utils.buildTextColor
import com.ilustris.motiv.foundation.utils.getFontStyle
import com.ilustris.motiv.foundation.utils.getFontWeight
import com.ilustris.motiv.foundation.utils.getTextAlign
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState
import com.skydoves.landscapist.glide.GlideRequestType

@Composable
fun NewStyleScreen(navController: NavController) {
    val viewModel = hiltViewModel<NewStyleViewModel>()
    val style = viewModel.newStyle.observeAsState().value

    val font = FontUtils.getFontFamily(style?.textProperties?.fontFamily ?: DEFAULT_FONT_FAMILY)
    var gifBitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    var currentOption by remember {
        mutableStateOf(FormOptions.values().first())
    }


    val brush =
        gifBitmap?.asAndroidBitmap()?.paletteFromBitMap()?.colorsFromPalette() ?: managerBrushes()

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
                Text("Configurações do estilo")
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

        Box(
            modifier = Modifier
                .constrainAs(card) {
                    top.linkTo(topBar.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(actions.top)
                    height = Dimension.fillToConstraints
                }
                .padding(16.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            GlideImage(
                imageModel = { NEW_STYLE_BACKGROUND },
                glideRequestType = GlideRequestType.GIF,
                onImageStateChanged = {
                    if (it is GlideImageState.Success && gifBitmap == null) {
                        gifBitmap = it.imageBitmap
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(defaultRadius))
                    .border(
                        brush = gradientAnimation(brush),
                        width = 2.dp,
                        shape = RoundedCornerShape(defaultRadius)
                    )
            )

            Text(
                text = "Motiv",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = style?.textColor.buildTextColor(),
                    shadow = style.buildStyleShadow(),
                    textAlign = style?.textProperties?.textAlignment?.getTextAlign()
                        ?: TextAlign.Center,
                    fontFamily = font,
                    fontStyle = style?.textProperties?.fontStyle?.getFontStyle(),
                    fontWeight = style?.textProperties?.fontStyle?.getFontWeight()
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .animateContentSize(tween(1500, easing = EaseIn))
            )
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
                val selectedColor = animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.6f
                    ),
                    tween(1000, easing = EaseIn)
                )
                IconButton(
                    onClick = {
                        currentOption = option
                    }, modifier = Modifier
                        .padding(8.dp)
                        .background(
                            brush = if (isSelected) gradientAnimation(managerBrushes()) else grayGradients(),
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
            text = "Cor do texto",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        ColorList(
            colors = colors.filter { it != backgroundColor },
            currentColor = textColor,
            onPickColor = updateTextColor
        )
        Text(
            text = "Cor de fundo",
            style = MaterialTheme.typography.bodyLarge,
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
            text = "Ajustes de sombreamento",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
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
                    updateShadowStyle(shadowStyle?.copy(dx = sliderY) ?: ShadowStyle(dx = sliderY))
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
        FontStyle.NORMAL -> R.drawable.ic_text_24
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