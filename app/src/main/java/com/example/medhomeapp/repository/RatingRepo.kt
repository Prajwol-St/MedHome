package com.example.medhomeapp.repository

import com.example.medhomeapp.model.RatingModel

interface RatingRepo {

    fun addRating(
        rating: RatingModel,
        callback: (Boolean, String) -> Unit
    )

    fun getRatingsByDoctor(
        doctorId: String,
        callback: (List<RatingModel>) -> Unit
    )

    fun updateDoctorAverageRating(
        doctorId: String
    )

    fun hasPatientRatedAppointment(
        appointmentId: String,
        callback: (Boolean) -> Unit
    )

    fun editRating(
        ratingId: String,
        newRating: Float,
        newReview: String,
        callback: (Boolean, String) -> Unit
    )
}