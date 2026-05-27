package com.aureliajets.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aureliajets.ui.Airport
import com.aureliajets.ui.searchAirports
import java.util.Calendar

@Composable
fun AureliaBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE91E8C),
                        Color(0xFFFF6B6B),
                        Color(0xFF64B5F6),
                        Color(0xFF90CAF9)
                    )
                )
            ),
        content = content
    )
}

@Composable
fun AureliaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFF666699)) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else visualTransformation,
        keyboardOptions = keyboardOptions,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.85f)),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF1565C0),
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = Color(0xFF0D1B3E),
            unfocusedTextColor = Color(0xFF0D1B3E)
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun AureliaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = Color(0xFF1565C0),
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
        )
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

// ── Visual Transformations ──────────────────────────────────────────────

class CnicVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length > 13) text.text.substring(0, 13) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 4 || i == 11) out += "-"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 11) return offset + 1
                if (offset <= 13) return offset + 2
                return 15
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 5) return offset
                if (offset <= 13) return offset - 1
                if (offset <= 15) return offset - 2
                return 13
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

class ExpiryDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length > 4) text.text.substring(0, 4) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 && trimmed.length > 2) out += "/"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return if (text.text.length > 2) offset + 1 else offset
                return if (text.text.length > 2) 5 else 2
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return if (text.text.length > 2) offset - 1 else offset
                return if (text.text.length > 2) 4 else 2
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

class CreditCardVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length > 16) text.text.substring(0, 16) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i != 15) out += " "
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset + 1
                if (offset <= 11) return offset + 2
                if (offset <= 15) return offset + 3
                return 19
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 9) return offset - 1
                if (offset <= 14) return offset - 2
                if (offset <= 19) return offset - 3
                return 16
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

// ── Airport Autocomplete Field ──────────────────────────────────────────────

@Composable
fun AirportSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onAirportSelected: (Airport) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isOrigin: Boolean = true
) {
    val suggestions = remember(value) { searchAirports(value) }
    var showDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(suggestions) {
        showDropdown = suggestions.isNotEmpty()
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                showDropdown = it.length >= 2
            },
            placeholder = { Text(placeholder, color = Color(0xFF666699)) },
            leadingIcon = {
                Icon(
                    if (isOrigin) Icons.Default.FlightTakeoff else Icons.Default.FlightLand,
                    contentDescription = null,
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.85f)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1565C0),
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = Color(0xFF0D1B3E),
                unfocusedTextColor = Color(0xFF0D1B3E)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        AnimatedVisibility(visible = showDropdown && suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                LazyColumn(modifier = Modifier.heightIn(max = 220.dp)) {
                    items(suggestions) { airport ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onAirportSelected(airport)
                                    onValueChange(airport.display)
                                    showDropdown = false
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF1565C0).copy(alpha = 0.12f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        airport.code,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1565C0)
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    airport.city,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0D1B3E)
                                )
                                Text(
                                    "${airport.country} — ${airport.name}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        if (suggestions.last() != airport) {
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                        }
                    }
                }
            }
        }
    }
}

// ── Date Picker Field ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    onDateSelected: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    minDateMillis: Long? = null
) {
    var showDialog by remember { mutableStateOf(false) }

    val todayMillis = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val datePickerState = key(minDateMillis) {
        rememberDatePickerState(
            initialSelectedDateMillis = minDateMillis ?: todayMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= (minDateMillis ?: todayMillis)
                }
            }
        )
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(placeholder, color = Color(0xFF666699)) },
            trailingIcon = {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = "Pick date",
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.85f))
                .clickable { showDialog = true },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1565C0),
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledTextColor = Color(0xFF0D1B3E),
                disabledPlaceholderColor = Color(0xFF666699),
                disabledTrailingIconColor = Color(0xFF1565C0)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            enabled = false
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDialog = true }
        )
    }

    if (showDialog) {
        // Updated colors for better visibility in the calendar
        val datePickerColors = DatePickerDefaults.colors(
            containerColor = Color.White,
            titleContentColor = Color(0xFF0D1B3E),
            headlineContentColor = Color(0xFF1565C0),
            weekdayContentColor = Color.DarkGray,
            subheadContentColor = Color.Gray,
            yearContentColor = Color.Black,
            currentYearContentColor = Color(0xFF1565C0),
            selectedYearContentColor = Color.White,
            selectedYearContainerColor = Color(0xFF1565C0),
            dayContentColor = Color.Black,
            selectedDayContentColor = Color.White,
            selectedDayContainerColor = Color(0xFF1565C0),
            todayContentColor = Color(0xFF1565C0),
            todayDateBorderColor = Color(0xFF1565C0)
        )

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance().apply { timeInMillis = millis }
                        val day = cal.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
                        val month = (cal.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
                        val year = cal.get(Calendar.YEAR)
                        onDateSelected("$day/$month/$year")
                    }
                }) { Text("OK", color = Color(0xFF1565C0)) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = Color.White)
        ) {
            DatePicker(
                state = datePickerState,
                colors = datePickerColors
            )
        }
    }
}

@Composable
fun AureliaDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(placeholder, color = Color(0xFF666699)) },
            trailingIcon = {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF1565C0)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.85f)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1565C0),
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledTextColor = Color(0xFF0D1B3E),
                disabledPlaceholderColor = Color(0xFF666699),
                disabledTrailingIconColor = Color(0xFF1565C0)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            enabled = false
        )
        Box(modifier = Modifier.matchParentSize().clickable { expanded = true })

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .widthIn(min = 180.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option,
                            color = if (option == value) Color(0xFF1565C0) else Color(0xFF0D1B3E),
                            fontWeight = if (option == value) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ExpiryDateField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { raw ->
            val digits = raw.filter { it.isDigit() }.take(4)
            onValueChange(digits)
        },
        placeholder = { Text("MM/YY", color = Color(0xFF666699)) },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.85f)),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF1565C0),
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = Color(0xFF0D1B3E),
            unfocusedTextColor = Color(0xFF0D1B3E)
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        visualTransformation = ExpiryDateVisualTransformation()
    )
}
