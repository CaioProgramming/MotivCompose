@file:OptIn(ExperimentalFoundationApi::class)

package com.ilustris.motivcompose.features.home.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ilustris.motiv.foundation.ui.component.QuoteCard
import com.ilustris.motiv.foundation.ui.theme.MotivTheme
import com.ilustris.motiv.foundation.ui.theme.quoteCardModifier
import com.ilustris.motiv.foundation.ui.theme.motivBrushes
import com.ilustris.motivcompose.features.radio.ui.RadioView

@Composable
fun HomeView() {

    val quotes = listOf(
        "É a solidão que inspira os poetas, cria os artistas e anima o gênio.",
        "vivi muitos abandonos,mas eu não desisti de mim.aprendi que ninguém pode(e nem deveria)me amar mais do que eu mesma."
    )

    val radioExpanded = remember {
        mutableStateOf(false)
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { quotes.size }
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (header, content) = createRefs()

        VerticalPager(
            modifier = Modifier
                .constrainAs(content) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.matchParent
                    height = Dimension.fillToConstraints
                }
                .padding(16.dp),
            state = pagerState,
            userScrollEnabled = true,
            pageContent = {
                QuoteCard(quotes[it], onClick = { quote ->
                    Log.i(javaClass.simpleName, "HomeView: quote selected $quote")
                }, modifier = Modifier.quoteCardModifier())
            }
        )

        RadioView(expanded = radioExpanded.value, modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .constrainAs(header) {
                top.linkTo(parent.top)
                width = Dimension.matchParent
            }) { radioExpanded.value = !radioExpanded.value }

    }


}

@Preview(showBackground = true, showSystemUi = true, name = "Home View", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeViewPreview() {
    MotivTheme(true) {
        HomeView()
    }
}