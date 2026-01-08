package com.example.medhomeapp.repository

import com.example.medhomeapp.model.api.FoodDetailsResponse
import com.example.medhomeapp.model.api.FoodSearchResult

class NutritionRepoImpl: NutritionRepo {
    override fun searchFoods(
        query: String,
        pageSize: Int,
        onSuccess: (List<FoodSearchResult>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getFoodDetails(
        fdcId: String,
        onSuccess: (FoodDetailsResponse) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun searchFoodsByCategory(
        category: String,
        onSuccess: (List<FoodSearchResult>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getCommonFoods(
        onSuccess: (List<FoodSearchResult>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}