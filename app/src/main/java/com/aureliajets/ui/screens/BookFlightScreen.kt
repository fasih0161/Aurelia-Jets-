package com.aureliajets.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aureliajets.ui.components.*
import com.aureliajets.viewmodel.AureliaViewModel
import java.util.Calendar

@Composable
fun BookFlightScreen(
    viewModel: AureliaViewModel,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    onProfile: () -> Unit
) {
    val fromLocation by viewModel.fromLocation.collectAsState()
    val toLocation by viewModel.toLocation.collectAsState()
    val departureDate by viewModel.departureDate.collectAsState()
    val returnDate by viewModel.returnDate.collectAsState()
    val seats by viewModel.seats.collectAsState()
    val flightClass by viewModel.flightClass.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    var errorMsg by remember { mutableStateOf("") }

    val oneDayMillis = 86400000L

    val departureMinDateMillis = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, 1) // Departure must be tomorrow onwards
        }.timeInMillis
    }

    // Parse departure date to millis for return date min (Strictly Greater than departure)
    val returnMinDateMillis = remember(departureDate) {
        if (departureDate.length == 10) {
            try {
                val parts = departureDate.split("/")
                Calendar.getInstance().apply {
                    set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    add(Calendar.DAY_OF_YEAR, 1) // Must be at least the next day after departure
                }.timeInMillis
            } catch (e: Exception) { departureMinDateMillis + oneDayMillis }
        } else departureMinDateMillis + oneDayMillis
    }

    val seatOptions = (1..20).map { it.toString() }
    val classOptions = listOf("Economy", "Business", "First Class", "Private Suite")

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
                user?.let {
                    IconButton(onClick = onProfile) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column {
                    Text(
                        "Hello, ${user?.firstName ?: "Traveller"} ✈",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        "Let's book your\nnext flight",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                AirportSearchField(
                    value = fromLocation,
                    onValueChange = { viewModel.fromLocation.value = it; errorMsg = "" },
                    onAirportSelected = { viewModel.fromLocation.value = it.display },
                    placeholder = "From — City or Airport",
                    isOrigin = true
                )

                AirportSearchField(
                    value = toLocation,
                    onValueChange = { viewModel.toLocation.value = it; errorMsg = "" },
                    onAirportSelected = { viewModel.toLocation.value = it.display },
                    placeholder = "To — City or Airport",
                    isOrigin = false
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DatePickerField(
                        value = departureDate,
                        onDateSelected = { 
                            viewModel.departureDate.value = it
                            // If return date exists and is no longer valid, clear it
                            if (returnDate.isNotEmpty()) {
                                viewModel.returnDate.value = ""
                            }
                        },
                        placeholder = "Departure",
                        modifier = Modifier.weight(1f),
                        minDateMillis = departureMinDateMillis
                    )
                    DatePickerField(
                        value = returnDate,
                        onDateSelected = { viewModel.returnDate.value = it },
                        placeholder = "Return",
                        modifier = Modifier.weight(1f),
                        minDateMillis = returnMinDateMillis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AureliaDropdown(
                        value = seats,
                        onValueChange = { viewModel.seats.value = it },
                        options = seatOptions,
                        placeholder = "Seats",
                        modifier = Modifier.weight(1f)
                    )
                    AureliaDropdown(
                        value = flightClass,
                        onValueChange = { viewModel.flightClass.value = it },
                        options = classOptions,
                        placeholder = "Class",
                        modifier = Modifier.weight(1f)
                    )
                }

                if (errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = Color.Yellow, fontSize = 13.sp)
                }

                AureliaButton(
                    text = "Find Jets",
                    onClick = {
                        when {
                            fromLocation.isBlank() -> errorMsg = "Please enter departure city"
                            toLocation.isBlank() -> errorMsg = "Please enter destination city"
                            departureDate.isBlank() -> errorMsg = "Please select departure date"
                            else -> onContinue()
                        }
                    }
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
