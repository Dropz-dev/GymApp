package com.example.gymapp.data.model

data class TrainingDay(
    val id: Long,
    val name: String,
    val exercises: List<ExerciseSet>
) 