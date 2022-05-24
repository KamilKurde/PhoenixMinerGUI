import activity.Summary
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import com.github.KamilKurde.*
import data.Settings
import data.miner.Miner
import functions.*
import kotlinx.coroutines.*
import phoenix.phoenixPathIsCorrect

val logo = @Composable { painterResource("icon.ico") }

var settings = Settings.load() ?: Settings()

fun main(args: Array<String>) = Application({ settings.addError(it) }) {
	val phoenixAvailable = phoenixPathIsCorrect(settings.phoenixPath)
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
				settings.nokill = true
			}
			args.forEach { arg ->
				settings.miners.firstOrNull {
					it.name == arg || tryOrFalse {
						it.id.value == arg.toInt()
					}
				}?.let { settings.startMiner(it) }
			}
			if ("/nomos" !in args) {
				settings.miners.filter { it.mineOnStartup }.forEach {
					settings.startMiner(it)
				}
			}
		}
	}
	title = "PhoenixMiner GUI"
	icon = logo
	windowState = WindowState(
		width = settings.width.dp,
		height = settings.height.dp,
		placement = settings.placement,
		position = WindowPosition(settings.positionX.dp, settings.positionY.dp)
	)
	defaultTheme = settings.theme
	onCloseRequest = {
		runBlocking { Miner.stopAllMiners("/nokill" !in args) }
		settings.apply {
			height = windowState.size.height.value.toInt()
			width = windowState.size.width.value.toInt()
			placement = windowState.placement
			positionX = windowState.position.x.value.toInt()
			positionY = windowState.position.y.value.toInt()
			save()
		}
	}
	
	startActivity(Intent(Summary::class))
	
	val settingsScope = CoroutineScope(Job())
	settingsScope.launch {
		delay(1000L)
		settings.executionLoop()
	}
}