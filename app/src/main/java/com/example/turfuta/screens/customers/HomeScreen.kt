package com.example.turfuta.screens.customers

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.turfuta.AuthViewModel
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.turfuta.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val username = authViewModel.username.value ?: "Guest"
    val featuredTurfs = authViewModel.turfs.value
    val pendingBookings by authViewModel.userBookings.collectAsState()

    // Trigger the fetch operations on the first composition or when userId changes
    LaunchedEffect(key1 = FirebaseAuth.getInstance().currentUser?.uid) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        authViewModel.fetchAllTurfs()
        authViewModel.fetchPendingBookings(userId = currentUserId)
    }

    Scaffold(
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (pendingBookings.isNotEmpty()) {
                            PendingBookingCard(
                                booking = pendingBookings.first(),
                                onClick = { navController.navigate("history") }
                            )
                        } else {
                            Text(
                                text = "No pending bookings.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                    }
                }

                // Featured Turfs Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Featured Turfs",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (featuredTurfs.isNotEmpty()) {
                            val pagerState = rememberPagerState(pageCount = { featuredTurfs.size })

                            HorizontalPager(state = pagerState) { page ->
                                val turf = featuredTurfs[page]
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    if (turf.images.isNotEmpty()) {
                                        Image(
                                            painter = rememberAsyncImagePainter(turf.images[0]),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(250.dp)
                                                .clip(MaterialTheme.shapes.medium),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = turf.name,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp
                                        ),
                                        modifier = Modifier.padding(top = 8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Location: ${turf.location}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )

                                    Text(
                                        text = "Cost: ${turf.cost}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )

                                    Text(
                                        text = "Available: ${turf.timeAvailable}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(pagerState.pageCount) { iteration ->
                                    val color =
                                        if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                                    Box(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .size(12.dp)
                                    )
                                }
                            }
                        } else {
                            Text("Loading featured turfs...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun PendingBookingCard(booking: Booking, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)) // Light background for contrast
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title with accent color
            Text(
                text = "Pending Booking",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF04764E)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Booking details
            Text(
                text = "Turf: ${booking.turfId}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF333333)
            )
            Text(
                text = "Date: ${booking.bookingDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF333333)
            )
            Text(
                text = "Time: ${booking.bookingTime}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF333333)
            )
            Text(
                text = "Cost: ${booking.cost}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button with accent color
            Button(
                onClick = onClick,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF04764E),
                    contentColor = Color.White
                )
            ) {
                Text("View All Bookings")
            }
        }
    }
}
