package com.aureliajets.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aureliajets.data.database.AureliaDatabase
import com.aureliajets.data.entity.Booking
import com.aureliajets.data.entity.User
import com.aureliajets.data.repository.AureliaRepository
import com.aureliajets.data.session.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class BookingState {
    object Idle : BookingState()
    object Loading : BookingState()
    data class Success(val bookingId: Long) : BookingState()
    data class Error(val message: String) : BookingState()
}

class AureliaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AureliaDatabase.getDatabase(application)
    private val repository = AureliaRepository(db.userDao(), db.bookingDao())
    val sessionManager = SessionManager(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Idle)
    val bookingState: StateFlow<BookingState> = _bookingState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _lastBookingId = MutableStateFlow<Int?>(null)
    val lastBookingId: StateFlow<Int?> = _lastBookingId

    private val _lastBooking = MutableStateFlow<Booking?>(null)
    val lastBooking: StateFlow<Booking?> = _lastBooking

    // Flight search fields
    val fromLocation = MutableStateFlow("")
    val toLocation = MutableStateFlow("")
    val departureDate = MutableStateFlow("")
    val returnDate = MutableStateFlow("")
    val seats = MutableStateFlow("1")
    val flightClass = MutableStateFlow("Business")

    val bookedJetNames: StateFlow<List<String>> = repository.getAllBookedJetNames()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Check on startup if user is already logged in
    val isSessionActive: Boolean get() = sessionManager.isLoggedIn()

    init {
        // Restore session if exists
        if (sessionManager.isLoggedIn()) {
            viewModelScope.launch {
                val userId = sessionManager.getUserId()
                val user = repository.getUserById(userId)
                if (user != null) {
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)
                } else {
                    sessionManager.clearSession()
                }
            }
        }
    }

    fun signUp(
        firstName: String, lastName: String, username: String,
        password: String, phone: String, cnic: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val existing = repository.getUserByUsername(username)
                if (existing != null) {
                    _authState.value = AuthState.Error("Username already exists")
                    return@launch
                }
                val user = User(
                    firstName = firstName, lastName = lastName, username = username,
                    password = password, phoneNumber = phone, cnic = cnic
                )
                val id = repository.registerUser(user)
                val newUser = user.copy(id = id.toInt())
                _currentUser.value = newUser
                sessionManager.saveSession(id.toInt(), username, firstName, lastName)
                _authState.value = AuthState.Success(newUser)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = repository.loginUser(username, password)
                if (user != null) {
                    _currentUser.value = user
                    sessionManager.saveSession(user.id, user.username, user.firstName, user.lastName)
                    _authState.value = AuthState.Success(user)
                } else {
                    _authState.value = AuthState.Error("Invalid username or password")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun createBooking(
        jetName: String, pricePerHour: String,
        cardHolder: String, cardNumber: String,
        expiry: String, cvv: String
    ) {
        viewModelScope.launch {
            _bookingState.value = BookingState.Loading
            try {
                val userId = _currentUser.value?.id ?: 0
                val booking = Booking(
                    userId = userId,
                    jetName = jetName,
                    fromLocation = fromLocation.value,
                    toLocation = toLocation.value,
                    departureDate = departureDate.value,
                    returnDate = returnDate.value,
                    seats = seats.value,
                    flightClass = flightClass.value,
                    pricePerHour = pricePerHour,
                    cardHolderName = cardHolder,
                    cardNumber = cardNumber,
                    expiryDate = expiry,
                    cvv = cvv
                )
                val id = repository.createBooking(booking)
                _lastBookingId.value = id.toInt()
                _lastBooking.value = booking.copy(id = id.toInt())
                _bookingState.value = BookingState.Success(id)
            } catch (e: Exception) {
                _bookingState.value = BookingState.Error(e.message ?: "Booking failed")
            }
        }
    }

    fun getBookingsForCurrentUser(): Flow<List<Booking>>? {
        val userId = _currentUser.value?.id ?: return null
        return repository.getBookingsByUser(userId)
    }

    fun updateRating(rating: Int) {
        viewModelScope.launch {
            _lastBookingId.value?.let { id ->
                repository.updateRating(id, rating)
            }
        }
    }

    fun resetAuthState() { _authState.value = AuthState.Idle }
    fun resetBookingState() { _bookingState.value = BookingState.Idle }

    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.Idle
        sessionManager.clearSession()
        // Reset flight fields
        fromLocation.value = ""
        toLocation.value = ""
        departureDate.value = ""
        returnDate.value = ""
        seats.value = "1"
        flightClass.value = "Business"
    }
}
