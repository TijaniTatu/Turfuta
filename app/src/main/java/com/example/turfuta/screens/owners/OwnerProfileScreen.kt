package com.example.turfuta.screens.owners

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun OwnerProfileScreen(modifier: Modifier = Modifier, navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var username by remember { mutableStateOf(TextFieldValue("")) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var profilePhotoUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Load user data from Firestore
    LaunchedEffect(userId) {
        userId?.let {
            db.collection("users").document(it).get().addOnSuccessListener { document ->
                username = TextFieldValue(document.getString("username") ?: "")
                phoneNumber = TextFieldValue(document.getString("phone_number") ?: "")
                profilePhotoUrl = document.getString("profilePhotoUrl") ?: ""
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Photo Section
        Box(modifier = Modifier.size(120.dp)) {
            if (selectedImageUri != null) {
                Image(
                    painter = rememberImagePainter(selectedImageUri),
                    contentDescription = "Selected Profile Photo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = rememberImagePainter(profilePhotoUrl),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Username Input
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username", color = Color(0xFF04764E)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF04764E),
                cursorColor = Color(0xFF04764E)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phone Number Input
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number", color = Color(0xFF04764E)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF04764E),
                cursorColor = Color(0xFF04764E)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save and Cancel Buttons
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { updateUserData(userId, username.text, phoneNumber.text, selectedImageUri, db, storage) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF04764E))
            ) {
                Text("Save", color = Color.White)
            }
            Button(
                onClick = { /* Reset Changes or Navigate Back */ },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
            ) {
                Text("Cancel", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Out Button
        Button(
            onClick = {
                auth.signOut()
                navController.navigate("login")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF04764E))
        ) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Sign Out", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Delete Account Button
        Button(
            onClick = {
                userId?.let { id ->
                    // Delete user document from Firestore
                    db.collection("users").document(id).delete().addOnSuccessListener {
                        // Delete user profile photo from Firebase Storage
                        val photoRef = FirebaseStorage.getInstance().reference.child("profile_photos/$id.jpg")
                        photoRef.delete().addOnSuccessListener {
                            // Delete the user account in Firebase Authentication
                            auth.currentUser?.delete()?.addOnSuccessListener {
                                // Trigger navigation after successful deletion
                                navController.navigate("login")
                            }?.addOnFailureListener { exception ->
                                exception.printStackTrace()
                            }
                        }.addOnFailureListener { exception ->
                            exception.printStackTrace()
                        }
                    }.addOnFailureListener { exception ->
                        exception.printStackTrace()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Account", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Delete Account", color = Color.White)
        }
    }
}

// Function to Update User Data
private fun updateUserData(
    userId: String?,
    username: String,
    phoneNumber: String,
    selectedImageUri: Uri?,
    db: FirebaseFirestore,
    storage: FirebaseStorage
) {
    userId?.let {
        // Update Firestore Data
        val updates = mapOf(
            "username" to username,
            "phone_number" to phoneNumber
        )
        db.collection("users").document(it).update(updates)

        // Upload Profile Photo if Changed
        if (selectedImageUri != null) {
            val photoRef = storage.reference.child("profile_photos/$it.jpg")
            photoRef.putFile(selectedImageUri).addOnSuccessListener {
                photoRef.downloadUrl.addOnSuccessListener { uri ->
                    db.collection("users").document(it.toString()).update("profilePhotoUrl", uri.toString())
                }
            }
        }
    }
}
