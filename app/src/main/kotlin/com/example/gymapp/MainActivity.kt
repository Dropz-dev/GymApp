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
import com.example.gymapp.data.model.*
import com.example.gymapp.ui.screens.*
import com.example.gymapp.ui.theme.GymAppTheme
import java.time.LocalDate
import kotlinx.coroutines.launch
import android.content.pm.ActivityInfo
import android.view.WindowManager
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var database: WorkoutDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Keep screen on during workout
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Lock orientation to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
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
    val coroutineScope = rememberCoroutineScope()
    
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
                                    }.toList()
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
                    navController.navigate("workout_details/${workout.id}")
                },
                onDeleteWorkout = { workout ->
                    coroutineScope.launch {
                        database.workoutDao().deleteFullWorkout(workout.id)
                    }
                },
                onProgressClick = {
                    navController.navigate("progress_dashboard")
                },
                onLibraryClick = {
                    navController.navigate("exercise_library")
                },
                onProgramsClick = {
                    navController.navigate("programs")
                },
                onNutritionClick = {
                    navController.navigate("nutrition")
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
                    navController.navigate(
                        "track_workout/${workout.type.name}/${workout.date}?workoutId=${workout.id}"
                    )
                },
                onDelete = {
                    coroutineScope.launch {
                        database.workoutDao().deleteFullWorkout(workout.id)
                        navController.popBackStack()
                    }
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
            route = "track_workout/{workoutType}/{date}?workoutId={workoutId}",
            arguments = listOf(
                navArgument("workoutType") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("workoutId") { 
                    type = NavType.LongType
                    defaultValue = -1L 
                }
            )
        ) { backStackEntry ->
            val workoutType = WorkoutType.valueOf(
                backStackEntry.arguments?.getString("workoutType") ?: WorkoutType.PUSH.name
            )
            val date = LocalDate.parse(
                backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
            )
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: -1L

            // Get existing exercises from the workout being edited
            val existingWorkout = if (workoutId != -1L) {
                workouts.find { it.id == workoutId }
            } else null

            // Get newly selected exercises, if any
            val newlySelectedExercises = navController
                .currentBackStackEntry
                ?.savedStateHandle
                ?.get<List<WorkoutExercise>>("selected_exercises")

            // Determine which exercises to show
            val initialExercises = when {
                // If we're editing and have new selections, use those
                existingWorkout != null && newlySelectedExercises != null -> {
                    // Create a map of existing exercises by their ID
                    val existingExercisesMap = existingWorkout.exercises.associateBy { it.exercise.id }
                    
                    // Combine existing and new exercises, preserving sets for existing ones
                    (existingWorkout.exercises + newlySelectedExercises)
                        .distinctBy { it.exercise.id }
                        .map { exercise ->
                            // If this exercise already existed, keep its sets
                            existingExercisesMap[exercise.exercise.id] ?: exercise
                        }
                }
                // If we're editing but no new selections, use existing exercises
                existingWorkout != null -> existingWorkout.exercises
                // If we have new selections but aren't editing, use those
                newlySelectedExercises != null -> newlySelectedExercises
                // Otherwise, start with empty list
                else -> emptyList()
            }
            
            WorkoutTrackingScreen(
                workoutType = workoutType,
                date = date,
                initialExercises = initialExercises,
                onAddExercises = {
                    navController.navigate(
                        "select_exercises/${workoutType.name}/${date}?workoutId=${workoutId}"
                    )
                },
                onSaveWorkout = { workout ->
                    val finalWorkout = if (workoutId != -1L) {
                        workout.copy(id = workoutId)
                    } else {
                        workout
                    }
                    onSaveWorkout(finalWorkout)
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "select_exercises/{workoutType}/{date}?workoutId={workoutId}",
            arguments = listOf(
                navArgument("workoutType") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("workoutId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val workoutType = WorkoutType.valueOf(
                backStackEntry.arguments?.getString("workoutType") ?: WorkoutType.PUSH.name
            )
            val date = LocalDate.parse(
                backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
            )
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: -1L
            
            // Get the existing workout if we're editing
            val existingWorkout = if (workoutId != -1L) {
                workouts.find { it.id == workoutId }
            } else null
            
            ExerciseSelectionScreen(
                workoutType = workoutType,
                date = date,
                initialExercises = emptyList(),
                database = database,
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
                    },
                    onDelete = {
                        coroutineScope.launch {
                            database.workoutDao().deleteFullWorkout(workout.id)
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    }
                )
            }
        }

        // Add progress dashboard route
        composable("progress_dashboard") {
            ProgressDashboardScreen(
                workouts = workouts,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Add exercise library route
        composable("exercise_library") {
            ExerciseLibraryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Add programs route
        composable("programs") {
            ProgramsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onProgramSelected = { program ->
                    // For now, just navigate to create workout
                    // In the future, we can add program tracking
                    navController.navigate("create_workout")
                }
            )
        }

        // Add nutrition route
        composable("nutrition") {
            NutritionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
} 