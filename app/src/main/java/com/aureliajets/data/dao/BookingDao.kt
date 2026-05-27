package com.aureliajets.data.dao

import androidx.room.*
import com.aureliajets.data.entity.Booking
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: Booking): Long

    @Query("SELECT * FROM bookings WHERE userId = :userId ORDER BY createdAt DESC")
    fun getBookingsByUser(userId: Int): Flow<List<Booking>>

    @Query("SELECT * FROM bookings ORDER BY id DESC LIMIT 1")
    suspend fun getLastBooking(): Booking?

    @Query("UPDATE bookings SET rating = :rating WHERE id = :bookingId")
    suspend fun updateRating(bookingId: Int, rating: Int)

    @Query("SELECT COUNT(*) FROM bookings WHERE userId = :userId")
    suspend fun getBookingCount(userId: Int): Int

    @Query("SELECT jetName FROM bookings")
    fun getAllBookedJetNames(): Flow<List<String>>
}
