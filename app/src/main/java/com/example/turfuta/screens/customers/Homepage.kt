package com.example.turfuta.screens.customers

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.turfuta.AuthViewModel
import com.example.turfuta.screens.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    // Variables for user's profile photo and name
    var userName by remember { mutableStateOf("Loading...") }
    var profilePhotoUrl by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) } // For dropdown menu state

    // Titles for each page
    val pageTitles = listOf("Home", "Search", "History", "Profile")

    // Fetch user data from Firebase
    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userName = document.getString("username") ?: "User"
                        profilePhotoUrl = document.getString("profilePhotoUrl") ?: ""
                    } else {
                        userName = "User"
                        profilePhotoUrl = ""
                    }
                }
                .addOnFailureListener {
                    userName = "Error loading name"
                    profilePhotoUrl = ""
                }
        } else {
            userName = "Guest"
            profilePhotoUrl = ""
        }
    }

    Scaffold(
        topBar = {
            // Custom TopAppBar with curved bottom corners
            Surface(
                shape = RoundedCornerShape(
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                color = Color(0xFF04764E), // Correct color for the background
                shadowElevation = 8.dp
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = pageTitles[pagerState.currentPage],
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White // Ensure text remains white
                        )
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showMenu = !showMenu }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More options",
                                    tint = Color.White // Ensure icon is white
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Home") },
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(0)
                                        }
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Search") },
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(1)
                                        }
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("History") },
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(2)
                                        }
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Profile") },
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(3)
                                        }
                                        showMenu = false
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
        }
,
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
                0 -> HomeScreen(navController = navController)
                1 -> SearchScreen(navController = navController, authViewModel = authViewModel)
                2 -> HistoryScreen(navController = navController)
                3 -> ProfileScreen(navController = navController, authViewModel = authViewModel)
                else -> Text(
                    "Page not found",
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

