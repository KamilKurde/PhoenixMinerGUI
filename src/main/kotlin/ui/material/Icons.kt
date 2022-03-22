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

val Icons.Rounded.LightMode: ImageVector
	get() = materialIcon(name = "Rounded.LightMode") {
		materialPath {
			moveTo(12.0f, 7.0f)
			curveToRelative(-2.76f, 0.0f, -5.0f, 2.24f, -5.0f, 5.0f)
			reflectiveCurveToRelative(2.24f, 5.0f, 5.0f, 5.0f)
			reflectiveCurveToRelative(5.0f, -2.24f, 5.0f, -5.0f)
			reflectiveCurveTo(14.76f, 7.0f, 12.0f, 7.0f)
			lineTo(12.0f, 7.0f)
			close()
			moveTo(2.0f, 13.0f)
			lineToRelative(2.0f, 0.0f)
			curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
			reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
			lineToRelative(-2.0f, 0.0f)
			curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
			reflectiveCurveTo(1.45f, 13.0f, 2.0f, 13.0f)
			close()
			moveTo(20.0f, 13.0f)
			lineToRelative(2.0f, 0.0f)
			curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
			reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
			lineToRelative(-2.0f, 0.0f)
			curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
			reflectiveCurveTo(19.45f, 13.0f, 20.0f, 13.0f)
			close()
			moveTo(11.0f, 2.0f)
			verticalLineToRelative(2.0f)
			curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
			reflectiveCurveToRelative(1.0f, -0.45f, 1.0f, -1.0f)
			verticalLineTo(2.0f)
			curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
			reflectiveCurveTo(11.0f, 1.45f, 11.0f, 2.0f)
			close()
			moveTo(11.0f, 20.0f)
			verticalLineToRelative(2.0f)
			curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
			reflectiveCurveToRelative(1.0f, -0.45f, 1.0f, -1.0f)
			verticalLineToRelative(-2.0f)
			curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
			curveTo(11.45f, 19.0f, 11.0f, 19.45f, 11.0f, 20.0f)
			close()
			moveTo(5.99f, 4.58f)
			curveToRelative(-0.39f, -0.39f, -1.03f, -0.39f, -1.41f, 0.0f)
			curveToRelative(-0.39f, 0.39f, -0.39f, 1.03f, 0.0f, 1.41f)
			lineToRelative(1.06f, 1.06f)
			curveToRelative(0.39f, 0.39f, 1.03f, 0.39f, 1.41f, 0.0f)
			reflectiveCurveToRelative(0.39f, -1.03f, 0.0f, -1.41f)
			lineTo(5.99f, 4.58f)
			close()
			moveTo(18.36f, 16.95f)
			curveToRelative(-0.39f, -0.39f, -1.03f, -0.39f, -1.41f, 0.0f)
			curveToRelative(-0.39f, 0.39f, -0.39f, 1.03f, 0.0f, 1.41f)
			lineToRelative(1.06f, 1.06f)
			curveToRelative(0.39f, 0.39f, 1.03f, 0.39f, 1.41f, 0.0f)
			curveToRelative(0.39f, -0.39f, 0.39f, -1.03f, 0.0f, -1.41f)
			lineTo(18.36f, 16.95f)
			close()
			moveTo(19.42f, 5.99f)
			curveToRelative(0.39f, -0.39f, 0.39f, -1.03f, 0.0f, -1.41f)
			curveToRelative(-0.39f, -0.39f, -1.03f, -0.39f, -1.41f, 0.0f)
			lineToRelative(-1.06f, 1.06f)
			curveToRelative(-0.39f, 0.39f, -0.39f, 1.03f, 0.0f, 1.41f)
			reflectiveCurveToRelative(1.03f, 0.39f, 1.41f, 0.0f)
			lineTo(19.42f, 5.99f)
			close()
			moveTo(7.05f, 18.36f)
			curveToRelative(0.39f, -0.39f, 0.39f, -1.03f, 0.0f, -1.41f)
			curveToRelative(-0.39f, -0.39f, -1.03f, -0.39f, -1.41f, 0.0f)
			lineToRelative(-1.06f, 1.06f)
			curveToRelative(-0.39f, 0.39f, -0.39f, 1.03f, 0.0f, 1.41f)
			reflectiveCurveToRelative(1.03f, 0.39f, 1.41f, 0.0f)
			lineTo(7.05f, 18.36f)
			close()
		}
	}

val Icons.Rounded.DarkMode: ImageVector
	get() = materialIcon(name = "Rounded.DarkMode") {
		materialPath {
			moveTo(11.01f, 3.05f)
			curveTo(6.51f, 3.54f, 3.0f, 7.36f, 3.0f, 12.0f)
			curveToRelative(0.0f, 4.97f, 4.03f, 9.0f, 9.0f, 9.0f)
			curveToRelative(4.63f, 0.0f, 8.45f, -3.5f, 8.95f, -8.0f)
			curveToRelative(0.09f, -0.79f, -0.78f, -1.42f, -1.54f, -0.95f)
			curveToRelative(-0.84f, 0.54f, -1.84f, 0.85f, -2.91f, 0.85f)
			curveToRelative(-2.98f, 0.0f, -5.4f, -2.42f, -5.4f, -5.4f)
			curveToRelative(0.0f, -1.06f, 0.31f, -2.06f, 0.84f, -2.89f)
			curveTo(12.39f, 3.94f, 11.9f, 2.98f, 11.01f, 3.05f)
			close()
		}
	}