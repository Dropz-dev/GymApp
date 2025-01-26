package com.example.gymapp.data.model

data class WorkoutSet(
    val setNumber: Int,
    val weight: Float,
    val reps: Int
)

data class WorkoutExercise(
    val exercise: Exercise,
    val sets: List<WorkoutSet> = listOf()
)

data class Workout(
    val id: Long = System.currentTimeMillis(),
    val type: WorkoutType,
    val date: java.time.LocalDate,
    val exercises: List<WorkoutExercise> = listOf()
) 