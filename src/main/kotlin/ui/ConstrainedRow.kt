package ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import kotlin.math.max

@Suppress("FunctionName")
@Composable
fun ConstrainedRow(modifier: Modifier = Modifier, sizePerElement: Dp, vararg elements: @Composable () -> Unit) {
	BoxWithConstraints(modifier = modifier) {
		val numberOfItems = (maxWidth / sizePerElement).toInt()
		Row {
			elements.take(max(numberOfItems, 1)).forEach {
				it()
			}
		}
	}
}