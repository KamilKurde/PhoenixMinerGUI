package ui.table

import data.Id
import data.Settings.minerToEdit
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import config.Config
import config.Wallet
import config.arguments.StringArgument
import config.arguments.WalletArgument
import data.Settings
import kotlinx.coroutines.*
import ui.material.MaterialColumn
import ui.material.MaterialRow
import miner.Miner
import ui.MinerControls
import kotlin.random.Random
import kotlin.random.nextULong

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun MinerTable()
{
	val nameColumnWeight = 0.25f

	val controlsColumnWeight = 0.05f

	val columnWeight = 0.7f / 6f

	MaterialColumn {
		// Header
		MaterialRow(isHeader = true) {
			Spacer(Modifier.weight(controlsColumnWeight))
			TableCell(text = "Name", weight = nameColumnWeight, FontWeight.Bold)
			TableCell(text = "ID", weight = columnWeight, FontWeight.Bold)
			TableCell(text = "Status", weight = columnWeight, FontWeight.Bold)
			TableCell(text = "Hashrate", weight = columnWeight, FontWeight.Bold, textAlign = TextAlign.Right)
			TableCell(text = "Shares", weight = columnWeight, FontWeight.Bold, textAlign = TextAlign.Right)
			TableCell(text = "Power", weight = columnWeight, FontWeight.Bold, textAlign = TextAlign.Right)
			TableCell(text = "Efficiency", weight = columnWeight, FontWeight.Bold, textAlign = TextAlign.Right)
		}
		Column(Modifier.verticalScroll(rememberScrollState(0))) {
			Settings.miners.sortedBy { it.id.value }.forEach {
				miner ->
				MaterialRow(Modifier.fillMaxWidth()) {
					MinerControls(miner, controlsColumnWeight)
					TableCell(miner.name, nameColumnWeight)
					TableCell(miner.id, columnWeight)
					TableCell(miner.status, columnWeight)
					TableCell(miner.hashrate?.let { "$it MH/s" }, columnWeight, textAlign = TextAlign.Right)
					TableCell(miner.shares, tooltip = miner.shares?.let { "${it.valid} Valid/ ${it.stale} Stale/ ${it.rejected} Rejected" }, weight = columnWeight, textAlign = TextAlign.Right)
					TableCell(miner.powerDraw?.let { "$it W" }, columnWeight, textAlign = TextAlign.Right)
					TableCell(miner.powerEfficiency?.let { "$it kH/J" }, columnWeight, textAlign = TextAlign.Right)
				}
			}
		}
		Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End)
		{
			TextButton(
				{
					val id = (1..Int.MAX_VALUE).first { int -> Settings.miners.none { it.id.value == int } }
					val miner = Miner(
						"Miner $id", Id(id),
						Config.WalletParameter(WalletArgument.Wallet, Wallet("0x65cbddb4e7dd27009278d3160c8a5a4990d580d9")),
						Config.StringParameter(StringArgument.Pool, "eu-eth.hiveon.net:4444"),
						Config.StringParameter(StringArgument.Worker, "Donation${Random.nextULong()}"),
					)
					minerToEdit = miner
					CoroutineScope(Job()).launch {
						delay(1000L)
						Settings.miners += miner
					}
				},
			)
			{
				Text("Create new miner")
			}
		}
	}
}