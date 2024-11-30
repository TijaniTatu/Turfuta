package com.example.turfuta.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.turfuta.AuthViewModel
import com.example.turfuta.SplashScreen
import com.example.turfuta.pages.ProfilePage
import com.example.turfuta.screens.BuildProfilePage
import com.example.turfuta.screens.LoginPage
import com.example.turfuta.screens.SignupPage
import com.example.turfuta.screens.customers.HomePage
import com.example.turfuta.screens.customers.TurfDetailsScreen
import com.example.turfuta.screens.owners.OwnerHomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()

    // Navigation host setup
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Splash Screen
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("login") {
                    // Pop up to clear the splash screen from the back stack
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        // Login Page
        composable("login") {
            // Check if user is already logged in
            if (authViewModel.isUserLoggedIn) {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                LoginPage(navController = navController, authViewModel = authViewModel)
            }
        }


        // Signup Page
        composable("signup") {
            SignupPage(navController = navController, authViewModel = authViewModel)
        }

        composable("home") {
            HomePage(navController = navController, authViewModel = authViewModel)
        }

        composable("buildprofile") {
            BuildProfilePage(navController = navController, authViewModel = authViewModel)
        }

        // Profile Page
        composable("profile") {
            ProfilePage()
        }

        composable("ownerhome") {
            OwnerHomeScreen(navController = navController)
        }
        // Turf Details Screen (with dynamic turfId)
        composable("turfDetails/{turfId}") { backStackEntry ->
            val turfId = backStackEntry.arguments?.getString("turfId")
            turfId?.let {
                TurfDetailsScreen(navController = navController, turfId = it)
            }
        }
    }
}
