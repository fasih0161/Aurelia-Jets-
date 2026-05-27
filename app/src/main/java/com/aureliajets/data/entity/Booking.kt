package com.aureliajets.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val jetName: String,
    val fromLocation: String,
    val toLocation: String,
    val departureDate: String,
    val returnDate: String,
    val seats: String,
    val flightClass: String,
    val pricePerHour: String,
    val cardHolderName: String,
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String,
    val rating: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
