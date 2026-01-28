package com.example.medhomeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.CalorieGoalModel
import com.example.medhomeapp.model.DailySummaryModel
import com.example.medhomeapp.model.FoodItemModel
import com.example.medhomeapp.model.api.FoodSearchResult
import com.example.medhomeapp.repository.CalorieRepository
import com.example.medhomeapp.repository.NutritionRepo
import com.example.medhomeapp.viewmodel.CalorieViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class CalorieViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun searchFoods_success_test() {
        val nutritionRepo = mock<NutritionRepo>()
        val viewModel = CalorieViewModel()

        val mockResults = listOf(
            FoodSearchResult(
                fdcId = "1",
                description = "Apple",
                dataType = "Foundation",
                brandOwner = "",
                foodNutrients = emptyList()
            ),
            FoodSearchResult(
                fdcId = "2",
                description = "Banana",
                dataType = "Foundation",
                brandOwner = "",
                foodNutrients = emptyList()
            )
        )

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<FoodSearchResult>) -> Unit>(2)
            onSuccess(mockResults)
            null
        }.`when`(nutritionRepo).searchFoods(any(), any(), any(), any())

        viewModel.searchFoods("apple")

        assertEquals(mockResults, viewModel.searchResults.value)
        assertEquals(false, viewModel.isSearching.value)
        assertNull(viewModel.searchError.value)

        verify(nutritionRepo).searchFoods(any(), any(), any(), any())
    }

    @Test
    fun searchFoods_error_test() {
        val nutritionRepo = mock<NutritionRepo>()
        val viewModel = CalorieViewModel()

        val errorMsg = "Failed to search foods"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(3)
            onError(Exception(errorMsg))
            null
        }.`when`(nutritionRepo).searchFoods(any(), any(), any(), any())

        viewModel.searchFoods("apple")

        assertEquals(errorMsg, viewModel.searchError.value)
        assertEquals(false, viewModel.isSearching.value)

        verify(nutritionRepo).searchFoods(any(), any(), any(), any())
    }

    @Test
    fun addFoodItem_success_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        val newFood = FoodItemModel(
            name = "Apple",
            calories = 95.0,
            protein = 0.5,
            carbs = 25.0,
            fat = 0.3,
            servingSize = "1 medium",
            servingAmount = 1.0,
            mealType = "snack",
            apiSource = "usda",
            date = "2024-01-15"
        )

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            onSuccess()
            null
        }.`when`(calorieRepo).addFoodItem(eq(newFood), any(), any())

        viewModel.addFoodItem(newFood)

        assertEquals("Food added successfully", viewModel.successMessage.value)
        assertEquals(false, viewModel.isAddingFood.value)
        assertNull(viewModel.errorMessage.value)

        verify(calorieRepo).addFoodItem(eq(newFood), any(), any())
    }

    @Test
    fun addFoodItem_error_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        val newFood = FoodItemModel(
            name = "Apple",
            calories = 95.0,
            protein = 0.5,
            carbs = 25.0,
            fat = 0.3,
            servingSize = "1 medium",
            servingAmount = 1.0,
            mealType = "snack",
            apiSource = "usda",
            date = "2024-01-15"
        )
        val errorMsg = "Failed to add food"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(2)
            onError(Exception(errorMsg))
            null
        }.`when`(calorieRepo).addFoodItem(eq(newFood), any(), any())

        viewModel.addFoodItem(newFood)

        assertEquals(errorMsg, viewModel.errorMessage.value)
        assertEquals(false, viewModel.isAddingFood.value)
        assertNull(viewModel.successMessage.value)

        verify(calorieRepo).addFoodItem(eq(newFood), any(), any())
    }

    @Test
    fun deleteFoodItem_success_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        val foodItemId = "food123"

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            onSuccess()
            null
        }.`when`(calorieRepo).deleteFoodItem(eq(foodItemId), any(), any())

        viewModel.deleteFoodItem(foodItemId)

        assertEquals("Food deleted successfully", viewModel.successMessage.value)
        assertNull(viewModel.errorMessage.value)

        verify(calorieRepo).deleteFoodItem(eq(foodItemId), any(), any())
    }

    @Test
    fun deleteFoodItem_error_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        val foodItemId = "food123"
        val errorMsg = "Failed to delete food"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(2)
            onError(Exception(errorMsg))
            null
        }.`when`(calorieRepo).deleteFoodItem(eq(foodItemId), any(), any())

        viewModel.deleteFoodItem(foodItemId)

        assertEquals(errorMsg, viewModel.errorMessage.value)
        assertNull(viewModel.successMessage.value)

        verify(calorieRepo).deleteFoodItem(eq(foodItemId), any(), any())
    }

    @Test
    fun updateFoodItem_success_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        val foodItemId = "food123"
        val updatedFood = FoodItemModel(
            name = "Apple Updated",
            calories = 100.0,
            protein = 0.6,
            carbs = 26.0,
            fat = 0.4,
            servingSize = "1 large",
            servingAmount = 1.0,
            mealType = "snack",
            apiSource = "usda",
            date = "2024-01-15"
        )

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(2)
            onSuccess()
            null
        }.`when`(calorieRepo).updateFoodItem(eq(foodItemId), eq(updatedFood), any(), any())

        viewModel.updateFoodItem(foodItemId, updatedFood)

        assertEquals("Food updated successfully", viewModel.successMessage.value)
        assertNull(viewModel.errorMessage.value)

        verify(calorieRepo).updateFoodItem(eq(foodItemId), eq(updatedFood), any(), any())
    }

    @Test
    fun updateFoodItem_error_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        val foodItemId = "food123"
        val updatedFood = FoodItemModel(
            name = "Apple Updated",
            calories = 100.0,
            protein = 0.6,
            carbs = 26.0,
            fat = 0.4,
            servingSize = "1 large",
            servingAmount = 1.0,
            mealType = "snack",
            apiSource = "usda",
            date = "2024-01-15"
        )
        val errorMsg = "Failed to update food"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(3)
            onError(Exception(errorMsg))
            null
        }.`when`(calorieRepo).updateFoodItem(eq(foodItemId), eq(updatedFood), any(), any())

        viewModel.updateFoodItem(foodItemId, updatedFood)

        assertEquals(errorMsg, viewModel.errorMessage.value)
        assertNull(viewModel.successMessage.value)

        verify(calorieRepo).updateFoodItem(eq(foodItemId), eq(updatedFood), any(), any())
    }

    @Test
    fun loadFoodItemsByDate_success_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        val mockFoodItems = listOf(
            FoodItemModel(
                name = "Apple",
                calories = 95.0,
                protein = 0.5,
                carbs = 25.0,
                fat = 0.3,
                servingSize = "1 medium",
                servingAmount = 1.0,
                mealType = "snack",
                apiSource = "usda",
                date = "2024-01-15"
            ),
            FoodItemModel(
                name = "Banana",
                calories = 105.0,
                protein = 1.3,
                carbs = 27.0,
                fat = 0.4,
                servingSize = "1 medium",
                servingAmount = 1.0,
                mealType = "breakfast",
                apiSource = "usda",
                date = "2024-01-15"
            )
        )

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<FoodItemModel>) -> Unit>(1)
            onSuccess(mockFoodItems)
            null
        }.`when`(calorieRepo).getFoodItemsByDate(any(), any(), any())

        viewModel.loadFoodItemsByDate("2024-01-15")

        assertEquals(mockFoodItems, viewModel.todaysFoodItems.value)
        assertEquals(false, viewModel.isLoadingFoodItems.value)
        assertNull(viewModel.foodItemsError.value)

        verify(calorieRepo).getFoodItemsByDate(any(), any(), any())
    }

    @Test
    fun loadFoodItemsByDate_error_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        val errorMsg = "Failed to load food items"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(2)
            onError(Exception(errorMsg))
            null
        }.`when`(calorieRepo).getFoodItemsByDate(any(), any(), any())

        viewModel.loadFoodItemsByDate("2024-01-15")

        assertEquals(errorMsg, viewModel.foodItemsError.value)
        assertEquals(false, viewModel.isLoadingFoodItems.value)

        verify(calorieRepo).getFoodItemsByDate(any(), any(), any())
    }

    @Test
    fun setCalorieGoal_success_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        val goal = CalorieGoalModel(
            targetCalories = 2000.0,
            proteinGoal = 150.0,
            carbsGoal = 250.0,
            fatGoal = 65.0
        )

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            onSuccess()
            null
        }.`when`(calorieRepo).setCalorieGoal(eq(goal), any(), any())

        viewModel.setCalorieGoal(2000.0, 150.0, 250.0, 65.0)

        assertEquals("Goal set successfully", viewModel.successMessage.value)
        assertNull(viewModel.errorMessage.value)

        verify(calorieRepo).setCalorieGoal(eq(goal), any(), any())
    }

    @Test
    fun setCalorieGoal_error_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        val goal = CalorieGoalModel(
            targetCalories = 2000.0,
            proteinGoal = 150.0,
            carbsGoal = 250.0,
            fatGoal = 65.0
        )
        val errorMsg = "Failed to set goal"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(2)
            onError(Exception(errorMsg))
            null
        }.`when`(calorieRepo).setCalorieGoal(eq(goal), any(), any())

        viewModel.setCalorieGoal(2000.0, 150.0, 250.0, 65.0)

        assertEquals(errorMsg, viewModel.errorMessage.value)
        assertNull(viewModel.successMessage.value)

        verify(calorieRepo).setCalorieGoal(eq(goal), any(), any())
    }

    @Test
    fun updateCalorieGoal_success_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            onSuccess()
            null
        }.`when`(calorieRepo).updateCalorieGoal(any(), any(), any())

        viewModel.updateCalorieGoal(2500.0)

        assertEquals("Goal updated successfully", viewModel.successMessage.value)
        assertNull(viewModel.errorMessage.value)

        verify(calorieRepo).updateCalorieGoal(any(), any(), any())
    }

    @Test
    fun updateCalorieGoal_error_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        val errorMsg = "Failed to update goal"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(2)
            onError(Exception(errorMsg))
            null
        }.`when`(calorieRepo).updateCalorieGoal(any(), any(), any())

        viewModel.updateCalorieGoal(2500.0)

        assertEquals(errorMsg, viewModel.errorMessage.value)
        assertNull(viewModel.successMessage.value)

        verify(calorieRepo).updateCalorieGoal(any(), any(), any())
    }

    @Test
    fun loadCalorieGoal_success_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        val mockGoal = CalorieGoalModel(
            targetCalories = 2000.0,
            proteinGoal = 150.0,
            carbsGoal = 250.0,
            fatGoal = 65.0
        )

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(CalorieGoalModel) -> Unit>(0)
            onSuccess(mockGoal)
            null
        }.`when`(calorieRepo).getCalorieGoal(any(), any())

        viewModel.loadCalorieGoal()

        assertEquals(mockGoal, viewModel.calorieGoal.value)
        assertEquals(false, viewModel.isLoadingGoal.value)
        assertNull(viewModel.errorMessage.value)

        verify(calorieRepo).getCalorieGoal(any(), any())
    }

    @Test
    fun loadCalorieGoal_error_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        val errorMsg = "Failed to load goal"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(1)
            onError(Exception(errorMsg))
            null
        }.`when`(calorieRepo).getCalorieGoal(any(), any())

        viewModel.loadCalorieGoal()

        assertEquals(errorMsg, viewModel.errorMessage.value)
        assertEquals(false, viewModel.isLoadingGoal.value)

        verify(calorieRepo).getCalorieGoal(any(), any())
    }

    @Test
    fun loadDailySummary_success_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        val mockSummary = DailySummaryModel(
            date = "2024-01-15",
            totalCalories = 1850.0,
            totalProtein = 120.0,
            totalCarbs = 200.0,
            totalFat = 60.0
        )

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(DailySummaryModel) -> Unit>(1)
            onSuccess(mockSummary)
            null
        }.`when`(calorieRepo).getDailySummary(any(), any(), any())

        viewModel.loadDailySummary("2024-01-15")

        assertEquals(mockSummary, viewModel.dailySummary.value)
        assertEquals(false, viewModel.isLoadingSummary.value)
        assertNull(viewModel.errorMessage.value)

        verify(calorieRepo).getDailySummary(any(), any(), any())
    }

    @Test
    fun loadDailySummary_error_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        val errorMsg = "Failed to load summary"

        doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(2)
            onError(Exception(errorMsg))
            null
        }.`when`(calorieRepo).getDailySummary(any(), any(), any())

        viewModel.loadDailySummary("2024-01-15")

        assertEquals(errorMsg, viewModel.errorMessage.value)
        assertEquals(false, viewModel.isLoadingSummary.value)

        verify(calorieRepo).getDailySummary(any(), any(), any())
    }

    @Test
    fun clearMessages_test() {
        val calorieRepo = mock<CalorieRepository>()
        val viewModel = CalorieViewModel()

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            onSuccess()
            null
        }.`when`(calorieRepo).addFoodItem(any(), any(), any())

        val food = FoodItemModel(
            name = "Test",
            calories = 100.0,
            protein = 5.0,
            carbs = 10.0,
            fat = 3.0,
            servingSize = "1 serving",
            servingAmount = 1.0,
            mealType = "snack",
            apiSource = "manual",
            date = "2024-01-01"
        )
        viewModel.addFoodItem(food)

        assertEquals("Food added successfully", viewModel.successMessage.value)

        viewModel.clearMessages()

        assertNull(viewModel.errorMessage.value)
        assertNull(viewModel.successMessage.value)
    }
}