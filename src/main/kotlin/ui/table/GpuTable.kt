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
                        { weight -> TableCell("Name", weight, fontWeight = FontWeight.Bold) },
                        { weight -> TableCell("in Use", weight, fontWeight = FontWeight.Bold) },
                        { weight -> TableCell("Performance", weight, fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) },
                        { weight -> TableCell("Temperature", weight, fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) },
                        { weight -> TableCell("Power", weight, fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) },
                        { weight -> TableCell("Efficiency", weight, fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) }
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
                        { weight -> TableCell(gpu.name.removeStrings("NVIDIA", "GeForce", "AMD", "Radeon"), weight, tooltip = gpu.name) },
                        { weight -> TableCell(gpu.inUse.toString(), weight) },
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