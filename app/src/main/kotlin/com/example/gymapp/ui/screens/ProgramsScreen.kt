package com.example.gymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gymapp.data.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramsScreen(
    onNavigateBack: () -> Unit,
    onProgramSelected: (WorkoutProgram) -> Unit
) {
    var selectedProgram by remember { mutableStateOf<WorkoutProgram?>(null) }
    var showProgramDetails by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Workout Programs",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(PresetPrograms.programs) { program ->
                ProgramCard(
                    program = program,
                    onClick = {
                        selectedProgram = program
                        showProgramDetails = true
                    }
                )
            }
        }
    }

    // Program details dialog
    if (showProgramDetails && selectedProgram != null) {
        AlertDialog(
            onDismissRequest = { showProgramDetails = false },
            title = { Text(selectedProgram!!.name) },
            text = {
                Column {
                    Text(
                        text = selectedProgram!!.description,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "Difficulty: ${selectedProgram!!.difficulty}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Duration: ${selectedProgram!!.duration} weeks",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Workouts per week: ${selectedProgram!!.workoutsPerWeek}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Workouts:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    selectedProgram!!.workouts.forEach { workout ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(
                                    text = workout.name,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "${workout.exercises.size} exercises",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedProgram?.let { program ->
                            onProgramSelected(program)
                        }
                        showProgramDetails = false
                    }
                ) {
                    Text("Start Program")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProgramDetails = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProgramCard(
    program: WorkoutProgram,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = program.name,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = program.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = program.difficulty.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${program.workoutsPerWeek}x per week",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${program.duration} weeks",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
} 