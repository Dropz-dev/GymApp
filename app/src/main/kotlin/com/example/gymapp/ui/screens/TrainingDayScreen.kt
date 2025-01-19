import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun TrainingDayScreen(
    trainingDay: TrainingDay,
    onExerciseCompleted: (Long, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = trainingDay.name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(trainingDay.exercises) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    onRepsCompleted = { reps -> 
                        onExerciseCompleted(exercise.id, reps)
                    }
                )
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseSet,
    onRepsCompleted: (Int) -> Unit
) {
    var repsInput by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = exercise.exerciseName,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(height = 8.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Target: ${exercise.targetWeight}kg × ${exercise.targetReps}")
                TextField(
                    value = repsInput,
                    onValueChange = { repsInput = it },
                    label = { Text("Actual Reps") },
                    modifier = Modifier.width(100.dp)
                )
                Button(
                    onClick = {
                        repsInput.toIntOrNull()?.let { reps ->
                            onRepsCompleted(reps)
                            repsInput = ""
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
} 