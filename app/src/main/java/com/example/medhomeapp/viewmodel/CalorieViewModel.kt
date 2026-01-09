package com.example.medhomeapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.CalorieGoalModel
import com.example.medhomeapp.model.DailySummaryModel
import com.example.medhomeapp.model.FoodItemModel
import com.example.medhomeapp.model.api.FoodSearchResult
import com.example.medhomeapp.repository.CalorieRepository
import com.example.medhomeapp.repository.CalorieRepositoryImpl
import com.example.medhomeapp.repository.NutritionRepo
import com.example.medhomeapp.repository.NutritionRepoImpl
import com.example.medhomeapp.utils.getCurrentDate
import com.example.medhomeapp.utils.toFoodItemModel

class CalorieViewModel : ViewModel() {

    private val calorieRepository: CalorieRepository = CalorieRepositoryImpl()
    private val nutritionRepository: NutritionRepo = NutritionRepoImpl()

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _searchResults = MutableLiveData<List<FoodSearchResult>>(emptyList())
    val searchResults: LiveData<List<FoodSearchResult>> = _searchResults

    private val _isSearching = MutableLiveData<Boolean>(false)
    val isSearching: LiveData<Boolean> = _isSearching

    private val _searchError = MutableLiveData<String?>(null)
    val searchError: LiveData<String?> = _searchError

    private val _selectedDate = MutableLiveData<String>(getCurrentDate())
    val selectedDate: LiveData<String> = _selectedDate

    private val _todaysFoodItems = MutableLiveData<List<FoodItemModel>>(emptyList())
    val todaysFoodItems: LiveData<List<FoodItemModel>> = _todaysFoodItems

    private val _isLoadingFoodItems = MutableLiveData<Boolean>(false)
    val isLoadingFoodItems: LiveData<Boolean> = _isLoadingFoodItems

    private val _foodItemsError = MutableLiveData<String?>(null)
    val foodItemsError: LiveData<String?> = _foodItemsError

    private val _dailySummary = MutableLiveData<DailySummaryModel?>(null)
    val dailySummary: LiveData<DailySummaryModel?> = _dailySummary

    private val _isLoadingSummary = MutableLiveData<Boolean>(false)
    val isLoadingSummary: LiveData<Boolean> = _isLoadingSummary

    private val _calorieGoal = MutableLiveData<CalorieGoalModel?>(null)
    val calorieGoal: LiveData<CalorieGoalModel?> = _calorieGoal

    private val _isLoadingGoal = MutableLiveData<Boolean>(false)
    val isLoadingGoal: LiveData<Boolean> = _isLoadingGoal

    private val _isAddingFood = MutableLiveData<Boolean>(false)
    val isAddingFood: LiveData<Boolean> = _isAddingFood

    private val _addFoodSuccess = MutableLiveData<Boolean?>(null)
    val addFoodSuccess: LiveData<Boolean?> = _addFoodSuccess

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _successMessage = MutableLiveData<String?>(null)
    val successMessage: LiveData<String?> = _successMessage


    init {
        loadTodaysData()
        loadCalorieGoal()
    }

    fun searchFoods(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _searchError.value = null
            return
        }

        _searchQuery.value = query
        _isSearching.value = true
        _searchError.value = null

