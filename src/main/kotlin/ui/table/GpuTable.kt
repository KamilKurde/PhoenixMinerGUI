package ui.table

import data.Settings
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@ExperimentalFoundationApi
@ExperimentalSerializationApi
@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@Composable
fun GpuTable(
	modifier: Modifier = Modifier
)
{
	MaterialColumn(modifier) {
		LazyColumn {
			stickyHeader {
				MaterialRow(isHeader = true) {
					TableCell("Name", NAME_COLUMN_WEIGHT, FontWeight.Bold)
					TableCell("ID", DATA_COLUMN_WEIGHT, FontWeight.Bold)
					TableCell("in Use", DATA_COLUMN_WEIGHT, FontWeight.Bold)
					TableCell("Performance", DATA_COLUMN_WEIGHT, FontWeight.Bold, textAlign = TextAlign.Right)
					TableCell("Temperature", DATA_COLUMN_WEIGHT, FontWeight.Bold, textAlign = TextAlign.Right)
					TableCell("Power", DATA_COLUMN_WEIGHT, FontWeight.Bold, textAlign = TextAlign.Right)
					TableCell("Efficiency", DATA_COLUMN_WEIGHT, FontWeight.Bold, textAlign = TextAlign.Right)
				}
			}

			if (Settings.gpus.isEmpty())
			{
				item {
					LinearProgressIndicator(Modifier.fillMaxWidth())
				}
			}

			items(Settings.gpus.size)
			{
				MaterialRow(Modifier.fillMaxWidth()) {
					val gpu = Settings.gpus[it]
					TableCell(gpu.name, NAME_COLUMN_WEIGHT)
					TableCell(gpu.id, DATA_COLUMN_WEIGHT)
					TableCell(gpu.inUse.toString(), DATA_COLUMN_WEIGHT)
					TableCell(gpu.percentage?.let { "$it%" }, DATA_COLUMN_WEIGHT, textAlign = TextAlign.Right)
					TableCell(gpu.temperature?.let { "$it°C" }, DATA_COLUMN_WEIGHT, textAlign = TextAlign.Right)
					TableCell(gpu.powerDraw?.let { "$it W" }, DATA_COLUMN_WEIGHT, textAlign = TextAlign.Right)
					TableCell(gpu.powerEfficiency?.let { "$it kH/J" }, DATA_COLUMN_WEIGHT, textAlign = TextAlign.Right)
				}
			}
		}
	}


}