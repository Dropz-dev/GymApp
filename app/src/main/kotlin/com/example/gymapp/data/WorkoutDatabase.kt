package com.example.gymapp.data

import android.content.Context
import androidx.room.*
import com.example.gymapp.data.model.*
import com.example.gymapp.ui.screens.WorkoutType
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

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

@Entity(tableName = "workout_progress")
data class WorkoutProgressEntity(
    @PrimaryKey val id: Long,
    val type: String,
    val date: LocalDate,
    val exercises: List<WorkoutExerciseProgressEntity>
)

data class WorkoutExerciseProgressEntity(
    val exerciseId: Long,
    val exerciseName: String,
    val exerciseCategory: String,
    val sets: List<WorkoutSetProgressEntity>
)

data class WorkoutSetProgressEntity(
    val setNumber: Int,
    val weight: Float,
    val reps: Int
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

    @Query("DELETE FROM workouts WHERE id = :workoutId")
    suspend fun deleteWorkout(workoutId: Long)

    @Transaction
    suspend fun deleteFullWorkout(workoutId: Long) {
        deleteWorkoutSets(workoutId)
        deleteWorkoutExercises(workoutId)
        deleteWorkout(workoutId)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Insert
    suspend fun insertWorkoutExercise(exercise: WorkoutExerciseEntity): Long

    @Insert
    suspend fun insertWorkoutSet(set: WorkoutSetEntity)

    @Transaction
    suspend fun insertFullWorkout(workout: Workout) {
        // For updates, we need to handle existing data
        if (workout.id != 0L) {
            // First, delete all existing sets and exercises
            deleteWorkoutSets(workout.id)
            deleteWorkoutExercises(workout.id)
            
            // Update the workout entity
            insertWorkout(
                WorkoutEntity(
                    id = workout.id,
                    type = workout.type.name,
                    date = workout.date
                )
            )

            // Insert the updated exercises and sets
            workout.exercises.distinctBy { it.exercise.id }.forEach { exercise ->
                val exerciseId = insertWorkoutExercise(
                    WorkoutExerciseEntity(
                        workoutId = workout.id,
                        exerciseId = exercise.exercise.id,
                        exerciseName = exercise.exercise.name,
                        exerciseCategory = exercise.exercise.category.name
                    )
                )

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
        } else {
            // For new workouts, just insert everything
            val workoutId = insertWorkout(
                WorkoutEntity(
                    id = workout.id,
                    type = workout.type.name,
                    date = workout.date
                )
            )

            workout.exercises.distinctBy { it.exercise.id }.forEach { exercise ->
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
    @Query("SELECT * FROM custom_exercises ORDER BY name ASC")
    fun getAllCustomExercises(): Flow<List<CustomExerciseEntity>>

    @Query("SELECT * FROM custom_exercises WHERE id = :id")
    suspend fun getCustomExerciseById(id: Long): CustomExerciseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomExercise(exercise: CustomExerciseEntity): Long

    @Delete
    suspend fun deleteCustomExercise(exercise: CustomExerciseEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM custom_exercises WHERE name = :name)")
    suspend fun exerciseExists(name: String): Boolean
}

@Dao
interface WorkoutAutoSaveDao {
    @Query("SELECT * FROM workout_progress WHERE id = :workoutId")
    fun getWorkoutProgress(workoutId: Long): Flow<WorkoutProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWorkoutProgress(workout: WorkoutProgressEntity)

    @Query("DELETE FROM workout_progress WHERE id = :workoutId")
    suspend fun clearWorkoutProgress(workoutId: Long)
}

@Database(
    entities = [
        WorkoutEntity::class,
        WorkoutExerciseEntity::class,
        WorkoutSetEntity::class,
        CustomExerciseEntity::class,
        WorkoutProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun customExerciseDao(): CustomExerciseDao
    abstract fun workoutAutoSaveDao(): WorkoutAutoSaveDao

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

    @TypeConverter
    fun fromWorkoutExerciseProgressList(value: List<WorkoutExerciseProgressEntity>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toWorkoutExerciseProgressList(value: String): List<WorkoutExerciseProgressEntity> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromWorkoutSetProgressList(value: List<WorkoutSetProgressEntity>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toWorkoutSetProgressList(value: String): List<WorkoutSetProgressEntity> {
        return Json.decodeFromString(value)
    }
} 