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

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Chart
        selectedExercise?.let { exercise ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                val entries = workouts
                    .filter { workout ->
                        workout.exercises.any { it.exercise.id == exercise.id }
                    }
                    .flatMap { workout ->
                        workout.exercises
                            .filter { it.exercise.id == exercise.id }
                            .flatMap { it.sets }
                            .map { set ->
                                Entry(
                                    workout.date.toEpochDay().toFloat(),
                                    set.weight * set.reps
                                )
                            }
                    }
                    .sortedBy { it.x }

                if (entries.isNotEmpty()) {
                    AndroidView(
                        factory = { context ->
                            LineChart(context).apply {
                                description.isEnabled = false
                                setTouchEnabled(true)
                                isDragEnabled = true
                                setScaleEnabled(true)
                                setPinchZoom(true)
                                
                                val dataSet = LineDataSet(entries, "Volume Progress").apply {
                                    color = MaterialTheme.colorScheme.primary.toArgb()
                                    setDrawCircles(true)
                                    setDrawValues(false)
                                    lineWidth = 2f
                                }
                                
                                data = LineData(dataSet)
                                
                                xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                                    override fun getFormattedValue(value: Float): String {
                                        return java.time.LocalDate.ofEpochDay(value.toLong())
                                            .format(DateTimeFormatter.ofPattern("MM/dd"))
                                    }
                                }
                                
                                invalidate()
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        NoDataMessage()
                    }
                }
            }
        }

        // Stats Summary
        selectedExercise?.let { exercise ->
            val exerciseStats = workouts
                .filter { workout ->
                    workout.exercises.any { it.exercise.id == exercise.id }
                }
                .flatMap { workout ->
                    workout.exercises
                        .filter { it.exercise.id == exercise.id }
                        .flatMap { it.sets }
                }

            if (exerciseStats.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Statistics",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val maxWeight = exerciseStats.maxOf { it.weight }
                        val maxReps = exerciseStats.maxOf { it.reps }
                        val maxVolume = exerciseStats.maxOf { it.weight * it.reps }

                        Text("Personal Records:")
                        Text("Max Weight: $maxWeight kg")
                        Text("Max Reps: $maxReps")
                        Text("Max Volume: $maxVolume kg")
                    }
                }
            }
        }
    }
}

@Composable
private fun NoDataMessage() {
    Text("No data available for this exercise")
} 