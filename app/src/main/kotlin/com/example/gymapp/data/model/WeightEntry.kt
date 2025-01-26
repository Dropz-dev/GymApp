package com.example.gymapp.data.model

import java.time.LocalDate

data class WeightEntry(
    val id: Long,
    val date: LocalDate,
    val weight: Float
)

data class WeightSummary(
    val averageWeight: Float,
    val period: String
) 