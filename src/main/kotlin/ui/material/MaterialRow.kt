package ui.material

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Suppress("FunctionName")
@Composable
fun MaterialRow(modifier: Modifier = Modifier, isHeader: Boolean = false, content: @Composable RowScope.() -> Unit) {
	val interactionSource = remember { MutableInteractionSource() }
	val hovered by interactionSource.collectIsHoveredAsState()
	Row(
		modifier = modifier
			.height(if (isHeader) 56.dp else 52.dp)
			.hoverable(interactionSource)
			.background(if (hovered && !isHeader) Color(0xFFF5F5F5) else Color.White),
		verticalAlignment = Alignment.CenterVertically,
		content = content
	)
	if (!isHeader) {
		Divider(color = Color(0f, 0f, 0f, 0.12f), thickness = 1.dp)
	}
}