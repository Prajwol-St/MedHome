package com.example.medhomeapp.network

import com.example.medhomeapp.model.api.FoodDetailsResponse
import com.example.medhomeapp.model.api.FoodSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface NutritionApiService {
    @GET(ApiConstants.SEARCH_FOODS)
    fun searchFoods(
        @Query("query") query: String,
        @Query("pageSize") pageSize: Int = ApiConstants.DEFAULT_PAGE_SIZE,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("dataType") dataType: List<String>? = listOf(
            ApiConstants.DATA_TYPE_FOUNDATION,
            ApiConstants.DATA_TYPE_SR_LEGACY
        ),
        @Query("api_key") apiKey: String = ApiConstants.USDA_API_KEY
    ): Call<FoodSearchResponse>

    @GET(ApiConstants.GET_FOOD_DETAILS)
    fun getFoodDetails(
        @Path("fdcId") fdcId: String,
        @Query("api_key") apiKey: String = ApiConstants.USDA_API_KEY
    ): Call<FoodDetailsResponse>
}