package miner

import data.Id
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import config.Config
import config.Parameters
import config.arguments.GpusArgument
import config.arguments.StringArgument
import data.Settings
import data.folder
import functions.getPIDsFor
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import functions.taskKill
import functions.tryWithoutCatch
import java.io.File

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
@Serializable
class MinerData(val name: String = "", val id: Id = Id(1), val mineOnStartup: Boolean = false, val settings: Array<String> = emptyArray())
{
	fun toMiner() = Miner(name, id, mineOnStartup, Parameters(*settings))
}

@ExperimentalSerializationApi
@Suppress("BlockingMethodInNonBlockingContext")
@ExperimentalCoroutinesApi
class Miner(name: String = "", id: Id = Id(1), startMiningOnStartup: Boolean, parameters: Parameters)
{
	constructor(name: String = "", id: Id = Id(1), startMiningOnStartup: Boolean, vararg parameters: Config): this(name, id, startMiningOnStartup, Parameters(*parameters))

	@ExperimentalSerializationApi
	companion object
	{
		fun killAllMiners()
		{
			// Kills all other PhoenixMiner instances
			runBlocking {
				taskKill("PhoenixMiner.exe", true)
			}
		}

		fun stopAllMiners()
		{
			runBlocking {
				Settings.miners.forEach { it.stopMining() }
			}
			killAllMiners()
		}
	}

	var name by mutableStateOf(name)

	var id by mutableStateOf(id)

	var mineOnStartup by mutableStateOf(startMiningOnStartup)

	var parameters by mutableStateOf(parameters)

	var status by mutableStateOf(MinerStatus.Offline)

	var hashrate by mutableStateOf<Float?>(null)

	var shares by mutableStateOf<Shares?>(null)

	var powerDraw by mutableStateOf<Float?>(null)

	var powerEfficiency by mutableStateOf<Int?>(null)

	private var pid: Int? = null

	private var coroutineJob = Job()

	private val gpusFromConfig get() = parameters.copy().firstOrNull { it is Config.GpusParameter && it.configElement == GpusArgument.Gpus } as Config.GpusParameter?

	var assignedGpuIds = gpusFromConfig?.value ?: Settings.gpus.map { it.id }.toTypedArray()

	fun toMinerData() = MinerData(name, id, mineOnStartup, parameters.toStringArray())

	private val file = File(folder + File.separator + "miner$id.bat")

