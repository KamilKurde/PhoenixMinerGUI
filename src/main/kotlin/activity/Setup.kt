package activity

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.KamilKurde.Activity
import functions.tryWithoutCatch
import phoenix.openFileDialog
import phoenix.phoenixPathIsCorrect
import settings
import ui.material.Tooltip

class Setup : Activity() {
	
	@Suppress("OPT_IN_IS_NOT_ENABLED")
	@OptIn(ExperimentalFoundationApi::class)
	override fun onCreate() {
		super.onCreate()
		setContent {
			Column(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
				Image(painterResource("icon.png"), "PhoenixMiner GUI Icon", Modifier.weight(0.75f), contentScale = ContentScale.Inside)
				Spacer(modifier = Modifier.height(16.dp))
				Column(Modifier.weight(0.25f), horizontalAlignment = Alignment.CenterHorizontally) {
					Text("Welcome to PhoenixMiner GUI, to start link PhoenixMiner executable below", color = MaterialTheme.colors.onBackground)
					Spacer(modifier = Modifier.height(16.dp))
					Button(
						onClick = {
							tryWithoutCatch {
								(openFileDialog(ComposeWindow(), "choose PhoenixMiner.exe file").absolutePath).let {
									if (phoenixPathIsCorrect(it)) {
										settings.phoenixPath = it
										settings.save()
										finish()
									}
								}
							}
						},
						modifier = Modifier.wrapContentSize()
					)
					{
						TooltipArea(
							{
								Tooltip("This version was tested for PhoenixMiner 6.1b however it should work with newer version as well")
							}
						)
						{
							Text("Choose File")
						}
					}
				}
			}
		}
	}
}