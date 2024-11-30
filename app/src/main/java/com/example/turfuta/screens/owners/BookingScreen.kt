package com.example.turfuta.screens.owners

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun BookingScreen(modifier: Modifier = Modifier) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var ownerUid by remember { mutableStateOf<String?>(null) }
    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch current user ID
    LaunchedEffect(currentUser) {
        ownerUid = currentUser?.uid
        if (ownerUid != null) {
            bookings = fetchBookingsForOwner(ownerUid!!)
            isLoading = false
        }
    }

    // UI
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF04764E))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(bookings) { booking ->
                BookingCard(
                    booking = booking,
                    onCancel = { cancelBooking(booking.id) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: Booking,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* Add navigation or actions if needed */ },
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "Booking ID: ${booking.id}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF04764E)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Customer Name: ${booking.customerName}")
            Text(text = "Date: ${booking.date}")
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF04764E))
            ) {
                Text(text = "Cancel Booking", color = Color.White)
            }
        }
    }
}

// Data class for booking
data class Booking(
    val id: String,
    val customerName: String,
    val date: String,
    val phone: String
)

// Fetch bookings from Firebase
suspend fun fetchBookingsForOwner(ownerUid: String): List<Booking> {
    val firestore = FirebaseFirestore.getInstance()
    val snapshot = firestore.collection("bookings")
        .whereEqualTo("turfId", ownerUid)
        .get()
        .await()

    return snapshot.documents.map { doc ->
        Booking(
            id = doc.id,
            customerName = doc.getString("userName") ?: "Unknown",
            date = doc.getString("bookingDate") ?: "Unknown",
            phone = doc.getString("userPhone") ?: "Unknown"
        )
    }
}

// Cancel booking in Firebase
fun cancelBooking(bookingId: String) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("bookings").document(bookingId).delete()
}
