package com.example.turfuta.screens.customers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController


@Composable
fun SearchScreen(navController: NavHostController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            Text("Welcome to the Search Screen!")
            Button(onClick = { navController.navigate("home/details") }) {
                Text("Go to Search Screen")
            }
        }
    }
}