import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import data.Settings
import data.Settings.minerToEdit
import functions.*
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import miner.Miner
import phoenix.phoenixPathIsCorrect
import ui.AnimatedVisibilityWithFade
import ui.screen.*
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
	CoroutineScope(Job()).launch {
		if (phoenixAvailable) {
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
				withContext(Dispatchers.Main)
				{
					Settings.miners.firstOrNull {
						it.name == arg || tryOrFalse {
							it.id.value == arg.toInt()
						}
					}?.let { Settings.startMiner(it) }
				}
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
	val windowState = rememberWindowState(
		width = Settings.width.dp,
		height = Settings.height.dp,
		placement = Settings.placement,
		position = WindowPosition(Settings.positionX.dp, Settings.positionY.dp)
	)
	Window(
		title = "PhoenixMiner GUI",
		icon = icon,
		state = windowState,
		onCloseRequest = {
			Miner.stopAllMiners("/nokill" !in args)
			Settings.saveSettings()
			exitApplication()
		}
	) {
		window.addWindowStateListener {
			Settings.apply {
				height = window.height
				width = window.width
				placement = window.placement
				positionX = windowState.position.x.value.toInt()
				positionY = windowState.position.y.value.toInt()
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