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
        // Chest Exercises
        Exercise(1, "Bench Press", ExerciseCategory.CHEST),
        Exercise(2, "Incline Bench Press", ExerciseCategory.CHEST),
        Exercise(3, "Decline Bench Press", ExerciseCategory.CHEST),
        Exercise(4, "Dumbbell Flyes", ExerciseCategory.CHEST),
        Exercise(5, "Push-Ups", ExerciseCategory.CHEST),
        Exercise(6, "Cable Flyes", ExerciseCategory.CHEST),
        Exercise(7, "Dumbbell Press", ExerciseCategory.CHEST),

        // Shoulder Exercises
        Exercise(8, "Military Press", ExerciseCategory.SHOULDERS),
        Exercise(9, "Lateral Raises", ExerciseCategory.SHOULDERS),
        Exercise(10, "Front Raises", ExerciseCategory.SHOULDERS),
        Exercise(11, "Rear Delt Flyes", ExerciseCategory.SHOULDERS),
        Exercise(12, "Face Pulls", ExerciseCategory.SHOULDERS),
        Exercise(13, "Upright Rows", ExerciseCategory.SHOULDERS),
        Exercise(14, "Arnold Press", ExerciseCategory.SHOULDERS),

        // Triceps Exercises
        Exercise(15, "Tricep Pushdown", ExerciseCategory.TRICEPS),
        Exercise(16, "Skull Crushers", ExerciseCategory.TRICEPS),
        Exercise(17, "Overhead Tricep Extension", ExerciseCategory.TRICEPS),
        Exercise(18, "Diamond Push-Ups", ExerciseCategory.TRICEPS),
        Exercise(19, "Tricep Dips", ExerciseCategory.TRICEPS),
        Exercise(20, "Close Grip Bench Press", ExerciseCategory.TRICEPS),

        // Back Exercises
        Exercise(21, "Pull-Ups", ExerciseCategory.BACK),
        Exercise(22, "Lat Pulldown", ExerciseCategory.BACK),
        Exercise(23, "Barbell Rows", ExerciseCategory.BACK),
        Exercise(24, "Dumbbell Rows", ExerciseCategory.BACK),
        Exercise(25, "Seated Cable Rows", ExerciseCategory.BACK),
        Exercise(26, "T-Bar Rows", ExerciseCategory.BACK),
        Exercise(27, "Deadlift", ExerciseCategory.BACK),

        // Biceps Exercises
        Exercise(28, "Barbell Curls", ExerciseCategory.BICEPS),
        Exercise(29, "Dumbbell Curls", ExerciseCategory.BICEPS),
        Exercise(30, "Hammer Curls", ExerciseCategory.BICEPS),
        Exercise(31, "Preacher Curls", ExerciseCategory.BICEPS),
        Exercise(32, "Concentration Curls", ExerciseCategory.BICEPS),
        Exercise(33, "Cable Curls", ExerciseCategory.BICEPS),
        Exercise(34, "Incline Dumbbell Curls", ExerciseCategory.BICEPS),

        // Legs Exercises
        Exercise(35, "Squats", ExerciseCategory.LEGS),
        Exercise(36, "Leg Press", ExerciseCategory.LEGS),
        Exercise(37, "Romanian Deadlift", ExerciseCategory.LEGS),
        Exercise(38, "Leg Extensions", ExerciseCategory.LEGS),
        Exercise(39, "Leg Curls", ExerciseCategory.LEGS),
        Exercise(40, "Calf Raises", ExerciseCategory.LEGS),
        Exercise(41, "Lunges", ExerciseCategory.LEGS),
        Exercise(42, "Bulgarian Split Squats", ExerciseCategory.LEGS)
    )
} 