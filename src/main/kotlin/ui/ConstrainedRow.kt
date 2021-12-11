package ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Suppress("FunctionName")
@Composable
fun ConstrainedRow(modifier: Modifier = Modifier, sizePerElement: Dp, vararg elements: @Composable RowScope.() -> Unit) {
	BoxWithConstraints(modifier = modifier) {
		val numberOfItems = (maxWidth / sizePerElement).toInt()
		Row {
			for (i in 0 until numberOfItems) {
				if (i >= elements.size) {
					break
				}
				elements[i]()
			}
		}
	}
}