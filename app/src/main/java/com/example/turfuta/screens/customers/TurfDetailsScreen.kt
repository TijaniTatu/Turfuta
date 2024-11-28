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
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurfDetailsScreen(
    navController: NavHostController,
    turfId: String,
    authViewModel: AuthViewModel = viewModel()
) {

    LaunchedEffect(turfId) {
        authViewModel.getTurfDetails(turfId)
    }

    val turf by authViewModel.turfDetails.collectAsState()

    // Define states for the date and time input
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Get current date and time for picker dialogs
    val calendar = Calendar.getInstance()

    // Access the context
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Turf Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            if (turf == null) {
                // Display loading state
                Text(
                    text = "Loading...",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    textAlign = TextAlign.Center
                )
                return@Scaffold
            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues), // Adjust content based on top bar padding
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

                if (turf?.availability == false) {

                    Text(
                        text = "This turf is currently not available for booking.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {

                    Text(
                        text = selectedDate ?: "Select Date",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        calendar.set(year, month, dayOfMonth)
                                        selectedDate = sdf.format(calendar.time)
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                    )


                    Text(
                        text = selectedTime ?: "Select Time",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clickable {
                                TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    false
                                ).show()
                            }
                    )

                    // Button to book the turf
                    Button(
                        onClick = {
                            if (selectedDate != null && selectedTime != null) {
                                authViewModel.bookTurf(
                                    turfId,
                                    "userId",
                                    selectedDate!!,
                                    selectedTime!!,
                                    (turf?.cost ?: "0.0").toString()
                                )
                                // Show success dialog
                                showSuccessDialog = true
                            } else {
                                Toast.makeText(context, "Please select both date and time", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Book This Turf")
                    }
                }
            }


            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    confirmButton = {
                        Button(onClick = {
                            showSuccessDialog = false
                            navController.navigate("home")
                        }) {
                            Text("OK")
                        }
                    },
                    title = { Text("Booking Successful") },
                    text = { Text("Your booking was successful. Returning to the homepage.") }
                )
            }
        }
    )
}
