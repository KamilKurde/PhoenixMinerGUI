@file:OptIn(ExperimentalCoroutinesApi::class)
@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")

package functions

import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import kotlinx.coroutines.ExperimentalCoroutinesApi

suspend fun taskKill(imageName: String, forcefully: Boolean) = process("taskkill", if (forcefully) "/F" else "", "/IM", imageName, stdout = Redirect.SILENT, stderr = Redirect.SILENT)

suspend fun taskKill(pid: Int, forcefully: Boolean) = process("taskkill", if (forcefully) "/F" else "", "/PID", pid.toString(), stdout = Redirect.SILENT, stderr = Redirect.SILENT)