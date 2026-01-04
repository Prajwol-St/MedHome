package com.example.medhomeapp.model

data class FoodItemModel(
    val id: String = "",
    val name: String = "",
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val servingSize: String = "",
    val servingAmount: Double = 1.0,
    val mealType: String = "other",
    val fdcId: String = "",
    val apiSource: String = "manual",
    val timestamp: Long = System.currentTimeMillis(),
    val date: String = ""
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "calories" to calories,
            "protein" to protein,
            "carbs" to carbs,
            "fat" to fat,
            "servingSize" to servingSize,
            "servingAmount" to servingAmount,
            "mealType" to mealType,
            "fdcId" to fdcId,
            "apiSource" to apiSource,
            "timestamp" to timestamp,
            "date" to date
        )
    }


}
