package com.example.turfuta.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.turfuta.screens.customers.HistoryScreen
import com.example.turfuta.screens.customers.HomeScreen
import com.example.turfuta.screens.customers.ProfileScreen
import com.example.turfuta.screens.customers.SearchScreen

@Composable
fun TabWithNavHost(tab: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "$tab/main") {
        composable("$tab/main") {
            when (tab) {
                "home" -> HomeScreen(navController)
                "search" -> SearchScreen(navController)
                "history" -> HistoryScreen(navController)
                "profile" -> ProfileScreen()
            }
        }
        composable("$tab/details") {
            Text(text = "Details for $tab")
        }
    }
}
