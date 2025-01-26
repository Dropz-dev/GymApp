package com.example.gymapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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
    onWorkoutCreated: (WorkoutType, LocalDate) -> Unit
) {
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

        Spacer(modifier = Modifier.weight(1f))

        // Create button
        Button(
            onClick = { 
                selectedWorkoutType?.let { type ->
                    onWorkoutCreated(type, selectedDate)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = selectedWorkoutType != null
        ) {
            Text("Continue to Exercise Selection")
        }
    }
} 