package ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import data.Settings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import miner.Miner
import miner.MinerStatus

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun RowScope.MinerControls(miner: Miner, weight: Float)
{
	val modifier = Modifier.weight(weight / 2)
	val minerRunning = miner.status != MinerStatus.Offline
	IconButton(
		{
				   if (minerRunning)
				   {
					   miner.stopMining()
				   }
				   else
				   {
					   Settings.startMiner(miner)
				   }
			   }, modifier = modifier)
	{
		Icon(if (minerRunning) Icons.Rounded.Close else Icons.Rounded.PlayArrow, if (minerRunning) "Stop button" else "Start button", tint = Color.Black)
	}
	IconButton({ Settings.minerToEdit = miner }, modifier = modifier)
	{
		Icon(Icons.Rounded.Settings, "Edit", tint = Color.Black)
	}
}