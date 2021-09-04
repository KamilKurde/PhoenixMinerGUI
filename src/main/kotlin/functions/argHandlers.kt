package functions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Array<String>.ifArg(arg: String, block: () -> Unit) = if (arg in this) block() else Unit

fun Array<String>.ifArgCoroutine(arg: String, coroutineScope: CoroutineScope, block: suspend CoroutineScope.() -> Unit) {
	if (arg in this) coroutineScope.launch(block = block)
}

fun Array<String>.ifNoArg(arg: String, block: () -> Unit) = if (arg !in this) block() else Unit

fun Array<String>.ifNoArgCoroutine(arg: String, coroutineScope: CoroutineScope, block: suspend CoroutineScope.() -> Unit) {
	if (arg !in this) coroutineScope.launch(block = block)
}