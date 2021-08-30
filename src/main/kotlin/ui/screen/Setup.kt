package ui.screen

import androidx.compose.foundation.BoxWithTooltip
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import phoenix.openFileDialog
import phoenix.phoenixPathIsCorrect
import tryWithoutCatch
import ui.material.Tooltip

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
@Composable
fun Setup()
{
	Column(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
		Image(painterResource("icon.png"), "PhoenixMiner GUI Icon", Modifier.weight(0.75f), contentScale = ContentScale.Inside)
		Spacer(modifier = Modifier.height(16.dp))
		Column(Modifier.weight(0.25f), horizontalAlignment = Alignment.CenterHorizontally) {
			Text("Welcome to PhoenixMiner GUI, to start link PhoenixMiner executable below")
			Spacer(modifier = Modifier.height(16.dp))
			Button(
				onClick = {
					tryWithoutCatch{
						(openFileDialog(ComposeWindow(), "choose PhoenixMiner.exe file").absolutePath).let {
							if (phoenixPathIsCorrect(it))
							{
								data.Settings.phoenixPath = it
								data.Settings.saveSettings()
							}
						}
					}
				},
				modifier = Modifier.wrapContentSize()
			)
			{
				BoxWithTooltip({
								   Tooltip("This version was tested for PhoenixMiner 5.7b however it should work with newer version as well")
							   })
				{ Text("Choose File") }
			}
		}
	}
}