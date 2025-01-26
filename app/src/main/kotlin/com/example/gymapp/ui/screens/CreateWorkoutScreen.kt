package com.example.gymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    onWorkoutCreated: () -> Unit
) {
    var workoutName by remember { mutableStateOf("") }
    var exercises by remember { mutableStateOf(listOf<String>()) }
    var currentExercise by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Create New Workout",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Workout name input
        OutlinedTextField(
            value = workoutName,
            onValueChange = { workoutName = it },
            label = { Text("Workout Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        // Date selection (basic implementation - can be enhanced with a date picker)
        Text(
            text = "Date: ${selectedDate}",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Exercise input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = currentExercise,
                onValueChange = { currentExercise = it },
                label = { Text("Exercise Name") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            
            IconButton(
                onClick = {
                    if (currentExercise.isNotEmpty()) {
                        exercises = exercises + currentExercise
                        currentExercise = ""
                    }
                }
            ) {
                Icon(Icons.Default.Add, "Add Exercise")
            }
        }

        // Exercise list
        Text(
            text = "Exercises",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(exercises) { exercise ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(exercise)
                        IconButton(
                            onClick = {
                                exercises = exercises - exercise
                            }
                        ) {
                            Icon(Icons.Default.Delete, "Remove Exercise")
                        }
                    }
                }
            }
        }

        // Create button
        Button(
            onClick = onWorkoutCreated,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = workoutName.isNotEmpty() && exercises.isNotEmpty()
        ) {
            Text("Create Workout")
        }
    }
} 