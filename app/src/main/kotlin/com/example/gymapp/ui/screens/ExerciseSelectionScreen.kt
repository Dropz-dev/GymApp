package com.example.gymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ExerciseSelectionScreen(
    workoutType: WorkoutType,
    date: LocalDate,
    onSaveWorkout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${workoutType.toString()} Workout",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Placeholder text - we'll replace this with the exercise list
        Text(
            text = "Exercise selection coming soon!\nYou'll be able to choose from your custom exercise list.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSaveWorkout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Save Workout")
        }
    }
} 