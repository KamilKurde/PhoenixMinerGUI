package ui.table

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ui.material.Tooltip

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Suppress("FunctionName", "EXPERIMENTAL_IS_NOT_ENABLED")
@Composable
fun RowScope.TableCell(
	text: Any?,
	weight: Float? = 1f,
	modifier: Modifier = Modifier,
	isHeader: Boolean = false,
	textAlign: TextAlign? = null,
	tooltip: String? = text?.toString(),
) {
	var modifier = modifier.padding(horizontal = 16.dp)
	val fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal
	weight?.let {
		modifier = modifier.weight(it, false)
	}
	TooltipArea({
		if (tooltip != null) {
			Tooltip(tooltip)
		}
	}, modifier = modifier)
	{
		AnimatedContent(text?.toString() ?: "-") { targetState ->
			Text(
				text = targetState,
				color = MaterialTheme.colors.onSurface.copy(alpha = if (isHeader) 0.6f else 1f),
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				fontWeight = fontWeight,
				modifier = Modifier.fillMaxWidth().padding(16.dp),
				textAlign = textAlign
			)
		}
		
	}
}