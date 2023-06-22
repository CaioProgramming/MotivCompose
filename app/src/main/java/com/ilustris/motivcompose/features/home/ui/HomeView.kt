@file:OptIn(ExperimentalFoundationApi::class)

package com.ilustris.motivcompose.features.home.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.ilustris.motiv.foundation.model.Quote
import com.ilustris.motiv.foundation.ui.component.QuoteCard
import com.ilustris.motiv.foundation.ui.theme.MotivTheme
import com.ilustris.motiv.foundation.ui.theme.quoteCardModifier
import com.ilustris.motivcompose.features.home.presentation.HomeViewModel
import com.ilustris.motivcompose.features.radio.ui.RadioView
import com.silent.ilustriscore.core.model.ViewModelBaseState

@Composable
fun HomeView() {

    val mediaPlayer = MediaPlayer()
    val context = LocalContext.current
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val quotesState = homeViewModel.viewModelState.observeAsState().value
    val playingRadio = homeViewModel.playingRadio.observeAsState().value
    val radioExpanded = remember {
        mutableStateOf(false)
    }

    fun playRadio() {
        playingRadio?.run {
            mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC)
            try {
                mediaPlayer.setDataSource(playingRadio.url)
                mediaPlayer.setVolume(0.2f, 0.2f)
                mediaPlayer.prepare()
                mediaPlayer.start()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(javaClass.simpleName, "playRadio: Error playing radio ${e.message}")
                homeViewModel.updatePlayingRadio(null)
            }
        }
    }


    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (playerView, content) = createRefs()


        val quotes = if (quotesState is ViewModelBaseState.DataListRetrievedState) {
            quotesState.dataList as List<Quote>
        } else {
            null
        }
        quotes?.run {
            val pagerState = rememberPagerState() { size }
            val blurViewAnimation by animateDpAsState(
                targetValue = if (!radioExpanded.value) 0.dp else 5.dp,
                tween(1500)
            )
            val isUserScrolling = snapshotFlow { pagerState.isScrollInProgress }.collectAsState(
                initial = false
            )
            if (isUserScrolling.value) {
                radioExpanded.value = false
            }
            VerticalPager(
                modifier = Modifier
                    .constrainAs(content) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom, 16.dp)
                        width = Dimension.matchParent
                        height = Dimension.fillToConstraints
                    }
                    .blur(blurViewAnimation, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .animateContentSize(tween(1500, easing = LinearOutSlowInEasing)),
                state = pagerState,
                userScrollEnabled = true,
                pageContent = { index ->
                    QuoteCard(get(index), onClick = { quote ->
                        Log.i(javaClass.simpleName, "HomeView: quote selected $quote")
                    }, modifier = Modifier.quoteCardModifier())
                }
            )

            AnimatedVisibility(
                visible = true,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .constrainAs(playerView) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.matchParent
                    }
            ) {
                RadioView(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = radioExpanded.value,
                    playingRadio = playingRadio,
                    onSelectRadio = {
                        homeViewModel.updatePlayingRadio(it)
                    },
                ) { radioExpanded.value = !radioExpanded.value }
            }


        }
    }

    LaunchedEffect(Unit) {
        homeViewModel.getAllData()
    }

    LaunchedEffect(playingRadio) {
        if (playingRadio != null) {
            playRadio()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Home View", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeViewPreview() {
    MotivTheme(true) {
        HomeView()
    }
}