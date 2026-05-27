package com.aureliajets.ui

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val BOOK_FLIGHT = "book_flight"
    const val JET_LIST = "jet_list"
    const val JET_DETAIL = "jet_detail/{jetIndex}"
    const val BOOKING_DETAILS = "booking_details/{jetIndex}"
    const val BOOKING_SUCCESS = "booking_success"
    const val TICKET = "ticket"
    const val RATING = "rating"
    const val PROFILE = "profile"

    fun jetDetail(index: Int) = "jet_detail/$index"
    fun bookingDetails(index: Int) = "booking_details/$index"
}
