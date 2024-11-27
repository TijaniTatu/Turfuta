package com.example.turfuta.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.turfuta.AuthViewModel
import com.example.turfuta.SplashScreen
import com.example.turfuta.pages.ProfilePage
import com.example.turfuta.screens.LoginPage
import com.example.turfuta.screens.SignupPage
import com.example.turfuta.screens.HomePage
import com.example.turfuta.screens.owners.OwnerHomeScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
){
    val navController = rememberNavController()

    // Navigation host setup
    NavHost(
        navController = navController,
        startDestination = "splash"
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
            LoginPage(navController = navController, authViewModel = authViewModel)
        }

        // Signup Page
        composable("signup") {
            SignupPage(navController = navController, authViewModel = authViewModel)
        }

        // Home Page with Pager and BottomNavigation
        composable("home") {
            HomePage()
        }

        // Profile Page
        composable("profile") {
            ProfilePage()
        }

        composable("ownerhome") {
            OwnerHomeScreen(navController = navController)
        }
    }
}
