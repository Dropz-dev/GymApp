package com.example.gymapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class WorkoutType {
    PUSH, PULL, ARMS, LEGS;
    
    override fun toString(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    onWorkoutCreated: () -> Unit
) {
    var workoutName by remember { mutableStateOf("") }
    var exercises by remember { mutableStateOf(listOf<String>()) }
    var currentExercise by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedWorkoutType by remember { mutableStateOf<WorkoutType?>(null) }
    var showWorkoutTypeMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Create New Workout",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Workout type selection
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showWorkoutTypeMenu = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedWorkoutType?.toString() ?: "Select Workout Type")
                    Icon(Icons.Default.KeyboardArrowDown, "Select Type")
                }
            }
            
            DropdownMenu(
                expanded = showWorkoutTypeMenu,
                onDismissRequest = { showWorkoutTypeMenu = false }
            ) {
                WorkoutType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.toString()) },
                        onClick = {
                            selectedWorkoutType = type
                            workoutName = type.toString()
                            showWorkoutTypeMenu = false
                        }
                    )
                }
            }
        }

        // Date selection with calendar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Date: ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}")
                    Icon(Icons.Default.KeyboardArrowDown, "Select Date")
                }
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate
                    .atStartOfDay()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            )
            
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                selectedDate = java.time.Instant
                                    .ofEpochMilli(millis)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Exercise input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = currentExercise,
                onValueChange = { currentExercise = it },
                label = { Text("Exercise Name") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            
            IconButton(
                onClick = {
                    if (currentExercise.isNotEmpty()) {
                        exercises = exercises + currentExercise
                        currentExercise = ""
                    }
                }
            ) {
                Icon(Icons.Default.Add, "Add Exercise")
            }
        }

        // Exercise list
        Text(
            text = "Exercises",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(exercises) { exercise ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(exercise)
                        IconButton(
                            onClick = {
                                exercises = exercises - exercise
                            }
                        ) {
                            Icon(Icons.Default.Delete, "Remove Exercise")
                        }
                    }
                }
            }
        }

        // Create button
        Button(
            onClick = onWorkoutCreated,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = selectedWorkoutType != null && exercises.isNotEmpty()
        ) {
            Text("Create Workout")
        }
    }
} 