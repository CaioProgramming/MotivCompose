@file:OptIn(ExperimentalFoundationApi::class)

package com.ilustris.motivcompose.features.home.ui

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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.ilustris.motiv.foundation.ui.component.QuoteCard
import com.ilustris.motiv.foundation.ui.theme.MotivTheme
import com.ilustris.motiv.foundation.ui.theme.quoteCardModifier
import com.ilustris.motivcompose.features.home.presentation.HomeViewModel
import com.ilustris.motivcompose.features.radio.ui.RadioView
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.delayedFunction

@Composable
fun HomeView() {

    val mediaPlayer = MediaPlayer()
    val context = LocalContext.current
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val quotes = homeViewModel.quotes
    val state = homeViewModel.viewModelState.observeAsState().value
    val playingRadio = homeViewModel.playingRadio.observeAsState().value

    val radioExpanded = remember {
        mutableStateOf(false)
    }


    fun playRadio() {

        delayedFunction(500) {
            playingRadio?.run {
                mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC)
                try {
                    mediaPlayer.setDataSource(playingRadio.url)
                    mediaPlayer.prepareAsync()
                    mediaPlayer.setOnPreparedListener {
                        mediaPlayer.setVolume(0.3f, 0.3f)
                        mediaPlayer.start()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(javaClass.simpleName, "playRadio: Error playing radio ${e.message}")
                    homeViewModel.updatePlayingRadio(null)
                }
            }
        }
    }


    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (playerView, content) = createRefs()

        val pagerState = rememberPagerState() {
            quotes.size
        }

        LaunchedEffect(pagerState.currentPage) {
            if (pagerState.currentPage == quotes.size - 3 && quotes.size >= 10) {
                homeViewModel.loadMoreQuotes(pagerState.currentPage)
            }
        }

        AnimatedVisibility(visible = quotes.size > 0,
            enter = fadeIn(),
            exit = scaleOut(),
            modifier = Modifier
                .constrainAs(content) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom, 16.dp)
                    width = Dimension.matchParent
                    height = Dimension.fillToConstraints
                }) {


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
                    .blur(blurViewAnimation, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .animateContentSize(tween(1500, easing = LinearOutSlowInEasing)),
                state = pagerState,
                userScrollEnabled = true,
                pageContent = { index ->
                    quotes.let {
                        QuoteCard(
                            it[index],
                            modifier = Modifier
                                .padding(16.dp)
                                .quoteCardModifier()
                        )
                    }

                }
            )
        }



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