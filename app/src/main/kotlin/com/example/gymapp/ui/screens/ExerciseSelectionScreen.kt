package com.example.gymapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gymapp.data.model.Exercise
import com.example.gymapp.data.model.ExerciseCategory
import com.example.gymapp.data.model.ExerciseList
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSelectionScreen(
    workoutType: WorkoutType,
    date: LocalDate,
    onSaveWorkout: () -> Unit
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
            text = "${workoutType.toString()} Workout",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Search exercises") },
            leadingIcon = { Icon(Icons.Default.Search, "Search") },
            singleLine = true
        )

        // Category filter chips
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null },
                label = { Text("All") }
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            ExerciseCategory.values().forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category.toString()) }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Selected exercises count
        Text(
            text = "Selected: ${selectedExercises.size}",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Exercise list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filteredExercises = ExerciseList.allExercises.filter { exercise ->
                val matchesSearch = exercise.name.contains(searchQuery, ignoreCase = true)
                val matchesCategory = selectedCategory == null || exercise.category == selectedCategory
                matchesSearch && matchesCategory
            }

            items(filteredExercises) { exercise ->
                val isSelected = exercise in selectedExercises
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedExercises = if (isSelected) {
                                selectedExercises - exercise
                            } else {
                                selectedExercises + exercise
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = exercise.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = exercise.category.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Save button
        Button(
            onClick = onSaveWorkout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = selectedExercises.isNotEmpty()
        ) {
            Text("Save Workout (${selectedExercises.size} exercises)")
        }
    }
} 