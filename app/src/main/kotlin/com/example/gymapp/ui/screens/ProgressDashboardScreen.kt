package com.example.gymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.example.gymapp.data.model.Workout
import com.example.gymapp.data.model.Exercise
import java.time.format.DateTimeFormatter
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import android.content.Context
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import kotlin.math.abs

data class ExerciseProgress(
    val date: LocalDate,
    val maxWeight: Float,
    val maxReps: Int,
    val maxVolume: Float,
    val totalVolume: Float,
    val sets: List<WorkoutSet>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressDashboardScreen(
    workouts: List<Workout>,
    onNavigateBack: () -> Unit
) {
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val exercises = remember(workouts) {
        workouts.flatMap { it.exercises.map { it.exercise } }.distinct()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Progress Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Exercise selector
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            TextField(
                value = selectedExercise?.name ?: "Select Exercise",
                onValueChange = { },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.exposedDropdownSize()
            ) {
                exercises.forEach { exercise ->
                    DropdownMenuItem(
                        text = { Text(exercise.name) },
                        onClick = { 
                            selectedExercise = exercise
                            expanded = false
                        }
                    )
                }
            }
        }

        selectedExercise?.let { exercise ->
            // Calculate exercise progress history
            val progressHistory = workouts
                .filter { workout ->
                    workout.exercises.any { it.exercise.id == exercise.id }
                }
                .sortedBy { it.date }
                .map { workout ->
                    val exerciseSets = workout.exercises
                        .first { it.exercise.id == exercise.id }
                        .sets
                    
                    ExerciseProgress(
                        date = workout.date,
                        maxWeight = exerciseSets.maxOf { it.weight },
                        maxReps = exerciseSets.maxOf { it.reps },
                        maxVolume = exerciseSets.maxOf { it.weight * it.reps },
                        totalVolume = exerciseSets.sumOf { 
                            (it.weight * it.reps).toDouble() 
                        }.toFloat(),
                        sets = exerciseSets
                    )
                }

            if (progressHistory.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Progress Chart
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        ) {
                            val entries = progressHistory.map { progress ->
                                Entry(
                                    progress.date.toEpochDay().toFloat(),
                                    progress.totalVolume
                                )
                            }

                            val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
                            AndroidView(
                                factory = { context -> 
                                    setupLineChart(context, entries, primaryColor)
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Personal Records
                    item {
                        PersonalRecordsCard(progressHistory)
                    }

                    // Recent History
                    item {
                        Text(
                            text = "Recent History",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(progressHistory.reversed().take(10)) { progress ->
                        WorkoutHistoryCard(
                            progress = progress,
                            previousProgress = progressHistory
                                .getOrNull(progressHistory.indexOf(progress) - 1)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No data available for this exercise")
                }
            }
        }
    }
}

@Composable
private fun PersonalRecordsCard(progressHistory: List<ExerciseProgress>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Personal Records",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val maxWeight = progressHistory.maxOf { it.maxWeight }
            val maxReps = progressHistory.maxOf { it.maxReps }
            val maxVolume = progressHistory.maxOf { it.maxVolume }
            val maxTotalVolume = progressHistory.maxOf { it.totalVolume }

            // Find dates when these records were set
            val maxWeightDate = progressHistory
                .first { it.maxWeight == maxWeight }
                .date
            val maxRepsDate = progressHistory
                .first { it.maxReps == maxReps }
                .date
            val maxVolumeDate = progressHistory
                .first { it.maxVolume == maxVolume }
                .date

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RecordItem(
                    label = "Max Weight",
                    value = "%.1f kg".format(maxWeight),
                    date = maxWeightDate
                )
                RecordItem(
                    label = "Max Reps",
                    value = maxReps.toString(),
                    date = maxRepsDate
                )
                RecordItem(
                    label = "Max Set Volume",
                    value = "%.1f kg".format(maxVolume),
                    date = maxVolumeDate
                )
                RecordItem(
                    label = "Max Workout Volume",
                    value = "%.1f kg".format(maxTotalVolume),
                    date = maxVolumeDate
                )
            }
        }
    }
}

@Composable
private fun RecordItem(
    label: String,
    value: String,
    date: LocalDate
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WorkoutHistoryCard(
    progress: ExerciseProgress,
    previousProgress: ExerciseProgress?
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = progress.date.format(
                        DateTimeFormatter.ofPattern("MMM dd, yyyy")
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
                
                // Volume comparison with previous workout
                previousProgress?.let { prev ->
                    val volumeDiff = progress.totalVolume - prev.totalVolume
                    val percentChange = (volumeDiff / prev.totalVolume * 100)
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                volumeDiff > 0 -> Icons.Default.ArrowDropUp
                                volumeDiff < 0 -> Icons.Default.ArrowDropDown
                                else -> Icons.Default.Remove
                            },
                            contentDescription = "Volume change",
                            tint = when {
                                volumeDiff > 0 -> Color.Green
                                volumeDiff < 0 -> Color.Red
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                        Text(
                            text = "%.1f%%".format(abs(percentChange)),
                            color = when {
                                volumeDiff > 0 -> Color.Green
                                volumeDiff < 0 -> Color.Red
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Set details
            progress.sets.forEach { set ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Set ${set.setNumber}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${set.weight}kg × ${set.reps}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "%.1fkg".format(set.weight * set.reps),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Volume")
                Text(
                    text = "%.1f kg".format(progress.totalVolume),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun setupLineChart(context: Context, entries: List<Entry>, color: Int): LineChart {
    return LineChart(context).apply {
        description.isEnabled = false
        setTouchEnabled(true)
        isDragEnabled = true
        setScaleEnabled(true)
        setPinchZoom(true)
        
        val dataSet = LineDataSet(entries, "Volume Progress").apply {
            this.color = color
            setDrawCircles(true)
            setDrawValues(false)
            lineWidth = 2f
        }
        
        data = LineData(dataSet)
        
        xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return LocalDate.ofEpochDay(value.toLong())
                    .format(DateTimeFormatter.ofPattern("MM/dd"))
            }
        }
        
        invalidate()
    }
} 