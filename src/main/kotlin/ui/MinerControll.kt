package ui

import ID_COLUMN_SIZE
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.Settings
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import miner.Miner
import miner.MinerStatus
import ui.table.TableCell

@ExperimentalAnimationApi
@ExperimentalSerializationApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun RowScope.MinerControls(miner: Miner, size: Int) {
	val modifier = Modifier.width((size / 2).dp)
	val minerRunning = miner.status != MinerStatus.Offline && miner.status != MinerStatus.Closing
	Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Start) {
		TableCell(miner.id, modifier = Modifier.width(ID_COLUMN_SIZE.dp))
		IconButton(
			{
				if (minerRunning) {
					CoroutineScope(Job()).launch {
						miner.stopMining()
					}
				} else {
					Settings.startMiner(miner)
				}
			}, modifier = modifier
		)
		{
			Icon(if (minerRunning) Icons.Rounded.Close else Icons.Rounded.PlayArrow, if (minerRunning) "Stop button" else "Start button", tint = Color.Black)
		}
		IconButton({ Settings.minerToEdit = miner }, modifier = modifier)
		{
			Icon(Icons.Rounded.Settings, "Edit", tint = Color.Black)
		}
	}
}