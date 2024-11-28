package com.example.turfuta.screens.customers

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.turfuta.AuthViewModel
import com.example.turfuta.Turf

@Composable
fun TurfDetailsScreen(
    navController: NavHostController,
    turfId: String,
    authViewModel: AuthViewModel = viewModel()
) {
    // Fetch turf details using the viewModel
    LaunchedEffect(turfId) {
        authViewModel.getTurfDetails(turfId)
    }

    val turf by authViewModel.turfDetails.collectAsState()

    // Define states for the date and time input
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }

    // Display loading state
    if (turf == null) {
        Text(text = "Loading...", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
        return
    }

    // Display the details of the selected turf
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Turf Image
        val imageUrl = turf?.images?.firstOrNull() ?: ""
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = turf?.name,
            modifier = Modifier
                .size(250.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = turf?.name ?: "Turf Name",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Location: ${turf?.location}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Price: ${turf?.cost}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Available From: ${turf?.timeAvailable}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Description: ${turf?.description}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Date and Time Picker
        OutlinedTextField(
            value = selectedDate ?: "Select Date",
            onValueChange = {},
            label = { Text("Select Date") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .clickable {
                    // Show a date picker dialog
                    // Use Android's DatePickerDialog or a custom implementation
                }
        )

        OutlinedTextField(
            value = selectedTime ?: "Select Time",
            onValueChange = {},
            label = { Text("Select Time") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable {

                }
        )

        // Button to book the turf
        Button(
            onClick = {
                if (selectedDate != null && selectedTime != null) {
                    // Call the updated booking function in the ViewModel
                    authViewModel.bookTurf(
                        turfId,
                        "userId", // Get the current user's ID
                        selectedDate!!,
                        selectedTime!!,  // Pass the selected time
                        (turf?.cost ?: "0.0").toString()
                    )
                } else {
                    // Show error: date or time not selected
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Book This Turf")
        }
    }
}

