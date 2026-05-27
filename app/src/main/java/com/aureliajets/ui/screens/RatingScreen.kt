package com.aureliajets.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aureliajets.ui.components.AureliaBackground
import com.aureliajets.ui.components.AureliaButton
import com.aureliajets.viewmodel.AureliaViewModel

@Composable
fun RatingScreen(
    viewModel: AureliaViewModel,
    onDone: () -> Unit
) {
    var selectedRating by remember { mutableStateOf(0) }

    AureliaBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(40.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Thanks for your\nprecious time.\nWishing you a safe\nand comfortable\njourney.",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )

                Spacer(Modifier.height(40.dp))

                Text(
                    "Rate us",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = if (star <= selectedRating) Icons.Filled.Star else Icons.Filled.StarOutline,
                            contentDescription = "$star stars",
                            tint = if (star <= selectedRating) Color(0xFFFFD700) else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { selectedRating = star }
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "For any query",
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    "contact us",
                    fontSize = 16.sp,
                    color = Color(0xFF42A5F5),
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                AureliaButton(
                    text = "Submit Rating",
                    onClick = {
                        if (selectedRating > 0) {
                            viewModel.updateRating(selectedRating)
                        }
                        onDone()
                    }
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
