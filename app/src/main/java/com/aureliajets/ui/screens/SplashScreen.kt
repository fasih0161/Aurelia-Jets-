package com.aureliajets.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.aureliajets.ui.components.AureliaBackground
import com.aureliajets.ui.components.AureliaButton

@Composable
fun SplashScreen(onContinue: () -> Unit) {
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
                // Logo Icon
                Surface(
                    modifier = Modifier.size(70.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF0D1B3E)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("✈", fontSize = 32.sp, color = Color.White)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "Aurelia Jets",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Evoking beauty, rarity,\nand superior value.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                // Hero Image for Splash
                SubcomposeAsyncImage(
                    model = "https://images.unsplash.com/photo-1464037866556-6812c9d1c72e?auto=format&fit=crop&w=800&q=80",
                    contentDescription = "Private Jet",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    loading = { Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.1f))) },
                    error = { Text("✈", fontSize = 80.sp, color = Color.White.copy(alpha = 0.9f)) }
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Come\nFly\nwith us",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFFE91E8C),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                AureliaButton(text = "Get Started", onClick = onContinue)

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
