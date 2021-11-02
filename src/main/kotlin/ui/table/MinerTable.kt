package ui.table

import CONTROLS_COLUMN_SIZE
import ID_COLUMN_SIZE
import NAME_COLUMN_SIZE
import SIZE_PER_ELEMENT
import data.Id
import data.Settings.minerToEdit
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
                        { TableCell(text = "Name", modifier = Modifier.defaultMinSize(minWidth = NAME_COLUMN_SIZE.dp), fontWeight = FontWeight.Bold) },
                        { TableCell(text = "Status", fontWeight = FontWeight.Bold) },
                        { TableCell(text = "Hashrate", fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) },
                        { TableCell(text = "Shares", fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) },
                        { TableCell(text = "Power", fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) },
                        { TableCell(text = "Efficiency", fontWeight = FontWeight.Bold, textAlign = TextAlign.Right) }
                    )
                }
            }
            items(Settings.miners.size)
            {
                val miner = Settings.miners[it]
                MaterialRow(Modifier.fillMaxWidth()) {
                    MinerControls(miner, CONTROLS_COLUMN_SIZE)
                    TableCell(miner.id, null, modifier = Modifier.width(ID_COLUMN_SIZE.dp))
                    ConstrainedRow(
                        Modifier.weight(1f),
                        SIZE_PER_ELEMENT.dp,
                        { TableCell(miner.name, tooltip = miner.name + (miner.pid?.let { ", PID: $it" } ?: ""), modifier = Modifier.defaultMinSize(minWidth = NAME_COLUMN_SIZE.dp)) },
                        { TableCell(miner.status) },
                        { TableCell(miner.hashrate?.let { "$it MH/s" }, textAlign = TextAlign.Right) },
                        { TableCell(miner.shares, tooltip = miner.shares?.let { "${it.valid} Valid/ ${it.stale} Stale/ ${it.rejected} Rejected" }, textAlign = TextAlign.Right) },
                        { TableCell(miner.powerDraw?.let { "$it W" }, textAlign = TextAlign.Right) },
                        { TableCell(miner.powerEfficiency?.let { "$it kH/J" }, textAlign = TextAlign.Right) }
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
                        Config.StringParameter(StringArgument.Pool, "eu1.ethermine.org:4444"),
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