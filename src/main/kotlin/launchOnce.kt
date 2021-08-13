import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CoroutineScope.launchOnce(block: suspend CoroutineScope.() -> Unit)
{
	var wasExecuted by remember {mutableStateOf(false)}
	if (!wasExecuted)
	{
		wasExecuted = true
		this.launch(block = block)
	}
}