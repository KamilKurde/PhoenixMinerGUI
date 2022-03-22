@file:Suppress("BlockingMethodInNonBlockingContext")

package data

import androidx.compose.runtime.*
import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import settings
import java.io.File

data class Gpu(val name: String, var id: Id = Id(1)) {
	
	val inUse
		get(): Boolean {
			val isUsed = settings.activeMiners.any { miner -> miner.assignedGpuIds.any { it == id } }
			if (!isUsed) {
				percentage = null
				temperature = null
				time = null
				powerDraw = null
				powerEfficiency = null
			}
			return isUsed
		}
	
	var percentage by mutableStateOf<Int?>(null)
	
	var temperature by mutableStateOf<Int?>(null)
	
	var time by mutableStateOf<Time?>(null)
	
	var powerDraw by mutableStateOf<Int?>(null)
	
	var powerEfficiency by mutableStateOf<Int?>(null)
}

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun getGpus(): Array<Gpu> {
	val file = File(folder + File.separator + "deviceDiscovery.bat")
	file.createNewFile()
	val path = settings.phoenixPath
	file.writeText(
		"echo off\n" +
				"\"$path\" -list -gbase 0"
	)
	val gpuList = mutableListOf<Gpu>()
	process(file.absolutePath, stdout = Redirect.CAPTURE).output.forEach { line ->
		if (line.startsWith("GPU")) {
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
	return gpuList.toTypedArray()
}