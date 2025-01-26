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
import com.example.gymapp.data.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutTrackingScreen(
    workoutType: WorkoutType,
    date: LocalDate,
    onAddExercises: () -> Unit,
    onSaveWorkout: (Workout) -> Unit
) {
    var workoutExercises by remember { mutableStateOf(listOf<WorkoutExercise>()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "${workoutType.toString()} Workout",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(workoutExercises) { workoutExercise ->
                ExerciseTrackingCard(
                    workoutExercise = workoutExercise,
                    onExerciseUpdate = { updated ->
                        workoutExercises = workoutExercises.map { 
                            if (it.exercise.id == updated.exercise.id) updated else it 
                        }
                    },
                    onDeleteExercise = { exercise ->
                        workoutExercises = workoutExercises.filter { it.exercise.id != exercise.id }
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onAddExercises,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(Modifier.width(8.dp))
                Text("Add Exercises")
            }

            Button(
                onClick = {
                    val workout = Workout(
                        id = System.currentTimeMillis(),
                        type = workoutType,
                        date = date,
                        exercises = workoutExercises
                    )
                    onSaveWorkout(workout)
                },
                modifier = Modifier.weight(1f),
                enabled = workoutExercises.isNotEmpty() && 
                         workoutExercises.all { it.sets.isNotEmpty() }
            ) {
                Text("Save Workout")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseTrackingCard(
    workoutExercise: WorkoutExercise,
    onExerciseUpdate: (WorkoutExercise) -> Unit,
    onDeleteExercise: (Exercise) -> Unit
) {
    var showAddSetDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = workoutExercise.exercise.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = workoutExercise.exercise.category.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = { onDeleteExercise(workoutExercise.exercise) }) {
                    Icon(Icons.Default.Delete, "Remove Exercise")
                }
            }

            if (workoutExercise.sets.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    workoutExercise.sets.forEach { set ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Set ${set.setNumber}")
                            Text("${set.weight} kg × ${set.reps} reps")
                        }
                    }
                }
            }

            Button(
                onClick = { showAddSetDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Add Set")
            }
        }
    }

    if (showAddSetDialog) {
        var weight by remember { mutableStateOf("") }
        var reps by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddSetDialog = false },
            title = { Text("Add Set") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it },
                        label = { Text("Reps") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val weightValue = weight.toFloatOrNull()
                        val repsValue = reps.toIntOrNull()
                        
                        if (weightValue != null && repsValue != null) {
                            val newSet = WorkoutSet(
                                setNumber = workoutExercise.sets.size + 1,
                                weight = weightValue,
                                reps = repsValue
                            )
                            onExerciseUpdate(
                                workoutExercise.copy(
                                    sets = workoutExercise.sets + newSet
                                )
                            )
                            showAddSetDialog = false
                        }
                    },
                    enabled = weight.isNotEmpty() && reps.isNotEmpty()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 