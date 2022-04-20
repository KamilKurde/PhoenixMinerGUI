package functions

import androidx.compose.runtime.*

class SafeDerivedStateOf<T>(calculation: () -> T) : State<T> {
	
	private val state by derivedStateOf(calculation)
	override val value: T
		get() {
			while (true) {
				try {
					return state
				} catch (e: IllegalStateException) {
					continue
				}
			}
		}
}

fun <T> safeDerivedStateOf(calculation: () -> T) = SafeDerivedStateOf(calculation)