package com.example.medhomeapp.repository

import com.example.medhomeapp.model.CalorieGoalModel
import com.example.medhomeapp.model.DailySummaryModel
import com.example.medhomeapp.model.FoodItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CalorieRepositoryImpl : CalorieRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private fun getUserId(): String? = auth.currentUser?.uid

    private fun getCalorieTrackerRef(): DatabaseReference? {
        val userId = getUserId() ?: return null
        return database.getReference("calorieTracker").child(userId)
    }
    override fun addFoodItem(
        foodItem: FoodItemModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ref = getCalorieTrackerRef()?.child("foodItems") ?: run {
            onError(Exception("User not logged in"))
            return
        }

        val newItemRef = ref.push()
        val itemId = newItemRef.key ?: run {
            onError(Exception("Failed to generate item ID"))
            return
        }

        val foodItemWithId = foodItem.copy(id = itemId)
        newItemRef.setValue(foodItemWithId.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun getFoodItemsByDate(
        date: String,
        onSuccess: (List<FoodItemModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ref = getCalorieTrackerRef()?.child("foodItems") ?: run {
            onError(Exception("User not logged in"))
            return
        }

        ref.orderByChild("date").equalTo(date)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<FoodItemModel>()
                    for (child in snapshot.children) {
                        child.getValue(FoodItemModel::class.java)?.let { items.add(it) }
                    }
                    onSuccess(items.sortedByDescending { it.timestamp })
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }

    override fun getAllFoodItems(
        onSuccess: (List<FoodItemModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ref = getCalorieTrackerRef()?.child("foodItems") ?: run {
            onError(Exception("User not logged in"))
            return
        }

        ref.orderByChild("timestamp")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<FoodItemModel>()
                    for (child in snapshot.children) {
                        child.getValue(FoodItemModel::class.java)?.let { items.add(it) }
                    }
                    onSuccess(items.sortedByDescending { it.timestamp })
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }

    override fun getFoodItemById(
        foodItemId: String,
        onSuccess: (FoodItemModel?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ref = getCalorieTrackerRef()?.child("foodItems")?.child(foodItemId) ?: run {
            onError(Exception("User not logged in"))
            return
        }

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val foodItem = snapshot.getValue(FoodItemModel::class.java)
                onSuccess(foodItem)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
    }

    override fun updateFoodItem(
        foodItemId: String,
        foodItem: FoodItemModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ref = getCalorieTrackerRef()?.child("foodItems")?.child(foodItemId) ?: run {
            onError(Exception("User not logged in"))
            return
        }

        ref.setValue(foodItem.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun deleteFoodItem(
        foodItemId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getFoodItemByMealType(
        date: String,
        mealType: String,
        onSuccess: (List<FoodItemModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getFoodItemByDateRange(
        startDate: String,
        endDate: String,
        onSuccess: (List<FoodItemModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun setCalorieGoal(
        goal: CalorieGoalModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getCalorieGoal(
        onSuccess: (CalorieGoalModel?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun updateCalorieGoal(
        targetCalories: Double,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun updateMacroGoals(
        proteinGoal: Double,
        carbsGoal: Double,
        fatGoal: Double,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getDailySummary(
        date: String,
        onSuccess: (DailySummaryModel) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getWeeklySummary(
        startDate: String,
        endDate: String,
        onSuccess: (List<DailySummaryModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getMonthlySummary(
        month: String,
        onSuccess: (List<DailySummaryModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getTotalCaloriesForDate(
        date: String,
        onSuccess: (Double) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getAverageCaloriesForWeek(
        startDate: String,
        endDate: String,
        onSuccess: (Double) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getMealHistoryByType(
        mealType: String,
        limit: Int,
        onSuccess: (List<FoodItemModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getCurrentUserId(): String? {
        TODO("Not yet implemented")
    }
}