package ui

import data.Id
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import config.Config
import config.SettingsConfig
import config.Wallet
import data.Settings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ui.material.MaterialRow
import ui.table.TableCell

@ExperimentalCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun ParameterUI(settings: SettingsConfig, displayTooltip: Boolean = true)
{
	MaterialRow {
		val config = settings.config
		if (config.required)
		{
			Spacer(modifier = Modifier.weight(0.05f))
		}
		else{
			Box(modifier = Modifier.weight(0.05f), contentAlignment = Alignment.Center)
			{
				Checkbox(
					settings.enabled,
					{settings.enabled = it}
				)
			}
		}
		TableCell(config.name, 0.1f, textAlign = TextAlign.Left, tooltip = if (displayTooltip) config.name else null)
		TableCell(config.description, 0.55f, textAlign = TextAlign.Left, tooltip = if (displayTooltip) config.description else null)
		Box(modifier = Modifier.weight(0.3f)) {
			when (config)
			{
				is Config.NumberParameter ->
				{
					var temporalValue by remember { mutableStateOf(config.valueAsString) }
					var errorState by remember { mutableStateOf(false) }
					TextField(
						value = temporalValue,
						modifier = Modifier.fillMaxSize(),
						onValueChange = {
							temporalValue = it
							try
							{
								val int = temporalValue.toInt()
								if (int in config.element.range)
								{
									config.value = int
									errorState = false
								}
								else
								{
									errorState = true
								}
							}
							catch (e: Exception)
							{
								errorState = true
							}
						},
						isError = errorState,
						singleLine = true,
						label = { Text("Numeric value") }
					)
				}
				is Config.WalletParameter ->
				{
					var temporalValue by remember { mutableStateOf(config.valueAsString) }
					var errorState by remember { mutableStateOf(false) }
					TextField(
						value = temporalValue,
						modifier = Modifier.fillMaxSize(),
						onValueChange = {
							temporalValue = it
							try
							{
								config.value = Wallet(temporalValue)
								errorState = false
							}
							catch (e: Exception)
							{
								errorState = true
							}
						},
						isError = errorState,
						singleLine = true,
						label = { Text("Wallet address") }
					)
				}
				is Config.GpusParameter ->
				{
					var temporalValue by remember { mutableStateOf(config.valueAsString) }
					var errorState by remember { mutableStateOf(false) }
					TextField(
						value = temporalValue,
						modifier = Modifier.fillMaxSize(),
						onValueChange = {
							newValue ->
							temporalValue = newValue
							try
							{
								val ids = temporalValue.split(",").map { Id(it.toInt()) }.toTypedArray()
								if (ids.all { id -> Settings.gpus.any { gpu -> gpu.id.value == id.value } })
								{
									config.value = ids
									errorState = false
								}
							}
							catch (e: Exception)
							{
								errorState = true
							}
						},
						isError = errorState,
						singleLine = true,
						label = { Text("IDs of GPUs separated by commas, by default all GPUs are used") }
					)
				}
				is Config.BooleanParameter ->
				{
					Switch(
						config.value,
						{ config.value = it },
						modifier = Modifier.fillMaxSize(),
					)
				}
				is Config.StringParameter ->
				{
					TextField(
						value = config.value,
						modifier = Modifier.fillMaxSize(),
						onValueChange = {
							config.value = it
						},
						singleLine = true,
						label = { Text("Value") }
					)
				}
			}
		}
	}
}