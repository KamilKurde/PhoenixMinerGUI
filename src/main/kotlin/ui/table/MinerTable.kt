package ui.table

import CONTROLS_COLUMN_SIZE
import NAME_COLUMN_SIZE
import SIZE_PER_ELEMENT
import activity.Minersettings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.KamilKurde.*
import settings
import ui.ConstrainedRow
import ui.MinerControls
import ui.material.*

@Suppress("FunctionName", "EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MinerTable(
	activity: Activity,
	modifier: Modifier = Modifier,
) {
	MaterialColumn(modifier.roundedBorder()) {
		LazyColumn {
			stickyHeader {
				MaterialRow(isHeader = true) {
					ConstrainedRow(
						Modifier.weight(1f),
						SIZE_PER_ELEMENT.dp,
						{ TableCell(text = "ID", isHeader = true) },
						{ TableCell(text = "Name", modifier = Modifier.defaultMinSize(minWidth = NAME_COLUMN_SIZE.dp), isHeader = true) },
						{ TableCell(text = "Status", isHeader = true) },
						{ TableCell(text = "Hashrate", isHeader = true, textAlign = TextAlign.Right) },
						{ TableCell(text = "Shares", isHeader = true, textAlign = TextAlign.Right) },
						{ TableCell(text = "Time", isHeader = true, textAlign = TextAlign.Right) },
						{ TableCell(text = "Power", isHeader = true, textAlign = TextAlign.Right) },
						{ TableCell(text = "Efficiency", isHeader = true, textAlign = TextAlign.Right) }
					)
				}
			}
			items(settings.miners.size)
			{
				val miner = settings.miners[it]
				MaterialRow(Modifier.fillMaxWidth()) {
					ConstrainedRow(
						Modifier.weight(1f),
						SIZE_PER_ELEMENT.dp,
						{ MinerControls(miner, CONTROLS_COLUMN_SIZE, activity) },
						{ TableCell(miner.name, tooltip = miner.name + (miner.pid?.let { ", PID: $it" } ?: ""), modifier = Modifier.defaultMinSize(minWidth = NAME_COLUMN_SIZE.dp)) },
						{ TableCell(miner.status) },
						{ TableCell(miner.hashrate?.let { "$it MH/s" }, textAlign = TextAlign.Right) },
						{ TableCell(miner.shares, tooltip = miner.shares?.let { "${it.valid} Valid/ ${it.stale} Stale/ ${it.rejected} Rejected" }, textAlign = TextAlign.Right) },
						{ TableCell(miner.time?.totalTime, textAlign = TextAlign.Right) },
						{ TableCell(miner.powerDraw?.let { "$it W" }, textAlign = TextAlign.Right) },
						{ TableCell(miner.powerEfficiency?.let { "$it kH/J" }, textAlign = TextAlign.Right) }
					)
				}
			}
		}
		MaterialRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween)
		{
			TextButton(
				modifier = Modifier.padding(start = 8.dp),
				onClick = {
					settings.darkMode = !settings.darkMode
					Application.windows.forEach { it.defaultTheme = settings.colors }
				})
			{
				Icon(if (settings.darkMode) Icons.Rounded.LightMode else Icons.Rounded.DarkMode, "Change theme")
			}
			TextButton(
				modifier = Modifier.padding(end = 8.dp),
				onClick = {
					val intent = Intent(Minersettings::class)
					activity.startActivity(intent)
				},
			)
			{
				Text("Create new miner")
			}
		}
	}
}