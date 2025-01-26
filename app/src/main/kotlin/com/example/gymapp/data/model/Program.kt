package com.example.gymapp.data.model

data class Program(
    val id: Long,
    val name: String,
    val trainingDays: List<TrainingDay>
) 