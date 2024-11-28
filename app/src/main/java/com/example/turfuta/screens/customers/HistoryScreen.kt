package com.example.turfuta.screens.customers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.turfuta.AuthViewModel
import com.example.turfuta.Booking

@Composable
fun HistoryScreen(navController: NavHostController, authViewModel: AuthViewModel = viewModel()) {
    val userId = "userId"
    LaunchedEffect(Unit) {
        authViewModel.fetchUserBookings(userId)
    }

    val bookings by authViewModel.userBookings.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Booking History",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            if (bookings.isEmpty()) {
                Text(
                    text = "No bookings found.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize().padding(top = 32.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(bookings) { booking ->
                        BookingItem(booking)
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItem(booking: Booking) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Turf ID: ${booking.turfId}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Date: ${booking.bookingDate}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Time: ${booking.bookingTime}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Cost: ${booking.cost}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}