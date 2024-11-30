package com.example.turfuta.screens.owners

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turfuta.R
import com.google.accompanist.pager.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

sealed class OwnerScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : OwnerScreen("home", "Home", Icons.Default.Home)
    object Bookings : OwnerScreen("bookings", "Bookings", Icons.Default.ShoppingCart)
    object Profile : OwnerScreen("profile", "Profile", Icons.Default.Person)
    object TurfManagement : OwnerScreen("turf_management", "Manage Turf", Icons.Default.Build)
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
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

    var userName by remember { mutableStateOf("Loading...") }
    var profilePhotoUrl by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) } // For dropdown menu state

    // Titles for each page
    val pageTitles = listOf("Home", "Bookings", "Profile", "TurfManagement")
    val auth = FirebaseAuth.getInstance()

    Scaffold(
        topBar = {
            // Custom TopAppBar with curved bottom corners
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                color = Color(0xFF04764E), // Correct color for the background
                shadowElevation = 8.dp
            ) {
                androidx.compose.material3.TopAppBar(
                    title = {
                        androidx.compose.material3.Text(
                            text = pageTitles[pagerState.currentPage],
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White // Ensure text remains white
                        )
                    },
                    actions = {
                        Box {
                            androidx.compose.material3.IconButton(onClick = {
                                showMenu = !showMenu
                            }) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More options",
                                    tint = Color.White // Ensure icon is white
                                )
                            }
                            androidx.compose.material3.DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { androidx.compose.material3.Text("Home") },
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(0)
                                        }
                                        showMenu = false
                                    }
                                )
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { androidx.compose.material3.Text("Search") },
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(1)
                                        }
                                        showMenu = false
                                    }
                                )
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { androidx.compose.material3.Text("History") },
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(2)
                                        }
                                        showMenu = false
                                    }
                                )
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { androidx.compose.material3.Text("Profile") },
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(3)
                                        }
                                        showMenu = false
                                    }
                                )
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { Text("log out") },
                                    onClick = {
                                        auth.signOut()
                                        navController.navigate("login")
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color(0xFF04764E) // Set the correct background color
                    )
                )
            }
        },
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
            OwnerScreen.Bookings -> Box(Modifier.fillMaxSize()) { BookingScreen() }
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

    NavigationBar (
        containerColor = Color(0xFF04764E) // Dark Green for the background
    ){
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

