package ui.table

import data.Settings
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import ui.ConstrainedRow
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
					TableCell("ID", ESSENTIAL_DATA_COLUMN_WEIGHT, FontWeight.Bold)
					TableCell("in Use", ESSENTIAL_DATA_COLUMN_WEIGHT, FontWeight.Bold)
					ConstrainedRow(
						Modifier.weight(1f - NAME_COLUMN_WEIGHT - (ESSENTIAL_DATA_COLUMN_WEIGHT * 2)),
						SIZE_PER_ELEMENT.dp,
						{ weight -> TableCell("Performance", weight, FontWeight.Bold, textAlign = TextAlign.Right) },
						{ weight -> TableCell("Temperature", weight, FontWeight.Bold, textAlign = TextAlign.Right) },
						{ weight -> TableCell("Power", weight, FontWeight.Bold, textAlign = TextAlign.Right) },
						{ weight -> TableCell("Efficiency", weight, FontWeight.Bold, textAlign = TextAlign.Right) }
					)
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
					TableCell(gpu.id, ESSENTIAL_DATA_COLUMN_WEIGHT)
					TableCell(gpu.inUse.toString(), ESSENTIAL_DATA_COLUMN_WEIGHT)
					ConstrainedRow(
						Modifier.weight(1f - NAME_COLUMN_WEIGHT - (ESSENTIAL_DATA_COLUMN_WEIGHT * 2)),
						SIZE_PER_ELEMENT.dp,
						{ weight -> TableCell(gpu.percentage?.let { "$it%" }, weight, textAlign = TextAlign.Right) },
						{ weight -> TableCell(gpu.temperature?.let { "$it°C" }, weight, textAlign = TextAlign.Right) },
						{ weight -> TableCell(gpu.powerDraw?.let { "$it W" }, weight, textAlign = TextAlign.Right) },
						{ weight -> TableCell(gpu.powerEfficiency?.let { "$it kH/J" }, weight, textAlign = TextAlign.Right) }
					)
				}
			}
		}
	}


}