package com.example.medhomeapp.model.api

import com.google.gson.annotations.SerializedName

data class FoodDetailsResponse(
    @SerializedName("fdcId")
    val fdcId: String = "",

    @SerializedName("description")
    val description: String = "",

    @SerializedName("dataType")
    val dataType: String = "",

    @SerializedName("foodNutrients")
    val foodNutrients: List<FoodNutrientDetail> = emptyList(),

    @SerializedName("servingSize")
    val servingSize: Double? = null,

    @SerializedName("servingSizeUnit")
    val servingSizeUnit: String? = null,

    @SerializedName("brandOwner")
    val brandOwner: String? = null
) {
    fun getCalories(): Double {
        return foodNutrients.find {
            it.nutrient.name.contains("Energy", ignoreCase = true)
        }?.amount ?: 0.0
    }

    fun getProtein(): Double {
        return foodNutrients.find {
            it.nutrient.name.contains("Protein", ignoreCase = true)
        }?.amount ?: 0.0
    }

    fun getCarbs(): Double {
        return foodNutrients.find {
            it.nutrient.name.contains("Carbohydrate", ignoreCase = true)
        }?.amount ?: 0.0
    }

    fun getFat(): Double {
        return foodNutrients.find {
            it.nutrient.name.contains("Total lipid", ignoreCase = true) ||
                    it.nutrient.name.contains("Fat", ignoreCase = true)
        }?.amount ?: 0.0
    }
}

data class FoodNutrientDetail(
    @SerializedName("nutrient")
    val nutrient: Nutrient = Nutrient(),

    @SerializedName("amount")
    val amount: Double = 0.0
)

data class Nutrient(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("unitName")
    val unitName: String = ""
)