package data.miner

import androidx.compose.runtime.*
import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import config.Arguments
import config.Option
import config.arguments.GpusArgument
import config.arguments.StringArgument
import data.*
import data.serializers.MinerSerializer
import functions.taskKill
import functions.tryWithoutCatch
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import settings
import java.io.File

@Serializable(MinerSerializer::class)
@Suppress("BlockingMethodInNonBlockingContext")
class Miner(name: String = "", id: Id = Id(1), startMiningOnStartup: Boolean, arguments: Arguments) {
	
	constructor(name: String = "", id: Id = Id(1), startMiningOnStartup: Boolean, vararg options: Option) : this(name, id, startMiningOnStartup, Arguments(*options))
	
	companion object {
		
		fun killAllMiners() {
			// Kills all other PhoenixMiner instances
			runBlocking {
				taskKill("PhoenixMiner.exe", true)
			}
		}
		
		suspend fun stopAllMiners(killOtherMiners: Boolean = true) {
			settings.miners.forEach { it.stopMining() }
			if (killOtherMiners) {
				killAllMiners()
			}
		}
	}
	
	var name by mutableStateOf(name)
	
	var id by mutableStateOf(id)
	
	var mineOnStartup by mutableStateOf(startMiningOnStartup)
	
	var arguments by mutableStateOf(arguments)
	
	var status by mutableStateOf(MinerStatus.Offline)
	
	val isActive by derivedStateOf { status != MinerStatus.Offline && status != MinerStatus.Waiting }
	
	var hashrate by mutableStateOf<Float?>(null)
	
	var shares by mutableStateOf<Shares?>(null)
	
	var time by mutableStateOf<Time?>(null)
	
	var powerDraw by mutableStateOf<Float?>(null)
	
	var powerEfficiency by mutableStateOf<Int?>(null)
	
	var pid by mutableStateOf<Int?>(null)
	
	private var processingJob = Job()
	
	private val gpusFromOption get() = arguments.copy().firstOrNull { it is Option.Gpus && it.commandlineArgument == GpusArgument.Gpus && it.value.isNotEmpty() } as Option.Gpus?
	
	val assignedGpuIds get() = gpusFromOption?.value ?: settings.gpus.map { it.id }.toTypedArray()
	
	private val file = File(tmp + File.separator + "miner$id.bat")
	
	fun log(message: String) = println("Miner $id: $message")
	
	private fun resetTemporalData() {
		pid = null
		hashrate = null
		powerDraw = null
		powerEfficiency = null
	}
	
	private fun updateGpusPower(line: String) {
		val split = line.split(" ")
		powerDraw = split[2].toFloat()
		tryWithoutCatch {
			powerEfficiency = split[4].toInt()
		}
	}
	
	private fun updateEthSpeed(line: String) {
		line.removePrefix("Eth speed: ").split(", ").forEach {
			val (first, second) = it.split(" ")
			when {
				second == "MH/s" -> {
					hashrate = first.toFloat()
				}
				
				first == "shares:" -> {
					val sharesList = second.removeSuffix(",").split("/").map { it.toInt() }.toTypedArray()
					shares?.apply {
						valid = sharesList[0]
						stale = sharesList[1]
						rejected = sharesList[2]
					}
					if (shares == null) {
						shares = Shares(sharesList)
					}
				}
				
				first == "time:" -> {
					time?.totalTime = second
					if (time == null) {
						time = Time(second)
					}
					assignedGpuIds.map { id -> settings.gpus[id.value] }.forEach { gpu ->
						gpu.time = time
					}
				}
			}
		}
	}
	
	private fun updatePowerRelatedStats(input: List<String>) {
		var splitLineMultiGpu = input
		while (splitLineMultiGpu.isNotEmpty()) {
			val currentGpu = settings.gpus.first { it.id == assignedGpuIds[splitLineMultiGpu[0].removePrefix("GPU").removeSuffix(":").toInt()] }
			
			currentGpu.temperature = splitLineMultiGpu[1].removeSuffix("C").toInt()
			currentGpu.percentage = splitLineMultiGpu[2].removeSuffix("%").toInt()
			currentGpu.powerDraw = splitLineMultiGpu[3].removeSuffix(",").removeSuffix("W").toInt()
			
			splitLineMultiGpu = splitLineMultiGpu.drop(4)
		}
	}
	
