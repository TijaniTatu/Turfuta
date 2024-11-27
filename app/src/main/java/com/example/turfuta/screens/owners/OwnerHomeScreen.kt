package com.example.turfuta.screens.owners

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turfuta.R
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

sealed class OwnerScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : OwnerScreen("home", "Home", Icons.Default.Home)
    object Bookings : OwnerScreen("bookings", "Bookings", Icons.Default.ShoppingCart)
    object Profile : OwnerScreen("profile", "Profile", Icons.Default.Person)
    object TurfManagement : OwnerScreen("turf_management", "Manage Turf", Icons.Default.Build)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OwnerHomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val items = listOf(
        OwnerScreen.Home,
        OwnerScreen.Bookings,
        OwnerScreen.Profile,
        OwnerScreen.TurfManagement
    )

    Scaffold(
        bottomBar = {
            OwnerBottomNavigation(
                pagerState = pagerState,
                items = items,
                onTabSelected = { page ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                }
            )
        }
    ) { paddingValues ->
        OwnerPager(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .systemBarsPadding(), // Ensures content respects system bars
            pagerState = pagerState,
            items = items,
            navController
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OwnerPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    items: List<OwnerScreen>,
    navController: NavController
) {
    HorizontalPager(
        state = pagerState,
        count = items.size,
        modifier = modifier
    ) { page ->
        when (items[page]) {
            OwnerScreen.Home -> Box(Modifier.fillMaxSize()) { OwnerHomePage() }
            OwnerScreen.Bookings -> Box(Modifier.fillMaxSize()) { Text("Bookings Screen") }
            OwnerScreen.Profile -> Box(Modifier.fillMaxSize()) { OwnerProfileScreen(navController = navController) }
            OwnerScreen.TurfManagement -> Box(Modifier.fillMaxSize()) { TurfManagementScreen() }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OwnerBottomNavigation(
    pagerState: PagerState,
    items: List<OwnerScreen>,
    onTabSelected: (Int) -> Unit
) {

    NavigationBar {
        items.forEachIndexed { index, screen->
            val isSelected = pagerState.currentPage == index
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        modifier = Modifier.size(24.dp) // Adjust icon size
                    )
                },
                label = {Text(screen.title)},
                selected = isSelected,
                onClick = {onTabSelected(index)}
            )
        }
    }
}

