package ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import config.*
import data.Id
import data.Settings
import ui.material.MaterialRow
import ui.table.TableCell
import ui.theme.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Suppress("FunctionName", "EXPERIMENTAL_IS_NOT_ENABLED")
@Composable
fun ParameterUI(settings: OptionWrapper, displayTooltip: Boolean = true) {
	MaterialRow {
		val config = settings.config
		if (config.required) {
			Spacer(modifier = Modifier.weight(CHECKBOX_WEIGHT))
		} else {
			Box(modifier = Modifier.weight(CHECKBOX_WEIGHT), contentAlignment = Alignment.Center)
			{
				Checkbox(
					settings.enabled,
					{ settings.enabled = it }
				)
			}
		}
		TableCell(config.name, NAME_WEIGHT, textAlign = TextAlign.Left, tooltip = if (displayTooltip) config.name else null)
		TableCell(config.description, DESCRIPTION_WEIGHT, textAlign = TextAlign.Left, tooltip = if (displayTooltip) config.description else null)
		Box(modifier = Modifier.weight(VALUE_WEIGHT)) {
			when (config) {
				is Option.Number -> {
					var temporalValue by remember { mutableStateOf(config.valueAsString) }
					var errorState by remember { mutableStateOf(false) }
					TextField(
						value = temporalValue,
						modifier = Modifier.fillMaxSize(),
						onValueChange = {
							temporalValue = it
							try {
								val int = temporalValue.toInt()
								if (int in config.element.range) {
									config.value = int
									errorState = false
								} else {
									errorState = true
								}
							} catch (e: Exception) {
								errorState = true
							}
						},
						isError = errorState,
						singleLine = true,
						label = { Text("Numeric value") }
					)
				}
				
				is Option.Wallet -> {
					var temporalValue by remember { mutableStateOf(config.valueAsString) }
					var errorState by remember { mutableStateOf(false) }
					TextField(
						value = temporalValue,
						modifier = Modifier.fillMaxSize(),
						onValueChange = {
							temporalValue = it
							try {
								config.value = Wallet(temporalValue)
								errorState = false
							} catch (e: Exception) {
								errorState = true
							}
						},
						isError = errorState,
						singleLine = true,
						label = { Text("Wallet address") }
					)
				}
				
				is Option.Gpus -> {
					var temporalValue by remember { mutableStateOf(config.valueAsString) }
					var errorState by remember { mutableStateOf(false) }
					TextField(
						value = temporalValue,
						modifier = Modifier.fillMaxSize(),
						onValueChange = { newValue ->
							temporalValue = newValue
							try {
								val ids = temporalValue.split(",").map { Id(it.toInt()) }.toTypedArray()
								if (ids.all { id -> Settings.gpus.any { gpu -> gpu.id == id } } && ids.isNotEmpty()) {
									config.value = ids
									errorState = false
								} else {
									errorState = true
								}
							} catch (e: Exception) {
								errorState = true
							}
						},
						isError = errorState,
						singleLine = true,
						label = { Text("IDs of GPUs separated by commas") }
					)
				}
				
				is Option.Boolean -> {
					Switch(
						config.value,
						{ config.value = it },
						modifier = Modifier.fillMaxSize(),
					)
				}
				
				is Option.String -> {
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