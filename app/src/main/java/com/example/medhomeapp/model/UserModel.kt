package com.example.medhomeapp.model

data class UserModel(
    val id: String = "",
    val role: String = "patient",
    val name: String = "",
    val email: String = "",
    val contact: String = "",
    val gender: String = "",
    val dateOfBirth: String = "",
    val emailVerified: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = "",
    val bloodGroup: String = "",
    val emergencyContact: String = "",
    val address: String = "",
    val profileImageUrl: String = "",
    val profileImagePublicId: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "role" to role,
            "name" to name,
            "email" to email,
            "contact" to contact,
            "gender" to gender,
            "dateOfBirth" to dateOfBirth,
            "emailVerified" to emailVerified,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "bloodGroup" to bloodGroup,
            "emergencyContact" to emergencyContact,
            "address" to address,
            "profileImageUrl" to profileImageUrl,
            "profileImagePublicId" to profileImagePublicId
        )
    }
}