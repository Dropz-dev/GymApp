package com.example.gymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.example.gymapp.data.model.Exercise
import com.example.gymapp.data.model.ExerciseDetails
import androidx.compose.foundation.clickable
import com.example.gymapp.data.model.ExerciseList
import com.example.gymapp.data.model.ExerciseCategory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymapp.data.WorkoutDatabase
import com.example.gymapp.data.CustomExerciseEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLibraryScreen(
    onNavigateBack: () -> Unit,
    database: WorkoutDatabase = WorkoutDatabase.getDatabase(LocalContext.current)
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExerciseCategory?>(null) }
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
                    category = ExerciseCategory.valueOf(entity.category),
                    isCustom = true
                )
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddExerciseDialog = true }
            ) {
                Icon(Icons.Default.Add, "Add Exercise")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Exercise Library",
                style = MaterialTheme.typography.headlineMedium,
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
                selectedTabIndex = if (selectedCategory == null) 0 else ExerciseCategory.values().indexOf(selectedCategory) + 1,
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
                        Text(category.toString(), modifier = Modifier.padding(8.dp))
                    }
                }
            }

            // Exercise list with details
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                val filteredExercises = ExerciseList.exercises.filter { exercise ->
                    val matchesSearch = exercise.name.contains(searchQuery, ignoreCase = true)
                    val matchesCategory = selectedCategory == null || exercise.category == selectedCategory
                    matchesSearch && matchesCategory
                }

                items(filteredExercises) { exercise ->
                    ExerciseDetailCard(
                        exercise = exercise,
                        isExpanded = selectedExercise == exercise,
                        onClick = { 
                            selectedExercise = if (selectedExercise == exercise) null else exercise
                        },
                        onDeleteClick = { 
                            if (exercise.isCustom) {
                                selectedExercise = exercise
                                showDeleteConfirmation = true
                            }
                        }
                    )
                }
            }
        }

        if (showAddExerciseDialog) {
            AddExerciseDialog(
                onDismiss = { showAddExerciseDialog = false },
                onExerciseAdded = { name, category ->
                    coroutineScope.launch {
                        // Check if exercise with same name exists
                        if (!database.customExerciseDao().exerciseExists(name)) {
                            val exercise = CustomExerciseEntity(
                                name = name,
                                category = category.name
                            )
                            database.customExerciseDao().insertCustomExercise(exercise)
                            showAddExerciseDialog = false
                        } else {
                            // Show error (you might want to handle this better)
                            // For now, we'll just keep the dialog open
                        }
                    }
                }
            )
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Delete Exercise") },
                text = { 
                    Text("Are you sure you want to delete ${selectedExercise?.name}? " +
                         "This will also remove it from any workouts that use it.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedExercise?.let { exercise ->
                                if (exercise.isCustom) {
                                    coroutineScope.launch {
                                        database.customExerciseDao().deleteCustomExercise(
                                            CustomExerciseEntity(
                                                id = exercise.id,
                                                name = exercise.name,
                                                category = exercise.category.name
                                            )
                                        )
                                    }
                                }
                            }
                            showDeleteConfirmation = false
                            selectedExercise = null
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailCard(
    exercise: Exercise,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = exercise.category.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                if (exercise.isCustom) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete exercise",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                val details = ExerciseDetails.getDetails(exercise.id)
                details?.let {
                    Text("Primary Muscles: ${it.primaryMuscles.joinToString(", ")}")
                    Text("Secondary Muscles: ${it.secondaryMuscles.joinToString(", ")}")
                    Text("Instructions:", style = MaterialTheme.typography.titleMedium)
                    it.instructions.forEach { instruction ->
                        Text("• $instruction")
                    }
                    if (it.tips.isNotEmpty()) {
                        Text("Tips:", style = MaterialTheme.typography.titleMedium)
                        it.tips.forEach { tip ->
                            Text("• $tip")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExerciseDialog(
    onDismiss: () -> Unit,
    onExerciseAdded: (String, ExerciseCategory) -> Unit
) {
    var exerciseName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExerciseCategory?>(null) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Exercise") },
        text = {
            Column {
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { 
                        exerciseName = it
                        showError = false
                    },
                    label = { Text("Exercise Name") },
                    isError = showError,
                    supportingText = if (showError) {
                        { Text("An exercise with this name already exists") }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Category selection chips in a scrollable row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ExerciseCategory.values()) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.toString()) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedCategory?.let { category ->
                        onExerciseAdded(exerciseName.trim(), category)
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