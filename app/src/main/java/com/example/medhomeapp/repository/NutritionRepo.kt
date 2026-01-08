package com.example.medhomeapp.repository

import com.example.medhomeapp.model.api.FoodDetailsResponse
import com.example.medhomeapp.model.api.FoodSearchResult

interface NutritionRepo {

    fun searchFoods(
        query: String,
        pageSize: Int = 10,
        onSuccess: (List<FoodSearchResult>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun getFoodDetails(
        fdcId: String,
        onSuccess: (FoodDetailsResponse) -> Unit,
        onError: (Exception) -> Unit
    )

    fun searchFoodsByCategory(
        category: String,
        onSuccess: (List<FoodSearchResult>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun getCommonFoods(
        onSuccess: (List<FoodSearchResult>) -> Unit,
        onError: (Exception) -> Unit
    )
}