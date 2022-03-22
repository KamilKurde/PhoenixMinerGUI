package ui.material

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Suppress("FunctionName")
@Composable
fun Tooltip(text: String) {
	Surface(shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, Color.Black)) {
		Crossfade(text)
		{
			Text(it, modifier = Modifier.background(MaterialTheme.colors.surface).padding(4.dp), color = MaterialTheme.colors.onSurface)
		}
	}
}