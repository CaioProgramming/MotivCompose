@file:OptIn(ExperimentalFoundationApi::class)

package com.ilustris.motivcompose.features.profile.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ilustris.motiv.foundation.ui.component.CardBackground
import com.ilustris.motiv.foundation.ui.component.QuoteCard
import com.ilustris.motiv.foundation.ui.theme.brushsFromPalette
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.grayGradients
import com.ilustris.motiv.foundation.ui.theme.paletteFromBitMap
import com.ilustris.motiv.foundation.ui.theme.quoteCardModifier
import com.ilustris.motiv.foundation.ui.theme.radioIconModifier
import com.ilustris.motivcompose.features.profile.presentation.ProfileViewModel
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState
import com.skydoves.landscapist.glide.GlideRequestType
import kotlinx.coroutines.launch

@Composable
fun ProfileView(userID: String? = null, navController: NavController) {

    val viewModel = hiltViewModel<ProfileViewModel>()
    val user = viewModel.user.observeAsState().value
    val userQuotes = viewModel.userQuotes


    var profileBitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState() {
        userQuotes.size
    }

    val borderBrush = profileBitmap?.asAndroidBitmap()?.paletteFromBitMap()?.brushsFromPalette()
        ?: grayGradients()



    LaunchedEffect(userQuotes) {
        Log.i("Profile view", "ProfileView: showing ${userQuotes.size} quotes")
    }

    LaunchedEffect(Unit) {
        viewModel.fetchUser(userID?.replace("{userId}", ""))
    }

    LaunchedEffect(user) {
        user?.let {
            if (userQuotes.isEmpty()) {
                viewModel.getUserQuotes(it.uid)
            }
        }
    }

    fun moveToPage(position: Int) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(position)
        }
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(tween(1000))
    ) {

        item {
            AnimatedVisibility(visible = user != null,
                enter = scaleIn() + fadeIn(),
                exit = shrinkOut() + fadeOut(),
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .animateContentSize(tween(100)) { initialValue, targetValue ->
                        if (initialValue.height > targetValue.height) {
                            fadeIn() + scaleIn()
                        } else {
                            shrinkOut() + fadeOut()
                        }
                    }) {
                ConstraintLayout {
                    val (userName, profilePic, filterTab) = createRefs()
                    val cardHeight by animateDpAsState(targetValue = if (pagerState.currentPage != 0) 0.dp else 200.dp)
                    val picSize by animateDpAsState(targetValue = if (pagerState.currentPage != 0) 64.dp else 100.dp)
                    val cardAlphaAnimation by animateFloatAsState(
                        targetValue = if (pagerState.currentPage != 0) 0f else 1f,
                        tween(2500)
                    )
                    CardBackground(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(cardHeight)
                            .background(MaterialTheme.colorScheme.surface)
                            .graphicsLayer(alpha = cardAlphaAnimation),
                        backgroundImage = user?.cover
                    ) { }

                    GlideImage(
                        imageModel = { user?.picurl },
                        glideRequestType = GlideRequestType.BITMAP,
                        onImageStateChanged = {
                            if (it is GlideImageState.Success) {
                                profileBitmap = it.imageBitmap
                            }
                        },
                        modifier = Modifier
                            .constrainAs(profilePic) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .radioIconModifier(0f, picSize, borderBrush, 2.dp)
                            .clickable {
                                moveToPage(0)
                            }
                    )
                }
            }
        }

        stickyHeader {
            Text(
                text = user?.name ?: "",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Button(
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(
                        topStart = defaultRadius,
                        bottomStart = defaultRadius
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(text = "Publicações")
                }
                Button(
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(topEnd = defaultRadius, bottomEnd = defaultRadius),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(text = "Favoritos")
                }
            }
        }


        items(userQuotes.size) {
            QuoteCard(
                loadAsGif = false,
                animationEnabled = false,
                quoteDataModel = userQuotes[it],
                modifier = Modifier
                    .size(500.dp)
                    .padding(4.dp)
                    .quoteCardModifier(),
                onClickUser = {
                    navController.navigate("profile/{userId}".replace("{userId}", it))
                },
                onShare = {},
                onLike = {},
                onDelete = {}
            )
        }

    }
}
