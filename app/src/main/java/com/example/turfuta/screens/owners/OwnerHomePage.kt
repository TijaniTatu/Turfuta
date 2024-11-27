package com.example.turfuta.screens.owners

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OwnerHomePage(modifier: Modifier = Modifier) {
    val user = FirebaseAuth.getInstance().currentUser
    val userName = user?.displayName ?: "Owner"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
      Text("Welcome back, $userName", style = MaterialTheme.typography.headlineMedium)

      Spacer(modifier = Modifier.height(16.dp))

       Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth() ){
           StatCard(title = "Today's Bookings", value = "5")
           StatCard(title = "Revenue Today", value = "KES 10,000")
       }

        Spacer(modifier = Modifier.height(16.dp))

        // Notifications
        Text("Notifications")
        NotificationCard(message = "2 pending booking requests") // Replace with dynamic data

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Links
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            QuickLinkButton(label = "Manage Turf", onClick = { /* Navigate */ })
            QuickLinkButton(label = "View Reports", onClick = { /* Navigate */ })
        }

    }

}

@Composable
fun StatCard(title: String, value: String) {
    Card(modifier = Modifier.size(150.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(value, style = MaterialTheme.typography.headlineSmall)
            Text(title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun NotificationCard(message: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(message, modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun QuickLinkButton(label: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(label)
    }
}