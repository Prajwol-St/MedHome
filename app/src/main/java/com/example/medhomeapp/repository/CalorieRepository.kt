package com.example.medhomeapp.repository

import com.example.medhomeapp.model.FoodItemModel

interface CalorieRepository {

    fun addFoodItem(
        foodItem : FoodItemModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )

    fun getFoodItemsByDate(
        date: String,
        onSuccess: (List<FoodItemModel>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun getAllFoodItems(
        onSuccess: (List<FoodItemModel>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun getFoodItemById(
        foodItemId: String,
        onSuccess: (FoodItemModel?) -> Unit,
        onError: (Exception) -> Unit
    )
}