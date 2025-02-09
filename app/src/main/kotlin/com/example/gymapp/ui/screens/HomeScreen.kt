package com.example.gymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymapp.data.model.Workout
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutHistoryCard(
    workout: Workout,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = workout.type.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = workout.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                IconButton(
                    onClick = { showDeleteConfirmation = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete workout",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Exercise summary
            Text(
                text = "${workout.exercises.size} exercises",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Total volume
            val totalVolume = workout.exercises.sumOf { exercise ->
                exercise.sets.sumOf { (it.weight * it.reps).toDouble() }
            }.toFloat()

            Text(
                text = "Total Volume: %.1f kg".format(totalVolume),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Workout") },
            text = { Text("Are you sure you want to delete this workout? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
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

@Composable
fun HomeScreen(
    onWeighInClick: () -> Unit,
    onTrainingClick: () -> Unit,
    onWorkoutClick: (Workout) -> Unit,
    onDeleteWorkout: (Workout) -> Unit,
    onProgressClick: () -> Unit,
    onLibraryClick: () -> Unit,
    onProgramsClick: () -> Unit,
    onNutritionClick: () -> Unit,
    recentWorkouts: List<Workout> = emptyList()
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElevatedButton(
                    onClick = onWeighInClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                ) {
                    Text(
                        text = "Track Weight",
                        fontSize = 24.sp
                    )
                }

                ElevatedButton(
                    onClick = onProgressClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                ) {
                    Text(
                        text = "Progress",
                        fontSize = 24.sp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElevatedButton(
                    onClick = onTrainingClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                ) {
                    Text(
                        text = "Start Training",
                        fontSize = 24.sp
                    )
                }

                ElevatedButton(
                    onClick = onLibraryClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                ) {
                    Text(
                        text = "Exercise Library",
                        fontSize = 24.sp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElevatedButton(
                    onClick = onProgramsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                ) {
                    Text(
                        text = "Programs",
                        fontSize = 24.sp
                    )
                }

                ElevatedButton(
                    onClick = onNutritionClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                ) {
                    Text(
                        text = "Nutrition",
                        fontSize = 24.sp
                    )
                }
            }

            if (recentWorkouts.isNotEmpty()) {
                Text(
                    text = "Previous Workouts",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        items(recentWorkouts) { workout ->
            WorkoutHistoryCard(
                workout = workout,
                onClick = { onWorkoutClick(workout) },
                onDelete = { onDeleteWorkout(workout) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
} 