package com.example.turfuta.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfilePage(modifier: Modifier = Modifier) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var profilePhotoUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("Loading...") }
    var userType by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null){
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    profilePhotoUrl = document.getString("profilePhotoUrl").toString() ?: ""
                    username = document.getString("username").toString() ?: "Unknown"
                    userType = document.getString("userType").toString() ?: "Unkown"
                }
                .addOnFailureListener{
                    username = "Error"
                    userType = "Error"
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        if (profilePhotoUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(profilePhotoUrl),
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(8.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }else {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "No Image",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }

        //Username
        Text(
            text = username,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        //UserType
        Text(
            text = when (userType) {
                "turf_owner" -> "Turf Owner"
                "looking_for_turf" -> "Looking for Turf"
                else -> "Unknown"
            },
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

}