package com.aureliajets.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.aureliajets.data.entity.Booking
import com.aureliajets.ui.components.AureliaBackground
import com.aureliajets.ui.components.BackButton
import com.aureliajets.ui.sampleJets
import com.aureliajets.viewmodel.AureliaViewModel

@Composable
fun ProfileScreen(
    viewModel: AureliaViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val bookings by (viewModel.getBookingsForCurrentUser()
        ?: kotlinx.coroutines.flow.flowOf(emptyList()))
        .collectAsState(initial = emptyList())

    AureliaBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 8.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BackButton(onClick = onBack)
                Text(
                    "My Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(onClick = onLogout) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = Color.White
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar circle
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFE91E8C), Color(0xFF1565C0))
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    user?.firstName?.firstOrNull()?.uppercase() ?: "?",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "${user?.firstName ?: ""} ${user?.lastName ?: ""}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D1B3E)
                            )
                            Text(
                                "@${user?.username ?: ""}",
                                fontSize = 14.sp,
                                color = Color(0xFF1565C0),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                ProfileDetail("Phone", user?.phoneNumber ?: "—")
                                ProfileDetail("CNIC", user?.cnic ?: "—")
                                ProfileDetail("Flights", bookings.size.toString())
                            }
                        }
                    }
                }

                // Booking history header
                if (bookings.isNotEmpty()) {
                    item {
                        Text(
                            "Booking History",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    items(bookings) { booking ->
                        BookingHistoryCard(booking)
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                        ) {
                            Text(
                                "No bookings yet.\nBook your first flight! ✈",
                                modifier = Modifier.padding(24.dp),
                                fontSize = 15.sp,
                                color = Color.White,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun ProfileDetail(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = Color.Gray)
        Text(
            value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0D1B3E)
        )
    }
}

@Composable
private fun BookingHistoryCard(booking: Booking) {
    val jetInfo = sampleJets.find { it.name == booking.jetName }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.88f)),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    jetInfo?.let {
                        Image(
                            painter = painterResource(id = it.imageRes),
                            contentDescription = it.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    Text(
                        booking.jetName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D1B3E)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1565C0).copy(alpha = 0.1f)
                ) {
                    Text(
                        "#${booking.id.toString().padStart(4, '0')}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 11.sp,
                        color = Color(0xFF1565C0),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(booking.fromLocation, fontSize = 13.sp, color = Color(0xFF333366), fontWeight = FontWeight.Medium)
                Text("  ✈  ", fontSize = 13.sp, color = Color(0xFF1565C0))
                Text(booking.toLocation, fontSize = 13.sp, color = Color(0xFF333366), fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    booking.departureDate + if (booking.returnDate.isNotBlank()) " → ${booking.returnDate}" else "",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    "${booking.seats} seat(s) · ${booking.flightClass}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            if (booking.rating > 0) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "★".repeat(booking.rating) + "☆".repeat(5 - booking.rating),
                    fontSize = 13.sp,
                    color = Color(0xFFFFD700)
                )
            }
        }
    }
}
