@file:OptIn(ExperimentalFoundationApi::class)

package com.ilustris.manager.feature.giphy

import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.views.dialogview.GiphyDialogView
import com.giphy.sdk.ui.views.dialogview.setup
import com.ilustris.manager.R
import com.ilustris.motiv.foundation.ui.theme.getDeviceHeight

@Composable
fun GifSheet(visible: Boolean, onSelectGif: (String) -> Unit) {
    val context = LocalContext.current
    Giphy.configure(context, stringResource(id = R.string.giphy_api), true)
    val settings = GPHSettings(
        theme = GPHTheme.Automatic,
        mediaTypeConfig = arrayOf(GPHContentType.gif),
        stickerColumnCount = 2,
        selectedContentType = GPHContentType.gif
    )
    AnimatedVisibility(visible = visible, enter = slideInVertically { it }, exit = fadeOut()) {

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .height(getDeviceHeight().dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = "Selecione um GIPHY",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    GiphyDialogView(it).apply {
                        setup(
                            settings,
                        )

                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        listener = object : GiphyDialogView.Listener {
                            override fun didSearchTerm(term: String) {}
                            override fun onClosed(selectedContentType: GPHContentType) {}

                            override fun onFocusSearch() {}

                            override fun onGifSelected(
                                media: Media,
                                searchTerm: String?,
                                selectedContentType: GPHContentType
                            ) {
                                media.images.downsizedMedium?.gifUrl?.let { url ->
                                    onSelectGif(url)

                                }
                            }

                        }
                    }
                },
            )
        }

    }
}