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
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurfDetailsScreen(
    navController: NavHostController,
    turfId: String,
    authViewModel: AuthViewModel = viewModel()
) {
    // Current user information
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    var userName by remember { mutableStateOf<String?>(null) }
    var userPhone by remember { mutableStateOf<String?>(null) }

    // Fetch user details
    LaunchedEffect(currentUser) {
        val userId = currentUser?.uid
        if (userId != null) {
            val userSnapshot = firestore.collection("users").document(userId).get().await()
            userName = userSnapshot.getString("username")
            userPhone = userSnapshot.getString("phone_number")
        }
    }

    // Fetch turf details
    LaunchedEffect(turfId) {
        authViewModel.getTurfDetails(turfId)
    }

    val turf by authViewModel.turfDetails.collectAsState()

    // Booking states
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Date and time picker setup
    val calendar = Calendar.getInstance()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Surface(
                shape = RoundedCornerShape(
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                color = Color(0xFF04764E), // Correct color for the background
                shadowElevation = 8.dp
            ) {
                TopAppBar(
                    title = { Text("Turf Details", style = MaterialTheme.typography.bodySmall) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF04764E))
                )
            }
        },
        content = { paddingValues ->
            if (turf == null) {
                // Loading state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF04764E))
                }
                return@Scaffold
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues),
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
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color(0xFF04764E)
                )
                Text(
                    text = "Location: ${turf?.location}",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Price: ${turf?.cost}",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Available From: ${turf?.timeAvailable}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Description: ${turf?.description}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (turf?.availability == false) {
                    Text(
                        text = "This turf is currently not available for booking.",
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.error),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    // Date Picker
                    Text(
                        text = selectedDate ?: "Select Date",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF04764E)),
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

                    // Time Picker
                    Text(
                        text = selectedTime ?: "Select Time",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF04764E)),
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

                    // Booking Button
                    Button(
                        onClick = {
                            if (selectedDate != null && selectedTime != null && userName != null && userPhone != null) {
                                authViewModel.bookTurf(
                                    turfId,
                                    currentUser?.uid ?: "",
                                    selectedDate!!,
                                    selectedTime!!,
                                    (turf?.cost ?: "0.0").toString(),
                                    userName!!,
                                    userPhone!!
                                )
                                showSuccessDialog = true
                            } else {
                                Toast.makeText(context, "Please complete all fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF04764E)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(12.dp) // Rounded corners for the button
                    ) {
                        Text("Book This Turf", color = Color.White)
                    }
                }
            }

            // Success Dialog
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
                    title = { Text("Booking Successful", style = MaterialTheme.typography.bodySmall) },
                    text = { Text("Your booking was successful. Returning to the homepage.") }
                )
            }
        }
    )
}
