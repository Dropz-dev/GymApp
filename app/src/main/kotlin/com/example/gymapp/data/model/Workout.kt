package com.example.gymapp.data.model

import java.time.LocalDate

data class Workout(
    val id: Long,
    val type: String,
    val date: LocalDate,
    val exercises: List<String>
) 