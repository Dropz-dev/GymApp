package com.example.gymapp.data.model

data class Exercise(
    val id: Long,
    val name: String,
    val category: ExerciseCategory
)

enum class ExerciseCategory {
    CHEST, SHOULDERS, TRICEPS, BACK, BICEPS, LEGS;

    override fun toString(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

object ExerciseList {
    val allExercises = listOf(
        Exercise(1, "Incline Bench Press", ExerciseCategory.CHEST),
        Exercise(2, "Chest Machine", ExerciseCategory.CHEST),
        Exercise(3, "Tricep Pushdown Rope", ExerciseCategory.TRICEPS),
        Exercise(4, "Tricep Pushdown Bar", ExerciseCategory.TRICEPS),
        Exercise(5, "Lateral Raise", ExerciseCategory.SHOULDERS),
        Exercise(6, "Shoulder Press Machine", ExerciseCategory.SHOULDERS),
        Exercise(7, "Dips", ExerciseCategory.TRICEPS),
        Exercise(8, "Close Grip Bench", ExerciseCategory.TRICEPS),
        Exercise(9, "Butterfly", ExerciseCategory.CHEST),
        Exercise(10, "Dumbbell Shoulder Press", ExerciseCategory.SHOULDERS),
        Exercise(11, "Lat Pulldown", ExerciseCategory.BACK),
        Exercise(12, "Lat Machine", ExerciseCategory.BACK),
        Exercise(13, "Nautilus Machine", ExerciseCategory.BACK),
        Exercise(14, "Row Machine", ExerciseCategory.BACK),
        Exercise(15, "SZ Curls", ExerciseCategory.BICEPS),
        Exercise(16, "Strict Curls", ExerciseCategory.BICEPS),
        Exercise(17, "Shrugs", ExerciseCategory.SHOULDERS),
        Exercise(18, "Preacher Curls", ExerciseCategory.BICEPS),
        Exercise(19, "Reverse Butterfly", ExerciseCategory.BACK),
        Exercise(20, "Smith Machine Squats", ExerciseCategory.LEGS),
        Exercise(21, "Barbell Squats", ExerciseCategory.LEGS),
        Exercise(22, "Calve Raises Seated", ExerciseCategory.LEGS),
        Exercise(23, "Calve Raises Standing", ExerciseCategory.LEGS),
        Exercise(24, "RDLs", ExerciseCategory.LEGS),
        Exercise(25, "Legpress", ExerciseCategory.LEGS),
        Exercise(26, "Behind The Back Tricep", ExerciseCategory.TRICEPS),
        Exercise(27, "Cable Curls", ExerciseCategory.BICEPS),
        Exercise(28, "Seated Incline Curls", ExerciseCategory.BICEPS),
        Exercise(29, "Cable Tricep Behind The Back", ExerciseCategory.TRICEPS),
        Exercise(30, "Brachialis", ExerciseCategory.BICEPS)
    )
} 