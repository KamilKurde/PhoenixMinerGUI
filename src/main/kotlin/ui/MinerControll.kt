package ui

import ID_COLUMN_SIZE
import activity.Minersettings
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.KamilKurde.Activity
import com.github.KamilKurde.Intent
import data.miner.Miner
import data.miner.MinerStatus
import kotlinx.coroutines.*
import settings
import ui.material.Stop
import ui.table.TableCell

@Suppress("FunctionName")
@Composable
fun RowScope.MinerControls(miner: Miner, size: Int, activity: Activity) {
	val modifier = Modifier.width((size / 2).dp)
	val minerRunning by derivedStateOf { miner.status != MinerStatus.Offline && miner.status != MinerStatus.Closing }
	Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Start) {
		TableCell(miner.id, modifier = Modifier.width(ID_COLUMN_SIZE.dp))
		IconButton(
			{
				if (minerRunning) {
					CoroutineScope(Job()).launch {
						miner.stopMining()
					}
				} else {
					settings.startMiner(miner)
				}
			}, modifier = modifier
		)
		{
			Icon(if (minerRunning) Icons.Rounded.Stop else Icons.Rounded.PlayArrow, if (minerRunning) "Stop button" else "Start button", tint = MaterialTheme.colors.onSurface)
		}
		IconButton({ activity.startActivity(Intent(Minersettings::class).putExtra("minerID", settings.miners.indexOf(miner))) }, modifier = modifier)
		{
			Icon(Icons.Rounded.Settings, "Edit", tint = MaterialTheme.colors.onSurface)
		}
	}
}