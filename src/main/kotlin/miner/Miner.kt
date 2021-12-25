package miner

import androidx.compose.runtime.*
import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import config.Config
import config.Parameters
import config.arguments.GpusArgument
import config.arguments.StringArgument
import data.*
import functions.*
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.io.File

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
@Serializable
class MinerData(val name: String = "", val id: Id = Id(1), val mineOnStartup: Boolean = false, val settings: Array<String> = emptyArray()) {
	fun toMiner() = Miner(name, id, mineOnStartup, Parameters(*settings))
}

@ExperimentalSerializationApi
@Suppress("BlockingMethodInNonBlockingContext")
@ExperimentalCoroutinesApi
class Miner(name: String = "", id: Id = Id(1), startMiningOnStartup: Boolean, parameters: Parameters) {
	constructor(name: String = "", id: Id = Id(1), startMiningOnStartup: Boolean, vararg parameters: Config) : this(name, id, startMiningOnStartup, Parameters(*parameters))

	@ExperimentalSerializationApi
	companion object {
		fun killAllMiners() {
			// Kills all other PhoenixMiner instances
			runBlocking {
				taskKill("PhoenixMiner.exe", true)
			}
		}

		suspend fun stopAllMiners(killOtherMiners: Boolean = true) {
			Settings.miners.forEach { it.stopMining() }
			if (killOtherMiners) {
				killAllMiners()
			}
		}
	}

	var name by mutableStateOf(name)

	var id by mutableStateOf(id)

	var mineOnStartup by mutableStateOf(startMiningOnStartup)

	var parameters by mutableStateOf(parameters)

	var status by mutableStateOf(MinerStatus.Offline)

	val isActive get() = status != MinerStatus.Offline && status != MinerStatus.Waiting

	var hashrate by mutableStateOf<Float?>(null)

	var shares by mutableStateOf<Shares?>(null)

	var time by mutableStateOf<Time?>(null)

	var powerDraw by mutableStateOf<Float?>(null)

	var powerEfficiency by mutableStateOf<Int?>(null)

	var pid by mutableStateOf<Int?>(null)

	private var processingJob = Job()

	private var pidJob = Job()

	private val gpusFromConfig get() = parameters.copy().firstOrNull { it is Config.GpusParameter && it.configElement == GpusArgument.Gpus } as Config.GpusParameter?

	var assignedGpuIds = gpusFromConfig?.value ?: Settings.gpus.map { it.id }.toTypedArray()

	fun toMinerData() = MinerData(name, id, mineOnStartup, parameters.toStringArray())

	private val file = File(folder + File.separator + "miner$id.bat")

	private fun log(message: String) = println("Miner $id: $message")

	private fun resetTemporalData() {
		pid = null
		hashrate = null
		powerDraw = null
		powerEfficiency = null
	}

	private fun updateGpusPower(line: String)
	{
		val split = line.split(" ")
		powerDraw = split[2].toFloat()
		tryWithoutCatch {
			powerEfficiency = split[4].toInt()
		}
	}

	private fun updateEthSpeed(line: String)
	{
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
					assignedGpuIds.map { id -> Settings.gpus[id.value] }.forEach { gpu ->
						gpu.time = time
					}
				}
			}
		}
	}

	private fun updateGpuStats(line: String)
	{
		for (internalId in assignedGpuIds.indices) {
			if (line.startsWith("GPU$internalId") && !(line.startsWith("GPU$internalId: Using") || line.startsWith("GPU$internalId: DAG"))) {
				val gpuSettingsIndex = Settings.gpus.indexOfFirst { it.id == assignedGpuIds[internalId] }
				val gpu = Settings.gpus[gpuSettingsIndex]
				val splitLine = line.split(" ")
				if (line.startsWith("GPU$internalId: cclock")) {
					gpu.powerEfficiency = splitLine[splitLine.size - 2].toInt()
				} else {
					var splitLineMultiGpu = splitLine
					while (splitLineMultiGpu.isNotEmpty()) {
						val currentGpu = Settings.gpus.first { it.id == assignedGpuIds[splitLineMultiGpu[0].removePrefix("GPU").removeSuffix(":").toInt()] }

						currentGpu.temperature = splitLineMultiGpu[1].removeSuffix("C").toInt()
						currentGpu.percentage = splitLineMultiGpu[2].removeSuffix("%").toInt()
						currentGpu.powerDraw = splitLineMultiGpu[3].removeSuffix(",").removeSuffix("W").toInt()

						splitLineMultiGpu = splitLineMultiGpu.drop(4)
					}
				}
			}

		}
	}

	private fun updateGpusThrottling(line: String)
	{
		val split = line.split(" ")
		for (internalId in 1..assignedGpuIds.size + 1) {
			split.forEachIndexed { index, string ->
				if (string == "GPU$internalId") {
					val gpuSettingsIndex = Settings.gpus.indexOfFirst { it.id == assignedGpuIds[internalId - 1] }
					val gpu = Settings.gpus[gpuSettingsIndex]
					gpu.percentage = split[index + 1].removeSuffix(",").removePrefix("(").removeSuffix("%)").toInt()
				}
			}
		}
	}

	private fun process(line: String) {
		if (line.isNotBlank()) {
			log(line)
		}
		when {
			line.contains("Phoenix Miner") -> status = MinerStatus.Connecting
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
				Settings.startMiner(this@Miner)
			}
		}
	}

	fun startMining() {
		runBlocking {
			stopMining(false)
		}
		processingJob = Job()
		CoroutineScope(processingJob + Dispatchers.IO).launch {
			val formattedSettings = parameters.copy()
			if (!formattedSettings.any { it is Config.StringParameter && it.configElement == StringArgument.Password }) {
				formattedSettings.add(Config.StringParameter(StringArgument.Password, "x"))
			}
			val settingsAsArray = (formattedSettings.map { it.fullParameter } + "-hstats 2" + "-rmode 0" + "-cdm 0" + "-fret 1000" + "-gbase 0").toTypedArray()
			val settingsAsString = settingsAsArray.joinToString(separator = " ")
			file.createNewFile()
			file.writeText(
				"@echo off\n" +
						"\"${Settings.phoenixPath}\" $settingsAsString\n" +
						"echo miner stopped"
			)
			pidJob = Job()
			CoroutineScope(pidJob + Dispatchers.IO).launch {
				while (pid == null) {
					delay(100L)
					pid = getPIDsFor("PhoenixMiner.exe").firstOrNull {
						Settings.activeMiners.none { miner -> miner.pid == it }
					}
				}
			}
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
		}
	}

	suspend fun stopMining(setStatus: Boolean = true) {
		val previousStatus = status
		status = MinerStatus.Closing
		processingJob.cancelAndJoin()
		while (pid == null && (previousStatus != MinerStatus.Waiting && previousStatus != MinerStatus.Offline)) {
			delay(200L)
			log("PID for miner wasn't obtained, waiting")
		}
		pid?.let { taskKill(it, true) }
		pidJob.cancelAndJoin()
		resetTemporalData()
		shares = null
		time = null
		if (setStatus)
		{
			status = MinerStatus.Offline
		}
	}
}