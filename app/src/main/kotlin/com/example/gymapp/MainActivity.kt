package com.example.gymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gymapp.data.model.Program
import com.example.gymapp.data.model.TrainingDay
import com.example.gymapp.data.model.ExerciseSet
import com.example.gymapp.navigation.Screen
import com.example.gymapp.ui.screens.HomeScreen
import com.example.gymapp.ui.screens.ProgramListScreen
import com.example.gymapp.ui.screens.TrainingDayScreen
import com.example.gymapp.ui.theme.GymAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Sample program data (we'll move this to a proper data source later)
        val sampleProgram = Program(
            id = 1L,
            name = "Push/Pull/Legs",
            trainingDays = listOf(
                TrainingDay(
                    id = 1L,
                    name = "Push Day",
                    exercises = listOf(
                        ExerciseSet(1L, "Bench Press", 100f, 8),
                        ExerciseSet(2L, "Shoulder Press", 60f, 10),
                        ExerciseSet(3L, "Triceps Extension", 30f, 12)
                    )
                ),
                // Add Pull and Legs days similarly
            )
        )
        
        setContent {
            GymAppTheme {
                val navController = rememberNavController()
                
                NavHost(navController = navController, startDestination = Screen.Home.route) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onNavigateToPrograms = {
                                navController.navigate(Screen.ProgramList.route)
                            }
                        )
                    }
                    
                    composable(Screen.ProgramList.route) {
                        ProgramListScreen(
                            programs = listOf(sampleProgram),
                            onProgramSelected = { programId, dayId ->
                                navController.navigate(
                                    Screen.TrainingDay.createRoute(programId, dayId)
                                )
                            }
                        )
                    }
                    
                    composable(
                        route = Screen.TrainingDay.route,
                        arguments = listOf(
                            navArgument("programId") { type = NavType.StringType },
                            navArgument("dayId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val programId = backStackEntry.arguments?.getString("programId")?.toLongOrNull() ?: return@composable
                        val dayId = backStackEntry.arguments?.getString("dayId")?.toLongOrNull() ?: return@composable
                        
                        // Find the training day (in a real app, this would come from a ViewModel)
                        val trainingDay = sampleProgram.trainingDays.find { it.id == dayId }
                        
                        trainingDay?.let { day ->
                            TrainingDayScreen(
                                trainingDay = day,
                                onExerciseCompleted = { exerciseId, reps ->
                                    // TODO: Save the completed exercise
                                }
                            )
                        }
                    }
                }
            }
        }
    }
} 