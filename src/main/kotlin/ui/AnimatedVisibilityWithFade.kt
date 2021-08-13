package ui

import androidx.compose.animation.*
import androidx.compose.runtime.Composable


@ExperimentalAnimationApi
@Composable
fun AnimatedVisibilityWithFade(visible: Boolean, content: @Composable AnimatedVisibilityScope.() -> Unit) = AnimatedVisibility(visible, enter = fadeIn(), exit = fadeOut(), content = content)