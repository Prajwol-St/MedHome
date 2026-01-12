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

fun getCurrentDate(): String{
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(Date())
}

fun Long.toDateString(): String{
    val dateFormat = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
    return dateFormat.format(Date(this))
}

fun Long.toTimeString(): String{
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return timeFormat.format(Date(this))
}

fun String.toTimestamp(): Long{
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.parse(this)?.time ?: System.currentTimeMillis()
    }catch (e: Exception){
        System.currentTimeMillis()
    }
}