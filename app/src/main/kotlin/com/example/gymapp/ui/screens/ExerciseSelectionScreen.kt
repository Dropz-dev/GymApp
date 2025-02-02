package com.example.gymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.gymapp.data.database.WorkoutDatabase
import com.example.gymapp.data.CustomExerciseEntity
import com.example.gymapp.data.model.Exercise
import com.example.gymapp.data.model.ExerciseList
import com.example.gymapp.data.model.ExerciseCategory
import com.example.gymapp.data.model.WorkoutExercise
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch

@Composable
fun ExerciseSelectionScreen(
    workoutType: WorkoutType,
    date: LocalDate,
    onSaveWorkout: (List<WorkoutExercise>) -> Unit,
    initialExercises: List<WorkoutExercise> = emptyList(),
    database: WorkoutDatabase
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExerciseCategory?>(null) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    
    var selectedExercises by remember { 
        mutableStateOf(initialExercises.map { it.exercise }.toSet())
    }

    // Collect custom exercises
    val customExercises by database.customExerciseDao()
        .getAllCustomExercises()
        .collectAsState(initial = emptyList())

    // Update ExerciseList with custom exercises
    LaunchedEffect(customExercises) {
        ExerciseList.updateCustomExercises(
            customExercises.map { entity ->
                Exercise(
                    id = entity.id,
                    name = entity.name,
                    category = ExerciseCategory.fromString(entity.category),
                    isCustom = true
                )
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select Exercises",
                style = MaterialTheme.typography.headlineMedium,
            )
            
            IconButton(onClick = { showAddExerciseDialog = true }) {
                Icon(Icons.Default.Add, "Add Exercise")
            }
        }

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
                val existingExercisesMap = initialExercises.associateBy { it.exercise }
                val workoutExercises = selectedExercises.map { exercise ->
                    existingExercisesMap[exercise] ?: WorkoutExercise(exercise = exercise, sets = emptyList())
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

    if (showAddExerciseDialog) {
        val coroutineScope = rememberCoroutineScope()
        AddExerciseDialog(
            onDismiss = { showAddExerciseDialog = false },
            onExerciseAdded = { name, category ->
                val exercise = CustomExerciseEntity(
                    name = name,
                    category = category.name
                )
                coroutineScope.launch {
                    database.customExerciseDao().insertCustomExercise(exercise)
                }
                showAddExerciseDialog = false
            }
        )
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

@Composable
private fun AddExerciseDialog(
    onDismiss: () -> Unit,
    onExerciseAdded: (String, ExerciseCategory) -> Unit
) {
    var exerciseName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExerciseCategory?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Exercise") },
        text = {
            Column {
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = { Text("Exercise Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ExerciseCategory.values().toList()) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.name) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedCategory?.let { category ->
                        onExerciseAdded(exerciseName, category)
                    }
                },
                enabled = exerciseName.isNotBlank() && selectedCategory != null
            ) {
                Text("Add Exercise")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 