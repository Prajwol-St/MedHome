package com.example.medhomeapp.repository

import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.model.RatingModel
import com.google.firebase.database.*

class RatingRepoImpl : RatingRepo {

    private val db = FirebaseDatabase.getInstance()
    private val ratingRef = db.getReference("ratings")
    private val doctorRef = db.getReference("User")

    override fun addRating(
        rating: RatingModel,
        callback: (Boolean, String) -> Unit
    ) {
        // Check if already rated
        hasPatientRatedAppointment(rating.appointmentId) { alreadyRated ->
            if (alreadyRated) {
                callback(false, "You have already rated this appointment")
                return@hasPatientRatedAppointment
            }

            val ratingWithId = rating.copy(ratingId = rating.appointmentId)

            ratingRef.child(rating.appointmentId)
                .setValue(ratingWithId.toMap())
                .addOnSuccessListener {
                    // Update doctor's average rating
                    updateDoctorAverageRating(rating.doctorId)
                    callback(true, "Rating submitted successfully")
                }
                .addOnFailureListener {
                    callback(false, "Failed to submit rating: ${it.message}")
                }
        }
    }

    override fun getRatingsByDoctor(
        doctorId: String,
        callback: (List<RatingModel>) -> Unit
    ) {
        ratingRef.orderByChild("doctorId")
            .equalTo(doctorId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val ratings = snapshot.children.mapNotNull {
                        it.getValue(RatingModel::class.java)
                    }
                    callback(ratings.sortedByDescending { it.createdAt })
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    override fun updateDoctorAverageRating(doctorId: String) {
        getRatingsByDoctor(doctorId) { ratings ->
            if (ratings.isEmpty()) {
                return@getRatingsByDoctor
            }

            val averageRating = ratings.map { it.rating }.average().toFloat()
            val totalRatings = ratings.size
            val totalReviews = ratings.count { it.review.isNotBlank() }

            val updates = mapOf(
                "averageRating" to averageRating,
                "totalRatings" to totalRatings,
                "totalReviews" to totalReviews
            )

            doctorRef.child(doctorId).updateChildren(updates)
        }
    }

    override fun hasPatientRatedAppointment(
        appointmentId: String,
        callback: (Boolean) -> Unit
    ) {
        ratingRef.child(appointmentId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }

    override fun editRating(
        ratingId: String,
        newRating: Float,
        newReview: String,
        callback: (Boolean, String) -> Unit
    ) {
        ratingRef.child(ratingId).get()
            .addOnSuccessListener { snapshot ->
                val existingRating = snapshot.getValue(RatingModel::class.java)

                if (existingRating == null) {
                    callback(false, "Rating not found")
                    return@addOnSuccessListener
                }

                val updates = mapOf(
                    "rating" to newRating,
                    "review" to newReview,
                    "isEdited" to true,
                    "editedAt" to System.currentTimeMillis()
                )

                ratingRef.child(ratingId).updateChildren(updates)
                    .addOnSuccessListener {
                        // Update doctor's average rating
                        updateDoctorAverageRating(existingRating.doctorId)
                        callback(true, "Rating updated successfully")
                    }
                    .addOnFailureListener {
                        callback(false, "Failed to update rating: ${it.message}")
                    }
            }
            .addOnFailureListener {
                callback(false, "Failed to fetch rating: ${it.message}")
            }
    }
}