	private fun updateGpuStats(line: String) {
		for (internalId in assignedGpuIds.indices) {
			if (line.startsWith("GPU$internalId") && !(line.startsWith("GPU$internalId: Using") || line.startsWith("GPU$internalId: DAG"))) {
				val gpuSettingsIndex = settings.gpus.indexOfFirst { it.id == assignedGpuIds[internalId] }
				val gpu = settings.gpus[gpuSettingsIndex]
				val splitLine = line.split(" ")
				if (line.startsWith("GPU$internalId: cclock")) {
					gpu.powerEfficiency = splitLine[splitLine.size - 2].toInt()
				} else {
					updatePowerRelatedStats(splitLine)
				}
			}
			
		}
	}
	
	private fun updateGpusThrottling(line: String) {
		val split = line.split(" ")
		for (internalId in 1..assignedGpuIds.size + 1) {
			split.forEachIndexed { index, string ->
				if (string == "GPU$internalId") {
					val gpusettingsIndex = settings.gpus.indexOfFirst { it.id == assignedGpuIds[internalId - 1] }
					val gpu = settings.gpus[gpusettingsIndex]
					gpu.percentage = split[index + 1].removeSuffix(",").removePrefix("(").removeSuffix("%)").toInt()
				}
			}
		}
	}
	
	private fun process(line: String) {
		try {
			if (line.isNotBlank()) {
				log(line)
			}
			when {
				line.contains("Connecting") -> status = MinerStatus.Connecting
				line.contains("Generating DAG") -> status = MinerStatus.DagBuilding
				line.contains("DAG generated") -> status = MinerStatus.Running
				line.contains("Eth: Mining") -> status = MinerStatus.Running
				line.startsWith("GPUs power: ") && status == MinerStatus.Running -> updateGpusPower(line)
				line.startsWith("Eth: Could not connect to") || line.startsWith("Eth: Can't resolve host") -> status = MinerStatus.ConnectionError
				line.startsWith("Eth speed: ") && status == MinerStatus.Running -> updateEthSpeed(line)
				line.startsWith("GPU") && status == MinerStatus.Running -> updateGpuStats(line)
				// Workaround for phoenix not reporting throttled usage in normal stats
				line.startsWith("Throttling GPUs") -> updateGpusThrottling(line)
				
				line.startsWith("miner stopped") -> {
					resetTemporalData()
					status = MinerStatus.ProgramError
					settings.startMiner(this@Miner)
				}
			}
		} catch (e: Exception) {
			log(e.stackTraceToString())
			settings.addError(e, false)
		}
	}
	
	@Suppress("EXPERIMENTAL_IS_NOT_ENABLED", "OPT_IN_IS_NOT_ENABLED")
	@OptIn(ExperimentalCoroutinesApi::class)
	fun startMining() {
		processingJob = Job()
		status = MinerStatus.Launching
		CoroutineScope(processingJob + Dispatchers.IO).launch {
			val formattedsettings = arguments.copy()
			if (!formattedsettings.any { it is Option.String && it.commandlineArgument == StringArgument.Password }) {
				formattedsettings.add(Option.String(StringArgument.Password, "x"))
			}
			val settingsAsArray = (formattedsettings.map { it.fullArgument } + "-hstats 2" + "-rmode 0" + "-cdm 0" + "-fret 1000" + "-gbase 0").toTypedArray()
			val settingsAsString = settingsAsArray.joinToString(separator = " ")
			file.createNewFile()
			file.writeText(
				"@echo off\n" +
						"\"${settings.phoenixPath}\" $settingsAsString\n" +
						"echo miner stopped"
			)
			try {
				process(
					file.absolutePath,
					stdout = Redirect.CAPTURE,
					// setting environmental variables like instructed on PhoenixMiner.org
					env = mapOf(
						"GPU_FORCE_64BIT_PTR" to "0",
						"GPU_MAX_HEAP_SIZE" to "100",
						"GPU_USE_SYNC_OBJECTS" to "1",
						"GPU_MAX_ALLOC_PERCENT" to "100",
						"GPU_SINGLE_ALLOC_PERCENT" to "100"
					),
					consumer = ::process
				)
			} catch (e: CancellationException) {
				log("Killed by cancellation")
			}
		}
	}
	
	suspend fun stopMining() {
		if (status == MinerStatus.Offline) {
			return
		}
		status = MinerStatus.Closing
		pid?.let { taskKill(it, true) }
		processingJob.cancelAndJoin()
		resetTemporalData()
		shares = null
		time = null
		status = MinerStatus.Offline
	}
}