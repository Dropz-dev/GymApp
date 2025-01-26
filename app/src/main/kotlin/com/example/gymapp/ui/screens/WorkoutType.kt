package com.example.gymapp.ui.screens

enum class WorkoutType {
    PUSH,
    PULL,
    LEGS;

    override fun toString(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }
} 