	fun startMining()
	{
		coroutineJob = Job()
		val coroutineScope = CoroutineScope(coroutineJob + Dispatchers.IO)

		coroutineScope.launch {
			status = MinerStatus.Connecting
			val formattedSettings = parameters.copy()
			if (!formattedSettings.any { it is Config.StringParameter && it.configElement == StringArgument.Password })
			{
				formattedSettings.add(Config.StringParameter(StringArgument.Password, "x"))
			}
			val settingsAsArray = (formattedSettings.map { it.fullParameter } + "-hstats 2" + "-rmode 0" + "-cdm 0").toTypedArray()
			val settingsAsString = settingsAsArray.joinToString(separator = " ")
			file.createNewFile()
			file.writeText(
				"@echo off\n" +
				"\"${Settings.phoenixPath}\" $settingsAsString\n" +
				"echo miner stopped"
			)
			assignedGpuIds.forEach {
				Settings.gpus[it.toInt() - 1].inUse = true
			}
			var working = false
			while (isActive)
			{
				if (!working)
				{
					coroutineScope.launch {
						delay(500L)
						getPIDsFor("PhoenixMiner.exe").forEach { pid ->
							if (Settings.miners.none { it.pid == pid })
							{
								this@Miner.pid = pid
							}
						}
					}
					process(file.absolutePath,
							stdout = Redirect.CAPTURE,
						// setting environmental variables like instructed on PhoenixMiner.org
							env = mapOf(
								"GPU_FORCE_64BIT_PTR" to "0",
								"GPU_MAX_HEAP_SIZE" to "100",
								"GPU_USE_SYNC_OBJECTS" to "1",
								"GPU_MAX_ALLOC_PERCENT" to "100",
								"GPU_SINGLE_ALLOC_PERCENT" to "100"
							),
							consumer = { line ->
								if (line.isNotBlank())
								{
									println("Miner $id: $line")
								}
								when
								{
									line.startsWith("Phoenix Miner")                                 ->
									{
										// Resetting stats, doesn't matter on normal startup, shows empty values on miner reset
										working = true
										hashrate = null
										powerDraw = null
										powerEfficiency = null
										assignedGpuIds.forEach { id ->
											val gpu = Settings.gpus.first { it.id == id }
											gpu.resetGpuStats()
											gpu.inUse = true
										}
									}
									line.contains("Generating DAG")                                  -> status = MinerStatus.DagBuilding
									line.contains("DAG generated")                                   -> status = MinerStatus.Running
									line.startsWith("GPUs power: ") && status == MinerStatus.Running ->
									{
										val split = line.split(" ")
										powerDraw = split[2].toFloat()
										tryWithoutCatch {
											powerEfficiency = split[4].toInt()
										}
									}
									line.startsWith("Eth speed: ") && status == MinerStatus.Running  ->
									{
										var currentData = line.removePrefix("Eth speed: ")
										while (currentData.isNotEmpty())
										{
											val split = currentData.split(" ")
											when
											{
												split[1] == "MH/s,"   -> hashrate = split[0].toFloat()
												split[0] == "shares:" ->
												{
													val sharesList = split[1].removeSuffix(",").split("/").map { it.toInt() }.toTypedArray()
													shares?.apply {
														valid = sharesList[0]
														stale = sharesList[1]
														rejected = sharesList[2]
													}
													if (shares == null)
													{
														shares = Shares(sharesList)
													}
												}
											}
											currentData = split.drop(2).joinToString(separator = " ")
										}
									}
									line.startsWith("GPU") && status == MinerStatus.Running          ->
									{
										for (internalId in 1..assignedGpuIds.size + 1)
										{
											if (line.startsWith("GPU$internalId") && !(line.startsWith("GPU$internalId: Using") || line.startsWith("GPU$internalId: DAG")))
											{
												val gpuSettingsIndex = Settings.gpus.indexOfFirst { it.id == assignedGpuIds[internalId - 1] }
												val gpu = Settings.gpus[gpuSettingsIndex]
												val splitLine = line.split(" ")
												if (line.startsWith("GPU$internalId: cclock"))
												{
													gpu.powerEfficiency = splitLine[splitLine.size - 2].toInt()
												}
												else
												{
													var splitLineMultiGpu = splitLine
													while (splitLineMultiGpu.isNotEmpty())
													{
														val currentGpu = Settings.gpus.first { it.id == assignedGpuIds[splitLineMultiGpu[0].removePrefix("GPU").removeSuffix(":").toInt() - 1] }

														currentGpu.temperature = splitLineMultiGpu[1].removeSuffix("C").toInt()
														currentGpu.percentage = splitLineMultiGpu[2].removeSuffix("%").toInt()
														currentGpu.powerDraw = splitLineMultiGpu[3].removeSuffix(",").removeSuffix("W").toInt()

														splitLineMultiGpu = splitLineMultiGpu.drop(4)
													}
												}
											}

										}
									}
									// Workaround for phoenix not reporting throttled usage in normal stats
									line.startsWith("Throttling GPUs")                               ->
									{
										val split = line.split(" ")
										for (internalId in 1..assignedGpuIds.size + 1)
										{
											split.forEachIndexed { index, string ->
												if (string == "GPU$internalId")
												{
													val gpuSettingsIndex = Settings.gpus.indexOfFirst { it.id == assignedGpuIds[internalId - 1] }
													val gpu = Settings.gpus[gpuSettingsIndex]
													gpu.percentage = split[index + 1].removeSuffix(",").removePrefix("(").removeSuffix("%)").toInt()
												}
											}
										}
									}
									line.startsWith("miner stopped")                                 ->
									{
										working = false
										status = MinerStatus.Error
										pid = null
									}
								}
							})
				}
				delay(1000L)
			}

		}
	}

	suspend fun stopMining()
	{
		status = MinerStatus.Closing
		pid?.let { taskKill(it, true) }
		coroutineJob.cancelAndJoin()
		assignedGpuIds.forEach { id ->
			Settings.gpus[id.value - 1].resetGpuStats()
		}
		hashrate = null
		shares = null
		powerDraw = null
		powerEfficiency = null
		status = MinerStatus.Offline
	}
}