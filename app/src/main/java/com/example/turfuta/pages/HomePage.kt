package com.example.turfuta.navigation
import androidx.compose.material.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.Text



// Main Navigation Graph
//@Composable
//fun AppNavGraph(navController: NavHostController = rememberNavController()) {
//    NavHost(
//        navController = navController,
//        startDestination = "home"
//    ) {
//        composable("home") {
//
//        }
//        composable("search") {
//            // Implement Search Screen
//        }
//        composable("history") {
//            // Implement History Screen
//        }
//        composable("profile") {
//            // Implement Profile Screen
//        }
//    }
//}

@Composable
fun HomePage(navController: NavHostController) {
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavigationBar(navController) } // Pass navController here
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF6F6F6)) // Light gray background
        ) {
            GreetingSection()
            Spacer(modifier = Modifier.height(16.dp))
            UpcomingBookingsSection()
            Spacer(modifier = Modifier.height(16.dp))
            HistorySection()
            Spacer(modifier = Modifier.height(16.dp))
            PromotionalCard()
            Spacer(modifier = Modifier.height(16.dp))
            NewsSection()
        }
    }
}

// TopBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text("Good Morning, Adrian", color = Color.White) },
        backgroundColor = Color(0xFF1B5E20), // Green
        actions = {
            IconButton(onClick = { /* Handle notifications */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = { navController.navigate("home") }, // Navigate to Home
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("search") }, // Navigate to Search
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("history") }, // Navigate to History
            icon = { Icon(Icons.Default.DateRange, contentDescription = "History") },
            label = { Text("History") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("profile") }, // Navigate to Profile
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
    }
}

// GreetingSection
@Composable
fun GreetingSection() {
    Text(
        text = "Good Morning, Adrian",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

// UpcomingBookingsSection
@Composable
fun UpcomingBookingsSection() {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Upcoming bookings", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Red Devils vs V. Greens - 28 October 2024", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// HistorySection
@Composable
fun HistorySection() {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("History", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Kijiji 5 star turf - 1st October 2024", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// PromotionalCard
@Composable
fun PromotionalCard() {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(150.dp),
        elevation = 4.dp,
        backgroundColor = Color(0xFF1B5E20), // Green
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Find a turf\nGet 54% OFF",
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

// NewsSection
@Composable
fun NewsSection() {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("News", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Upcoming tournaments: Platinum cup at Jamuhuri grounds", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
