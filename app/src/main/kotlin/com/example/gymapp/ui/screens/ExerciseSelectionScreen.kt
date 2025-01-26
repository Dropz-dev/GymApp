package com.example.gymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gymapp.data.model.Exercise
import com.example.gymapp.data.model.ExerciseList
import com.example.gymapp.data.model.ExerciseCategory
import com.example.gymapp.data.model.WorkoutExercise
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ExerciseSelectionScreen(
    workoutType: WorkoutType,
    date: LocalDate,
    onSaveWorkout: (List<WorkoutExercise>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExerciseCategory?>(null) }
    var selectedExercises by remember { mutableStateOf(setOf<Exercise>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Exercises",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "${workoutType.toString()} - ${date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search exercises") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Category filter
        ScrollableTabRow(
            selectedTabIndex = ExerciseCategory.values().indexOf(selectedCategory) + 1,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Tab(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null }
            ) {
                Text("All", modifier = Modifier.padding(8.dp))
            }
            
            ExerciseCategory.values().forEach { category ->
                Tab(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category }
                ) {
                    Text(category.name, modifier = Modifier.padding(8.dp))
                }
            }
        }

        // Exercise list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filteredExercises = ExerciseList.exercises.filter { exercise ->
                val matchesSearch = exercise.name.contains(searchQuery, ignoreCase = true)
                val matchesCategory = selectedCategory == null || exercise.category == selectedCategory
                matchesSearch && matchesCategory
            }

            items(filteredExercises) { exercise ->
                ExerciseItem(
                    exercise = exercise,
                    isSelected = exercise in selectedExercises,
                    onSelectionChanged = { selected ->
                        selectedExercises = if (selected) {
                            selectedExercises + exercise
                        } else {
                            selectedExercises - exercise
                        }
                    }
                )
            }
        }

        Button(
            onClick = {
                val workoutExercises = selectedExercises.map { exercise ->
                    WorkoutExercise(exercise = exercise, sets = emptyList())
                }
                onSaveWorkout(workoutExercises)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = selectedExercises.isNotEmpty()
        ) {
            Text("Add Selected Exercises")
        }
    }
}

@Composable
private fun ExerciseItem(
    exercise: Exercise,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = exercise.category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChanged
            )
        }
    }
} 