package ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import miner.Miner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import config.Parameters
import data.Settings
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import miner.MinerStatus
import ui.ParameterUI
import ui.material.MaterialRow
import ui.table.TableCell
import ui.theme.CHECKBOX_WEIGHT
import ui.theme.DESCRIPTION_WEIGHT
import ui.theme.NAME_WEIGHT
import ui.theme.VALUE_WEIGHT

@ExperimentalSerializationApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun MinerSettings(miner: Miner)
{
	val wasWorking by remember { mutableStateOf(miner.status != MinerStatus.Offline && miner.status != MinerStatus.Closing) }
	Column(
		modifier = Modifier.fillMaxSize().padding(8.dp)
	) {
		var name by remember { mutableStateOf(miner.name) }
		val parameters by remember { mutableStateOf(miner.parameters.allConfigs()) }

		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth()
		) {
			IconButton(
				{
					Settings.minerToEdit = null
				})
			{
				Icon(Icons.Rounded.ArrowBack, "Back", tint = Color.Black)
			}
			Spacer(modifier = Modifier.width(16.dp))
			TextField(
				value = name,
				onValueChange = { name = it },
				label = { Text("Name") }
			)
			Spacer(modifier = Modifier.width(16.dp))
			Button(
				{
					miner.name = name.trim()
					miner.parameters = Parameters(*parameters.filter { it.enabled }.map { it.config }.toTypedArray())
					Settings.saveSettings()
					if (wasWorking)
					{
						CoroutineScope(Job()).launch {
							miner.stopMining()
							Settings.startMiner(miner)
						}
					}
				}
			)
			{
				Text("Save")
			}
		}

		Spacer(modifier = Modifier.height(32.dp))

		var deletionAlert by remember { mutableStateOf(false) }

		LazyColumn(
			modifier = Modifier.fillMaxWidth().wrapContentHeight()
				.border(1.dp, Color(0f, 0f, 0f, 0.12f), RoundedCornerShape(4.dp))
		) {
			stickyHeader {
				MaterialRow(isHeader = true) {
					Spacer(modifier = Modifier.weight(CHECKBOX_WEIGHT))
					TableCell("Name", NAME_WEIGHT, fontWeight = FontWeight.Bold, textAlign = TextAlign.Left)
					TableCell("Description", DESCRIPTION_WEIGHT, fontWeight = FontWeight.Bold, textAlign = TextAlign.Left)
					TableCell("Value", VALUE_WEIGHT, fontWeight = FontWeight.Bold, textAlign = TextAlign.Right)
				}
			}
			// Handling miner startup on program launch
			item {
				MaterialRow {
					Spacer(modifier = Modifier.weight(CHECKBOX_WEIGHT))
					TableCell("Mos", NAME_WEIGHT, textAlign = TextAlign.Left)
					TableCell("Start this miner on program launch", DESCRIPTION_WEIGHT, textAlign = TextAlign.Left)
					Box(modifier = Modifier.weight(VALUE_WEIGHT)) {
						Switch(
							miner.mineOnStartup,
							{ miner.mineOnStartup = it },
							modifier = Modifier.fillMaxSize(),
						)
					}
				}
			}
			items(parameters.size)
			{
				ParameterUI(parameters[it], !deletionAlert)
			}
			item {
				Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
					IconButton({
								   deletionAlert = true
							   })
					{
						Icon(Icons.Rounded.Delete, "Delete Button", tint = Color.Red)
					}
				}
			}
		}

		if (deletionAlert)
		{
			AlertDialog(
				modifier = Modifier.width(400.dp),
				onDismissRequest = { deletionAlert = false },
				title = {
					Text("Are you sure that you want to delete this miner?")
				},
				text = {
					Text("This action cannot be undone")
				},
				dismissButton = {
					OutlinedButton({ deletionAlert = false })
					{
						Text("No")
					}
				},
				confirmButton = {
					Button(
						{
							Settings.miners = Settings.miners.filter { it.id.value != miner.id.value }.toTypedArray()
							Settings.saveSettings()
							Settings.minerToEdit = null
							deletionAlert = false
						}, colors = ButtonDefaults.buttonColors(Color.Red, Color.White)
					)
					{
						Text("Yes")
					}
				}
			)
		}
	}
}