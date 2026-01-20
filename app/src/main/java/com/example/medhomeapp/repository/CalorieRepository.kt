package com.example.medhomeapp.repository

import com.example.medhomeapp.model.CalorieGoalModel
import com.example.medhomeapp.model.DailySummaryModel
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

    fun updateFoodItem(
        foodItemId: String,
        foodItem: FoodItemModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )

    fun deleteFoodItem(
        foodItemId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )

    fun getFoodItemByMealType(
        date: String,
        mealType: String,
        onSuccess: (List<FoodItemModel>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun getFoodItemByDateRange(
        startDate: String,
        endDate: String,
        onSuccess: (List<FoodItemModel>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun setCalorieGoal(
        goal : CalorieGoalModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )

    fun getCalorieGoal(
        onSuccess: (CalorieGoalModel?) -> Unit,
        onError: (Exception) -> Unit
    )

    fun updateCalorieGoal(
        targetCalories: Double,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )

    fun updateMacroGoals(
        proteinGoal: Double,
        carbsGoal: Double,
        fatGoal: Double,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )

    fun getDailySummary(
        date: String,
        onSuccess: (DailySummaryModel) -> Unit,
        onError: (Exception) -> Unit
    )

    fun getWeeklySummary(
        startDate: String,
        endDate: String,
        onSuccess: (List<DailySummaryModel>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun getMonthlySummary(
        month: String,
        onSuccess: (List<DailySummaryModel>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun getTotalCaloriesForDate(
        date: String,
        onSuccess: (Double) -> Unit,
        onError: (Exception) -> Unit
    )

    fun getAverageCaloriesForWeek(
        startDate: String,
        endDate: String,
        onSuccess: (Double) -> Unit,
        onError: (Exception) -> Unit
    )

    fun getMealHistoryByType(
        mealType: String,
        limit: Int,
        onSuccess: (List<FoodItemModel>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun getCurrentUserId(): String?
}