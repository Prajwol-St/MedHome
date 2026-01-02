package com.example.medhomeapp.model

data class HealthPackage(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val testsIncluded: List<String> = emptyList(),
    val price: Double = 0.0,
    val discountPercentage: Int = 0,
    val duration: String = "",
    val recommendedFor: String = "",
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "testsIncluded" to testsIncluded,
            "price" to price,
            "discountPercentage" to discountPercentage,
            "duration" to duration,
            "recommendedFor" to recommendedFor,
            "imageUrl" to imageUrl,
            "timestamp" to timestamp,
            "isActive" to isActive
        )
    }

    fun getDiscountedPrice(): Double {
        return if (discountPercentage > 0) {
            price - (price * discountPercentage / 100)
        } else {
            price
        }
    }

    fun getFormattedPrice(): String {
        return "NPR %.2f".format(price)
    }

    fun getFormattedDiscountedPrice(): String {
        return "NPR %.2f".format(getDiscountedPrice())
    }

    fun getSavingsAmount(): Double {
        return price - getDiscountedPrice()
    }

    fun hasDiscount(): Boolean = discountPercentage > 0
}