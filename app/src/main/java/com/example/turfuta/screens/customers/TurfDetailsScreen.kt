package com.example.turfuta.screens.customers

import android.annotation.SuppressLint
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
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
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

    // Define states for manual date and time input
    var manualDate by remember { mutableStateOf("") }
    var manualTime by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Display loading state
    if (turf == null) {
        Text(text = "Loading...", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar with back button
        TopAppBar(
            title = { Text("Turf Details") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

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

        // Manual Date Input
        OutlinedTextField(
            value = manualDate,
            onValueChange = { manualDate = it },
            label = { Text("Enter Date (yyyy-MM-dd)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Manual Time Input
        OutlinedTextField(
            value = manualTime,
            onValueChange = { manualTime = it },
            label = { Text("Enter Time (HH:mm)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Button to book the turf
        Button(
            onClick = {
                val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
                val timeRegex = Regex("""\d{2}:\d{2}""")

                if (!manualDate.matches(dateRegex) || !manualTime.matches(timeRegex)) {
                    Toast.makeText(context, "Invalid date or time format!", Toast.LENGTH_LONG).show()
                    return@Button
                }

                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                if (currentUserId == null) {
                    Toast.makeText(context, "You need to log in first!", Toast.LENGTH_LONG).show()
                    return@Button
                }

                // Book the turf using the valid input values
                authViewModel.bookTurf(
                    turfId,
                    currentUserId,
                    manualDate,
                    manualTime,
                    (turf?.cost ?: "0.0").toString()
                )

                showSuccessDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Book This Turf")
        }
    }

    // Success Alert Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false

                    navController.navigate("Home") {
                        popUpTo("TurfDetailsRoute") { inclusive = true }
                    }
                }) {
                    Text("OK")
                }
            },
            title = { Text("Booking Successful") },
            text = { Text("Your turf booking has been confirmed!") }
        )
    }
}
