package com.example.gymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLibraryScreen(
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExerciseCategory?>(null) }
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }

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
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailCard(
    exercise: Exercise,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = exercise.category.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
            
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