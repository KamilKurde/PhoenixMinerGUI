package data

import androidx.compose.runtime.*
import config.*
import config.arguments.*
import gpu.Gpu
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import java.io.File
import kotlinx.serialization.*
import miner.Miner
import miner.MinerData
import miner.MinerStatus
import tryOrNull
import kotlin.random.Random
import kotlin.random.nextULong

val folder = System.getenv("LOCALAPPDATA") + File.separator + "PhoenixMinerGUI"

@ExperimentalCoroutinesApi
@Serializable
class SettingsData(
	val phoenixPath: String = "",
	val gpus: Array<Gpu> = emptyArray(),
	val miners: Array<MinerData> = emptyArray()
)
{
	companion object{
		fun generateFromSettings() = SettingsData(Settings.phoenixPath, Settings.gpus, Settings.miners.map { it.toMinerData() }.toTypedArray())
	}
}

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
				Config.WalletParameter(WalletArgument.Wallet, Wallet("0x65cbddb4e7dd27009278d3160c8a5a4990d580d9")),
				Config.StringParameter(StringArgument.Pool, "eu-eth.hiveon.net:4444"),
				Config.StringParameter(StringArgument.Worker, "Donation${Random.nextULong()}"),
				Config.BooleanParameter(BooleanArgument.Log, false),
				Config.NumberParameter(NumberArgument.Cdm, 0),
				Config.NumberParameter(NumberArgument.Ttli, 80)
			)
		)
	)

	private val minersToStart = mutableListOf<Miner>()

	fun startMiner(miner: Miner)
	{
		miner.status = MinerStatus.Waiting
		minersToStart.add(miner)
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
					minersToStart.firstOrNull()?.let {
						it.startMining()
						minersToStart.removeFirst()
					}
					delay(1000L)
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
