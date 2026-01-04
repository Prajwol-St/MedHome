package com.example.medhomeapp.utils

import com.example.medhomeapp.model.FoodItemModel
import com.example.medhomeapp.model.api.FoodSearchResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun FoodSearchResult.toFoodItemModel(
    servingAmount: Double = 1.0,
    mealType: String = "other"
): FoodItemModel{
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentDate = dateFormat.format(Date())

    return FoodItemModel(
        id = "",
        name = description,
        calories = getCalories(),
        protein = getProtein(),
        carbs = getCarbs(),
        fat = getFat(),
        servingSize = "100g",
        servingAmount = servingAmount,
        mealType = mealType,
        fdcId = fdcId,
        apiSource = "USDA",
        timestamp = System.currentTimeMillis(),
        date = currentDate
    )
}

