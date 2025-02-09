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
import com.example.gymapp.data.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    onNavigateBack: () -> Unit,
    nutritionGoals: NutritionGoals = NutritionGoals(
        calories = 2500,
        protein = 180,
        carbs = 300,
        fats = 70
    )
) {
    var showAddEntry by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    // Temporary state for demo
    var entries by remember { mutableStateOf(emptyList<NutritionEntry>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Nutrition Tracking",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Date selector
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { selectedDate = selectedDate.minusDays(1) }
                ) {
                    Text("<")
                }
                
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    style = MaterialTheme.typography.titleMedium
                )
                
                IconButton(
                    onClick = { selectedDate = selectedDate.plusDays(1) }
                ) {
                    Text(">")
                }
            }
        }

        // Macro summary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Daily Summary",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val dailyNutrition = DailyNutrition(selectedDate, entries.filter { it.date == selectedDate })

                MacroProgressBar(
                    label = "Calories",
                    current = dailyNutrition.totalCalories,
                    goal = nutritionGoals.calories,
                    unit = "kcal"
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                MacroProgressBar(
                    label = "Protein",
                    current = dailyNutrition.totalProtein.toInt(),
                    goal = nutritionGoals.protein,
                    unit = "g"
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                MacroProgressBar(
                    label = "Carbs",
                    current = dailyNutrition.totalCarbs.toInt(),
                    goal = nutritionGoals.carbs,
                    unit = "g"
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                MacroProgressBar(
                    label = "Fats",
                    current = dailyNutrition.totalFats.toInt(),
                    goal = nutritionGoals.fats,
                    unit = "g"
                )
            }
        }

        // Meal entries
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(entries.filter { it.date == selectedDate }
                .sortedBy { it.timestamp }) { entry ->
                NutritionEntryCard(entry = entry)
            }
        }

        // Add entry button
        Button(
            onClick = { showAddEntry = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Add Food Entry")
        }
    }

    if (showAddEntry) {
        AddNutritionEntryDialog(
            onDismiss = { showAddEntry = false },
            onSave = { name, mealType, calories, protein, carbs, fats ->
                val newEntry = NutritionEntry(
                    id = System.currentTimeMillis(),
                    date = selectedDate,
                    mealType = mealType,
                    name = name,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fats = fats
                )
                entries = entries + newEntry
                showAddEntry = false
            }
        )
    }
}

@Composable
private fun MacroProgressBar(
    label: String,
    current: Int,
    goal: Int,
    unit: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$current / $goal $unit",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        LinearProgressIndicator(
            progress = (current.toFloat() / goal).coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}

@Composable
private fun NutritionEntryCard(
    entry: NutritionEntry
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
                    text = entry.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = entry.mealType.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${entry.calories} kcal")
                Text("P: ${entry.protein}g")
                Text("C: ${entry.carbs}g")
                Text("F: ${entry.fats}g")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddNutritionEntryDialog(
    onDismiss: () -> Unit,
    onSave: (String, MealType, Int, Float, Float, Float) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf(MealType.SNACK) }
    var showMealTypeMenu by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Food Entry") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Food Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                // Meal type selector
                ExposedDropdownMenuBox(
                    expanded = showMealTypeMenu,
                    onExpandedChange = { showMealTypeMenu = it }
                ) {
                    OutlinedTextField(
                        value = selectedMealType.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Meal Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showMealTypeMenu) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = showMealTypeMenu,
                        onDismissRequest = { showMealTypeMenu = false }
                    ) {
                        MealType.values().forEach { mealType ->
                            DropdownMenuItem(
                                text = { Text(mealType.toString()) },
                                onClick = {
                                    selectedMealType = mealType
                                    showMealTypeMenu = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calories") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = protein,
                    onValueChange = { protein = it },
                    label = { Text("Protein (g)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = carbs,
                    onValueChange = { carbs = it },
                    label = { Text("Carbs (g)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = fats,
                    onValueChange = { fats = it },
                    label = { Text("Fats (g)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val caloriesInt = calories.toIntOrNull() ?: 0
                    val proteinFloat = protein.toFloatOrNull() ?: 0f
                    val carbsFloat = carbs.toFloatOrNull() ?: 0f
                    val fatsFloat = fats.toFloatOrNull() ?: 0f
                    
                    if (name.isNotBlank()) {
                        onSave(name, selectedMealType, caloriesInt, proteinFloat, carbsFloat, fatsFloat)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 