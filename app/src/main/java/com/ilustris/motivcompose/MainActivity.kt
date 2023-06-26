@file:OptIn(ExperimentalMaterial3Api::class)

package com.ilustris.motivcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.ilustris.motiv.foundation.ui.component.MotivLoader
import com.ilustris.motiv.foundation.ui.component.TypeWriterText
import com.ilustris.motiv.foundation.ui.theme.MotivTheme
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.gradientFill
import com.ilustris.motiv.foundation.ui.theme.motivGradient
import com.ilustris.motivcompose.ui.navigation.MotivBottomNavigation
import com.ilustris.motivcompose.ui.navigation.MotivNavigationGraph
import com.silent.ilustriscore.core.model.ViewModelBaseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MotivTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = hiltViewModel()
                val currentUser = viewModel.currentUser.observeAsState().value
                val viewModelState = viewModel.viewModelState.observeAsState()
                val signInLauncher = rememberLauncherForActivityResult(
                    FirebaseAuthUIActivityResultContract()
                ) { result ->
                    viewModel.validateLogin(result)
                }
                val signInIntent = AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(AppModule.loginProviders)
                    .build()

                AnimatedVisibility(
                    visible = currentUser == null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        MotivLoader(modifier = Modifier.size(100.dp))

                        TypeWriterText(
                            text = "Faça login para explorar o ${getString(com.ilustris.motiv.foundation.R.string.app_name)}",
                            shadow = null,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                            textStyle = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                            fontFamily = MaterialTheme.typography.headlineMedium.fontFamily!!,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { signInLauncher.launch(signInIntent) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onBackground,
                                contentColor = MaterialTheme.colorScheme.background
                            ),
                            shape = RoundedCornerShape(defaultRadius),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .wrapContentHeight(align = Alignment.CenterVertically)
                        ) {
                            Text(
                                text = "Login",
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.background,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }


                }

                AnimatedVisibility(visible = currentUser != null) {
                    Scaffold(
                        topBar = {
                            Text(
                                text = "",
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .gradientFill(motivGradient()),
                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        },
                        bottomBar = {
                            MotivBottomNavigation(
                                navController = navController,
                                userProfilePic = currentUser?.picurl
                            )
                        }) {
                        MotivNavigationGraph(
                            navHostController = navController,
                            padding = it
                        )
                    }
                }


                LaunchedEffect(Unit) {
                    viewModel.fetchUser()
                }

                LaunchedEffect(navController) {
                    viewModel.validateAuth()
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