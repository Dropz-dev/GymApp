import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onNavigateToPrograms: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // TODO: Add image here
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onNavigateToPrograms,
            modifier = Modifier.size(width = 200.dp, height = 48.dp)
        ) {
            Text("Start Training")
        }
    }
} 