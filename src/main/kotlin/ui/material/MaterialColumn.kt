package ui.material

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MaterialColumn(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit)
{
	Column(modifier = modifier
		.animateContentSize()
		.wrapContentHeight()
		.border(1.dp, Color(0f, 0f, 0f, 0.12f), RoundedCornerShape(4.dp)), content = content)
}