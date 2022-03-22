package activity

import SPACERS_HEIGHT
import VERSION
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.KamilKurde.Activity
import com.github.KamilKurde.Intent
import functions.Shortcut
import kotlinx.coroutines.ExperimentalCoroutinesApi
import phoenix.phoenixPathIsCorrect
import settings
import ui.table.GpuTable
import ui.table.MinerTable

class Summary : Activity() {
	
	@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
	@OptIn(ExperimentalCoroutinesApi::class)
	override fun onCreate() {
		super.onCreate()
		if (!phoenixPathIsCorrect(settings.phoenixPath)) {
			startActivity(Intent(Setup::class))
		}
		setContent {
			Column(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.Top) {
				Column(modifier = Modifier.fillMaxWidth().height(SPACERS_HEIGHT.dp), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
					Text("GPUs", fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onBackground)
				}
				GpuTable(Modifier.weight(1f, false))
				Column(modifier = Modifier.padding(top = SPACERS_HEIGHT.dp).fillMaxWidth().height(SPACERS_HEIGHT.dp), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
					Text("Miners", fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onBackground)
				}
				MinerTable(this@Summary, Modifier.weight(1f, false), this@Summary::setTheme)
				Row(Modifier.fillMaxWidth().padding(top = SPACERS_HEIGHT.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically)
				{
					Text(VERSION, color = MaterialTheme.colors.onBackground)
					// This doesn't work without full distributable, distributable gives exe location as its path, while running from IDEA program gives project location as its path
					if (VERSION != "DEVELOPMENT") {
						Row(verticalAlignment = Alignment.CenterVertically) {
							Text("Launch PhoenixMiner GUI on system startup", color = MaterialTheme.colors.onBackground)
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
		}
	}
}