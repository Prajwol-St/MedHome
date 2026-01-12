package com.example.medhomeapp.model

data class DailySummaryModel(
    val date: String = "",
    val foodItems: List<FoodItemModel> = emptyList(),
    val totalCalories: Double = 0.0,
    val totalProtein: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val totalFat: Double = 0.0,
    val goal: CalorieGoalModel? = null,
    val remainingCalories: Double = 0.0,
    val calorieProgress: Float = 0f,
    val mealCount: Int = 0

){
    fun isGoalMet(): Boolean = goal != null && totalCalories >= goal.targetCalories

    fun isOverGoal(): Boolean = goal != null && totalCalories > goal.targetCalories

    fun isUnderGoal(): Boolean = goal != null && totalCalories < goal.targetCalories

    fun getPercentageConsumed(): Int {
        return if (goal != null && goal.targetCalories > 0) {
            ((totalCalories / goal.targetCalories) * 100).toInt()
        } else 0
    }

    fun hasBreakfast(): Boolean = foodItems.any { it.isBreakfast() }

    fun hasLunch(): Boolean = foodItems.any { it.isLunch() }

    fun hasDinner(): Boolean = foodItems.any { it.isDinner() }

    fun getBreakfastItems(): List<FoodItemModel> = foodItems.filter { it.isBreakfast() }

    fun getLunchItems(): List<FoodItemModel> = foodItems.filter { it.isLunch() }

    fun getDinnerItems(): List<FoodItemModel> = foodItems.filter { it.isDinner() }

    fun getSnackItems(): List<FoodItemModel> = foodItems.filter { it.isSnack() }

    fun getBreakfastCalories(): Double = getBreakfastItems().sumOf { it.getTotalCalories() }

    fun getLunchCalories(): Double = getLunchItems().sumOf { it.getTotalCalories() }

    fun getDinnerCalories(): Double = getDinnerItems().sumOf { it.getTotalCalories() }

    fun getSnackCalories(): Double = getSnackItems().sumOf { it.getTotalCalories() }

}
