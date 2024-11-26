package com.example.turfuta.screens.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.turfuta.AuthViewModel
import com.example.turfuta.ui.theme.TurfutaTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    username: String,
    onOptionSelected: (String) -> Unit
) {
    val greenColor = Color(0xFF388E3C) // Your desired green color
    var showMenu by remember { mutableStateOf(false) } // To control menu visibility

    CenterAlignedTopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp), // Adjust height if needed
        title = {
            Text(
                text = "Hello, $username!",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White // Ensure good contrast with green background
                )
            )
        },
        actions = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile Menu",
                    tint = Color.White
                )
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
                        onOptionSelected("profile")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Settings") },
                    onClick = {
                        showMenu = false
                        onOptionSelected("settings")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Sign Out") },
                    onClick = {
                        showMenu = false
                        onOptionSelected("signout")
                    }
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = greenColor, // Use specific green color
            titleContentColor = Color.White
        )
    )
}



@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    // Fetch the username from AuthViewModel and provide a default value if it's null
    val username = authViewModel.username.value ?: "Guest"

    Scaffold(
        topBar = {
            CustomTopBar(username = username) { option ->
                when (option) {
                    "profile" -> navController.navigate("profile")
                    "settings" -> navController.navigate("settings")
                    "signout" -> {
                        authViewModel.signout()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                }
            }
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
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Welcome to the Home Screen!")

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { navController.navigate("home/details") }) {
                        Text("Go to Home Details")
                    }
                }
            }
        }
    )
}

