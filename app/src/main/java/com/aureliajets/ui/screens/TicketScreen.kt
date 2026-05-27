package com.aureliajets.ui.screens

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.aureliajets.data.entity.Booking
import com.aureliajets.data.entity.User
import com.aureliajets.ui.components.AureliaBackground
import com.aureliajets.ui.components.AureliaButton
import com.aureliajets.ui.components.BackButton
import com.aureliajets.viewmodel.AureliaViewModel
import java.io.OutputStream

@Composable
fun TicketScreen(
    viewModel: AureliaViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val booking by viewModel.lastBooking.collectAsState()

    AureliaBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onClick = onBack)
                Text(
                    "Your Flight Ticket",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            if (user != null && booking != null) {
                TicketCard(user!!, booking!!)

                Spacer(Modifier.weight(1f))

                AureliaButton(
                    text = "Download PDF Ticket",
                    onClick = {
                        generatePdf(context, user!!, booking!!)
                    },
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No ticket information available", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun TicketCard(user: User, booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("AURELIA JETS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF1565C0))
                    Text("Boarding Pass", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Icon(
                    Icons.Default.Download,
                    contentDescription = null,
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Passenger Info
            Row(Modifier.fillMaxWidth()) {
                TicketInfoRow("PASSENGER NAME", "${user.firstName} ${user.lastName}", Modifier.weight(1.5f))
                TicketInfoRow("CNIC / ID", user.cnic, Modifier.weight(1f))
            }
            
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth()) {
                TicketInfoRow("FLIGHT REF", "#${booking.id.toString().padStart(6, '0')}", Modifier.weight(1f))
                TicketInfoRow("JET TYPE", booking.jetName, Modifier.weight(1.5f))
            }

            Spacer(Modifier.height(24.dp))

            // Route
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(booking.fromLocation.take(3).uppercase(), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0D1B3E))
                    Text(booking.fromLocation, fontSize = 12.sp, color = Color.Gray)
                }
                
                Text("✈", fontSize = 24.sp, color = Color(0xFF1565C0), modifier = Modifier.padding(horizontal = 16.dp))

                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(booking.toLocation.take(3).uppercase(), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0D1B3E))
                    Text(booking.toLocation, fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Dotted Line
            Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Details
            Row(Modifier.fillMaxWidth()) {
                TicketInfoRow("DATE", booking.departureDate, Modifier.weight(1f))
                TicketInfoRow("CLASS", booking.flightClass, Modifier.weight(1f))
                TicketInfoRow("SEATS", booking.seats, Modifier.weight(1f))
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Barcode
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(Modifier.padding(horizontal = 20.dp)) {
                    repeat(25) { index ->
                        Box(
                            Modifier
                                .width(if (index % 4 == 0) 3.dp else 1.5.dp)
                                .fillMaxHeight()
                                .padding(horizontal = 1.dp)
                                .background(Color.Black)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TicketInfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
        Text(value, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
    }
}

private fun generatePdf(context: Context, user: User, booking: Booking) {
    val pdfDocument = PdfDocument()
    val paint = Paint()
    val titlePaint = Paint()
    
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas: android.graphics.Canvas = page.canvas

    // Draw background
    paint.color = android.graphics.Color.WHITE
    canvas.drawRect(0f, 0f, 595f, 842f, paint)
    
    // Border
    paint.color = "#1565C0".toColorInt()
    paint.strokeWidth = 2f
    paint.style = Paint.Style.STROKE
    canvas.drawRect(25f, 25f, 570f, 817f, paint)

    // Header
    titlePaint.color = "#1565C0".toColorInt()
    titlePaint.textSize = 26f
    titlePaint.isFakeBoldText = true
    canvas.drawText("AURELIA JETS - BOARDING PASS", 50f, 80f, titlePaint)
    
    paint.style = Paint.Style.FILL
    paint.color = android.graphics.Color.BLACK
    paint.textSize = 14f
    canvas.drawText("CONFIRMED E-TICKET", 50f, 110f, paint)

    // Passenger Details
    paint.textSize = 11f
    paint.color = android.graphics.Color.GRAY
    canvas.drawText("PASSENGER NAME", 50f, 160f, paint)
    canvas.drawText("CNIC / ID", 350f, 160f, paint)
    
    paint.color = android.graphics.Color.BLACK
    paint.textSize = 16f
    paint.isFakeBoldText = true
    canvas.drawText("${user.firstName} ${user.lastName}", 50f, 185f, paint)
    canvas.drawText(user.cnic, 350f, 185f, paint)

    // Flight Info
    paint.isFakeBoldText = false
    paint.textSize = 11f
    paint.color = android.graphics.Color.GRAY
    canvas.drawText("FLIGHT REFERENCE", 50f, 230f, paint)
    canvas.drawText("JET TYPE", 350f, 230f, paint)
    
    paint.color = android.graphics.Color.BLACK
    paint.textSize = 16f
    canvas.drawText("#${booking.id.toString().padStart(6, '0')}", 50f, 255f, paint)
    canvas.drawText(booking.jetName, 350f, 255f, paint)

    // Route
    paint.textSize = 11f
    paint.color = android.graphics.Color.GRAY
    canvas.drawText("FROM", 50f, 310f, paint)
    canvas.drawText("TO", 350f, 310f, paint)

    paint.color = "#0D1B3E".toColorInt()
    paint.textSize = 28f
    paint.isFakeBoldText = true
    canvas.drawText(booking.fromLocation, 50f, 345f, paint)
    canvas.drawText(booking.toLocation, 350f, 345f, paint)

    // Footer Info
    paint.isFakeBoldText = false
    paint.textSize = 11f
    paint.color = android.graphics.Color.GRAY
    canvas.drawText("DATE", 50f, 400f, paint)
    canvas.drawText("CLASS", 200f, 400f, paint)
    canvas.drawText("SEATS", 350f, 400f, paint)

    paint.color = android.graphics.Color.BLACK
    paint.textSize = 15f
    canvas.drawText(booking.departureDate, 50f, 425f, paint)
    canvas.drawText(booking.flightClass, 200f, 425f, paint)
    canvas.drawText(booking.seats, 350f, 425f, paint)

    // Disclaimer
    paint.textSize = 10f
    paint.color = android.graphics.Color.DKGRAY
    canvas.drawText("Please arrive at the private terminal 30 minutes before departure.", 50f, 780f, paint)
    canvas.drawText("This ticket is valid only for the named passenger and jet specified above.", 50f, 795f, paint)

    pdfDocument.finishPage(page)

    val fileName = "AureliaJet_Ticket_${booking.id}.pdf"
    
    try {
        val outputStream: OutputStream?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            outputStream = uri?.let { context.contentResolver.openOutputStream(it) }
        } else {
            val file = java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
            outputStream = java.io.FileOutputStream(file)
        }

        outputStream?.use {
            pdfDocument.writeTo(it)
            Toast.makeText(context, "Ticket downloaded successfully to Downloads", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        pdfDocument.close()
    }
}
