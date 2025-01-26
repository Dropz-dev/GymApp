package com.example.gymapp.data.model

import java.time.LocalDate
import com.example.gymapp.ui.screens.WorkoutType

data class Workout(
    val id: Long,
    val type: WorkoutType,
    val date: LocalDate,
    val exercises: List<WorkoutExercise>
)

data class WorkoutExercise(
    val exercise: Exercise,
    val sets: List<WorkoutSet>
)

data class WorkoutSet(
    val setNumber: Int,
    val weight: Float,
    val reps: Int
) 