@file:OptIn(ExperimentalFoundationApi::class)

package com.ilustris.motivcompose.features.profile.ui

import ai.atick.material.MaterialColor
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ilustris.motiv.foundation.ui.component.CardBackground
import com.ilustris.motiv.foundation.ui.component.QuoteCard
import com.ilustris.motiv.foundation.ui.theme.brushsFromPalette
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.gradientFill
import com.ilustris.motiv.foundation.ui.theme.gradientOverlay
import com.ilustris.motiv.foundation.ui.theme.grayGradients
import com.ilustris.motiv.foundation.ui.theme.paletteFromBitMap
import com.ilustris.motiv.foundation.ui.theme.quoteCardModifier
import com.ilustris.motiv.foundation.ui.theme.radioIconModifier
import com.ilustris.motiv.foundation.ui.theme.transparentFadeGradient
import com.ilustris.motivcompose.features.profile.presentation.ProfileViewModel
import com.skydoves.landscapist.ImageOptions
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
    var coverBitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    val postsCount = viewModel.postsCount.value ?: 0
    val favoriteCount = viewModel.favoriteCount.value ?: 0

    val coverAlpha = animateFloatAsState(targetValue = if (profileBitmap != null) 1f else 0f)
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
                viewModel.getUserFavorites(it.uid)
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

        if (user != null) {
            item {
                Box() {
                    val avatarSize = 150.dp
                    CardBackground(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(avatarSize),
                        backgroundImage = user.cover
                    ) {
                        coverBitmap = it
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 16.dp, vertical = 50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        GlideImage(
                            imageModel = { user.picurl },
                            glideRequestType = GlideRequestType.BITMAP,
                            onImageStateChanged = {
                                if (it is GlideImageState.Success) {
                                    profileBitmap = it.imageBitmap
                                }
                            },
                            modifier = Modifier
                                .radioIconModifier(0f, avatarSize, borderBrush, 4.dp)
                                .clickable {
                                    moveToPage(0)
                                }
                        )

                        Text(
                            text = user.name,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        )

                        Row(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(
                                    text = "Publicações",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .graphicsLayer(alpha = 0.6f)
                                )

                                Text(
                                    text = postsCount.toString(),
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Black
                                )

                            }

                            Divider(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp)
                                    .clip(RoundedCornerShape(defaultRadius))
                                    .padding(horizontal = 4.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                                thickness = 1.dp
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(
                                    text = "Favoritos",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                Text(
                                    text = favoriteCount.toString(),
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Black
                                )

                            }
                        }
                    }
                }
            }

        }

        items(userQuotes.size) {
            QuoteCard(
                loadAsGif = true,
                animationEnabled = false,
                quoteDataModel = userQuotes[it],
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
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
