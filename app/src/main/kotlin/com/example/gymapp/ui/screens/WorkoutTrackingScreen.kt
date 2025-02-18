package com.example.gymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gymapp.data.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WorkoutTrackingScreen(
    workoutType: WorkoutType,
    date: LocalDate,
    initialExercises: List<WorkoutExercise> = emptyList(),
    onAddExercises: () -> Unit,
    onSaveWorkout: (Workout) -> Unit
) {
    var exercises by remember(initialExercises) { mutableStateOf(initialExercises) }
    var showSaveDialog by remember { mutableStateOf(false) }

    val originalExerciseIds = remember(initialExercises) {
        initialExercises.map { it.exercise.id }.toSet()
    }

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
            text = "Date: ${date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (exercises.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No exercises added yet. Click the button below to add exercises.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(
                    items = exercises.distinctBy { it.uniqueId },
                    key = { it.uniqueId }
                ) { exercise ->
                    ExerciseTrackingCard(
                        exercise = exercise,
                        onSetsUpdated = { updatedSets ->
                            exercises = exercises.map { e ->
                                if (e.uniqueId == exercise.uniqueId) {
                                    exercise.copy(sets = updatedSets)
                                } else {
                                    e
                                }
                            }
                        },
                        onDeleteExercise = {
                            exercises = exercises.filter { it.uniqueId != exercise.uniqueId }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onAddExercises,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = if (exercises.isEmpty()) "Add Exercises" else "Add More Exercises")
            }

            Button(
                onClick = { showSaveDialog = true },
                enabled = exercises.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Workout")
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Workout") },
            text = { Text("Are you sure you want to save this workout?") },
            confirmButton = {
                Button(
                    onClick = {
                        val workout = Workout(
                            id = System.currentTimeMillis(),
                            type = workoutType,
                            date = date,
                            exercises = exercises.distinctBy { it.uniqueId }
                        )
                        onSaveWorkout(workout)
                        showSaveDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ExerciseTrackingCard(
    exercise: WorkoutExercise,
    onSetsUpdated: (List<WorkoutSet>) -> Unit,
    onDeleteExercise: () -> Unit
) {
    var sets by remember { mutableStateOf(exercise.sets) }
    var showAddSetDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showDeleteConfirmation = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete exercise",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (sets.isEmpty()) {
                Text(
                    text = "No sets recorded yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                sets.forEachIndexed { index, set ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Set ${index + 1}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${set.weight}kg × ${set.reps} reps",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(
                            onClick = {
                                sets = sets.filterIndexed { i, _ -> i != index }
                                onSetsUpdated(sets)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete set"
                            )
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
                Column {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it },
                        label = { Text("Reps") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val weightNum = weight.toFloatOrNull()
                        val repsNum = reps.toIntOrNull()
                        if (weightNum != null && repsNum != null) {
                            val newSet = WorkoutSet(
                                setNumber = sets.size + 1,
                                weight = weightNum,
                                reps = repsNum
                            )
                            sets = sets + newSet
                            onSetsUpdated(sets)
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

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Exercise") },
            text = { Text("Are you sure you want to remove ${exercise.exercise.name} from this workout?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteExercise()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 