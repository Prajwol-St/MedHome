package com.example.medhomeapp.repository

import com.example.medhomeapp.model.api.FoodDetailsResponse
import com.example.medhomeapp.model.api.FoodSearchResponse
import com.example.medhomeapp.model.api.FoodSearchResult
import com.example.medhomeapp.network.RetrofitClient
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback

class NutritionRepoImpl: NutritionRepo {
    private val apiService = RetrofitClient.getApiService()

    override fun searchFoods(
        query: String,
        pageSize: Int,
        onSuccess: (List<FoodSearchResult>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (query.isBlank()) {
            onError(Exception("Search query cannot be empty"))
            return
        }

        apiService.searchFoods(query, pageSize).enqueue(object : Callback<FoodSearchResponse> {
            override fun onResponse(
                call: Call<FoodSearchResponse>,
                response: Response<FoodSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val foodList = response.body()?.foods ?: emptyList()
                    onSuccess(foodList)
                } else {
                    onError(Exception("API Error: ${response.code()} - ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<FoodSearchResponse>, t: Throwable) {
                onError(Exception("Network Error: ${t.message}", t))
            }
        })
    }

    override fun getFoodDetails(
        fdcId: String,
        onSuccess: (FoodDetailsResponse) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (fdcId.isBlank()) {
            onError(Exception("FDC ID cannot be empty"))
            return
        }

        apiService.getFoodDetails(fdcId).enqueue(object : Callback<FoodDetailsResponse> {
            override fun onResponse(
                call: Call<FoodDetailsResponse>,
                response: Response<FoodDetailsResponse>
            ) {
                if (response.isSuccessful) {
                    val foodDetails = response.body()
                    if (foodDetails != null) {
                        onSuccess(foodDetails)
                    } else {
                        onError(Exception("Food details not found"))
                    }
                } else {
                    onError(Exception("API Error: ${response.code()} - ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<FoodDetailsResponse>, t: Throwable) {
                onError(Exception("Network Error: ${t.message}", t))
            }
        })
    }

    override fun searchFoodsByCategory(
        category: String,
        onSuccess: (List<FoodSearchResult>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        searchFoods(category, 20, onSuccess, onError)
    }

    override fun getCommonFoods(
        onSuccess: (List<FoodSearchResult>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val commonFoodQueries = listOf("chicken", "rice", "apple", "milk", "bread")
        val allResults = mutableListOf<FoodSearchResult>()
        var completedQueries = 0

        commonFoodQueries.forEach { query ->
            searchFoods(query, 2, { results ->
                allResults.addAll(results)
                completedQueries++
                if (completedQueries == commonFoodQueries.size) {
                    onSuccess(allResults)
                }
            }, { error ->
                completedQueries++
                if (completedQueries == commonFoodQueries.size) {
                    if (allResults.isEmpty()) {
                        onError(error)
                    } else {
                        onSuccess(allResults)
                    }
                }
            })
        }
    }
}
