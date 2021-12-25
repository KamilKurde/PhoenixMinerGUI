@file:Suppress("unused")

package ui.material

import androidx.compose.material.icons.*
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Rounded.Stop: ImageVector
	get() = materialIcon(name = "Rounded.Stop") {
		materialPath {
			moveTo(8.0f, 6.0f)
			horizontalLineToRelative(8.0f)
			curveToRelative(1.1f, 0.0f, 2.0f, 0.9f, 2.0f, 2.0f)
			verticalLineToRelative(8.0f)
			curveToRelative(0.0f, 1.1f, -0.9f, 2.0f, -2.0f, 2.0f)
			horizontalLineTo(8.0f)
			curveToRelative(-1.1f, 0.0f, -2.0f, -0.9f, -2.0f, -2.0f)
			verticalLineTo(8.0f)
			curveToRelative(0.0f, -1.1f, 0.9f, -2.0f, 2.0f, -2.0f)
			close()
		}
	}