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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    ) {
        // Welcome Message
        Text(
            text = "Welcome back, $userName!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Quick Stats Section
        Text(
            text = "Quick Stats",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard(title = "Today's Bookings", value = "8")
            StatCard(title = "Revenue Today", value = "KES 12,500")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notifications Section
        Text(
            text = "Notifications",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        NotificationCard(message = "3 pending booking requests.")
        NotificationCard(message = "Your turf's availability has been updated successfully.")

        Spacer(modifier = Modifier.height(16.dp))

        // Actionable Insights Section
        Text(
            text = "Actionable Insights",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            ActionCard(
                title = "Pending Requests",
                subtitle = "Approve bookings",
                onClick = { /* Navigate to Pending Requests */ }
            )
            ActionCard(
                title = "View Calendar",
                subtitle = "Check availability",
                onClick = { /* Navigate to Calendar */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Links Section
        Text(
            text = "Quick Links",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickLinkButton(label = "Manage Turf", onClick = { /* Navigate */ })
            QuickLinkButton(label = "View Reports", onClick = { /* Navigate */ })
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .size(150.dp)
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF04764E))
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun NotificationCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = { /* Navigate to notifications */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF04764E))
            ) {
                Text("View", color = Color.White)
            }
        }
    }
}

@Composable
fun ActionCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(150.dp)
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF04764E))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF04764E))
            ) {
                Text("Go", color = Color.White)
            }
        }
    }
}

@Composable
fun QuickLinkButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF04764E))
    ) {
        Text(label, color = Color.White)
    }
}
