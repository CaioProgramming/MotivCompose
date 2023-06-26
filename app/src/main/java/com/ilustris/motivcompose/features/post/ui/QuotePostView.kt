@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)

package com.ilustris.motivcompose.features.post.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import com.ilustris.motiv.foundation.ui.component.CardBackground
import com.ilustris.motiv.foundation.ui.component.StyleIcon
import com.ilustris.motiv.foundation.ui.component.buildStyleShadow
import com.ilustris.motiv.foundation.ui.component.buildTextColor
import com.ilustris.motiv.foundation.ui.component.getTextAlign
import com.ilustris.motiv.foundation.ui.theme.brushsFromPalette
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.gradientFill
import com.ilustris.motiv.foundation.ui.theme.motivGradient
import com.ilustris.motiv.foundation.ui.theme.quoteCardModifier
import com.ilustris.motiv.foundation.utils.FontUtils
import com.ilustris.motivcompose.features.post.presentation.NewQuoteViewModel
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.delayedFunction
import kotlinx.coroutines.launch

@Composable
fun QuotePostView(navController: NavController) {


    val newQuoteViewModel = hiltViewModel<NewQuoteViewModel>()
    val state = newQuoteViewModel.viewModelState.observeAsState().value

    fun isSaving() = state == ViewModelBaseState.LoadingState

    val context = LocalContext.current
    val quote = newQuoteViewModel.newQuote.observeAsState().value
    val styles = newQuoteViewModel.styles
    val currentStyle = newQuoteViewModel.currentStyle.observeAsState().value
    val isFocusing = remember { mutableStateOf(false) }
    val backgroundBlur =
        animateDpAsState(targetValue = if (isFocusing.value || isSaving()) defaultRadius else 0.dp)
    val colorFilter =
        animateColorAsState(targetValue = if (isFocusing.value) Color.Black.copy(alpha = 0.2f) else Color.Transparent)

    var backgroundBitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    var palette by remember {
        mutableStateOf<Palette?>(null)
    }
    val focusRequester = remember { FocusRequester() }

    val rowState = rememberPagerState(pageCount = { styles.size })
    val scope = rememberCoroutineScope()
    fun getFont(fontPosition: Int) =
        FontUtils.getFontFamily(FontUtils.getFamily(context, fontPosition))

    LaunchedEffect(rowState.currentPage) {
        if (styles.isNotEmpty()) {
            newQuoteViewModel.updateStyle(styles[rowState.currentPage].id)
        }
    }


    LaunchedEffect(Unit) {
        if (styles.isEmpty()) {
            newQuoteViewModel.getStyles()
        }
    }

    LaunchedEffect(backgroundBitmap) {
        backgroundBitmap?.let {
            palette = Palette.Builder(it.asAndroidBitmap()).generate()
        }
    }

    fun scrollToStyle(index: Int) {
        scope.launch {
            rowState.animateScrollToPage(index)
        }
    }

    LaunchedEffect(currentStyle) {
        currentStyle?.let {
            scrollToStyle(styles.indexOf(it))
        }
    }

    LaunchedEffect(state) {
        if (state is ViewModelBaseState.DataSavedState) {
            delayedFunction(2000) {
                navController.popBackStack()
            }
        }
    }


    @Composable
    fun brush() = palette?.brushsFromPalette() ?: motivGradient()


    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (quoteCard, stylesRow, message) = createRefs()


        AnimatedVisibility(
            visible = true,
            modifier = Modifier.constrainAs(quoteCard) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom, 32.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            enter = scaleIn() + fadeIn(),
            exit = shrinkOut() + fadeOut()
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .quoteCardModifier()
                    .border(
                        width = 3.dp,
                        brush = brush(),
                        shape = RoundedCornerShape(
                            defaultRadius
                        )
                    )
            ) {
                val (text) = createRefs()

                CardBackground(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(backgroundBlur.value),
                    colorFilter = colorFilter.value,
                    backgroundImage = currentStyle?.backgroundURL,
                ) {
                    backgroundBitmap = it
                }




                AnimatedContent(targetState = currentStyle, transitionSpec = {
                    fadeIn() with fadeOut()
                }, modifier = Modifier
                    .constrainAs(text) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp)
                    .blur(if (isSaving()) 10.dp else 0.dp)) {
                    val textStyle = MaterialTheme.typography.headlineMedium
                        .copy(
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                            textAlign = it?.getTextAlign(),
                            color = it?.textColor.buildTextColor(),
                            shadow = it?.buildStyleShadow(),
                            fontFamily = getFont(it?.font ?: 0)
                        )

                    Column {
                        TextField(
                            value = quote?.quote ?: "",
                            enabled = !isSaving(),
                            onValueChange = { newQuoteViewModel.updateQuoteText(it) },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            textStyle = textStyle,
                            placeholder = {
                                Text(
                                    "Digite sua frase aqui",
                                    style = textStyle,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(0.4f)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(align = Alignment.CenterVertically)
                                .animateContentSize(tween(1000, easing = FastOutSlowInEasing))
                                .focusRequester(focusRequester)
                                .onFocusChanged {
                                    isFocusing.value = it.isFocused
                                }
                        )

                        TextField(
                            value = quote?.author ?: "",
                            enabled = !isSaving(),
                            maxLines = 1,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            textStyle = textStyle.copy(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                            placeholder = {
                                Text(
                                    "Autor",
                                    style = textStyle.copy(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(0.5f)
                                )
                            },
                            onValueChange = {
                                newQuoteViewModel.updateQuoteAuthor(it)
                            },
                            modifier = Modifier
                                .animateContentSize(
                                    tween(
                                        1000,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier
                .constrainAs(stylesRow) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .animateContentSize(tween(1500))
                .size(80.dp)
                .clip(CircleShape),
            visible = styles.isNotEmpty(),
            enter = scaleIn(), exit = shrinkOut()
        ) {
            HorizontalPager(
                state = rowState,
                userScrollEnabled = !isSaving(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .animateContentSize(tween(1500)),
                pageContent = {
                    val style = styles[it]
                    StyleIcon(
                        style = style,
                        isSaving = state == ViewModelBaseState.LoadingState,
                    ) { selectedStyle ->
                        quote?.let { quote ->
                            newQuoteViewModel.saveData(quote)
                        }
                    }
                })
        }


        AnimatedVisibility(visible = state is ViewModelBaseState.DataSavedState || isSaving(),
            modifier = Modifier
                .constrainAs(message) {
                    bottom.linkTo(stylesRow.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth()
                .padding(16.dp), enter = fadeIn(), exit = fadeOut()) {
            Text(
                "Salvando post...",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge.copy(currentStyle?.textColor.buildTextColor())
            )
        }


    }


}