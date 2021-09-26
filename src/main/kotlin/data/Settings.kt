package data

import Gpu
import androidx.compose.runtime.*
import config.*
import config.arguments.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import java.io.File
import kotlinx.serialization.*
import miner.Miner
import miner.MinerData
import miner.MinerStatus
import functions.tryOrNull
import kotlin.random.Random
import kotlin.random.nextULong

val folder = System.getenv("LOCALAPPDATA") + File.separator + "PhoenixMinerGUI"

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
@Serializable
class SettingsData(
	val phoenixPath: String = "",
	val gpus: Array<Gpu> = emptyArray(),
	val miners: Array<MinerData> = emptyArray()
)
{
	@ExperimentalSerializationApi
	companion object
	{
		fun generateFromSettings() = SettingsData(Settings.phoenixPath, Settings.gpus, Settings.miners.map { it.toMinerData() }.toTypedArray())
	}
}

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
object Settings
{
	var phoenixPath by mutableStateOf("")
	var gpus by mutableStateOf(emptyArray<Gpu>())
	var minerToEdit by mutableStateOf<Miner?>(null)
	var miners by mutableStateOf(
		arrayOf(
			Miner(
				"Donate Your Hashpower To The Dev", Id(1),
				false,
				Config.WalletParameter(WalletArgument.Wallet, Wallet("0x65cbddb4e7dd27009278d3160c8a5a4990d580d9")),
				Config.StringParameter(StringArgument.Pool, "eu-eth.hiveon.net:4444"),
				Config.StringParameter(StringArgument.Worker, "Donation${Random.nextULong()}"),
				Config.BooleanParameter(BooleanArgument.Log, false),
				Config.NumberParameter(NumberArgument.Ttli, 80)
			)
		)
	)

	private val minersToStart = mutableListOf<Miner>()

	fun startMiner(miner: Miner)
	{
		if (miner !in minersToStart)
		{
			miner.status = MinerStatus.Waiting
			minersToStart.add(miner)
		}
	}

	private val coroutineScope = CoroutineScope(Job())

	init
	{
		(tryOrNull {
			val file = File(folder + File.separator + "settings.json")
			Json.decodeFromString(file.readText())
		} ?: SettingsData.generateFromSettings()).let { settingsData ->
			phoenixPath = settingsData.phoenixPath
			gpus = settingsData.gpus
			miners = settingsData.miners.map { it.toMiner() }.toTypedArray()
		}

		coroutineScope.launch {
			try
			{
				while (true)
				{
					// Waiting for all other components to properly initialize (including UI) and giving timeframe for getting PID of new PhoenixMiner instance
					delay(1000L)
					minersToStart.firstOrNull()?.let { miner ->
						if (miner.status == MinerStatus.Waiting)
						{
							if (
								when
								{
									miner.assignedGpuIds.isNotEmpty() -> miner.assignedGpuIds.none { id -> gpus[id.value - 1].inUse }
									else                              -> gpus.none { it.inUse }
								}
							)
							{
								miner.startMining()
							}
							else
							{
								miner.status = MinerStatus.Offline
							}

							minersToStart.removeFirst()
						}
					}
				}
			}
			catch (e: CancellationException)
			{
				println("settings killed")
			}

		}
	}

	fun saveSettings()
	{
		File(folder).mkdirs()
		val file = File(folder + File.separator + "settings.json")
		file.createNewFile()
		file.writeText(Json.encodeToString(SettingsData.generateFromSettings()))
	}
}
