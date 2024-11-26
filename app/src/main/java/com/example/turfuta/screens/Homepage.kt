package com.example.turfuta.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun HomePage() {
    // Global Navigation Controller for the whole app
    val appNavController = rememberNavController()

    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(pagerState = pagerState) { selectedPage ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(selectedPage)
                }
            }
        }
    ) { innerPadding ->

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> TabWithNavHost("home", appNavController)
                1 -> TabWithNavHost("search", appNavController)
                2 -> TabWithNavHost("history", appNavController)
                3 -> TabWithNavHost("profile", appNavController)
                else -> Text("Page not found", color = Color.Red, textAlign = TextAlign.Center)
            }
        }
    }
}
