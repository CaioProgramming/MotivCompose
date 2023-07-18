@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class
)

package com.ilustris.motivcompose.features.home.ui

import ai.atick.material.MaterialColor
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ilustris.motiv.foundation.R
import com.ilustris.motiv.foundation.ui.component.QuoteCard
import com.ilustris.motiv.foundation.ui.theme.MotivTheme
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.gradientFill
import com.ilustris.motiv.foundation.ui.theme.motivGradient
import com.ilustris.motiv.foundation.ui.theme.quoteCardModifier
import com.ilustris.motivcompose.features.home.presentation.HomeViewModel
import com.ilustris.motivcompose.features.radio.ui.RadioView
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.delayedFunction
import com.skydoves.landscapist.components.rememberImageComponent

@Composable
fun HomeView(navController: NavController) {

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val quotes = homeViewModel.quotes
    var query by remember {
        mutableStateOf("")
    }
    val state = homeViewModel.viewModelState.observeAsState().value
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        val pagerState = rememberPagerState() {
            quotes.size
        }

        LaunchedEffect(pagerState.currentPage) {
            if (pagerState.currentPage == quotes.size - 3 && quotes.size >= 10) {
                homeViewModel.loadMoreQuotes(pagerState.currentPage)
            }
        }

        Text(
            text = LocalContext.current.getString(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .gradientFill(motivGradient()),
        )

        TextField(
            value = query,
            leadingIcon = {
                Icon(Icons.Rounded.Search, contentDescription = "Pesquisar")
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = query.isNotEmpty(),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut()
                ) {
                    Icon(
                        Icons.Sharp.Close,
                        contentDescription = "fechar",
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                query = ""
                                homeViewModel.getAllData()
                            }
                    )
                }

            },
            onValueChange = {
                query = it
            },
            placeholder = {
                Text(
                    text = "Busque inspirações...",
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            },
            shape = RoundedCornerShape(defaultRadius),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
                autoCorrect = true
            ),
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
                keyboardController?.hide()
                homeViewModel.searchQuote(query)
            }),
            maxLines = 1,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )

        AnimatedVisibility(
            visible = quotes.size > 0,
            enter = fadeIn(),
            exit = scaleOut(),
            modifier = Modifier
        ) {
            VerticalPager(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
                    .animateContentSize(tween(1500, easing = LinearOutSlowInEasing)),
                state = pagerState,
                pageSpacing = 8.dp,
                userScrollEnabled = true,
                pageContent = { index ->
                    QuoteCard(
                        quotes[index],
                        modifier = Modifier
                            .wrapContentSize()
                            .quoteCardModifier(),
                        onClickUser = {
                            navController.navigate("profile/$it")
                        },
                        onDelete = {},
                        onLike = {},
                        onShare = {},
                        onEdit = {}
                    )

                }
            )
        }
    }

    LaunchedEffect(Unit) {
        homeViewModel.getAllData()
    }

}