package ui.material

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Suppress("FunctionName")
@Composable
fun MaterialRow(modifier: Modifier = Modifier, isHeader: Boolean = false, horizontalArrangement: Arrangement.Horizontal = Arrangement.Start, verticalAlignment: Alignment.Vertical = Alignment.CenterVertically, content: @Composable RowScope.() -> Unit) {
	val interactionSource = remember { MutableInteractionSource() }
	val hovered by interactionSource.collectIsHoveredAsState()
	Row(
		modifier = modifier
			.height(if (isHeader) 56.dp else 52.dp)
			.hoverable(interactionSource)
			.background(if (hovered && !isHeader) MaterialTheme.colors.background else MaterialTheme.colors.surface),
		horizontalArrangement = horizontalArrangement,
		verticalAlignment = verticalAlignment,
		content = content
	)
	if (!isHeader) {
		Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f), thickness = 1.dp)
	}
}