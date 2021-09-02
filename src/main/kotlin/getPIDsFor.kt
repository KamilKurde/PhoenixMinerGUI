import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
suspend fun getPIDsFor(imageName: String) = process(
	"wmic", "process", "where", "name=\"$imageName\"", "get", "ProcessID",
	stdout = Redirect.CAPTURE,
).output.filter { !it.contains("ProcessId") && it.isNotEmpty() }.map { it.trim().toInt() }