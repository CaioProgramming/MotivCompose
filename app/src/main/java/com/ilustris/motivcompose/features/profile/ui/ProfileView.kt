@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

package com.ilustris.motivcompose.features.profile.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ilustris.motiv.foundation.ui.component.CardBackground
import com.ilustris.motiv.foundation.ui.component.CoverSheet
import com.ilustris.motiv.foundation.ui.component.IconSheet
import com.ilustris.motiv.foundation.ui.component.QuoteCard
import com.ilustris.motiv.foundation.ui.component.gradientAnimation
import com.ilustris.motiv.foundation.ui.theme.colorsFromPalette
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.gradientFill
import com.ilustris.motiv.foundation.ui.theme.motivBrushes
import com.ilustris.motiv.foundation.ui.theme.paletteFromBitMap
import com.ilustris.motiv.foundation.ui.theme.quoteCardModifier
import com.ilustris.motiv.foundation.ui.theme.radioIconModifier
import com.ilustris.motivcompose.features.profile.presentation.ProfileViewModel
import com.ilustris.motivcompose.features.profile.ui.component.CounterLabel
import com.ilustris.motivcompose.features.profile.ui.component.ProfileTab
import com.ilustris.motivcompose.ui.navigation.AppNavigation
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState
import com.skydoves.landscapist.glide.GlideRequestType
import kotlinx.coroutines.launch

@Composable
fun ProfileView(userID: String? = null, navController: NavController) {

    val viewModel = hiltViewModel<ProfileViewModel>()
    val user = viewModel.user.observeAsState().value
    var currentFilter by remember {
        mutableStateOf(ProfileFilter.POSTS)
    }

    val isOwnUser = viewModel.isOwnUser.observeAsState()
    val userQuotes =
        if (currentFilter == ProfileFilter.POSTS) viewModel.userQuotes else viewModel.userFavorites
    val listState = rememberLazyListState()
    var profileBitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    var coverBitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    val currentSheet = remember {
        mutableStateOf<ProfileSheet?>(null)
    }

    fun canShowData() = userQuotes.isNotEmpty()

    val postsCount = viewModel.postsCount.value ?: 0
    val favoriteCount = viewModel.favoriteCount.value ?: 0
    val coverAlpha = animateFloatAsState(
        targetValue = if (canShowData()) 1f else 0f,
        tween(2000, easing = FastOutSlowInEasing)
    )


    fun showUserTitle() = listState.firstVisibleItemIndex > 1

    val borderBrush = profileBitmap?.asAndroidBitmap()?.paletteFromBitMap()?.colorsFromPalette()
        ?: motivBrushes()

    LaunchedEffect(userQuotes) {
        Log.i("Profile view", "ProfileView: showing ${userQuotes.size} quotes")
    }

    LaunchedEffect(Unit) {
        viewModel.fetchUser(userID?.replace("{userId}", ""))
    }

    LaunchedEffect(user) {
        if (user != null && postsCount == 0) {
            viewModel.getUserQuotes(user.uid)
            viewModel.getUserFavorites(user.uid)
        }
    }




    LazyColumn(
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(tween(1500, easing = FastOutSlowInEasing))
    ) {

        if (user != null) {
            item {
                Box() {
                    val avatarSize = 150.dp
                    CardBackground(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(avatarSize)
                            .alpha(coverAlpha.value),
                        backgroundImage = user.cover
                    ) {
                        coverBitmap = it
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(start = 16.dp, end = 16.dp, top = 75.dp),
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
                                .radioIconModifier(
                                    0f,
                                    avatarSize,
                                    gradientAnimation(borderBrush),
                                    2.dp
                                )
                        )

                        Text(
                            text = user.name ?: "",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .alpha(coverAlpha.value)
                        )

                        Row(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(2.dp)
                                .alpha(coverAlpha.value),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            CounterLabel(label = "Publicações", count = postsCount)

                            Divider(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp)
                                    .clip(RoundedCornerShape(defaultRadius))
                                    .padding(horizontal = 4.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                                thickness = 1.dp
                            )

                            CounterLabel(label = "Favoritos", count = favoriteCount)
                        }
                    }

                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .alpha(coverAlpha.value)
                    ) {
                        Icon(Icons.Rounded.KeyboardArrowLeft, contentDescription = "Voltar")
                    }

                    AnimatedVisibility(
                        visible = isOwnUser.value == true,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .alpha(coverAlpha.value)
                    ) {
                        IconButton(onClick = {
                            navController.navigate(AppNavigation.SETTINGS.route)
                        }) {
                            Icon(Icons.Rounded.Settings, contentDescription = "Configurações")
                        }
                    }

                }
            }

            stickyHeader {

                AnimatedVisibility(
                    visible = canShowData() && showUserTitle(),
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = user.name, style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp)
                            .gradientFill(gradientAnimation(borderBrush))
                    )
                }

                AnimatedVisibility(
                    visible = canShowData(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {


                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {

                        ProfileFilter.values().forEach {
                            ProfileTab(
                                buttonText = it.title,
                                isSelected = it == currentFilter
                            ) {
                                currentFilter = it
                            }
                        }
                    }
                }

                Divider(
                    Modifier
                        .fillMaxWidth()
                        .alpha(coverAlpha.value),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    thickness = 1.dp
                )

            }
        }

        items(userQuotes.size) {
            AnimatedVisibility(
                visible = true,
                enter = scaleIn() + fadeIn(
                    tween(1500)
                ),
                exit = fadeOut()
            ) {
                QuoteCard(
                    loadAsGif = true,
                    animationEnabled = false,
                    quoteDataModel = userQuotes[it],
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .wrapContentSize()
                        .quoteCardModifier(),
                    onClickUser = {
                        if (it != user?.uid) {
                            navController.navigate("profile/{userId}".replace("{userId}", it))
                        }
                    },
                    onShare = {},
                    onLike = {},
                    onDelete = {},
                    onEdit = {}
                )
            }

        }

    }


    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect {
                Log.i("ListState", "ProfileView: first visible item $it")
            }
    }


}

enum class ProfileSheet {
    ICONS, COVERS
}

enum class ProfileFilter(val title: String) {
    POSTS("Posts"), FAVORITES("Favoritos")
}
