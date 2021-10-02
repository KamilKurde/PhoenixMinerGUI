package ui.table

import data.Id
import data.Settings.minerToEdit
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import config.Config
import config.Wallet
import config.arguments.StringArgument
import config.arguments.WalletArgument
import data.Settings
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import ui.material.MaterialColumn
import ui.material.MaterialRow
import miner.Miner
import ui.ConstrainedRow
import ui.MinerControls
import kotlin.random.Random
import kotlin.random.nextULong

@ExperimentalSerializationApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun MinerTable(
	modifier: Modifier = Modifier
)
{
	val controlsColumnWeight = 0.05f

	MaterialColumn(modifier) {
		LazyColumn {
			stickyHeader {
				MaterialRow(isHeader = true) {
					Spacer(Modifier.weight(controlsColumnWeight))
					TableCell(text = "Name", weight = NAME_COLUMN_WEIGHT - controlsColumnWeight, FontWeight.Bold)
					TableCell(text = "ID", weight = ESSENTIAL_DATA_COLUMN_WEIGHT, FontWeight.Bold)
					TableCell(text = "Status", weight = ESSENTIAL_DATA_COLUMN_WEIGHT, FontWeight.Bold)
					ConstrainedRow(
						Modifier.weight(1f - NAME_COLUMN_WEIGHT - (ESSENTIAL_DATA_COLUMN_WEIGHT * 2)),
						SIZE_PER_ELEMENT.dp,
						{ weight -> TableCell(text = "Hashrate", weight, fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) },
						{ weight -> TableCell(text = "Shares", weight, fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) },
						{ weight -> TableCell(text = "Power", weight, fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) },
						{ weight -> TableCell(text = "Efficiency", weight, fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) }
					)
				}
			}
			items(Settings.miners.size)
			{
				val miner = Settings.miners[it]
				MaterialRow(Modifier.fillMaxWidth()) {
					MinerControls(miner, controlsColumnWeight)
					TableCell(miner.name, NAME_COLUMN_WEIGHT - controlsColumnWeight)
					TableCell(miner.id, ESSENTIAL_DATA_COLUMN_WEIGHT)
					TableCell(miner.status, ESSENTIAL_DATA_COLUMN_WEIGHT)
					ConstrainedRow(
						Modifier.weight(1f - NAME_COLUMN_WEIGHT - (ESSENTIAL_DATA_COLUMN_WEIGHT * 2)),
						SIZE_PER_ELEMENT.dp,
						{ weight -> TableCell(miner.hashrate?.let { "$it MH/s" }, weight, textAlign = TextAlign.Right) },
						{ weight -> TableCell(miner.shares, tooltip = miner.shares?.let { "${it.valid} Valid/ ${it.stale} Stale/ ${it.rejected} Rejected" }, weight = weight, textAlign = TextAlign.Right) },
						{ weight -> TableCell(miner.powerDraw?.let { "$it W" }, weight, textAlign = TextAlign.Right) },
						{ weight -> TableCell(miner.powerEfficiency?.let { "$it kH/J" }, weight, textAlign = TextAlign.Right) }
					)
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
						false,
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