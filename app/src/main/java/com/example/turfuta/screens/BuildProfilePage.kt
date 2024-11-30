package com.example.turfuta.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.turfuta.AuthViewModel

@Composable
fun BuildProfilePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    var username by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var phone_number by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Photo Section
        Card(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, Color(0xFF04764E), CircleShape)
                .padding(2.dp)
        ) {
            if (photoUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(photoUri),
                    contentDescription = "Selected photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder Image
                Image(
                    painter = rememberAsyncImagePainter("https://via.placeholder.com/120"),
                    contentDescription = "Placeholder image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Select Photo Button
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF04764E))
        ) {
            Text("Select Profile Photo", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Username Input
        Card(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            TextField(
                value = phone_number,
                onValueChange = { phone_number = it },
                label = { Text("Phone Number +254") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Type Selection - Chips for better UX
        Text("Select User Type", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val userTypes = listOf("owner", "footballer")
            userTypes.forEach { type ->
                Chip(
                    label = type,
                    selected = userType == type,
                    onClick = { userType = type },
                    backgroundColor = if (userType == type) Color(0xFF04764E) else Color.Gray,
                    contentColor = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save Profile Button
        Button(
            onClick = {
                if (username.isNotEmpty() && userType.isNotEmpty()) {
                    authViewModel.buildProfile(username, photoUri, userType, phone_number) { success, error ->
                        if (success) {
                            Toast.makeText(context, "Profile created successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate("home")
                        } else {
                            Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = username.isNotEmpty() && userType.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF04764E))
        ) {
            Text("Build Profile", color = Color.White)
        }
    }
}

@Composable
fun Chip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    backgroundColor: Color,
    contentColor: Color
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = backgroundColor,
        contentColor = contentColor,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
