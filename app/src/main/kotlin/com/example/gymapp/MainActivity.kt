package com.example.gymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gymapp.data.WorkoutDatabase
import com.example.gymapp.data.model.Workout
import com.example.gymapp.data.model.Program
import com.example.gymapp.data.model.TrainingDay
import com.example.gymapp.data.model.ExerciseSet
import com.example.gymapp.data.model.Exercise
import com.example.gymapp.data.model.WorkoutExercise
import com.example.gymapp.ui.screens.*
import com.example.gymapp.ui.theme.GymAppTheme
import java.time.LocalDate
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var database: WorkoutDatabase
    private var currentWorkout: Workout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = WorkoutDatabase.getDatabase(this)
        
        // Sample program data
        val sampleProgram = Program(
            id = 1L,
            name = "Push/Pull/Legs",
            trainingDays = listOf(
                TrainingDay(
                    id = 1L,
                    name = "Push Day",
                    exercises = listOf(
                        ExerciseSet(1L, "Bench Press", 100.5f, 8),
                        ExerciseSet(2L, "Shoulder Press", 60.0f, 10),
                        ExerciseSet(3L, "Triceps Extension", 25.0f, 12)
                    )
                )
            )
        )
        
        setContent {
            GymAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GymmiApp(
                        onSaveWorkout = { workout ->
                            lifecycleScope.launch {
                                database.workoutDao().insertFullWorkout(workout)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GymmiApp(
    onSaveWorkout: (Workout) -> Unit
) {
    val navController = rememberNavController()
    var pendingWorkout by remember { mutableStateOf<Workout?>(null) }
    
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(
                onStartClick = {
                    navController.navigate("home")
                }
            )
        }
        
        composable("home") {
            HomeScreen(
                onWeighInClick = {
                    navController.navigate("weigh_in")
                },
                onTrainingClick = {
                    navController.navigate("create_workout")
                }
            )
        }

        composable("weigh_in") {
            WeighInScreen()
        }
        
        composable("create_workout") {
            CreateWorkoutScreen(
                onWorkoutCreated = { type, date ->
                    navController.navigate(
                        "track_workout/${type.name}/${date}"
                    )
                }
            )
        }

        composable(
            route = "track_workout/{workoutType}/{date}",
            arguments = listOf(
                navArgument("workoutType") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val workoutType = WorkoutType.valueOf(
                backStackEntry.arguments?.getString("workoutType") ?: WorkoutType.PUSH.name
            )
            val date = LocalDate.parse(
                backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
            )
            
            WorkoutTrackingScreen(
                workoutType = workoutType,
                date = date,
                onAddExercises = {
                    navController.navigate(
                        "select_exercises/${workoutType.name}/${date}"
                    )
                },
                onSaveWorkout = { workout ->
                    pendingWorkout = workout
                    navController.navigate("workout_summary")
                }
            )
        }

        composable(
            route = "select_exercises/{workoutType}/{date}",
            arguments = listOf(
                navArgument("workoutType") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val workoutType = WorkoutType.valueOf(
                backStackEntry.arguments?.getString("workoutType") ?: WorkoutType.PUSH.name
            )
            val date = LocalDate.parse(
                backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
            )
            
            ExerciseSelectionScreen(
                workoutType = workoutType,
                date = date,
                onSaveWorkout = {
                    navController.popBackStack()
                }
            )
        }

        composable("workout_summary") {
            pendingWorkout?.let { workout ->
                WorkoutSummaryScreen(
                    workout = workout,
                    onConfirm = {
                        onSaveWorkout(workout)
                        pendingWorkout = null
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onEdit = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
} 