package ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun ConstrainedRow(modifier: Modifier = Modifier, sizePerElement: Dp, vararg elements: @Composable RowScope.() -> Unit)
{
	BoxWithConstraints(modifier = modifier) {
		val numberOfItems = (maxWidth / sizePerElement).toInt()
		Row {
			for (i in 0 until numberOfItems)
			{
				if (i >= elements.size)
				{
					break
				}
				elements[i]()
			}
		}
	}
}