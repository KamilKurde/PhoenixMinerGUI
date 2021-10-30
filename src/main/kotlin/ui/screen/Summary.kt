package ui.screen

import SPACERS_HEIGHT
import VERSION
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
    Column(modifier = Modifier.fillMaxWidth().height(SPACERS_HEIGHT.dp), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("GPUs", fontWeight = FontWeight.Bold)
    }
    GpuTable(Modifier.weight(1f, false))
    Column(modifier = Modifier.fillMaxWidth().height(SPACERS_HEIGHT.dp), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Miners", fontWeight = FontWeight.Bold)
    }
    MinerTable(Modifier.weight(1f, false))
    Spacer(Modifier.height(SPACERS_HEIGHT.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically)
    {
        Text(VERSION)
        // This doesn't work without full distributable, distributable gives exe location as its path, while running normally from IDEA program gives project location as its path
        if (VERSION != "DEVELOPMENT")
        {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Launch PhoenixMiner GUI on system startup")
                Spacer(Modifier.width(16.dp))
                var enabled by remember { mutableStateOf(Shortcut.exist) }
                Switch(
                    enabled,
                    {
                        enabled = if (it) {
                            Shortcut.create()
                            true
                        } else {
                            Shortcut.delete()
                            false
                        }
                    })
            }
        }
    }
}