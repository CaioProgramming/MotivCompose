@file:OptIn(ExperimentalMaterial3Api::class)

package com.ilustris.motivcompose.features.settings.ui

import ai.atick.material.MaterialColor
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ilustris.motiv.foundation.ui.component.CardBackground
import com.ilustris.motiv.foundation.ui.component.gradientAnimation
import com.ilustris.motiv.foundation.ui.theme.brushsFromPalette
import com.ilustris.motiv.foundation.ui.theme.defaultRadius
import com.ilustris.motiv.foundation.ui.theme.gradientFill
import com.ilustris.motiv.foundation.ui.theme.grayGradients
import com.ilustris.motiv.foundation.ui.theme.motivBrushes
import com.ilustris.motiv.foundation.ui.theme.motivGradient
import com.ilustris.motiv.foundation.ui.theme.paletteFromBitMap
import com.ilustris.motiv.foundation.ui.theme.radioIconModifier
import com.ilustris.motivcompose.MainActivity
import com.ilustris.motivcompose.features.profile.ui.component.CounterLabel
import com.ilustris.motivcompose.features.settings.presentation.SettingsViewModel
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState
import com.skydoves.landscapist.glide.GlideRequestType
import java.util.Calendar

@Composable
fun SettingsView(navController: NavController) {

    val viewmodel: SettingsViewModel = hiltViewModel()

    val user = viewmodel.user.observeAsState()

    var profileBitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    val usernameText = remember { mutableStateOf(user.value?.name ?: "") }
    val borderBrush = profileBitmap?.asAndroidBitmap()?.paletteFromBitMap()?.brushsFromPalette()
        ?: grayGradients()

    val dialogState = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    AnimatedVisibility(
        visible = user.value != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LazyColumn() {

            item {
                Box() {
                    val avatarSize = 100.dp
                    CardBackground(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(avatarSize * 2),
                        backgroundImage = user.value?.cover
                    ) {
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        GlideImage(
                            imageModel = { user.value?.picurl },
                            glideRequestType = GlideRequestType.BITMAP,
                            onImageStateChanged = {
                                if (it is GlideImageState.Success) {
                                    profileBitmap = it.imageBitmap
                                }
                            },
                            modifier = Modifier
                                .radioIconModifier(0f, avatarSize, borderBrush, 2.dp)
                        )
                    }

                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Icon(Icons.Rounded.KeyboardArrowLeft, contentDescription = "Voltar")
                    }
                }
            }

            item {
                TextField(
                    value = usernameText.value,
                    onValueChange = {
                        usernameText.value = it
                    },
                    placeholder = {
                        Text(
                            text = user.value?.name ?: "Nome de usuário",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    shape = RoundedCornerShape(
                        0.dp
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    trailingIcon = {
                        val nameText = usernameText.value
                        if (nameText.isNotEmpty() && nameText != user.value?.name) {
                            IconButton(
                                colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = MaterialColor.LightBlueA200),
                                onClick = {
                                    viewmodel.updateUserName(usernameText.value)
                                }
                            ) {
                                Icon(Icons.Rounded.Check, contentDescription = "Salvar")
                            }
                        }

                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (usernameText.value.isNotEmpty() && usernameText.value != user.value?.name) {
                                viewmodel.updateUserName(usernameText.value)
                            }
                        }
                    ),
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        placeholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        containerColor = MaterialTheme.colorScheme.background,
                        cursorColor = MaterialTheme.colorScheme.onBackground,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
            }

            item {
                if (user.value?.admin == true) {
                    Text(
                        text = "Motiv +",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                            }
                            .padding(16.dp)
                            .gradientFill(gradientAnimation(motivBrushes()))

                    )
                }
            }


            item {
                Text(
                    text = "Remover conta",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable {
                            dialogState.value = true
                        }
                        .padding(16.dp)

                )
            }


            item {
                Text(
                    text = "Desconectar",
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialColor.Red800,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewmodel.logOut()
                            val activity = (context as? Activity)
                            context.startActivity(Intent(context, MainActivity::class.java))
                            activity?.finish()
                        }
                        .padding(16.dp)

                )
            }


            item {

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val context = LocalContext.current

                    fun openPlayStorePage() {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/dev?id=8106172357045720296")
                        )
                        context.startActivity(intent)
                    }

                    val ilustrisBrush = listOf(
                        MaterialColor.TealA400,
                        MaterialColor.BlueA400,
                        MaterialColor.LightBlueA700,
                    )

                    val gradientAnimation = gradientAnimation(ilustrisBrush)

                    Icon(
                        Icons.Rounded.Star,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .graphicsLayer(alpha = 0.99f)
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = gradientAnimation,
                                        blendMode = BlendMode.SrcAtop
                                    )
                                }
                            }
                            .clip(CircleShape)
                            .clickable() {
                                openPlayStorePage()
                            }
                    )
                }
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)

                Text(
                    text = "Desenvolvido por ilustris em 2018 - $currentYear",
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                )
            }

        }

        AnimatedVisibility(
            visible = dialogState.value,
            enter = fadeIn(),
            exit = scaleOut()
        ) {
            AlertDialog(modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
                .background(
                    MaterialTheme.colorScheme.surface, RoundedCornerShape(
                        defaultRadius
                    )
                ),
                shape = RoundedCornerShape(defaultRadius),
                title = {
                    Text(text = "Tem certeza?")
                },
                text = {
                    Text(text = "Você não poderá recuperar sua conta depois de excluí-la.")
                },
                dismissButton = {
                    Text(text = "Cancelar", textAlign = TextAlign.Center, modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            dialogState.value = false
                        }
                        .clip(RoundedCornerShape(defaultRadius))
                        .padding(8.dp))
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewmodel.deleteAccount()
                            dialogState.value = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(defaultRadius),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialColor.Red500,
                            contentColor = MaterialColor.White
                        )
                    ) {
                        Text(
                            text = "Excluir", textAlign = TextAlign.Center, modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),
                onDismissRequest = {
                    dialogState.value = false
                })
        }

    }

    LaunchedEffect(user) {
        usernameText.value = user.value?.name ?: ""
    }

    LaunchedEffect(Unit) {
        viewmodel.fetchUser()
    }
}