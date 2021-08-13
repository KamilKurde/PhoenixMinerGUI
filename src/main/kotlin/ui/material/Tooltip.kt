package ui.material

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Tooltip(text: String)
{
	Surface(shape = RoundedCornerShape(4.dp)) {
		Text(text, modifier = Modifier.background(Color.Gray).padding(4.dp), color = Color.White)
	}
}