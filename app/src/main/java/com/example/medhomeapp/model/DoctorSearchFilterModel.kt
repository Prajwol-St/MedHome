package com.example.medhomeapp.model

data class DoctorSearchFilterModel(
    val searchQuery: String = "",
    val specialization: String = "",
    val minRating: Float = 0f,
    val maxFee: Double = 10000.0,
    val minFee: Double = 0.0,
    val maxDistance: Double = 50.0, // in km
    val availableOn: String = "", // yyyy-MM-dd
    val sortBy: String = "rating_desc" // rating_desc, rating_asc, fee_asc, fee_desc, distance_asc, experience_desc
)