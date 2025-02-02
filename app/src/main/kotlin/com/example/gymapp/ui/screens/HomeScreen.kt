package com.example.gymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    modifier: Modifier = Modifier
) {
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = workout.type.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = workout.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    style = MaterialTheme.typography.bodyMedium
                )
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
}

@Composable
fun HomeScreen(
    onWeighInClick: () -> Unit,
    onTrainingClick: () -> Unit,
    onWorkoutClick: (Workout) -> Unit,
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

            ElevatedButton(
                onClick = onWeighInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Track Weight",
                    fontSize = 24.sp
                )
            }

            ElevatedButton(
                onClick = onTrainingClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Start Training",
                    fontSize = 24.sp
                )
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
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
} 