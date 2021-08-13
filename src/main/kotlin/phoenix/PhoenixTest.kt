package phoenix

import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
fun phoenixPathIsCorrect(path: String): Boolean
{
	if (!path.endsWith("PhoenixMiner.exe"))
	{
		return false
	}
	return runBlocking { process(path, "-h", stdout = Redirect.CAPTURE).output[0].startsWith("Phoenix Miner") }
}