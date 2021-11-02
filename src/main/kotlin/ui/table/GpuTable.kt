package ui.table

import CONTROLS_COLUMN_SIZE
import ID_COLUMN_SIZE
import SIZE_PER_ELEMENT
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
import functions.removeStrings
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
) {
    MaterialColumn(modifier) {
        LazyColumn {
            stickyHeader {
                MaterialRow(isHeader = true) {
                    Spacer(Modifier.width(CONTROLS_COLUMN_SIZE.dp))
                    TableCell("ID", null, modifier = Modifier.width(ID_COLUMN_SIZE.dp), fontWeight = FontWeight.Bold)
                    ConstrainedRow(
                        Modifier.weight(1f),
                        SIZE_PER_ELEMENT.dp,
                        { TableCell("Name", fontWeight = FontWeight.Bold) },
                        { TableCell("in Use", fontWeight = FontWeight.Bold) },
                        { TableCell("Performance", fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) },
                        { TableCell("Temperature", fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) },
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
                    Spacer(Modifier.width(CONTROLS_COLUMN_SIZE.dp))
                    TableCell(gpu.id, null, modifier = Modifier.width(ID_COLUMN_SIZE.dp))
                    ConstrainedRow(
                        Modifier.weight(1f),
                        SIZE_PER_ELEMENT.dp,
                        { TableCell(gpu.name.removeStrings("NVIDIA", "GeForce", "AMD", "Radeon"), tooltip = gpu.name) },
                        { TableCell(gpu.inUse.toString()) },
                        { TableCell(gpu.percentage?.let { "$it%" }, textAlign = TextAlign.Right) },
                        { TableCell(gpu.temperature?.let { "$it°C" }, textAlign = TextAlign.Right) },
                        { TableCell(gpu.powerDraw?.let { "$it W" }, textAlign = TextAlign.Right) },
                        { TableCell(gpu.powerEfficiency?.let { "$it kH/J" }, textAlign = TextAlign.Right) }
                    )
                }
            }
        }
    }


}