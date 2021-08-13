package ui.material

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.dp

@Composable
fun MaterialRow(modifier: Modifier = Modifier, isHeader: Boolean = false, content: @Composable RowScope.() -> Unit)
{
	var hovered by remember { mutableStateOf(false) }
	Row(
		modifier = modifier
			.height(if (isHeader) 56.dp else 52.dp)
			.pointerMoveFilter(
				onEnter = {
					hovered = true
					false
				},
				onExit = {
					hovered = false
					false
				}
			).background(if (hovered && !isHeader) Color(0xFFF5F5F5) else Color.White), verticalAlignment = Alignment.CenterVertically, content = content)
	if (!isHeader)
	{
		Divider(color = Color(0f, 0f, 0f, 0.12f), thickness = 1.dp)
	}
}