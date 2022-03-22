package ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable


private val lightColors = lightColors(
	primary = Primary,
	primaryVariant = PrimaryVariant,
	secondary = Secondary,
	secondaryVariant = SecondaryVariant,
	onError = OnError
)
/*private val darkColors = darkColors(
	primary = Primary,
	primaryVariant = PrimaryVariant,
	secondary = Secondary,
	secondaryVariant = SecondaryVariant,
	background = Color.Black,
	surface = Color.DarkGray,
	error = Error,
	onPrimary = OnPrimary,
	onSecondary = OnSecondary,
	onBackground = Color.White,
	onSurface = Color.White,
	onError = OnError,
)*/

@Suppress("FunctionName")
@Composable
fun AppTheme(content: @Composable () -> Unit) {
	MaterialTheme(
		colors = lightColors, content = content
	)
}