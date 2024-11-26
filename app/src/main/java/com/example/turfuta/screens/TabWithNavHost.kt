package com.example.turfuta.screens

import BookingScreen
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.turfuta.AuthViewModel
import com.example.turfuta.Turf
import com.example.turfuta.screens.customers.HistoryScreen
import com.example.turfuta.screens.customers.HomeScreen
import com.example.turfuta.screens.customers.ProfileScreen
import com.example.turfuta.screens.customers.SearchScreen

@Composable
fun TabWithNavHost(tab: String, appNavController: NavHostController) {
    val tabNavController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = tabNavController, startDestination = "$tab/main") {
        composable("$tab/main") {
            when (tab) {
                "home" -> HomeScreen(
                    navController = tabNavController,
                    appNavController = appNavController
                )
                "search" -> SearchScreen(tabNavController)
                "history" -> HistoryScreen(tabNavController)
                "profile" -> ProfileScreen(
                    navController = tabNavController,
                    authViewModel = authViewModel
                )
            }
        }


        composable("$tab/booking/{turfId}") { backStackEntry ->
            val turfId = backStackEntry.arguments?.getString("turfId")
            var turf by remember { mutableStateOf<Turf?>(null) }


            LaunchedEffect(turfId) {
                if (turfId != null) {
                    authViewModel.getTurfById(turfId) { fetchedTurf ->
                        turf = fetchedTurf
                    }
                }
            }


            if (turf != null) {
                BookingScreen(navController = tabNavController, turf = turf!!)
            } else {

                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            }
        }
    }
}


