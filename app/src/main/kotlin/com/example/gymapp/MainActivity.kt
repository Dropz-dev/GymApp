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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = WorkoutDatabase.getDatabase(this)
        
        setContent {
            GymAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GymmiApp(
                        database = database,
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
    database: WorkoutDatabase,
    onSaveWorkout: (Workout) -> Unit
) {
    val navController = rememberNavController()
    var pendingWorkout by remember { mutableStateOf<Workout?>(null) }
    
    // Collect workouts from the database
    val workouts by database.workoutDao()
        .getAllWorkouts()
        .collectAsState(initial = emptyList())
        .let { state ->
            // Convert database entities to domain model
            remember(state.value) {
                derivedStateOf {
                    state.value.map { workoutWithExercises ->
                        Workout(
                            id = workoutWithExercises.workout.id,
                            type = WorkoutType.valueOf(workoutWithExercises.workout.type),
                            date = workoutWithExercises.workout.date,
                            exercises = workoutWithExercises.exercises.map { exerciseWithSets ->
                                WorkoutExercise(
                                    exercise = Exercise(
                                        id = exerciseWithSets.exercise.exerciseId,
                                        name = exerciseWithSets.exercise.exerciseName,
                                        category = ExerciseCategory.valueOf(exerciseWithSets.exercise.exerciseCategory)
                                    ),
                                    sets = exerciseWithSets.sets.map { set ->
                                        WorkoutSet(
                                            setNumber = set.setNumber,
                                            weight = set.weight,
                                            reps = set.reps
                                        )
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    
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
                },
                onWorkoutClick = { workout ->
                    // Navigate to workout details screen
                    navController.navigate("workout_details/${workout.id}")
                },
                recentWorkouts = workouts.sortedByDescending { it.date }
            )
        }

        // Add workout details screen
        composable(
            route = "workout_details/{workoutId}",
            arguments = listOf(
                navArgument("workoutId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: return@composable
            val workout = workouts.find { it.id == workoutId } ?: return@composable
            
            WorkoutSummaryScreen(
                workout = workout,
                onConfirm = {
                    navController.popBackStack()
                },
                onEdit = {
                    navController.popBackStack()
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

            val selectedExercises = navController
                .currentBackStackEntry
                ?.savedStateHandle
                ?.get<List<WorkoutExercise>>("selected_exercises") ?: emptyList()
            
            WorkoutTrackingScreen(
                workoutType = workoutType,
                date = date,
                initialExercises = selectedExercises,
                onAddExercises = {
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("current_exercises", selectedExercises)
                    navController.navigate(
                        "select_exercises/${workoutType.name}/${date}"
                    )
                },
                onSaveWorkout = { workout ->
                    onSaveWorkout(workout)
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
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
            
            val currentExercises = navController
                .previousBackStackEntry
                ?.savedStateHandle
                ?.get<List<WorkoutExercise>>("current_exercises") ?: emptyList()
            
            ExerciseSelectionScreen(
                workoutType = workoutType,
                date = date,
                initialExercises = currentExercises,
                onSaveWorkout = { selectedExercises ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_exercises", selectedExercises)
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