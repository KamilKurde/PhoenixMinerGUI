package ui.table

import SIZE_PER_ELEMENT
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import data.Settings
import ui.ConstrainedRow
import ui.material.MaterialColumn
import ui.material.MaterialRow

@Suppress("FunctionName", "EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GpuTable(
	modifier: Modifier = Modifier
) {
	MaterialColumn(modifier) {
		LazyColumn {
			stickyHeader {
				MaterialRow(isHeader = true) {
					ConstrainedRow(
						Modifier.weight(1f),
						SIZE_PER_ELEMENT.dp,
						{ TableCell("ID", fontWeight = FontWeight.Bold) },
						{ TableCell("Name", fontWeight = FontWeight.Bold) },
						{ TableCell("in Use", fontWeight = FontWeight.Bold) },
						{ TableCell("Performance", fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) },
						{ TableCell("Temperature", fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) },
						{ TableCell("Time", fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) },
						{ TableCell("Power", fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) },
						{ TableCell("Efficiency", fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) }
					)
				}
			}

			if (Settings.gpus.isEmpty()) {
				item {
					LinearProgressIndicator(Modifier.fillMaxWidth())
				}
			}

			items(Settings.gpus.size)
			{
				MaterialRow(Modifier.fillMaxWidth()) {
					val gpu = Settings.gpus[it]
					val namesToRemove = listOf("NVIDIA", "GeForce", "AMD", "Radeon")
					val nameWithoutKeywords = gpu.name.substring(namesToRemove.maxOf { gpu.name.indexOf(it, ignoreCase = true) + it.length })
					ConstrainedRow(
						Modifier.weight(1f),
						SIZE_PER_ELEMENT.dp,
						{ TableCell(gpu.id) },
						{ TableCell(nameWithoutKeywords, tooltip = gpu.name) },
						{ TableCell(gpu.inUse.toString()) },
						{ TableCell(gpu.percentage?.let { "$it%" }, textAlign = TextAlign.Right) },
						{ TableCell(gpu.temperature?.let { "$it°C" }, textAlign = TextAlign.Right) },
						{ TableCell(gpu.time, textAlign = TextAlign.Right) },
						{ TableCell(gpu.powerDraw?.let { "$it W" }, textAlign = TextAlign.Right) },
						{ TableCell(gpu.powerEfficiency?.let { "$it kH/J" }, textAlign = TextAlign.Right) }
					)
				}
			}
		}
	}


}