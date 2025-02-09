package com.example.gymapp.data

import android.content.Context
import androidx.room.*
import com.example.gymapp.data.model.*
import com.example.gymapp.ui.screens.WorkoutType
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey val id: Long,
    val type: String,
    val date: LocalDate
)

@Entity(tableName = "workout_exercises")
data class WorkoutExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val exerciseCategory: String
)

@Entity(tableName = "workout_sets")
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val exerciseId: Long,
    val setNumber: Int,
    val weight: Float,
    val reps: Int
)

@Entity(tableName = "custom_exercises")
data class CustomExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String
)

data class WorkoutWithExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        entity = WorkoutExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    val exercises: List<WorkoutExerciseWithSets>
)

data class WorkoutExerciseWithSets(
    @Embedded val exercise: WorkoutExerciseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "exerciseId"
    )
    val sets: List<WorkoutSetEntity>
)

@Dao
interface WorkoutDao {
    @Transaction
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<WorkoutWithExercises>>

    @Query("DELETE FROM workout_sets WHERE workoutId = :workoutId")
    suspend fun deleteWorkoutSets(workoutId: Long)

    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteWorkoutExercises(workoutId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Insert
    suspend fun insertWorkoutExercise(exercise: WorkoutExerciseEntity): Long

    @Insert
    suspend fun insertWorkoutSet(set: WorkoutSetEntity)

    @Transaction
    suspend fun insertFullWorkout(workout: Workout) {
        // For updates, we want to keep existing exercises and their sets
        if (workout.id != 0L) {
            // Update the workout entity
            insertWorkout(
                WorkoutEntity(
                    id = workout.id,
                    type = workout.type.name,
                    date = workout.date
                )
            )

            // Get map of existing exercises
            val existingExercises = workout.exercises.associateBy { 
                "${it.exercise.id}_${workout.id}" 
            }

            // Delete exercises that are no longer present
            deleteWorkoutExercisesNotIn(
                workout.id,
                workout.exercises.map { it.exercise.id }.toSet()
            )

            // Update or insert exercises and their sets
            workout.exercises.forEach { exercise ->
                val exerciseKey = "${exercise.exercise.id}_${workout.id}"
                
                // Insert or update the exercise
                val exerciseId = insertWorkoutExercise(
                    WorkoutExerciseEntity(
                        workoutId = workout.id,
                        exerciseId = exercise.exercise.id,
                        exerciseName = exercise.exercise.name,
                        exerciseCategory = exercise.exercise.category.name
                    )
                )

                // Only update sets if they've changed
                if (!existingExercises.containsKey(exerciseKey) || 
                    existingExercises[exerciseKey]?.sets != exercise.sets) {
                    // Delete existing sets for this exercise
                    deleteWorkoutSetsForExercise(workout.id, exerciseId)

                    // Insert new sets
                    exercise.sets.forEach { set ->
                        insertWorkoutSet(
                            WorkoutSetEntity(
                                workoutId = workout.id,
                                exerciseId = exerciseId,
                                setNumber = set.setNumber,
                                weight = set.weight,
                                reps = set.reps
                            )
                        )
                    }
                }
            }
        } else {
            // For new workouts, just insert everything
            val workoutId = insertWorkout(
                WorkoutEntity(
                    id = workout.id,
                    type = workout.type.name,
                    date = workout.date
                )
            )

            workout.exercises.forEach { exercise ->
                val exerciseId = insertWorkoutExercise(
                    WorkoutExerciseEntity(
                        workoutId = workoutId,
                        exerciseId = exercise.exercise.id,
                        exerciseName = exercise.exercise.name,
                        exerciseCategory = exercise.exercise.category.name
                    )
                )

                exercise.sets.forEach { set ->
                    insertWorkoutSet(
                        WorkoutSetEntity(
                            workoutId = workoutId,
                            exerciseId = exerciseId,
                            setNumber = set.setNumber,
                            weight = set.weight,
                            reps = set.reps
                        )
                    )
                }
            }
        }
    }

    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId AND exerciseId NOT IN (:exerciseIds)")
    suspend fun deleteWorkoutExercisesNotIn(workoutId: Long, exerciseIds: Set<Long>)

    @Query("DELETE FROM workout_sets WHERE workoutId = :workoutId AND exerciseId = :exerciseId")
    suspend fun deleteWorkoutSetsForExercise(workoutId: Long, exerciseId: Long)
}

@Dao
interface CustomExerciseDao {
    @Query("SELECT * FROM custom_exercises")
    fun getAllCustomExercises(): Flow<List<CustomExerciseEntity>>

    @Insert
    suspend fun insertCustomExercise(exercise: CustomExerciseEntity): Long

    @Delete
    suspend fun deleteCustomExercise(exercise: CustomExerciseEntity)
}

@Database(
    entities = [
        WorkoutEntity::class,
        WorkoutExerciseEntity::class,
        WorkoutSetEntity::class,
        CustomExerciseEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun customExerciseDao(): CustomExerciseDao

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getDatabase(context: Context): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
} 