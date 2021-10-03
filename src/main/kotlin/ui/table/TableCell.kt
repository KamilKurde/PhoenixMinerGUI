package ui.table

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BoxWithTooltip
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ui.material.Tooltip

@ExperimentalAnimationApi
@Composable
fun RowScope.TableCell(
	text: Any?,
	weight: Float = 1f,
	modifier: Modifier = Modifier,
	fontWeight: FontWeight? = null,
	textAlign: TextAlign? = null,
	tooltip: String? = text?.toString()
) {
	BoxWithTooltip(
		modifier = modifier.padding(horizontal = 16.dp).weight(weight),
		tooltip = {
			if (tooltip != null)
			{
				Tooltip(tooltip)
			}
	})
	{
		AnimatedContent(text?.toString() ?: "-") {
			targetState ->
			Text(
				text = targetState,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				fontWeight = fontWeight,
				modifier = Modifier.fillMaxWidth().padding(16.dp),
				textAlign = textAlign
			)
		}

	}
}