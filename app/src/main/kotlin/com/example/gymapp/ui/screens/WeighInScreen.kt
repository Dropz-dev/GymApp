package com.example.gymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gymapp.data.model.WeightEntry
import com.example.gymapp.data.model.WeightSummary
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeighInScreen() {
    var weightInput by remember { mutableStateOf("") }
    var weightEntries by remember { mutableStateOf(listOf<WeightEntry>()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Weight Tracking",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Weight input section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = weightInput,
                onValueChange = { weightInput = it },
                label = { Text("Weight (kg)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Button(
                onClick = {
                    weightInput.toFloatOrNull()?.let { weight ->
                        val newEntry = WeightEntry(
                            id = System.currentTimeMillis(),
                            date = LocalDate.now(),
                            weight = weight
                        )
                        weightEntries = weightEntries + newEntry
                        weightInput = ""
                    }
                },
                enabled = weightInput.isNotEmpty()
            ) {
                Text("Add")
            }
        }

        // Summaries
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                val weeklyAverage = weightEntries
                    .filter { it.date.isAfter(LocalDate.now().minusWeeks(1)) }
                    .takeIf { it.isNotEmpty() }
                    ?.let { entries -> entries.sumOf { it.weight.toDouble() } / entries.size }
                    ?.toFloat()

                val monthlyAverage = weightEntries
                    .filter { it.date.isAfter(LocalDate.now().minusMonths(1)) }
                    .takeIf { it.isNotEmpty() }
                    ?.let { entries -> entries.sumOf { it.weight.toDouble() } / entries.size }
                    ?.toFloat()

                Text(
                    text = "Averages",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                weeklyAverage?.let {
                    Text("Weekly: %.1f kg".format(it))
                }
                
                monthlyAverage?.let {
                    Text("Monthly: %.1f kg".format(it))
                }
            }
        }

        // Recent entries
        Text(
            text = "Recent Entries",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(weightEntries.sortedByDescending { it.date }) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(entry.date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        Text("%.1f kg".format(entry.weight))
                    }
                }
            }
        }
    }
} 