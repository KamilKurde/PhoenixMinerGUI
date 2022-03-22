package ui.material

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun Modifier.roundedBorder() = this.border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f)	, RoundedCornerShape(4.dp))