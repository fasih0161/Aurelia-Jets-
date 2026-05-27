package com.aureliajets.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.aureliajets.ui.components.*
import com.aureliajets.ui.sampleJets
import com.aureliajets.viewmodel.AureliaViewModel
import com.aureliajets.viewmodel.BookingState
import java.util.Calendar

@Composable
fun BookingDetailsScreen(
    jetIndex: Int,
    viewModel: AureliaViewModel,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val jet = sampleJets.getOrNull(jetIndex) ?: return

    var cardHolder by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    val bookingState by viewModel.bookingState.collectAsState()
    val fromLocation by viewModel.fromLocation.collectAsState()
    val toLocation by viewModel.toLocation.collectAsState()
    val departureDate by viewModel.departureDate.collectAsState()
    val returnDate by viewModel.returnDate.collectAsState()
    val seats by viewModel.seats.collectAsState()
    val flightClass by viewModel.flightClass.collectAsState()

    LaunchedEffect(bookingState) {
        when (bookingState) {
            is BookingState.Success -> { viewModel.resetBookingState(); onConfirm() }
            is BookingState.Error -> errorMsg = (bookingState as BookingState.Error).message
            else -> {}
        }
    }

    AureliaBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onClick = onBack)
                Text(
                    "Booking Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Flight summary card
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = jet.imageRes),
                            contentDescription = jet.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                jet.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D1B3E)
                            )
                            Text(
                                "${jet.pricePerHour} / Hour",
                                fontSize = 14.sp,
                                color = Color(0xFF1565C0),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("FROM", fontSize = 10.sp, color = Color.Gray)
                            Text(fromLocation.ifBlank { "—" }, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0D1B3E))
                        }
                        Text("✈", fontSize = 18.sp, color = Color(0xFF1565C0))
                        Column(horizontalAlignment = Alignment.End) {
                            Text("TO", fontSize = 10.sp, color = Color.Gray)
                            Text(toLocation.ifBlank { "—" }, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0D1B3E))
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    HorizontalDivider(color = Color(0xFFDDDDDD))
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LabelValue("Departure", departureDate.ifBlank { "—" })
                        LabelValue("Return", returnDate.ifBlank { "—" })
                        LabelValue("Seats", seats)
                        LabelValue("Class", flightClass)
                    }
                }

                Text(
                    "Payment Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                AureliaTextField(
                    value = cardHolder,
                    onValueChange = { input ->
                        if (input.all { it.isLetter() || it.isWhitespace() }) {
                            cardHolder = input
                            errorMsg = ""
                        }
                    },
                    placeholder = "Card Holder Name"
                )

                AureliaTextField(
                    value = cardNumber,
                    onValueChange = { raw ->
                        val digits = raw.filter { it.isDigit() }.take(16)
                        cardNumber = digits
                        errorMsg = ""
                    },
                    placeholder = "Card Number  (16 digits)",
                    visualTransformation = CreditCardVisualTransformation()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExpiryDateField(
                        value = expiry,
                        onValueChange = { expiry = it; errorMsg = "" },
                        modifier = Modifier.weight(1f)
                    )
                    AureliaTextField(
                        value = cvv,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) { cvv = it; errorMsg = "" } },
                        placeholder = "CVV",
                        modifier = Modifier.weight(1f),
                        isPassword = true
                    )
                }

                if (errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = Color.Yellow, fontSize = 13.sp)
                }

                AureliaButton(
                    text = if (bookingState is BookingState.Loading) "Processing..." else "Confirm Booking",
                    onClick = {
                        val month = if (expiry.length >= 2) expiry.substring(0, 2).toIntOrNull() ?: 0 else 0
                        val year = if (expiry.length == 4) expiry.substring(2, 4).toIntOrNull() ?: 0 else 0
                        val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
                        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

                        when {
                            cardHolder.isBlank() -> errorMsg = "Card holder name is required"
                            cardNumber.length < 16 -> errorMsg = "Enter a valid 16-digit card number"
                            expiry.length < 4 -> errorMsg = "Enter expiry as MM/YY"
                            month !in 1..12 -> errorMsg = "Invalid month (01-12)"
                            year < currentYear -> errorMsg = "Card has expired (Year)"
                            year == currentYear && month < currentMonth -> errorMsg = "Card has expired (Month)"
                            cvv.length < 3 -> errorMsg = "CVV must be 3–4 digits"
                            else -> viewModel.createBooking(
                                jetName = jet.name,
                                pricePerHour = jet.pricePerHour,
                                cardHolder = cardHolder,
                                cardNumber = cardNumber,
                                expiry = "${expiry.substring(0, 2)}/${expiry.substring(2)}",
                                cvv = cvv
                            )
                        }
                    },
                    enabled = bookingState !is BookingState.Loading
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = Color.Gray)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0D1B3E))
    }
}
