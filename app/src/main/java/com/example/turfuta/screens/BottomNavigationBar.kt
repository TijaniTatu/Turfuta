package com.example.turfuta.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: String
) {
    NavigationBar(
        containerColor = Color(0xFF1B5E20) // Green
    ) {
        val items = listOf("home", "search", "history", "profile")

        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen,
                onClick = {
                    navController.navigate(screen) {
                        // Pop up to the root screen (optional based on behavior you want)
                        popUpTo("home") { inclusive = true }
                    }
                },
                icon = {
                    when (screen) {
                        "home" -> Icon(Icons.Default.Home, contentDescription = "Home")
                        "search" -> Icon(Icons.Default.Search, contentDescription = "Search")
                        "history" -> Icon(Icons.Default.DateRange, contentDescription = "History")
                        "profile" -> Icon(Icons.Default.Person, contentDescription = "Profile")
                        else -> Icon(Icons.Default.Home, contentDescription = "Unknown")
                    }
                },
                label = { Text(screen.capitalize(), color = Color.White) }
            )
        }
    }
}

