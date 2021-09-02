import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
suspend fun taskKill(imageName: String, forcefully: Boolean) = process("taskkill", if (forcefully) "/F" else "", "/IM", imageName, stdout = Redirect.SILENT, stderr = Redirect.SILENT)

@ExperimentalCoroutinesApi
suspend fun taskKill(pid: Int, forcefully: Boolean) = process("taskkill", if (forcefully) "/F" else "", "/PID", pid.toString(), stdout = Redirect.SILENT, stderr = Redirect.SILENT)