package ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ui.table.GpuTable
import ui.table.MinerTable

@ExperimentalCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun Summary() = Column(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.Top) {
	GpuTable()
	Spacer(Modifier.height(32.dp))
	MinerTable()
}