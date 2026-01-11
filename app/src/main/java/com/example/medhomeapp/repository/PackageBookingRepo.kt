package com.example.medhomeapp.repository

import com.example.medhomeapp.model.PackageBookingModel

interface PackageBookingRepo {

    fun createBooking(
        bookingModel: PackageBookingModel,
        callback: (Boolean, String) -> Unit
    )

    fun getBookingsByPatient(
        patientId: String,
        callback: (Boolean, String, List<PackageBookingModel>) -> Unit
    )

    fun getBookingsByDoctor(
        doctorId: String,
        callback: (Boolean, String, List<PackageBookingModel>) -> Unit
    )

    fun getBookingsByPackage(
        packageId: String,
        callback: (Boolean, String, List<PackageBookingModel>) -> Unit
    )

    fun getBookingById(
        bookingId: String,
        callback: (Boolean, String, PackageBookingModel?) -> Unit
    )

    fun updateBookingStatus(
        bookingId: String,
        status: String,
        callback: (Boolean, String) -> Unit
    )

    fun cancelBooking(
        bookingId: String,
        callback: (Boolean, String) -> Unit
    )

    fun getAllBookings(
        callback: (Boolean, String, List<PackageBookingModel>) -> Unit
    )

    fun deleteBooking(
        bookingId: String,
        callback: (Boolean, String) -> Unit
    )
}