package ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import functions.Shortcut
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import ui.table.GpuTable
import ui.table.MinerTable

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun Summary() = Column(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.Top) {
	GpuTable()
	Spacer(Modifier.height(32.dp))
	MinerTable()
	Spacer(Modifier.height(32.dp))
	// This doesn't work without full distributable, distributable gives exe location as its path, while running normally from IDEA program gives project location as its path
	Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End)
	{
		Text("Launch PhoenixMiner GUI on system startup")
		Spacer(Modifier.width(16.dp))
		var enabled by remember { mutableStateOf(Shortcut.exist) }
		Switch(
			enabled,
			{
				enabled = if (it)
				{
					Shortcut.create()
					true
				}
				else
				{
					Shortcut.delete()
					false
				}
			})
	}
}