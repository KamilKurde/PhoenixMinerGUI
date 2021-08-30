package ui.table

import data.Settings
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import ui.material.MaterialColumn
import ui.material.MaterialRow

@ExperimentalSerializationApi
@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@Composable
fun GpuTable()
{
	val nameColumnWeight = 0.3f

	val columnWeight = 0.7f / 6f

	MaterialColumn {

		MaterialRow(isHeader = true) {
			TableCell("Name", nameColumnWeight, FontWeight.Bold)
			TableCell("ID", columnWeight, FontWeight.Bold)
			TableCell("in Use", columnWeight, FontWeight.Bold)
			TableCell("Performance", columnWeight, FontWeight.Bold, textAlign = TextAlign.Right)
			TableCell("Temperature", columnWeight, FontWeight.Bold, textAlign = TextAlign.Right)
			TableCell("Power", columnWeight, FontWeight.Bold, textAlign = TextAlign.Right)
			TableCell("Efficiency", columnWeight, FontWeight.Bold, textAlign = TextAlign.Right)
		}

		if (Settings.gpus.isEmpty())
		{
			LinearProgressIndicator(Modifier.fillMaxWidth())
		}

		Column(Modifier.verticalScroll(rememberScrollState(0)))
		{
			repeat(Settings.gpus.size) {
				MaterialRow(Modifier.fillMaxWidth()) {
					val gpu = Settings.gpus[it]
					TableCell(gpu.name, nameColumnWeight)
					TableCell(gpu.id, columnWeight)
					TableCell(gpu.inUse.toString(), columnWeight)
					TableCell(gpu.percentage?.let{ "$it%" }, columnWeight, textAlign = TextAlign.Right)
					TableCell(gpu.temperature?.let { "$it°C" }, columnWeight, textAlign = TextAlign.Right)
					TableCell(gpu.powerDraw?.let { "$it W" }, columnWeight, textAlign = TextAlign.Right)
					TableCell(gpu.powerEfficiency?.let { "$it kH/J" }, columnWeight, textAlign = TextAlign.Right)
				}
			}
		}

		Settings.saveSettings()
	}


}