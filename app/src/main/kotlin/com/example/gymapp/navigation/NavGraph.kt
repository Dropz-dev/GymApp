sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ProgramList : Screen("program_list")
    object TrainingDay : Screen("training_day/{programId}/{dayId}") {
        fun createRoute(programId: Long, dayId: Long) = "training_day/$programId/$dayId"
    }
} 