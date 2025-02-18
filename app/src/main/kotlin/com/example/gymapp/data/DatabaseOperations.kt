package com.example.gymapp.data

import kotlinx.coroutines.flow.*
import java.io.IOException
import kotlin.Result

sealed class DatabaseResult<out T> {
    data class Success<T>(val data: T) : DatabaseResult<T>()
    data class Error(val exception: Exception) : DatabaseResult<Nothing>()
}

class DatabaseOperations(private val database: WorkoutDatabase) {
    suspend fun insertWorkout(workout: Workout): DatabaseResult<Long> {
        return try {
            val result = database.workoutDao().insertFullWorkout(workout)
            DatabaseResult.Success(result)
        } catch (e: Exception) {
            DatabaseResult.Error(e)
        }
    }

    suspend fun deleteWorkout(workoutId: Long): DatabaseResult<Unit> {
        return try {
            database.workoutDao().deleteFullWorkout(workoutId)
            DatabaseResult.Success(Unit)
        } catch (e: Exception) {
            DatabaseResult.Error(e)
        }
    }

    fun getAllWorkouts(): Flow<DatabaseResult<List<Workout>>> {
        return database.workoutDao()
            .getAllWorkouts()
            .map { workoutEntities ->
                try {
                    val workouts = workoutEntities.map { workoutWithExercises ->
                        Workout(
                            id = workoutWithExercises.workout.id,
                            type = WorkoutType.valueOf(workoutWithExercises.workout.type),
                            date = workoutWithExercises.workout.date,
                            exercises = workoutWithExercises.exercises.map { exerciseWithSets ->
                                WorkoutExercise(
                                    exercise = Exercise(
                                        id = exerciseWithSets.exercise.exerciseId,
                                        name = exerciseWithSets.exercise.exerciseName,
                                        category = ExerciseCategory.valueOf(
                                            exerciseWithSets.exercise.exerciseCategory
                                        )
                                    ),
                                    sets = exerciseWithSets.sets
                                        .sortedBy { it.setNumber }
                                        .map { set ->
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
                    DatabaseResult.Success(workouts)
                } catch (e: Exception) {
                    DatabaseResult.Error(e)
                }
            }
            .catch { e -> emit(DatabaseResult.Error(IOException(e))) }
    }

    suspend fun insertCustomExercise(exercise: CustomExerciseEntity): DatabaseResult<Long> {
        return try {
            val result = database.customExerciseDao().insertCustomExercise(exercise)
            DatabaseResult.Success(result)
        } catch (e: Exception) {
            DatabaseResult.Error(e)
        }
    }

    suspend fun deleteCustomExercise(exercise: CustomExerciseEntity): DatabaseResult<Unit> {
        return try {
            database.customExerciseDao().deleteCustomExercise(exercise)
            DatabaseResult.Success(Unit)
        } catch (e: Exception) {
            DatabaseResult.Error(e)
        }
    }

    fun getAllCustomExercises(): Flow<DatabaseResult<List<CustomExerciseEntity>>> {
        return database.customExerciseDao()
            .getAllCustomExercises()
            .map { DatabaseResult.Success(it) }
            .catch { e -> emit(DatabaseResult.Error(IOException(e))) }
    }

    suspend fun exerciseExists(name: String): DatabaseResult<Boolean> {
        return try {
            val exists = database.customExerciseDao().exerciseExists(name)
            DatabaseResult.Success(exists)
        } catch (e: Exception) {
            DatabaseResult.Error(e)
        }
    }

    // Auto-save functionality for workouts in progress
    private val workoutAutoSaveDao = database.workoutAutoSaveDao()

    suspend fun saveWorkoutProgress(workout: Workout): DatabaseResult<Unit> {
        return try {
            workoutAutoSaveDao.saveWorkoutProgress(
                WorkoutProgressEntity(
                    id = workout.id,
                    type = workout.type.name,
                    date = workout.date,
                    exercises = workout.exercises.map { exercise ->
                        WorkoutExerciseProgressEntity(
                            exerciseId = exercise.exercise.id,
                            exerciseName = exercise.exercise.name,
                            exerciseCategory = exercise.exercise.category.name,
                            sets = exercise.sets.map { set ->
                                WorkoutSetProgressEntity(
                                    setNumber = set.setNumber,
                                    weight = set.weight,
                                    reps = set.reps
                                )
                            }
                        )
                    }
                )
            )
            DatabaseResult.Success(Unit)
        } catch (e: Exception) {
            DatabaseResult.Error(e)
        }
    }

    fun getWorkoutProgress(workoutId: Long): Flow<DatabaseResult<Workout?>> {
        return workoutAutoSaveDao
            .getWorkoutProgress(workoutId)
            .map { progressEntity ->
                try {
                    progressEntity?.let { entity ->
                        DatabaseResult.Success(
                            Workout(
                                id = entity.id,
                                type = WorkoutType.valueOf(entity.type),
                                date = entity.date,
                                exercises = entity.exercises.map { exercise ->
                                    WorkoutExercise(
                                        exercise = Exercise(
                                            id = exercise.exerciseId,
                                            name = exercise.exerciseName,
                                            category = ExerciseCategory.valueOf(
                                                exercise.exerciseCategory
                                            )
                                        ),
                                        sets = exercise.sets.map { set ->
                                            WorkoutSet(
                                                setNumber = set.setNumber,
                                                weight = set.weight,
                                                reps = set.reps
                                            )
                                        }
                                    )
                                }
                            )
                        )
                    } ?: DatabaseResult.Success(null)
                } catch (e: Exception) {
                    DatabaseResult.Error(e)
                }
            }
            .catch { e -> emit(DatabaseResult.Error(IOException(e))) }
    }

    suspend fun clearWorkoutProgress(workoutId: Long): DatabaseResult<Unit> {
        return try {
            workoutAutoSaveDao.clearWorkoutProgress(workoutId)
            DatabaseResult.Success(Unit)
        } catch (e: Exception) {
            DatabaseResult.Error(e)
        }
    }
} 