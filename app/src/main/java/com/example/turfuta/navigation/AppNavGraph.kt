package com.example.turfuta.navigation


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.turfuta.SplashScreen
import com.example.turfuta.screens.LoginPage
import androidx.compose.ui.Modifier
import com.example.turfuta.AuthViewModel
import com.example.turfuta.screens.BottomNavigationBar
import com.example.turfuta.screens.customers.HistoryScreen
import com.example.turfuta.screens.HomePage
import com.example.turfuta.screens.customers.ProfileScreen
import com.example.turfuta.screens.customers.SearchScreen


import com.example.turfuta.screens.SignupPage
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavGraph(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()


    var currentRoute by remember { mutableStateOf("login") }


    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            currentRoute = "login"
            LoginPage(modifier, navController, authViewModel)
        }

        composable("signup") {
            currentRoute = "signup"
            SignupPage(modifier, navController, authViewModel)
        }

        composable("home") {
            currentRoute = "home"
            HomePage(navController)
        }

        composable("search") {
            currentRoute = "search"
            SearchScreen()
        }

        composable("history") {
            currentRoute = "history"
            HistoryScreen()
        }

        composable("profile") {
            currentRoute = "profile"
            ProfileScreen()
        }

        composable("profile") {
            ProfilePage(modifier)
        }

        composable("splash") {
            SplashScreen(onTimeout = {
                // Navigate to the login page from splash
                navController.navigate("login") {
                    // Pop up to clear the splash screen from the back stack
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
    }


    if (currentRoute != "login" && currentRoute != "signup") {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController, currentRoute = currentRoute)
            }
        ) { innerPadding ->

            Surface(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

                when (currentRoute) {
                    "home" -> HomePage(navController)
                    "search" -> SearchScreen()
                            "history" -> HistoryScreen()
                    "profile" -> ProfileScreen()
                    "login" -> LoginPage(modifier, navController, authViewModel)
                    else -> {}
                }
            }
        }
    } else {

        when (currentRoute) {
            "login" -> LoginPage(modifier, navController, authViewModel)
            "signup" -> SignupPage(modifier, navController, authViewModel)
        }
    }
}



