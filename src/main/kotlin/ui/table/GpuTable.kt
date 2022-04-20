package ui.table

import SIZE_PER_ELEMENT
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import settings
import ui.ConstrainedRow
import ui.material.*

private val namesToRemove = listOf("NVIDIA", "GeForce", "AMD", "Radeon")

@Suppress("FunctionName", "EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GpuTable(
	modifier: Modifier = Modifier,
) {
	MaterialColumn(modifier.roundedBorder()) {
		LazyColumn {
			stickyHeader {
				MaterialRow(isHeader = true) {
					ConstrainedRow(
						Modifier.weight(1f),
						SIZE_PER_ELEMENT.dp,
						{ TableCell("ID", isHeader = true) },
						{ TableCell("Name", isHeader = true) },
						{ TableCell("in Use", isHeader = true) },
						{ TableCell("Performance", isHeader = true, textAlign = TextAlign.Right) },
						{ TableCell("Temperature", isHeader = true, textAlign = TextAlign.Right) },
						{ TableCell("Time", isHeader = true, textAlign = TextAlign.Right) },
						{ TableCell("Power", isHeader = true, textAlign = TextAlign.Right) },
						{ TableCell("Efficiency", isHeader = true, textAlign = TextAlign.Right) }
					)
				}
			}
			
			if (settings.gpus.isEmpty()) {
				item {
					LinearProgressIndicator(Modifier.fillMaxWidth())
				}
			}
			
			items(settings.gpus.size)
			{
				MaterialRow(Modifier.fillMaxWidth()) {
					val gpu by derivedStateOf { settings.gpus[it] }
					val nameWithoutKeywords by derivedStateOf { gpu.name.substring(namesToRemove.maxOf { gpu.name.indexOf(it, ignoreCase = true) + it.length }) }
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