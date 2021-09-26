import data.Settings.minerToEdit
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.Settings
import functions.*
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import miner.Miner
import phoenix.phoenixPathIsCorrect
import ui.AnimatedVisibilityWithFade
import ui.screen.MinerSettings
import ui.screen.Setup
import ui.screen.Summary
import ui.theme.AppTheme

val icon @Composable get() = painterResource("icon.ico")

@ExperimentalSerializationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
fun main(args: Array<String>) = application {
	val phoenixAvailable = phoenixPathIsCorrect(Settings.phoenixPath)
	Window(
		title = "PhoenixMiner GUI",
		icon = icon,
		state = rememberWindowState(width = 1500.dp, height = 720.dp),
		onCloseRequest = {
			Miner.stopAllMiners()
			exitApplication()
		}
	) {
		rememberCoroutineScope().launchOnce {
			if (phoenixAvailable)
			{
				args.ifNoArgCoroutine("/nokill")
				{
					// Kills all other PhoenixMiner GUI instances
					val pid = ProcessHandle.current().pid().toInt()
					withContext(Dispatchers.Main)
					{
						getPIDsFor("PhoenixMiner GUI.exe").minus(pid).forEach {
							taskKill(it, true)
						}
						Miner.killAllMiners()
					}
				}
				args.forEach { arg ->
					Settings.miners.firstOrNull {
						it.name == arg || tryOrFalse {
							it.id.value == arg.toInt()
						}
					}?.let { Settings.startMiner(it) }
				}
				args.ifNoArg("/nomos")
				{
					Settings.miners.filter { it.mineOnStartup }.forEach {
						Settings.startMiner(it)
					}
				}
				Settings.gpus = getGpus()
				Settings.saveSettings()
			}
		}
		AppTheme {
			AnimatedVisibilityWithFade(visible = !phoenixAvailable)
			{
				Setup()
			}
			AnimatedVisibilityWithFade(visible = minerToEdit == null && phoenixPathIsCorrect(Settings.phoenixPath))
			{
				Summary()
			}
			AnimatedVisibilityWithFade(visible = minerToEdit != null && phoenixPathIsCorrect(Settings.phoenixPath))
			{
				minerToEdit?.let {
					MinerSettings(it)
				}
			}
		}
	}
}