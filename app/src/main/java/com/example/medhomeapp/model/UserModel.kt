package com.example.medhomeapp.model

class UserModel {
    data class User(
        val id: String = "",
        val name: String = "",
        val email: String = "",
        val contact: String = "",
        val gender: String = "",
        val age: Int = 0,
        val emailVerified: Boolean = false
    )

    data class UserSettings(
        val userId: String = "",
        val address: String = "",
        val emergencyContact: String = "",
        val notificationsEnabled: Boolean = true,
        val language: String = "en",
        val privacyAcknowledged: Boolean = false
    )

    data class Doctor(
        val id: String = "",
        val name: String = "",
        val specialization: String = "",
        val type: String = ""
    )
}

