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
import com.example.gymapp.data.model.ExerciseCategory
import com.example.gymapp.data.model.ExerciseDetails

@Composable
fun ExerciseLibraryScreen(
    onNavigateBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<ExerciseCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
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
    val exerciseDetails = ExerciseDetails.getDetails(exercise.id)

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
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
            }

            if (isExpanded && exerciseDetails != null) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Primary muscles
                Text(
                    text = "Primary Muscles:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(exerciseDetails.primaryMuscles.joinToString(", "))
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Secondary muscles
                Text(
                    text = "Secondary Muscles:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(exerciseDetails.secondaryMuscles.joinToString(", "))
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Instructions
                Text(
                    text = "Instructions:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                exerciseDetails.instructions.forEach { instruction ->
                    Text("• $instruction")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Tips
                Text(
                    text = "Tips:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                exerciseDetails.tips.forEach { tip ->
                    Text("• $tip")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Common mistakes
                Text(
                    text = "Common Mistakes to Avoid:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                exerciseDetails.commonMistakes.forEach { mistake ->
                    Text("• $mistake")
                }
            }
        }
    }
} 