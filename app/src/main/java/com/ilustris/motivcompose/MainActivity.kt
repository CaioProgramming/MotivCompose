@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.ilustris.motivcompose

import ai.atick.material.MaterialColor
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.ilustris.motiv.foundation.model.Radio
import com.ilustris.motiv.foundation.ui.component.MotivLoader
import com.ilustris.motiv.foundation.ui.component.AnimatedText
import com.ilustris.motiv.foundation.ui.component.gradientAnimation
import com.ilustris.motiv.foundation.ui.theme.MotivTheme
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.gradientFill
import com.ilustris.motiv.foundation.ui.theme.motivGradient
import com.ilustris.motivcompose.features.radio.ui.RadioView
import com.ilustris.motivcompose.ui.navigation.AppNavigation
import com.ilustris.motivcompose.ui.navigation.MotivBottomNavigation
import com.ilustris.motivcompose.ui.navigation.MotivNavigationGraph
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.delayedFunction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectIndexed

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mediaPlayer = MediaPlayer()
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
                val radioExpanded = remember { mutableStateOf(false) }

                val signInLauncher = rememberLauncherForActivityResult(
                    FirebaseAuthUIActivityResultContract()
                ) { result ->
                    viewModel.validateLogin(result)
                }
                val signInIntent = AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(AppModule.loginProviders)
                    .build()

                AnimatedVisibility(
                    visible = currentUser == null && viewModelState.value == ViewModelBaseState.RequireAuth,
                    enter = fadeIn(),
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
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val context = LocalContext.current

                    fun playRadio(radio: Radio) {
                        try {
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
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e(
                                javaClass.simpleName,
                                "playRadio: Error playing radio(${radio.name}) ${e.message}"
                            )
                            viewModel.updatePlayingRadio(null)
                            mediaPlayer.stop()
                        }
                    }

                    Scaffold(
                        bottomBar = {
                            MotivBottomNavigation(
                                navController = navController,
                                userProfilePic = currentUser?.picurl
                            )
                        }) {
                        ConstraintLayout(
                            modifier = Modifier
                                .padding(bottom = it.calculateBottomPadding())
                                .fillMaxSize()
                        ) {
                            val (content, player) = createRefs()
                            MotivNavigationGraph(
                                navHostController = navController,
                                padding = it,
                                modifier = Modifier.constrainAs(content) {
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                })



                            AnimatedVisibility(visible = showRadio,
                                enter = fadeIn(),
                                exit = shrinkOut() + fadeOut(),
                                modifier = Modifier
                                    .constrainAs(player) {
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                        bottom.linkTo(parent.bottom)
                                    }) {
                                RadioView(
                                    modifier = Modifier.fillMaxWidth(),
                                    onSelectRadio = ::playRadio,
                                    expanded = radioExpanded.value && showRadio,
                                    playingRadio = playingRadio,
                                    onExpand = {
                                        radioExpanded.value = !radioExpanded.value
                                    }
                                )
                            }
                        }
                    }
                }


                LaunchedEffect(Unit) {
                    viewModel.fetchUser()
                }

                LaunchedEffect(viewModelState.value) {
                    if (currentUser == null && viewModelState.value == ViewModelBaseState.RequireAuth) {
                        signInLauncher.launch(signInIntent)
                    }
                }

                LaunchedEffect(navController) {
                    viewModel.validateAuth()
                    navController.currentBackStackEntryFlow.collect { backStackEntry ->
                        showRadio = backStackEntry.destination.route != AppNavigation.POST.route
                        radioExpanded.value = false
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MotivTheme {
        Greeting("Android")
    }
}