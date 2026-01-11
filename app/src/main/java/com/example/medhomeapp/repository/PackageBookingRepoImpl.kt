package com.example.medhomeapp.repository

import com.example.medhomeapp.model.PackageBookingModel
import com.google.firebase.database.*

class PackageBookingRepoImpl : PackageBookingRepo {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("PackageBookings")

    override fun createBooking(
        bookingModel: PackageBookingModel,
        callback: (Boolean, String) -> Unit
    ) {
        val bookingId = ref.push().key ?: return callback(false, "Failed to generate ID")
        val bookingWithId = bookingModel.copy(id = bookingId)

        ref.child(bookingId).setValue(bookingWithId.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Booking created successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to create booking")
                }
            }
    }

    override fun getBookingsByPatient(
        patientId: String,
        callback: (Boolean, String, List<PackageBookingModel>) -> Unit
    ) {
        ref.orderByChild("patientId").equalTo(patientId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val bookings = mutableListOf<PackageBookingModel>()
                        for (data in snapshot.children) {
                            val booking = data.getValue(PackageBookingModel::class.java)
                            if (booking != null) {
                                bookings.add(booking)
                            }
                        }
                        callback(true, "Bookings fetched", bookings)
                    } else {
                        callback(true, "No bookings found", emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun getBookingsByDoctor(
        doctorId: String,
        callback: (Boolean, String, List<PackageBookingModel>) -> Unit
    ) {
        ref.orderByChild("doctorId").equalTo(doctorId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val bookings = mutableListOf<PackageBookingModel>()
                        for (data in snapshot.children) {
                            val booking = data.getValue(PackageBookingModel::class.java)
                            if (booking != null) {
                                bookings.add(booking)
                            }
                        }
                        callback(true, "Doctor bookings fetched", bookings)
                    } else {
                        callback(true, "No bookings found", emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun getBookingsByPackage(
        packageId: String,
        callback: (Boolean, String, List<PackageBookingModel>) -> Unit
    ) {
        ref.orderByChild("packageId").equalTo(packageId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val bookings = mutableListOf<PackageBookingModel>()
                        for (data in snapshot.children) {
                            val booking = data.getValue(PackageBookingModel::class.java)
                            if (booking != null) {
                                bookings.add(booking)
                            }
                        }
                        callback(true, "Package bookings fetched", bookings)
                    } else {
                        callback(true, "No bookings for this package", emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun getBookingById(
        bookingId: String,
        callback: (Boolean, String, PackageBookingModel?) -> Unit
    ) {
        ref.child(bookingId).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val booking = snapshot.getValue(PackageBookingModel::class.java)
                    if (booking != null) {
                        callback(true, "Booking fetched", booking)
                    } else {
                        callback(false, "Failed to parse booking", null)
                    }
                } else {
                    callback(false, "Booking not found", null)
                }
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message ?: "Failed to fetch", null)
            }
    }

    override fun updateBookingStatus(
        bookingId: String,
        status: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(bookingId).child("status").setValue(status)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Status updated")
                } else {
                    callback(false, task.exception?.message ?: "Failed to update")
                }
            }
    }

    override fun cancelBooking(
        bookingId: String,
        callback: (Boolean, String) -> Unit
    ) {
        updateBookingStatus(bookingId, "cancelled", callback)
    }

    override fun getAllBookings(
        callback: (Boolean, String, List<PackageBookingModel>) -> Unit
    ) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val bookings = mutableListOf<PackageBookingModel>()
                    for (data in snapshot.children) {
                        val booking = data.getValue(PackageBookingModel::class.java)
                        if (booking != null) {
                            bookings.add(booking)
                        }
                    }
                    callback(true, "All bookings fetched", bookings)
                } else {
                    callback(true, "No bookings found", emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }
}