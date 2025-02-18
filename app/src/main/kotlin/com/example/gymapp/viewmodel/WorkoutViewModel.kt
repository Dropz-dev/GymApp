package com.example.gymapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class WorkoutViewModel(
    private val databaseOperations: DatabaseOperations
) : ViewModel() {
    private val _workouts = MutableStateFlow<UiState<List<Workout>>>(UiState.Loading)
    val workouts: StateFlow<UiState<List<Workout>>> = _workouts.asStateFlow()

    private val _customExercises = MutableStateFlow<UiState<List<CustomExerciseEntity>>>(UiState.Loading)
    val customExercises: StateFlow<UiState<List<CustomExerciseEntity>>> = _customExercises.asStateFlow()

    // Cache for workout progress
    private val workoutProgressCache = mutableMapOf<Long, Workout>()

    init {
        loadWorkouts()
        loadCustomExercises()
    }

    private fun loadWorkouts() {
        viewModelScope.launch {
            databaseOperations.getAllWorkouts()
                .map { result ->
                    when (result) {
                        is DatabaseResult.Success -> UiState.Success(result.data)
                        is DatabaseResult.Error -> UiState.Error(
                            result.exception.message ?: "Unknown error"
                        )
                    }
                }
                .catch { e ->
                    _workouts.value = UiState.Error(e.message ?: "Unknown error")
                }
                .collect { state ->
                    _workouts.value = state
                }
        }
    }

    private fun loadCustomExercises() {
        viewModelScope.launch {
            databaseOperations.getAllCustomExercises()
                .map { result ->
                    when (result) {
                        is DatabaseResult.Success -> UiState.Success(result.data)
                        is DatabaseResult.Error -> UiState.Error(
                            result.exception.message ?: "Unknown error"
                        )
                    }
                }
                .catch { e ->
                    _customExercises.value = UiState.Error(e.message ?: "Unknown error")
                }
                .collect { state ->
                    _customExercises.value = state
                }
        }
    }

    fun saveWorkout(workout: Workout, onSuccess: () -> Unit) {
        viewModelScope.launch {
            when (val result = databaseOperations.insertWorkout(workout)) {
                is DatabaseResult.Success -> {
                    clearWorkoutProgress(workout.id)
                    onSuccess()
                }
                is DatabaseResult.Error -> {
                    // Handle error
                }
            }
        }
    }

    fun deleteWorkout(workoutId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            when (val result = databaseOperations.deleteWorkout(workoutId)) {
                is DatabaseResult.Success -> onSuccess()
                is DatabaseResult.Error -> {
                    // Handle error
                }
            }
        }
    }

    fun saveWorkoutProgress(workout: Workout) {
        viewModelScope.launch {
            workoutProgressCache[workout.id] = workout
            databaseOperations.saveWorkoutProgress(workout)
        }
    }

    fun getWorkoutProgress(workoutId: Long): Flow<Workout?> {
        return flow {
            // First check the cache
            workoutProgressCache[workoutId]?.let {
                emit(it)
                return@flow
            }

            // If not in cache, load from database
            databaseOperations.getWorkoutProgress(workoutId)
                .map { result ->
                    when (result) {
                        is DatabaseResult.Success -> result.data
                        is DatabaseResult.Error -> null
                    }
                }
                .collect { workout ->
                    workout?.let { workoutProgressCache[workoutId] = it }
                    emit(workout)
                }
        }
    }

    private fun clearWorkoutProgress(workoutId: Long) {
        viewModelScope.launch {
            workoutProgressCache.remove(workoutId)
            databaseOperations.clearWorkoutProgress(workoutId)
        }
    }

    fun addCustomExercise(exercise: CustomExerciseEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            when (val result = databaseOperations.insertCustomExercise(exercise)) {
                is DatabaseResult.Success -> onSuccess()
                is DatabaseResult.Error -> {
                    // Handle error
                }
            }
        }
    }

    fun deleteCustomExercise(exercise: CustomExerciseEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            when (val result = databaseOperations.deleteCustomExercise(exercise)) {
                is DatabaseResult.Success -> onSuccess()
                is DatabaseResult.Error -> {
                    // Handle error
                }
            }
        }
    }

    fun checkExerciseExists(name: String): Flow<Boolean> {
        return flow {
            when (val result = databaseOperations.exerciseExists(name)) {
                is DatabaseResult.Success -> emit(result.data)
                is DatabaseResult.Error -> emit(false)
            }
        }
    }

    // Helper function to get exercise history
    fun getExerciseHistory(exerciseId: Long): Flow<List<ExerciseProgress>> {
        return workouts.map { state ->
            when (state) {
                is UiState.Success -> {
                    state.data
                        .filter { workout ->
                            workout.exercises.any { it.exercise.id == exerciseId }
                        }
                        .map { workout ->
                            val exerciseSets = workout.exercises
                                .first { it.exercise.id == exerciseId }
                                .sets
                            
                            ExerciseProgress(
                                date = workout.date,
                                maxWeight = exerciseSets.maxOf { it.weight },
                                maxReps = exerciseSets.maxOf { it.reps },
                                maxVolume = exerciseSets.maxOf { it.weight * it.reps },
                                totalVolume = exerciseSets.sumOf { 
                                    (it.weight * it.reps).toDouble() 
                                }.toFloat(),
                                sets = exerciseSets
                            )
                        }
                        .sortedBy { it.date }
                }
                else -> emptyList()
            }
        }
    }
} 