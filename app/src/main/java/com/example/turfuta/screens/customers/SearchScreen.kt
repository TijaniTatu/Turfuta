package com.example.turfuta.screens.customers

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.turfuta.AuthViewModel
import com.example.turfuta.Turf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    val turfs by authViewModel.turfs.collectAsState()
    val searchResults by authViewModel.searchResults.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.fetchAllTurfs()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Section
        Text(
            text = "Find Your Turf",
            style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFF04764E)),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Search for the best turf around you",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Input
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by Location", color = Color(0xFF04764E)) },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFF04764E),
                unfocusedIndicatorColor = Color(0xFF04764E),
                cursorColor = Color(0xFF04764E)
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Price Range Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = minPrice,
                onValueChange = { minPrice = it },
                label = { Text("Min Price", color = Color(0xFF04764E)) },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color(0xFF04764E),
                    unfocusedIndicatorColor = Color(0xFF04764E),
                    cursorColor = Color(0xFF04764E)
                ),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            TextField(
                value = maxPrice,
                onValueChange = { maxPrice = it },
                label = { Text("Max Price", color = Color(0xFF04764E)) },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color(0xFF04764E),
                    unfocusedIndicatorColor = Color(0xFF04764E),
                    cursorColor = Color(0xFF04764E)
                ),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authViewModel.searchTurfs(
                    query = searchQuery.text,
                    minPrice = minPrice.toDoubleOrNull(),
                    maxPrice = maxPrice.toDoubleOrNull()
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF04764E)),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Search for Turfs", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Results
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            val results = if (searchQuery.text.isEmpty() && minPrice.isEmpty() && maxPrice.isEmpty()) {
                turfs
            } else {
                searchResults
            }

            if (results.isEmpty()) {
                item {
                    Text(
                        text = "No results found",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                    )
                }
            } else {
                items(results) { turf ->
                    TurfItem(turf = turf) { selectedTurf ->
                        navController.navigate("turfDetails/${selectedTurf.id}") // Navigate to booking screen
                    }
                }
            }
        }
    }
}

@Composable
fun TurfItem(turf: Turf, onClick: (Turf) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(turf) },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl = turf.images.firstOrNull() ?: "" // Default empty string if no image

            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = turf.name,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = turf.name,
                    style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF04764E))
                )
                Text(
                    text = turf.location,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                )
                Text(
                    text = "Price: ${turf.cost}",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary)
                )
            }
        }
    }
}
