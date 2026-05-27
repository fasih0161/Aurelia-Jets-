package com.aureliajets.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.aureliajets.ui.Routes
import com.aureliajets.ui.components.AureliaBackground
import com.aureliajets.ui.components.AureliaButton
import com.aureliajets.ui.components.GlassCard
import com.aureliajets.ui.sampleJets
import com.aureliajets.viewmodel.AureliaViewModel

@Composable
fun BookingSuccessScreen(
    viewModel: AureliaViewModel,
    onGenerateTicket: () -> Unit,
    onContinue: () -> Unit
) {
    val lastBooking by viewModel.lastBooking.collectAsState()

    AureliaBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .border(3.dp, Color(0xFF00BCD4), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color(0xFF00BCD4),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Booking Confirmed!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Your private jet is scheduled.",
                fontSize = 14.sp,
                color = Color(0xFF42A5F5),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Booking summary card
            lastBooking?.let { booking ->
                val jetInfo = sampleJets.find { it.name == booking.jetName }
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        jetInfo?.let {
                            Image(
                                painter = painterResource(id = it.imageRes),
                                contentDescription = it.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(Modifier.width(12.dp))
                        }
                        Column {
                            Text(
                                booking.jetName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D1B3E)
                            )
                            Text(
                                "Ref #${booking.id.toString().padStart(6, '0')}",
                                fontSize = 12.sp,
                                color = Color(0xFF1565C0),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryItem("From", booking.fromLocation)
                        Text("✈", fontSize = 18.sp, color = Color(0xFF1565C0))
                        SummaryItem("To", booking.toLocation, Alignment.End)
                    }
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFDDDDDD))
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryItem("Departs", booking.departureDate)
                        if (booking.returnDate.isNotBlank()) {
                            SummaryItem("Returns", booking.returnDate)
                        }
                        SummaryItem("Seats", booking.seats)
                        SummaryItem("Class", booking.flightClass)
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            AureliaButton(text = "Generate Ticket", onClick = onGenerateTicket)
            Spacer(Modifier.height(12.dp))
            AureliaButton(
                text = "Rate Your Experience",
                onClick = onContinue,
                containerColor = Color.White.copy(alpha = 0.1f),
                contentColor = Color.White
            )
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(horizontalAlignment = alignment) {
        Text(label, fontSize = 10.sp, color = Color.Gray)
        Text(value.ifBlank { "—" }, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0D1B3E))
    }
}
