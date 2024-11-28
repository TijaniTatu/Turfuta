package com.example.turfuta.screens.customers

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
import com.example.turfuta.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.turfuta.screens.BottomNavigationBar

@Composable
fun HomePage(navController: NavHostController, authViewModel: AuthViewModel = viewModel()) {
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
                0 -> HomeScreen(navController = navController) // Pass navController
                1 -> SearchScreen(navController = navController, authViewModel = authViewModel)
                2 -> HistoryScreen(navController = navController)
                3 -> ProfileScreen(navController = navController, authViewModel = authViewModel)
                else -> Text("Page not found", color = Color.Red, textAlign = TextAlign.Center)
            }
        }
    }
}
