package com.example.gymapp.data.model

data class Exercise(
    val id: Long,
    val name: String,
    val category: ExerciseCategory
)

enum class ExerciseCategory {
    CHEST,
    SHOULDERS,
    TRICEPS,
    BACK,
    BICEPS,
    LEGS
}

object ExerciseList {
    val exercises = listOf(
        Exercise(1, "Incline Bench Press", ExerciseCategory.CHEST),
        Exercise(2, "Tricep Pushdown Rope", ExerciseCategory.TRICEPS),
        Exercise(3, "Barbell Squats", ExerciseCategory.LEGS),
        Exercise(4, "Shoulder Press", ExerciseCategory.SHOULDERS),
        Exercise(5, "Lat Pulldown", ExerciseCategory.BACK),
        Exercise(6, "Bicep Curls", ExerciseCategory.BICEPS)
    )
} 