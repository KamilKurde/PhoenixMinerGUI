package functions

import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
suspend fun getPIDsFor(imageName: String) = process(
	// imageName must be wrapped like this because of how ProcessBuilder works https://stackoverflow.com/questions/12124935/processbuilder-adds-extra-quotes-to-command-line#answer-20009602
	"wmic", "process", "where", "name=\\\"$imageName\\\"", "get", "ProcessID",
	stdout = Redirect.CAPTURE, stderr = Redirect.SILENT
).output.filter { !it.contains("ProcessId") && it.isNotEmpty() }.map { it.trim().toInt() }