package activity

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.KamilKurde.Activity
import config.*
import config.arguments.StringArgument
import config.arguments.WalletArgument
import data.Id
import data.miner.Miner
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import settings
import ui.ParameterUI
import ui.material.MaterialRow
import ui.table.TableCell
import ui.theme.*
import kotlin.random.Random
import kotlin.random.nextULong

class Minersettings : Activity() {
	
	@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
	@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
	override fun onCreate() {
		super.onCreate()
		val minerID = intent.getExtra<Int>("minerID")
		val miner = if (minerID != null) {
			settings.miners[minerID]
		} else {
			val id = (0..Int.MAX_VALUE).first { int -> settings.miners.none { it.id.value == int } }
			Miner(
				"Miner $id", Id(id),
				false,
				Option.Wallet(WalletArgument.Wallet, Wallet("0x65cbddb4e7dd27009278d3160c8a5a4990d580d9")),
				Option.String(StringArgument.Pool, "eu1.ethermine.org:4444"),
				Option.String(StringArgument.Worker, "Donation${Random.nextULong()}"),
			)
		}
		val initialSettings = Json.encodeToString(miner)
		setContent {
			Column(
				modifier = Modifier.fillMaxSize().padding(8.dp)
			) {
				var name by remember { mutableStateOf(miner.name) }
				val parameters by remember { mutableStateOf(miner.arguments.allConfigs()) }
				
				Row(
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.fillMaxWidth()
				) {
					IconButton(
						{
							parent.back()
						})
					{
						Icon(Icons.Rounded.ArrowBack, "Back", tint = MaterialTheme.colors.onSurface)
					}
					Spacer(modifier = Modifier.width(16.dp))
					TextField(
						value = name,
						onValueChange = { name = it },
						label = { Text("Name") },
						colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onSurface)
					)
					Spacer(modifier = Modifier.width(16.dp))
					Button(
						{
							miner.name = name.trim()
							miner.arguments = Arguments(*parameters.filter { it.enabled }.map { it.config }.toTypedArray())
							if (settings.miners.none { it.id == miner.id }) {
								settings.miners.add(miner)
							}
							settings.save()
							// Ensure that miner is active and changes were made before restarting miner
							if (miner.isActive && initialSettings != Json.encodeToString(miner)) {
								CoroutineScope(Job()).launch {
									miner.log("Restarting miner")
									miner.stopMining()
									settings.startMiner(miner)
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
							TableCell("Name", NAME_WEIGHT, isHeader = true, textAlign = TextAlign.Left)
							TableCell("Description", DESCRIPTION_WEIGHT, isHeader = true, textAlign = TextAlign.Left)
							TableCell("Value", VALUE_WEIGHT, isHeader = true, textAlign = TextAlign.Center)
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
				
				if (deletionAlert) {
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
									settings.miners.remove(miner)
									settings.save()
									parent.back()
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
	}
}