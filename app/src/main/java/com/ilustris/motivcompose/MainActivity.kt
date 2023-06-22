@file:OptIn(ExperimentalMaterial3Api::class)

package com.ilustris.motivcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.ilustris.motiv.foundation.ui.theme.MotivTheme
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
                val viewModelState = viewModel.viewModelState.observeAsState()
                val signInLauncher = rememberLauncherForActivityResult(
                    FirebaseAuthUIActivityResultContract()
                ) { result ->
                    viewModel.validateLogin(result)
                }
                val signInIntent = AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(AppModule.loginProviders)
                    .build()

                Scaffold(bottomBar = { MotivBottomNavigation(navController = navController) }) {
                    MotivNavigationGraph(
                        navHostController = navController,
                        padding = it.calculateBottomPadding()
                    )
                }

                if (viewModelState.value == ViewModelBaseState.RequireAuth) {
                    signInLauncher.launch(signInIntent)
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