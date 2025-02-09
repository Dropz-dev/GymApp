package com.example.gymapp.data.model

import com.example.gymapp.ui.screens.WorkoutType

data class WorkoutTemplate(
    val id: Long,
    val name: String,
    val type: WorkoutType,
    val exercises: List<TemplateExercise>,
    val description: String = ""
)

data class TemplateExercise(
    val exercise: Exercise,
    val targetSets: Int,
    val targetRepsPerSet: IntRange,
    val notes: String = ""
)

data class WorkoutProgram(
    val id: Long,
    val name: String,
    val description: String,
    val difficulty: ProgramDifficulty,
    val duration: Int, // in weeks
    val workoutsPerWeek: Int,
    val workouts: List<WorkoutTemplate>
)

enum class ProgramDifficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED;

    override fun toString(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

object PresetPrograms {
    private val benchPress = Exercise(1L, "Bench Press", ExerciseCategory.CHEST)
    private val militaryPress = Exercise(8L, "Military Press", ExerciseCategory.SHOULDERS)
    private val tricepPushdown = Exercise(15L, "Tricep Pushdown", ExerciseCategory.TRICEPS)
    private val latPulldown = Exercise(22L, "Lat Pulldown", ExerciseCategory.BACK)
    private val dumbbellRows = Exercise(24L, "Dumbbell Rows", ExerciseCategory.BACK)
    private val dumbbellCurls = Exercise(29L, "Dumbbell Curls", ExerciseCategory.BICEPS)
    private val squats = Exercise(35L, "Squats", ExerciseCategory.LEGS)
    private val romanianDeadlift = Exercise(37L, "Romanian Deadlift", ExerciseCategory.LEGS)
    private val calfRaises = Exercise(40L, "Calf Raises", ExerciseCategory.LEGS)

    val programs = listOf(
        WorkoutProgram(
            id = 1,
            name = "Beginner Push/Pull/Legs",
            description = "A 3-day split perfect for beginners focusing on the main movement patterns.",
            difficulty = ProgramDifficulty.BEGINNER,
            duration = 8,
            workoutsPerWeek = 3,
            workouts = listOf(
                // Push Day
                WorkoutTemplate(
                    id = 1,
                    name = "Push Day",
                    type = WorkoutType.PUSH,
                    description = "Focus on chest, shoulders, and triceps",
                    exercises = listOf(
                        TemplateExercise(
                            exercise = benchPress,
                            targetSets = 3,
                            targetRepsPerSet = 8..12,
                            notes = "Focus on form and controlled movement"
                        ),
                        TemplateExercise(
                            exercise = militaryPress,
                            targetSets = 3,
                            targetRepsPerSet = 8..12
                        ),
                        TemplateExercise(
                            exercise = tricepPushdown,
                            targetSets = 3,
                            targetRepsPerSet = 10..15
                        )
                    )
                ),
                // Pull Day
                WorkoutTemplate(
                    id = 2,
                    name = "Pull Day",
                    type = WorkoutType.PULL,
                    description = "Focus on back and biceps",
                    exercises = listOf(
                        TemplateExercise(
                            exercise = latPulldown,
                            targetSets = 3,
                            targetRepsPerSet = 8..12
                        ),
                        TemplateExercise(
                            exercise = dumbbellRows,
                            targetSets = 3,
                            targetRepsPerSet = 8..12
                        ),
                        TemplateExercise(
                            exercise = dumbbellCurls,
                            targetSets = 3,
                            targetRepsPerSet = 10..15
                        )
                    )
                ),
                // Legs Day
                WorkoutTemplate(
                    id = 3,
                    name = "Legs Day",
                    type = WorkoutType.LEGS,
                    description = "Full lower body workout",
                    exercises = listOf(
                        TemplateExercise(
                            exercise = squats,
                            targetSets = 3,
                            targetRepsPerSet = 8..12,
                            notes = "Focus on depth and knee alignment"
                        ),
                        TemplateExercise(
                            exercise = romanianDeadlift,
                            targetSets = 3,
                            targetRepsPerSet = 8..12
                        ),
                        TemplateExercise(
                            exercise = calfRaises,
                            targetSets = 3,
                            targetRepsPerSet = 12..15
                        )
                    )
                )
            )
        )
    )
} 