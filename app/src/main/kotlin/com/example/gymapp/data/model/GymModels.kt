package com.example.gymapp.data.model

data class Program(
    val id: Long,
    val name: String,
    val trainingDays: List<TrainingDay>
)

data class TrainingDay(
    val id: Long,
    val name: String,  // e.g., "Push Day", "Leg Day"
    val exercises: List<ExerciseSet>
)

data class ExerciseSet(
    val id: Long,
    val exerciseName: String,
    val targetWeight: Float,  // in kg
    val targetReps: Int,
    val actualReps: Int? = null  // null until performed
) 