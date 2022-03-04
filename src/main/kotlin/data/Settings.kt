package data

import Gpu
import androidx.compose.runtime.*
import androidx.compose.ui.window.WindowPlacement
import functions.*
import getGpus
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import miner.*
import phoenix.phoenixPathIsCorrect
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

val folder = System.getenv("LOCALAPPDATA") + File.separator + "PhoenixMinerGUI"

@Serializable
class SettingsData constructor(
	val phoenixPath: String = "",
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
	
	private val serializer = Json {
		ignoreUnknownKeys = true
		encodeDefaults = true
	}
	
	var phoenixPath by mutableStateOf("")
	var gpus by mutableStateOf(emptyArray<Gpu>())
	val miners = mutableStateListOf(*getDefaultMiners())
	var width: Int = 720
	var height: Int = 1280
	var placement: WindowPlacement = WindowPlacement.Maximized
	var positionX: Int = 0
	var positionY: Int = 0
	
	val activeMiners get() = miners.filter { it.isActive }
	
	var nokill = false
	
	private val minersToStart = mutableListOf<Miner>()
	
	fun startMiner(miner: Miner) {
		if (miner !in minersToStart && (!miner.isActive || miner.status == MinerStatus.ProgramError)) {
			miner.status = MinerStatus.Waiting
			minersToStart.add(miner)
		}
	}
	
	private val errorLog = File(folder + File.separator + "error_log.txt")
	
	fun addError(e: Exception, printError: Boolean = true) {
		if (printError) {
			e.printStackTrace()
		}
		var errors = if (errorLog.exists()) errorLog.readLines() else emptyList()
		val errorHeader = { it: String -> !it.startsWith("\t") }
		if (errors.count(errorHeader) >= 100) {
			errors = errors.drop(1)
			errors = errors.drop(errors.indexOfFirst(errorHeader))
		}
		@Suppress("SuspiciousCollectionReassignment")
		errors += SimpleDateFormat("yyyy/MM/dd HH:mm:ss: ").format(Date()) + e.stackTraceToString()
		errorLog.writeText(errors.joinToString("\n"))
	}
	
	private val coroutineScope = CoroutineScope(Job())
	
	init {
		(tryOrNull {
			val file = File(folder + File.separator + "settings.json")
			@Suppress("JSON_FORMAT_REDUNDANT")
			serializer.decodeFromString(file.readText())
		} ?: SettingsData.generateFromSettings()).let { settingsData ->
			phoenixPath = settingsData.phoenixPath
			miners.clear()
			miners.addAll(settingsData.miners.map { it.toMiner() })
			width = settingsData.width
			height = settingsData.height
			placement = settingsData.placement
			positionX = settingsData.positionX
			positionY = settingsData.positionY
		}
		
		println("Miners count: ${miners.size}")
		
		coroutineScope.launch {
			try {
				while (true) {
					delay(100L)
					while (gpus.isEmpty()) {
						try {
							if (phoenixPathIsCorrect(phoenixPath)) {
								gpus = getGpus()
							}
						} catch (e: Exception) {
							addError(e)
						}
						delay(100L)
					}
					while (activeMiners.any { it.pid == null }) {
						val minerWithoutPid = activeMiners.first { it.pid == null }
						val unassignedPid = getPIDsFor("PhoenixMiner.exe").firstOrNull { pid -> activeMiners.none { it.pid == pid } }
						if (unassignedPid != null) {
							minerWithoutPid.pid = unassignedPid
						}
						delay(100L)
					}
					if (!nokill) {
						for (pid in getPIDsFor("PhoenixMiner.exe").filter { pid -> activeMiners.none { it.pid == pid } }) {
							taskKill(pid, true)
						}
					}
					minersToStart.firstOrNull()?.let { miner ->
						if (miner.status == MinerStatus.Waiting) {
							if (
								when {
									miner.assignedGpuIds.isNotEmpty() -> miner.assignedGpuIds.none { id -> gpus.getOrNull(id.value)?.inUse != false }
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
			} catch (e: Exception) {
				if (e is CancellationException)
				{
					println("Settings killed")
				}
				else
				{
					addError(e)
				}
			}
			
		}
	}
	
	fun save() {
		miners.sortBy { it.id.value }
		File(folder).mkdirs()
		val file = File(folder + File.separator + "settings.json")
		file.createNewFile()
		file.writeText(serializer.encodeToString(SettingsData.generateFromSettings()))
	}
}
