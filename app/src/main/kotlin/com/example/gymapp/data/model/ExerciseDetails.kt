package com.example.gymapp.data.model

data class ExerciseDetails(
    val primaryMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val instructions: List<String>,
    val tips: List<String>,
    val commonMistakes: List<String>
) {
    companion object {
        private val exerciseDetails = mapOf(
            // Bench Press
            1L to ExerciseDetails(
                primaryMuscles = listOf("Chest", "Anterior Deltoids", "Triceps"),
                secondaryMuscles = listOf("Lateral Deltoids", "Core", "Serratus Anterior"),
                instructions = listOf(
                    "Lie on a flat bench with feet firmly planted on the ground",
                    "Grip the bar slightly wider than shoulder width",
                    "Unrack the bar and lower it to your chest with control",
                    "Press the bar back up to the starting position",
                    "Keep your wrists straight and elbows at about 45 degrees"
                ),
                tips = listOf(
                    "Keep your shoulder blades retracted throughout the movement",
                    "Maintain a slight arch in your lower back",
                    "Focus on pushing yourself away from the bar",
                    "Take a deep breath and brace your core before each rep"
                ),
                commonMistakes = listOf(
                    "Bouncing the bar off your chest",
                    "Flaring elbows too wide",
                    "Not maintaining proper back arch",
                    "Uneven bar path"
                )
            ),
            
            // Squats
            35L to ExerciseDetails(
                primaryMuscles = listOf("Quadriceps", "Glutes", "Hamstrings"),
                secondaryMuscles = listOf("Core", "Lower Back", "Calves"),
                instructions = listOf(
                    "Position the bar on your upper back, not your neck",
                    "Stand with feet shoulder-width apart, toes slightly pointed out",
                    "Bend at hips and knees simultaneously",
                    "Lower until thighs are parallel to ground or slightly below",
                    "Drive through your heels to return to starting position"
                ),
                tips = listOf(
                    "Keep your chest up throughout the movement",
                    "Track your knees in line with your toes",
                    "Maintain a neutral spine",
                    "Breathe in on the way down, out on the way up"
                ),
                commonMistakes = listOf(
                    "Knees caving inward",
                    "Rising with the hips first (good morning squat)",
                    "Not reaching proper depth",
                    "Looking up instead of maintaining a neutral neck position"
                )
            ),

            // Deadlift
            27L to ExerciseDetails(
                primaryMuscles = listOf("Lower Back", "Hamstrings", "Glutes"),
                secondaryMuscles = listOf("Upper Back", "Core", "Forearms"),
                instructions = listOf(
                    "Stand with feet hip-width apart, bar over mid-foot",
                    "Bend at hips and knees to grip the bar",
                    "Keep your chest up and back straight",
                    "Drive through your heels and extend hips and knees",
                    "Return the weight to the ground with control"
                ),
                tips = listOf(
                    "Keep the bar close to your body throughout the movement",
                    "Engage your lats before lifting",
                    "Think of pushing the floor away rather than pulling the bar",
                    "Take slack out of the bar before initiating the pull"
                ),
                commonMistakes = listOf(
                    "Rounding the back",
                    "Starting with the bar too far from your shins",
                    "Not engaging your lats",
                    "Jerking the weight off the floor"
                )
            )
            // Add more exercises as needed
        )

        fun getDetails(exerciseId: Long): ExerciseDetails? {
            return exerciseDetails[exerciseId]
        }
    }
} 