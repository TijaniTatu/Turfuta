package com.example.turfuta.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.turfuta.SplashScreen
import com.example.turfuta.MainScreen
import com.example.turfuta.pages.LoginPage
import androidx.compose.ui.Modifier
import com.example.turfuta.AuthViewModel
import com.example.turfuta.pages.BuildProfilePage


import com.example.turfuta.pages.SignupPage
@Composable
fun AppNavGraph(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") { // Start at splash
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }

        composable("signup") {
            SignupPage(modifier, navController, authViewModel)
        }

        composable("home") {
            HomePage(navController) // Pass the navController
        }

        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
    }
}