@file:Suppress("BlockingMethodInNonBlockingContext")

import data.Id
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import data.Settings
import data.folder
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import miner.MinerStatus
import java.io.File

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
@Serializable
data class Gpu(val name: String, var id: Id = Id(1))
{
	var inUse by mutableStateOf(Settings.miners.any { miner -> (miner.status != MinerStatus.Offline && miner.status != MinerStatus.Waiting ) && miner.assignedGpuIds.any { it == id } })

	var percentage by mutableStateOf<Int?>(null)

	var temperature by mutableStateOf<Int?>(null)

	var powerDraw by mutableStateOf<Int?>(null)

	var powerEfficiency by mutableStateOf<Int?>(null)

	fun resetGpuStats()
	{
		powerEfficiency = null
		powerDraw = null
		percentage = null
		temperature = null
		inUse = false
	}
}

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
suspend fun getGpus() = coroutineScope {
	val file = File(folder + File.separator + "deviceDiscovery.bat")
	file.createNewFile()
	file.writeText(
		"echo off\n" +
		"\"${Settings.phoenixPath}\" -list"
	)
	val gpuList = mutableListOf<Gpu>()
	process(file.absolutePath, stdout = Redirect.CAPTURE).output.forEach { line ->
		if (line.startsWith("GPU"))
		{
			val splitLine = line.split(" ")
			val index = line.indexOf("(pcie")
			gpuList.add(
				Gpu(
					line.substring(0 until index).removePrefix(splitLine[0]).trim(),
					Id(splitLine[0].removePrefix("GPU").removeSuffix(":").toInt())
				)
			)

		}
	}
	gpuList.toTypedArray()
}