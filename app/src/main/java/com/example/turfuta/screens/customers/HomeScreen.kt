package com.example.turfuta.screens.customers

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.turfuta.AuthViewModel
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    username: String,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val greenColor = Color(0xFF388E3C)
    var showMenu by remember { mutableStateOf(false) }
    var profilePhotoUrl by remember { mutableStateOf("") }
    var fetchedUsername by remember { mutableStateOf(username) }


    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    profilePhotoUrl = document.getString("profilePhotoUrl") ?: ""
                    fetchedUsername = document.getString("username") ?: username
                }
        }
    }

    CenterAlignedTopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp), // Adjust height if needed
        title = {
            Text(
                text = "Hello, $fetchedUsername!",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        },
        actions = {
            IconButton(onClick = { showMenu = !showMenu }) {
                if (profilePhotoUrl.isNotEmpty()) {

                    Image(
                        painter = rememberAsyncImagePainter(profilePhotoUrl),
                        contentDescription = "Profile Menu",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {

                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Menu",
                        tint = Color.White
                    )
                }
            }

            // Dropdown Menu
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Profile") },
                    onClick = {
                        showMenu = false
                        navController.navigate("profile")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Settings") },
                    onClick = {
                        showMenu = false
                        navController.navigate("settings")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Sign Out") },
                    onClick = {
                        showMenu = false
                        authViewModel.signout()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = greenColor,
            titleContentColor = Color.White
        )
    )
}




@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    appNavController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val username = authViewModel.username.value ?: "Guest"
    val featuredTurfs = authViewModel.turfs.value

    // Trigger the fetch operation on the first composition
    LaunchedEffect(Unit) {
        authViewModel.fetchAllTurfs()
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                username = username,
                navController = appNavController,
                authViewModel = authViewModel
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Featured Turfs",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (featuredTurfs.isNotEmpty()) {
                        val pagerState = rememberPagerState(pageCount = { featuredTurfs.size })

                        HorizontalPager(state = pagerState) { page ->
                            val turf = featuredTurfs[page]
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Display the first image in the array
                                if (turf.images.isNotEmpty()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(turf.images[0]),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp)
                                            .clip(MaterialTheme.shapes.medium),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = turf.name,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp
                                    ),
                                    modifier = Modifier.padding(top = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Location: ${turf.location}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Text(
                                    text = "Cost: ${turf.cost}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Text(
                                    text = "Available: ${turf.timeAvailable}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        // Indicator Dots
                        Row(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(pagerState.pageCount) { iteration ->
                                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .size(12.dp)
                                )
                            }
                        }
                    } else {
                        Text("Loading featured turfs...")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate("home/details") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Go to Home Details")
                    }
                }
            }
        }
    )
}