        nutritionRepository.searchFoods(
            query = query,
            pageSize = 20,
            onSuccess = { results ->
                _searchResults.value = results
                _isSearching.value = false
                if (results.isEmpty()) {
                    _searchError.value = "No foods found for '$query'"
                }
            },
            onError = { error ->
                _searchError.value = error.message ?: "Failed to search foods"
                _isSearching.value = false
                _searchResults.value = emptyList()
            }
        )
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
        _searchQuery.value = ""
        _searchError.value = null
    }

    fun addFoodFromSearch(
        searchResult: FoodSearchResult,
        servingAmount: Double = 1.0,
        mealType: String = "other"
    ) {
        val foodItem = searchResult.toFoodItemModel(servingAmount, mealType)
        addFoodItem(foodItem)
    }

    fun addFoodItem(foodItem: FoodItemModel) {
        _isAddingFood.value = true
        _errorMessage.value = null

        calorieRepository.addFoodItem(
            foodItem = foodItem,
            onSuccess = {
                _isAddingFood.value = false
                _addFoodSuccess.value = true
                _successMessage.value = "Food added successfully"
                loadFoodItemsByDate(_selectedDate.value ?: getCurrentDate())
                clearSearchResults()
            },
            onError = { error ->
                _isAddingFood.value = false
                _addFoodSuccess.value = false
                _errorMessage.value = error.message ?: "Failed to add food"
            }
        )
    }
    fun addManualFood(
        name: String,
        calories: Double,
        protein: Double = 0.0,
        carbs: Double = 0.0,
        fat: Double = 0.0,
        servingSize: String = "1 serving",
        servingAmount: Double = 1.0,
        mealType: String = "other"
    ) {
        val foodItem = FoodItemModel(
            name = name,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            servingSize = servingSize,
            servingAmount = servingAmount,
            mealType = mealType,
            apiSource = "manual",
            date = _selectedDate.value ?: getCurrentDate()
        )

        addFoodItem(foodItem)
    }

    fun deleteFoodItem(foodItemId: String) {
        calorieRepository.deleteFoodItem(
            foodItemId = foodItemId,
            onSuccess = {
                _successMessage.value = "Food deleted successfully"
                loadFoodItemsByDate(_selectedDate.value ?: getCurrentDate())
            },
            onError = { error ->
                _errorMessage.value = error.message ?: "Failed to delete food"
            }
        )
    }

    fun updateFoodItem(foodItemId: String, foodItem: FoodItemModel) {
        calorieRepository.updateFoodItem(
            foodItemId = foodItemId,
            foodItem = foodItem,
            onSuccess = {
                _successMessage.value = "Food updated successfully"
                loadFoodItemsByDate(_selectedDate.value ?: getCurrentDate())
            },
            onError = { error ->
                _errorMessage.value = error.message ?: "Failed to update food"
            }
        )
    }

    fun loadFoodItemsByDate(date: String) {
        _isLoadingFoodItems.value = true
        _foodItemsError.value = null

        calorieRepository.getFoodItemsByDate(
            date = date,
            onSuccess = { items ->
                _todaysFoodItems.value = items
                _isLoadingFoodItems.value = false
                loadDailySummary(date)
            },
            onError = { error ->
                _foodItemsError.value = error.message ?: "Failed to load food items"
                _isLoadingFoodItems.value = false
            }
        )
    }

    fun loadAllFoodItems() {
        _isLoadingFoodItems.value = true

        calorieRepository.getAllFoodItems(
            onSuccess = { items ->
                _todaysFoodItems.value = items
                _isLoadingFoodItems.value = false
            },
            onError = { error ->
                _foodItemsError.value = error.message ?: "Failed to load food items"
                _isLoadingFoodItems.value = false
            }
        )
    }

    fun setCalorieGoal(
        targetCalories: Double,
        proteinGoal: Double = 0.0,
        carbsGoal: Double = 0.0,
        fatGoal: Double = 0.0
    ) {
        val goal = CalorieGoalModel(
            targetCalories = targetCalories,
            proteinGoal = proteinGoal,
            carbsGoal = carbsGoal,
            fatGoal = fatGoal
        )

        calorieRepository.setCalorieGoal(
            goal = goal,
            onSuccess = {
                _calorieGoal.value = goal
                _successMessage.value = "Goal set successfully"
                loadDailySummary(_selectedDate.value ?: getCurrentDate())
            },
            onError = { error ->
                _errorMessage.value = error.message ?: "Failed to set goal"
            }
        )
    }


    fun updateCalorieGoal(targetCalories: Double) {
        calorieRepository.updateCalorieGoal(
            targetCalories = targetCalories,
            onSuccess = {
                _successMessage.value = "Goal updated successfully"
                loadCalorieGoal()
                loadDailySummary(_selectedDate.value ?: getCurrentDate())
            },
            onError = { error ->
                _errorMessage.value = error.message ?: "Failed to update goal"
            }
        )
    }

    fun loadCalorieGoal() {
        _isLoadingGoal.value = true

        calorieRepository.getCalorieGoal(
            onSuccess = { goal ->
                _calorieGoal.value = goal
                _isLoadingGoal.value = false
            },
            onError = { error ->
                _errorMessage.value = error.message ?: "Failed to load goal"
                _isLoadingGoal.value = false
            }
        )
    }

    fun loadDailySummary(date: String) {
        _isLoadingSummary.value = true

        calorieRepository.getDailySummary(
            date = date,
            onSuccess = { summary ->
                _dailySummary.value = summary
                _isLoadingSummary.value = false
            },
            onError = { error ->
                _errorMessage.value = error.message ?: "Failed to load summary"
                _isLoadingSummary.value = false
            }
        )
    }
    fun loadTodaysData() {
        val today = getCurrentDate()
        _selectedDate.value = today
        loadFoodItemsByDate(today)
        loadDailySummary(today)
    }

    fun changeDate(date: String) {
        _selectedDate.value = date
        loadFoodItemsByDate(date)
        loadDailySummary(date)
    }

    fun loadWeeklySummary(startDate: String, endDate: String) {
        calorieRepository.getWeeklySummary(
            startDate = startDate,
            endDate = endDate,
            onSuccess = { summaries -> },
            onError = { error ->
                _errorMessage.value = error.message ?: "Failed to load weekly summary"
            }
        )
    }
    fun getTotalCaloriesForDate(date: String, callback: (Double) -> Unit) {
        calorieRepository.getTotalCaloriesForDate(
            date = date,
            onSuccess = callback,
            onError = { error ->
                _errorMessage.value = error.message ?: "Failed to get total calories"
            }
        )
    }

    fun getAverageCaloriesForWeek(startDate: String, endDate: String, callback: (Double) -> Unit) {
        calorieRepository.getAverageCaloriesForWeek(
            startDate = startDate,
            endDate = endDate,
            onSuccess = callback,
            onError = { error ->
                _errorMessage.value = error.message ?: "Failed to get average calories"
            }
        )
    }

    fun getFoodItemsByMealType(date: String, mealType: String) {
        calorieRepository.getFoodItemByMealType(
            date = date,
            mealType = mealType,
            onSuccess = { items -> },
            onError = { error ->
                _errorMessage.value = error.message ?: "Failed to load meal items"
            }
        )
    }
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
        _searchError.value = null
    }

    fun resetAddFoodSuccess() {
        _addFoodSuccess.value = null
    }

    fun getCurrentUserId(): String? {
        return calorieRepository.getCurrentUserId()
    }

}