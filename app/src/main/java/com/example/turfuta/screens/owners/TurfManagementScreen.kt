package com.example.turfuta.screens.owners

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlin.math.cos

@Composable
fun TurfManagementScreen(modifier: Modifier = Modifier) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val userId = auth.currentUser?.uid
    var turfExists by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    fun setTurfExists() {
        turfExists = true
    }

    LaunchedEffect(userId) {
        if (userId != null) {
            val turfDoc = db.collection("turfs").document(userId).get().await()
            turfExists = turfDoc.exists()
            loading = false
        }
    }

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        if (turfExists) {
            ManageTurfScreen(userId!!, db, storage)
        } else {
            CreateTurfScreen(userId!!, db, storage, ::setTurfExists) // Pass the function reference
        }
    }
}


@Composable
fun CreateTurfScreen(
    userId: String,
    db: FirebaseFirestore,
    storage: FirebaseStorage,
    setTurfExists: () -> Unit // Pass as a callback
) {
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var location by remember { mutableStateOf(TextFieldValue("")) }
    var timeAvailable by remember { mutableStateOf(TextFieldValue("")) }
    var images by remember { mutableStateOf<List<Uri>>(emptyList()) } // Handle image URIs here

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text("Seems you do not have a turf registered yet, let's get you started")
        }

        item {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Turf Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            TextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            TextField(
                value = timeAvailable,
                onValueChange = { timeAvailable = it },
                label = { Text("Time Available") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Button(
                onClick = {
                    // Upload to Firestore
                    db.collection("turfs").document(userId).set(
                        hashMapOf(
                            "name" to name.text,
                            "description" to description.text,
                            "location" to location.text,
                            "timeAvailable" to timeAvailable.text,
                            "images" to images.map { it.toString() } // Convert URIs to strings
                        )
                    ).addOnSuccessListener {
                        setTurfExists() // Call after successful creation
                    }.addOnFailureListener {
                        // Handle failure
                        println("Failed to create turf: ${it.message}")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Turf")
            }
        }
    }
}

@Composable
fun ManageTurfScreen(userId: String, db: FirebaseFirestore, storage: FirebaseStorage) {
    var availability by remember { mutableStateOf(false) }
    var images by remember { mutableStateOf<List<String>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var turfName by remember { mutableStateOf("") }
    var timesAvailable by remember { mutableStateOf("" ) }
    var cost by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    // For image selection
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploadImage(userId, uri, db, storage) { newImageUrl ->
                images = images + newImageUrl
            }
        }
    }

    LaunchedEffect(userId) {
        val turfDoc = db.collection("turfs").document(userId).get().await()
        availability = turfDoc.getBoolean("availability") ?: false
        images = turfDoc.get("images") as? List<String> ?: emptyList()
        turfName = turfDoc.getString("name") ?: "Unkown"
        timesAvailable = turfDoc.getString("timeAvailable") ?: "00:00"
        cost = turfDoc.getString("cost") ?: "0"
        location = turfDoc.getString("location") ?: "Unknown"
        loading = false
    }

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Name: $turfName", style = MaterialTheme.typography.h6)
                    Text(text = "Location: $location", style = MaterialTheme.typography.body1)
                }
            }

            item{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Card(
                        modifier = Modifier.weight(1f).padding(8.dp),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Time Available", style = MaterialTheme.typography.h6)
                            Text(text = timesAvailable, style = MaterialTheme.typography.body2)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f).padding(8.dp),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Price", style = MaterialTheme.typography.h6)
                            Text(text = cost, style = MaterialTheme.typography.body2)
                        }
                    }
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = availability,
                        onCheckedChange = { isAvailable ->
                            availability = isAvailable
                            db.collection("turfs").document(userId)
                                .update("availability", isAvailable)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Available: ${if (availability) "Yes" else "No"}")
                }
            }

            if (images.isNotEmpty()) {
                items(images) { imageUrl ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        elevation = 4.dp
                    ) {
                        Image(
                            painter = rememberImagePainter(imageUrl),
                            contentDescription = "Turf Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            } else {
                item {
                    Text(
                        "No images uploaded yet. Add some images to showcase your turf.",
                        color = Color.Gray
                    )
                }
            }

            item {
                Button(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Photos")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Photos")
                }
            }

            item {
                Button(
                    onClick = {
                        // Update turf details logic (if needed)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Details")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Details")
                }
            }
        }
    }
}

// Function to upload image to Firebase Storage and update Firestore
private fun uploadImage(
    userId: String,
    imageUri: Uri,
    db: FirebaseFirestore,
    storage: FirebaseStorage,
    onUploadSuccess: (String) -> Unit
) {
    val storageRef = storage.reference.child("turfs/$userId/${imageUri.lastPathSegment}")
    storageRef.putFile(imageUri).addOnSuccessListener {
        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
            // Update Firestore with new image URL
            db.collection("turfs").document(userId).update(
                "images",
                FieldValue.arrayUnion(downloadUrl.toString())
            ).addOnSuccessListener {
                onUploadSuccess(downloadUrl.toString())
            }
        }
    }.addOnFailureListener {
        // Handle upload failure
    }
}
