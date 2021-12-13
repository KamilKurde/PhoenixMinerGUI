package phoenix

import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import java.io.File

@ExperimentalCoroutinesApi
fun phoenixPathIsCorrect(path: String): Boolean {
	if (path.split(File.separator).last() != "PhoenixMiner.exe") {
		return false
	}
	return runBlocking { process(path, "-v", stdout = Redirect.CAPTURE).output[0].startsWith("Phoenix Miner") }
}