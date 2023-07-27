@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)

package com.ilustris.motivcompose

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.ilustris.motiv.foundation.model.Radio
import com.ilustris.motiv.foundation.ui.component.MotivLoader
import com.ilustris.motiv.foundation.ui.theme.MotivTheme
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.gradientAnimation
import com.ilustris.motiv.foundation.ui.theme.gradientFill
import com.ilustris.motivcompose.features.radio.ui.RadioSheet
import com.ilustris.motiv.foundation.navigation.AppNavigation
import com.ilustris.motivcompose.ui.navigation.MotivBottomNavigation
import com.ilustris.motivcompose.ui.navigation.MotivNavigationGraph
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ViewModelBaseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        )
        setContent {
            MotivTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = hiltViewModel()
                val currentUser = viewModel.currentUser.observeAsState().value
                val viewModelState = viewModel.viewModelState.observeAsState()
                val playingRadio = viewModel.playingRadio.observeAsState().value

                var showRadio by remember { mutableStateOf(true) }
                val showBottomNavigation = remember {
                    mutableStateOf(
                        true
                    )
                }
                var swipeEnabled by remember { mutableStateOf(true) }
                val signInLauncher = rememberLauncherForActivityResult(
                    FirebaseAuthUIActivityResultContract()
                ) { result ->
                    viewModel.validateLogin(result)
                }
                val signInIntent = AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(AppModule.loginProviders)
                    .build()

                AnimatedVisibility(
                    visible = viewModelState.value is ViewModelBaseState.ErrorState,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut()
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .gradientFill(gradientAnimation())
                    ) {
                        MotivLoader(modifier = Modifier
                            .size(100.dp)
                            .clickable { signInLauncher.launch(signInIntent) }
                            .padding(8.dp)
                            .clip(
                                CircleShape
                            ))
                    }
                }

                AnimatedVisibility(
                    visible = currentUser != null,
                    enter = fadeIn(tween(1500)),
                    exit = fadeOut()
                ) {
                    val context = LocalContext.current

                    fun playRadio(radio: Radio) {
                        try {
                            swipeEnabled = false
                            if (radio == playingRadio) {
                                if (mediaPlayer.isPlaying) {
                                    mediaPlayer.pause()
                                } else {
                                    mediaPlayer.start()
                                }
                                return
                            } else {
                                Log.i(
                                    javaClass.simpleName,
                                    "playRadio: Playing radio(${radio.name})"
                                )
                                if (mediaPlayer.isPlaying) {
                                    mediaPlayer.reset()
                                }
                                mediaPlayer.setDataSource(context, Uri.parse(radio.url))
                                mediaPlayer.prepareAsync()
                                mediaPlayer.setOnPreparedListener {
                                    mediaPlayer.setVolume(0.3f, 0.3f)
                                    mediaPlayer.start()
                                    viewModel.updatePlayingRadio(radio)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e(
                                javaClass.simpleName,
                                "playRadio: Error playing radio(${radio.name}) ${e.message}"
                            )
                            viewModel.updatePlayingRadio(null)
                            mediaPlayer.reset()
                        } finally {
                            swipeEnabled = true
                        }
                    }

                    BottomSheetScaffold(
                        sheetContent = {
                            RadioSheet(
                                playingRadio = playingRadio,
                                modifier = Modifier.fillMaxWidth(),
                                onSelectRadio = ::playRadio,
                            )
                        },
                        sheetShape = RoundedCornerShape(defaultRadius),
                        sheetGesturesEnabled = true,
                        sheetPeekHeight = 24.dp,
                        sheetBackgroundColor = MaterialTheme.colorScheme.background
                    ) {
                        Scaffold(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .padding(bottom = 24.dp),
                            bottomBar = {
                                AnimatedVisibility(visible = showBottomNavigation.value) {
                                    MotivBottomNavigation(
                                        navController = navController,
                                        userProfilePic = currentUser?.picurl
                                    )
                                }

                            }) { _ ->
                            MotivNavigationGraph(
                                navHostController = navController,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                LaunchedEffect(Unit) {
                    viewModel.fetchUser()
                }

                LaunchedEffect(viewModelState.value) {
                    if (viewModelState.value == ViewModelBaseState.RequireAuth) {
                        signInLauncher.launch(signInIntent)
                    } else if (viewModelState.value is ViewModelBaseState.ErrorState) {
                        if ((viewModelState.value as ViewModelBaseState.ErrorState).dataException == DataException.AUTH) {
                            signInLauncher.launch(signInIntent)
                        }
                    }
                }

                LaunchedEffect(navController) {
                    viewModel.validateAuth()
                    navController.currentBackStackEntryFlow.collect { backStackEntry ->
                        val previousRoute = navController.previousBackStackEntry?.destination?.route
                        val route = backStackEntry.destination.route
                        showRadio = route != AppNavigation.POST.route
                        showBottomNavigation.value =
                            AppNavigation.values().find { it.route == route }?.showBottomBar ?: true
                        if (previousRoute == AppNavigation.PROFILE.route) {
                            viewModel.fetchUser()
                        }
                    }
                }

            }
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        try {
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

