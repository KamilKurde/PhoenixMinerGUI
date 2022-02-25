import activity.Summary
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import com.github.KamilKurde.Application
import com.github.KamilKurde.Intent
import data.Settings
import functions.*
import kotlinx.coroutines.*
import miner.Miner
import phoenix.phoenixPathIsCorrect

val icon = @Composable { painterResource("icon.ico") }

fun main(args: Array<String>) = Application {
	val phoenixAvailable = phoenixPathIsCorrect(Settings.phoenixPath)
	if (phoenixAvailable) {
		CoroutineScope(Job()).launch {
			if ("/nokill" !in args) {
				// Kills all other PhoenixMiner GUI instances
				val pid = ProcessHandle.current().pid().toInt()
				getPIDsFor("PhoenixMiner GUI.exe").minus(pid).forEach {
					taskKill(it, true)
				}
				Miner.killAllMiners()
			} else {
				Settings.nokill = true
			}
			args.forEach { arg ->
				Settings.miners.firstOrNull {
					it.name == arg || tryOrFalse {
						it.id.value == arg.toInt()
					}
				}?.let { Settings.startMiner(it) }
			}
			if ("/nomos" !in args) {
				Settings.miners.filter { it.mineOnStartup }.forEach {
					Settings.startMiner(it)
				}
			}
		}
	}
	val windowState = WindowState(
		width = Settings.width.dp,
		height = Settings.height.dp,
		placement = Settings.placement,
		position = WindowPosition(Settings.positionX.dp, Settings.positionY.dp)
	)
	val intent = Intent(Summary::class)
	com.github.KamilKurde.Window(
		intent,
		title = "PhoenixMiner GUI",
		icon = icon,
		windowState = windowState,
		onCloseRequest = {
			runBlocking { Miner.stopAllMiners("/nokill" !in args) }
			Settings.apply {
				height = windowState.size.height.value.toInt()
				width = windowState.size.width.value.toInt()
				placement = windowState.placement
				positionX = windowState.position.x.value.toInt()
				positionY = windowState.position.y.value.toInt()
				save()
			}
			close()
		}
	)
}