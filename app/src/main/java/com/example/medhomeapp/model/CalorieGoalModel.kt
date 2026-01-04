package com.example.medhomeapp.model

data class CalorieGoalModel(
    val targetCalories: Double = 2000.0,
    val proteinGoal: Double = 0.0,
    val carbsGoal: Double = 0.0,
    val fatGoal: Double = 0.0,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "targetCalories" to targetCalories,
            "proteinGoal" to proteinGoal,
            "carbsGoal" to carbsGoal,
            "fatGoal" to fatGoal,
            "updatedAt" to updatedAt
        )
    }

    fun hasProteinGoal(): Boolean = proteinGoal > 0.0

    fun hasCarbsGoal(): Boolean = carbsGoal > 0.0

    fun hasFatGoal(): Boolean = fatGoal > 0.0

    fun hasMacroGoals(): Boolean = hasProteinGoal() || hasCarbsGoal() || hasFatGoal()
}
