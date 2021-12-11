package functions

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CoroutineScope.launchOnce(block: suspend CoroutineScope.() -> Unit) {
	var wasExecuted by remember { mutableStateOf(false) }
	if (!wasExecuted) {
		wasExecuted = true
		this.launch(block = block)
	}
}