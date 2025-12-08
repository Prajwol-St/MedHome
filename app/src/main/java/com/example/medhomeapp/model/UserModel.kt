package com.example.medhomeapp.model

data class UserModel(
    val id: Int = 0,
    val role: String = "patient",
    val name: String = "",
    val email: String = "",
    val passwordHash: String = "",
    val contact: String = "",
    val gender: String = "",
    val age: Int = 0,
    val emailVerified: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = "",
)
{
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "role" to role,
            "name" to name,
            "email" to email,
            "passwordHash" to passwordHash,
            "contact" to contact,
            "gender" to gender,
            "age" to age,
            "emailVerified" to emailVerified,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
}
