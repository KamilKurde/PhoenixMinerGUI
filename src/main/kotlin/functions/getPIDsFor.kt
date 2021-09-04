package functions

import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
suspend fun getPIDsFor(imageName: String): List<Int>
{
	// imageName must be wrapped like this because of how ProcessBuilder works https://stackoverflow.com/questions/12124935/processbuilder-adds-extra-quotes-to-command-line#answer-20009602
	val nameQuery = if(imageName.contains(" ")) "name=\\\"$imageName\\\"" else "name=\"$imageName\""
	val process = process("wmic", "process", "where", nameQuery , "get", "ProcessID", stdout = Redirect.CAPTURE, stderr = Redirect.SILENT)
	return process.output.filterNot { it.contains("ProcessId") || it.isEmpty() }.map { it.trim().toInt() }
}