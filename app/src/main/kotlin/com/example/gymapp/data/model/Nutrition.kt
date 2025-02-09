package com.example.gymapp.data.model

import java.time.LocalDate
import java.time.LocalDateTime

data class NutritionGoals(
    val calories: Int,
    val protein: Int,  // in grams
    val carbs: Int,    // in grams
    val fats: Int      // in grams
)

data class NutritionEntry(
    val id: Long,
    val date: LocalDate,
    val mealType: MealType,
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK,
    PRE_WORKOUT,
    POST_WORKOUT;

    override fun toString(): String {
        return name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
    }
}

data class DailyNutrition(
    val date: LocalDate,
    val entries: List<NutritionEntry>
) {
    val totalCalories: Int
        get() = entries.sumOf { it.calories }
    
    val totalProtein: Float
        get() = entries.sumOf { it.protein.toDouble() }.toFloat()
    
    val totalCarbs: Float
        get() = entries.sumOf { it.carbs.toDouble() }.toFloat()
    
    val totalFats: Float
        get() = entries.sumOf { it.fats.toDouble() }.toFloat()
} 