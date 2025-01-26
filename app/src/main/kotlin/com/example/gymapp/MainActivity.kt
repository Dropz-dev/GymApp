package com.example.gymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gymapp.data.model.Program
import com.example.gymapp.data.model.TrainingDay
import com.example.gymapp.data.model.ExerciseSet
import com.example.gymapp.ui.screens.*
import com.example.gymapp.ui.theme.GymAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
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
                    GymmiApp()
                }
            }
        }
    }
}

@Composable
fun GymmiApp() {
    val navController = rememberNavController()
    
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
                onWorkoutCreated = {
                    // For now, just navigate back to home
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
} 