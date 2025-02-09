package com.example.gymapp.data.model

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
                            exercise = ExerciseList.exercises.find { it.id == 1L }!!, // Bench Press
                            targetSets = 3,
                            targetRepsPerSet = 8..12,
                            notes = "Focus on form and controlled movement"
                        ),
                        TemplateExercise(
                            exercise = ExerciseList.exercises.find { it.id == 8L }!!, // Military Press
                            targetSets = 3,
                            targetRepsPerSet = 8..12
                        ),
                        TemplateExercise(
                            exercise = ExerciseList.exercises.find { it.id == 15L }!!, // Tricep Pushdown
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
                            exercise = ExerciseList.exercises.find { it.id == 22L }!!, // Lat Pulldown
                            targetSets = 3,
                            targetRepsPerSet = 8..12
                        ),
                        TemplateExercise(
                            exercise = ExerciseList.exercises.find { it.id == 24L }!!, // Dumbbell Rows
                            targetSets = 3,
                            targetRepsPerSet = 8..12
                        ),
                        TemplateExercise(
                            exercise = ExerciseList.exercises.find { it.id == 29L }!!, // Dumbbell Curls
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
                            exercise = ExerciseList.exercises.find { it.id == 35L }!!, // Squats
                            targetSets = 3,
                            targetRepsPerSet = 8..12,
                            notes = "Focus on depth and knee alignment"
                        ),
                        TemplateExercise(
                            exercise = ExerciseList.exercises.find { it.id == 37L }!!, // Romanian Deadlift
                            targetSets = 3,
                            targetRepsPerSet = 8..12
                        ),
                        TemplateExercise(
                            exercise = ExerciseList.exercises.find { it.id == 40L }!!, // Calf Raises
                            targetSets = 3,
                            targetRepsPerSet = 12..15
                        )
                    )
                )
            )
        ),
        // Add more preset programs here
    )
} 