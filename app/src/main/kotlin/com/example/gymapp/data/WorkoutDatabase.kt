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

    @Transaction
    @Query("SELECT * FROM workouts WHERE type = :type ORDER BY date DESC LIMIT 1")
    suspend fun getLastWorkoutByType(type: String): WorkoutWithExercises?

    @Insert
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Insert
    suspend fun insertWorkoutExercise(exercise: WorkoutExerciseEntity): Long

    @Insert
    suspend fun insertWorkoutSet(set: WorkoutSetEntity)

    @Transaction
    suspend fun insertFullWorkout(workout: Workout) {
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