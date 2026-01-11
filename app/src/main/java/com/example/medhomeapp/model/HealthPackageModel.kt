package com.example.medhomeapp.model

data class HealthPackageModel(
    val id: String = "",
    val doctorId: String = "",
    val doctorName: String = "",
    val packageName: String = "",
    val shortDescription: String = "",
    val fullDescription: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val duration: String = "",
    val includedServices: List<String> = emptyList(),
    val imageUrl: String = "",
    val imagePublicId: String = "",
    val isActive: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "doctorId" to doctorId,
            "doctorName" to doctorName,
            "packageName" to packageName,
            "shortDescription" to shortDescription,
            "fullDescription" to fullDescription,
            "price" to price,
            "category" to category,
            "duration" to duration,
            "includedServices" to includedServices,
            "imageUrl" to imageUrl,
            "imagePublicId" to imagePublicId,
            "isActive" to isActive,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
}