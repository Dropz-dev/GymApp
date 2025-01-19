package com.example.gymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gymapp.data.model.Program

@Composable
fun ProgramListScreen(
    programs: List<Program>,
    onProgramSelected: (Long, Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(programs) { program ->
            ProgramCard(program = program, onProgramSelected = onProgramSelected)
        }
    }
}

@Composable
private fun ProgramCard(
    program: Program,
    onProgramSelected: (Long, Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = program.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            program.trainingDays.forEach { day ->
                Button(
                    onClick = { onProgramSelected(program.id, day.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(day.name)
                }
            }
        }
    }
} 