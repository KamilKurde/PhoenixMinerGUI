package data

import Gpu
import androidx.compose.runtime.*
import androidx.compose.ui.window.WindowPlacement
import data.serializers.SettingsSerializer
import functions.*
import getGpus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import miner.*
import phoenix.phoenixPathIsCorrect
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

val folder = System.getenv("LOCALAPPDATA") + File.separator + "PhoenixMinerGUI"

@Serializable(with = SettingsSerializer::class)
class Settings(
	phoenixPath: String = "",
	miners: Array<Miner> = getDefaultMiners(),
	var width: Int = 720,
	var height: Int = 1280,
	var placement: WindowPlacement = WindowPlacement.Maximized,
	var positionX: Int = 0,
	var positionY: Int = 0,
) {
	
	var phoenixPath by mutableStateOf(phoenixPath)
	var gpus by mutableStateOf(emptyArray<Gpu>())
	val miners = mutableStateListOf(*miners)
	
	val activeMiners get() = miners.filter { it.isActive }
	
	var nokill = false
	
	private val minersToStart = mutableListOf<Miner>()
	
	companion object {
		
		private val serializer = Json {
			ignoreUnknownKeys = true
			encodeDefaults = true
		}
		
		fun load() = tryOrNull {
			val file = File(folder + File.separator + "settings.json")
			@Suppress("JSON_FORMAT_REDUNDANT")
			serializer.decodeFromString<Settings>(file.readText())
		}
	}
	
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
	
	private suspend fun obtainGpus() {
		try {
			if (phoenixPathIsCorrect(phoenixPath)) {
				gpus = getGpus()
			}
		} catch (e: Exception) {
			addError(e)
		}
	}
	
	private suspend fun obtainPids() {
		val minerWithoutPid = activeMiners.first { it.pid == null }
		val unassignedPid = getPIDsFor("PhoenixMiner.exe").firstOrNull { pid -> activeMiners.none { it.pid == pid } }
		if (unassignedPid != null) {
			minerWithoutPid.pid = unassignedPid
		}
	}
	
	private suspend fun killUnknownMiners() {
		for (pid in getPIDsFor("PhoenixMiner.exe").filter { pid -> activeMiners.none { it.pid == pid } }) {
			taskKill(pid, true)
		}
	}
	
	private fun startMinerImpl(miner: Miner) {
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
	
	private suspend fun singleExecutionLoop() {
		delay(100L)
		while (gpus.isEmpty()) {
			obtainGpus()
			delay(100L)
		}
		while (activeMiners.any { it.pid == null }) {
			obtainPids()
			delay(100L)
		}
		if (!nokill) {
			killUnknownMiners()
		}
		minersToStart.firstOrNull()?.let { miner ->
			if (miner.status == MinerStatus.Waiting) {
				startMinerImpl(miner)
			}
			minersToStart.removeFirst()
		}
	}
	
	private val loopMutex = Mutex()
	
	suspend fun executionLoop() {
		loopMutex.withLock {
			try {
				while (true) {
					singleExecutionLoop()
				}
			} catch (e: Exception) {
				if (e is CancellationException) {
					println("settings killed")
				} else {
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
		file.writeText(serializer.encodeToString(this))
	}
}
