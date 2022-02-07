package data

import Gpu
import androidx.compose.runtime.*
import androidx.compose.ui.window.WindowPlacement
import config.Config
import config.Wallet
import config.arguments.*
import functions.tryOrNull
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import miner.*
import java.io.File
import kotlin.random.Random
import kotlin.random.nextULong

val folder = System.getenv("LOCALAPPDATA") + File.separator + "PhoenixMinerGUI"

@Serializable
class SettingsData constructor(
	val phoenixPath: String = "",
	val gpus: Array<Gpu> = emptyArray(),
	val miners: Array<MinerData> = emptyArray(),
	val width: Int = 720,
	val height: Int = 1280,
	val placement: WindowPlacement = WindowPlacement.Maximized,
	val positionX: Int = 0,
	val positionY: Int = 0,
) {
	
	companion object {
		
		fun generateFromSettings() = SettingsData(
			Settings.phoenixPath,
			Settings.gpus,
			Settings.miners.map { it.toMinerData() }.toTypedArray(),
			Settings.width,
			Settings.height,
			Settings.placement,
			Settings.positionX,
			Settings.positionY
		)
	}
}

object Settings {
	
	var phoenixPath by mutableStateOf("")
	var gpus by mutableStateOf(emptyArray<Gpu>())
	var miners by mutableStateOf(
		arrayOf(
			Miner(
				"Donate Your Hashpower To The Dev", Id(1),
				false,
				Config.WalletParameter(WalletArgument.Wallet, Wallet("0x65cbddb4e7dd27009278d3160c8a5a4990d580d9")),
				Config.StringParameter(StringArgument.Pool, "eu1.ethermine.org:4444"),
				Config.StringParameter(StringArgument.Worker, "Donation${Random.nextULong()}"),
				Config.BooleanParameter(BooleanArgument.Log, false),
				Config.NumberParameter(NumberArgument.Ttli, 80)
			)
		)
	)
	var width: Int = 720
	var height: Int = 1280
	var placement: WindowPlacement = WindowPlacement.Maximized
	var positionX: Int = 0
	var positionY: Int = 0
	
	val activeMiners get() = miners.filter { it.isActive }
	
	private val minersToStart = mutableListOf<Miner>()
	
	fun startMiner(miner: Miner) {
		if (miner !in minersToStart) {
			miner.status = MinerStatus.Waiting
			minersToStart.add(miner)
		}
	}
	
	private val coroutineScope = CoroutineScope(Job())
	
	init {
		(tryOrNull {
			val file = File(folder + File.separator + "settings.json")
			Json.decodeFromString(file.readText())
		} ?: SettingsData.generateFromSettings()).let { settingsData ->
			phoenixPath = settingsData.phoenixPath
			gpus = settingsData.gpus
			miners = settingsData.miners.map { it.toMiner() }.toTypedArray()
			width = settingsData.width
			height = settingsData.height
			placement = settingsData.placement
			positionX = settingsData.positionX
			positionY = settingsData.positionY
		}
		
		coroutineScope.launch {
			try {
				withContext(Dispatchers.Main) { println("Miners count: ${miners.size}") }
				
				while (true) {
					delay(100L)
					while (activeMiners.any { it.pid == null }) {
						delay(100L)
					}
					minersToStart.firstOrNull()?.let { miner ->
						if (miner.status == MinerStatus.Waiting) {
							if (
								when {
									miner.assignedGpuIds.isNotEmpty() -> miner.assignedGpuIds.none { id -> gpus[id.value].inUse }
									else -> gpus.none { it.inUse }
								}
							) {
								miner.startMining()
							} else {
								miner.status = MinerStatus.Offline
							}
						}
						minersToStart.removeFirst()
					}
				}
			} catch (e: CancellationException) {
				println("settings killed")
			}
			
		}
	}
	
	fun save() {
		File(folder).mkdirs()
		val file = File(folder + File.separator + "settings.json")
		file.createNewFile()
		file.writeText(Json.encodeToString(SettingsData.generateFromSettings()))
	}
}
