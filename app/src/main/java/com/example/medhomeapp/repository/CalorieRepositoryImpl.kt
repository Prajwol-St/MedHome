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
        val ref = getCalorieTrackerRef()?.child("foodItems")?.child(foodItemId) ?: run {
            onError(Exception("User not logged in"))
            return
        }

        ref.removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }

    }

    override fun getFoodItemByMealType(
        date: String,
        mealType: String,
        onSuccess: (List<FoodItemModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        getFoodItemsByDate(date, { items ->
            val filteredItems = items.filter { it.mealType == mealType }
            onSuccess(filteredItems)
        }, onError)
    }

    override fun getFoodItemByDateRange(
        startDate: String,
        endDate: String,
        onSuccess: (List<FoodItemModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ref = getCalorieTrackerRef()?.child("foodItems") ?: run {
            onError(Exception("User not logged in"))
            return
        }

        ref.orderByChild("date").startAt(startDate).endAt(endDate)
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

    override fun setCalorieGoal(
        goal: CalorieGoalModel,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ref = getCalorieTrackerRef()?.child("calorieGoal") ?: run {
            onError(Exception("User not logged in"))
            return
        }

        ref.setValue(goal.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun getCalorieGoal(
        onSuccess: (CalorieGoalModel?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ref = getCalorieTrackerRef()?.child("calorieGoal") ?: run {
            onError(Exception("User not logged in"))
            return
        }

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val goal = snapshot.getValue(CalorieGoalModel::class.java)
                onSuccess(goal)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
    }

    override fun updateCalorieGoal(
        targetCalories: Double,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ref = getCalorieTrackerRef()?.child("calorieGoal") ?: run {
            onError(Exception("User not logged in"))
            return
        }

        val updates = mapOf(
            "targetCalories" to targetCalories,
            "updatedAt" to System.currentTimeMillis()
        )

        ref.updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun updateMacroGoals(
        proteinGoal: Double,
        carbsGoal: Double,
        fatGoal: Double,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ref = getCalorieTrackerRef()?.child("calorieGoal") ?: run {
            onError(Exception("User not logged in"))
            return
        }

        val updates = mapOf(
            "proteinGoal" to proteinGoal,
            "carbsGoal" to carbsGoal,
            "fatGoal" to fatGoal,
            "updatedAt" to System.currentTimeMillis()
        )

        ref.updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    override fun getDailySummary(
        date: String,
        onSuccess: (DailySummaryModel) -> Unit,
        onError: (Exception) -> Unit
    ) {
        getFoodItemsByDate(date, { foodItems ->
            getCalorieGoal({ goal ->
                val totalCalories = foodItems.sumOf { it.getTotalCalories() }
                val totalProtein = foodItems.sumOf { it.getTotalProtein() }
                val totalCarbs = foodItems.sumOf { it.getTotalCarbs() }
                val totalFat = foodItems.sumOf { it.getTotalFat() }

                val remainingCalories = (goal?.targetCalories ?: 0.0) - totalCalories
                val progress = if (goal != null && goal.targetCalories > 0) {
                    (totalCalories / goal.targetCalories).toFloat().coerceIn(0f, 1f)
                } else 0f

                val summary = DailySummaryModel(
                    date = date,
                    foodItems = foodItems,
                    totalCalories = totalCalories,
                    totalProtein = totalProtein,
                    totalCarbs = totalCarbs,
                    totalFat = totalFat,
                    goal = goal,
                    remainingCalories = remainingCalories,
                    calorieProgress = progress,
                    mealCount = foodItems.size
                )

                onSuccess(summary)
            }, onError)
        }, onError)
    }

    override fun getWeeklySummary(
        startDate: String,
        endDate: String,
        onSuccess: (List<DailySummaryModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        getFoodItemByDateRange(startDate, endDate, { foodItems ->
            getCalorieGoal({ goal ->
                val summaries = mutableListOf<DailySummaryModel>()
                    val groupedByDate = foodItems.groupBy { it.date }

                for ((date, items) in groupedByDate) {
                    val totalCalories = items.sumOf { it.getTotalCalories() }
                    val totalProtein = items.sumOf { it.getTotalProtein() }
                    val totalCarbs = items.sumOf { it.getTotalCarbs() }
                    val totalFat = items.sumOf { it.getTotalFat() }

                    val remainingCalories = (goal?.targetCalories ?: 0.0) - totalCalories
                    val progress = if (goal != null && goal.targetCalories > 0) {
                        (totalCalories / goal.targetCalories).toFloat().coerceIn(0f, 1f)
                    } else 0f

                    summaries.add(
                        DailySummaryModel(
                            date = date,
                            foodItems = items,
                            totalCalories = totalCalories,
                            totalProtein = totalProtein,
                            totalCarbs = totalCarbs,
                            totalFat = totalFat,
                            goal = goal,
                            remainingCalories = remainingCalories,
                            calorieProgress = progress,
                            mealCount = items.size
                        )
                    )
                }

                onSuccess(summaries.sortedBy { it.date })
            }, onError)
        }, onError)
    }

    override fun getMonthlySummary(
        month: String,
        onSuccess: (List<DailySummaryModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ref = getCalorieTrackerRef()?.child("foodItems") ?: run {
            onError(Exception("User not logged in"))
            return
        }

        ref.orderByChild("date").startAt(month).endAt("$month\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<FoodItemModel>()
                    for (child in snapshot.children) {
                        child.getValue(FoodItemModel::class.java)?.let { items.add(it) }
                    }

                    getCalorieGoal({ goal ->
                        val summaries = mutableListOf<DailySummaryModel>()
                        val groupedByDate = items.groupBy { it.date }

                        for ((date, dailyItems) in groupedByDate) {
                            val totalCalories = dailyItems.sumOf { it.getTotalCalories() }
                            val totalProtein = dailyItems.sumOf { it.getTotalProtein() }
                            val totalCarbs = dailyItems.sumOf { it.getTotalCarbs() }
                            val totalFat = dailyItems.sumOf { it.getTotalFat() }

                            val remainingCalories = (goal?.targetCalories ?: 0.0) - totalCalories
                            val progress = if (goal != null && goal.targetCalories > 0) {
                                (totalCalories / goal.targetCalories).toFloat().coerceIn(0f, 1f)
                            } else 0f

                            summaries.add(
                                DailySummaryModel(
                                    date = date,
                                    foodItems = dailyItems,
                                    totalCalories = totalCalories,
                                    totalProtein = totalProtein,
                                    totalCarbs = totalCarbs,
                                    totalFat = totalFat,
                                    goal = goal,
                                    remainingCalories = remainingCalories,
                                    calorieProgress = progress,
                                    mealCount = dailyItems.size
                                )
                            )
                        }

                        onSuccess(summaries.sortedBy { it.date })
                    }, onError)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }

    override fun getTotalCaloriesForDate(
        date: String,
        onSuccess: (Double) -> Unit,
        onError: (Exception) -> Unit
    ) {
        getFoodItemsByDate(date, { items ->
            val total = items.sumOf { it.getTotalCalories() }
            onSuccess(total)
        }, onError)
    }

    override fun getAverageCaloriesForWeek(
        startDate: String,
        endDate: String,
        onSuccess: (Double) -> Unit,
        onError: (Exception) -> Unit
    ) {
        getFoodItemByDateRange(startDate, endDate, { items ->
            val groupedByDate = items.groupBy { it.date }
            val dailyTotals = groupedByDate.map { (_, dailyItems) ->
                dailyItems.sumOf { it.getTotalCalories() }
            }
            val average = if (dailyTotals.isNotEmpty()) {
                dailyTotals.average()
            } else 0.0
            onSuccess(average)
        }, onError)
    }

    override fun getMealHistoryByType(
        mealType: String,
        limit: Int,
        onSuccess: (List<FoodItemModel>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        getAllFoodItems({ items ->
            val filteredItems = items.filter { it.mealType == mealType }.take(limit)
            onSuccess(filteredItems)
        }, onError)
    }

    override fun getCurrentUserId(): String? = getUserId()


}