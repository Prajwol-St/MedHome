package com.example.medhomeapp.model.api

import com.google.gson.annotations.SerializedName

data class FoodSearchResponse(
    @SerializedName("foods")
    val foods: List<FoodSearchResult> = emptyList(),

    @SerializedName("totalHits")
    val totalHits: Int = 0,

    @SerializedName("currentPage")
    val currentPage: Int = 1,

    @SerializedName("totalPages")
    val totalPages: Int = 1
)

data class FoodSearchResult(
    @SerializedName("fdcId")
    val fdcId: String = "",

    @SerializedName("description")
    val description: String = "",

    @SerializedName("dataType")
    val dataType: String = "",

    @SerializedName("brandOwner")
    val brandOwner: String = "",

    @SerializedName("foodNutrients")
    val foodNutrients: List<FoodNutrient> = emptyList()
) {
    fun getCalories(): Double {
        return foodNutrients
            .find { it.nutrientName.contains("Energy", ignoreCase = true) }
            ?.value ?: 0.0
    }

    fun getProtein(): Double {
        return foodNutrients
            .find { it.nutrientName.contains("Protein", ignoreCase = true) }
            ?.value ?: 0.0
    }

    fun getCarbs(): Double {
        return foodNutrients
            .find { it.nutrientName.contains("Carbohydrate", ignoreCase = true) }
            ?.value ?: 0.0
    }

    fun getFat(): Double {
        return foodNutrients
            .find { it.nutrientName.contains("Total lipid", ignoreCase = true) ||
                    it.nutrientName.contains("Fat", ignoreCase = true) }
            ?.value ?: 0.0
    }

    fun isBrandedFood(): Boolean = brandOwner.isNotEmpty()

    fun isFoundationFood(): Boolean = dataType == "Foundation"

    fun hasNutritionData(): Boolean = foodNutrients.isNotEmpty()
}

data class FoodNutrient(
    @SerializedName("nutrientId")
    val nutrientId: Int = 0,

    @SerializedName("nutrientName")
    val nutrientName: String = "",

    @SerializedName("nutrientNumber")
    val nutrientNumber: String = "",

    @SerializedName("unitName")
    val unitName: String = "",

    @SerializedName("value")
    val value: Double = 0.0
) {
    fun isCalorie(): Boolean = nutrientName.contains("Energy", ignoreCase = true)

    fun isProtein(): Boolean = nutrientName.contains("Protein", ignoreCase = true)

    fun isCarb(): Boolean = nutrientName.contains("Carbohydrate", ignoreCase = true)

    fun isFat(): Boolean = nutrientName.contains("lipid", ignoreCase = true) ||
            nutrientName.contains("Fat", ignoreCase = true)
}