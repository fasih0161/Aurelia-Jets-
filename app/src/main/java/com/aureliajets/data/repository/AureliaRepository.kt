package com.aureliajets.data.repository

import com.aureliajets.data.dao.BookingDao
import com.aureliajets.data.dao.UserDao
import com.aureliajets.data.entity.Booking
import com.aureliajets.data.entity.User
import kotlinx.coroutines.flow.Flow

class AureliaRepository(
    private val userDao: UserDao,
    private val bookingDao: BookingDao
) {
    suspend fun registerUser(user: User): Long = userDao.insert(user)
    suspend fun loginUser(username: String, password: String): User? = userDao.login(username, password)
    suspend fun getUserByUsername(username: String): User? = userDao.getUserByUsername(username)
    suspend fun getUserById(id: Int): User? = userDao.getUserById(id)
    suspend fun createBooking(booking: Booking): Long = bookingDao.insert(booking)
    fun getBookingsByUser(userId: Int): Flow<List<Booking>> = bookingDao.getBookingsByUser(userId)
    suspend fun getLastBooking(): Booking? = bookingDao.getLastBooking()
    suspend fun updateRating(bookingId: Int, rating: Int) = bookingDao.updateRating(bookingId, rating)
    fun getAllBookedJetNames(): Flow<List<String>> = bookingDao.getAllBookedJetNames()
}
