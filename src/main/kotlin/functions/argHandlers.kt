package functions

fun Array<String>.ifArg(arg: String, block: () -> Unit) = if (arg in this) block() else Unit

suspend fun Array<String>.ifArgCoroutine(arg: String, block: suspend () -> Unit) {
	if (arg in this) block()
}

fun Array<String>.ifNoArg(arg: String, block: () -> Unit) = if (arg !in this) block() else Unit

suspend fun Array<String>.ifNoArgCoroutine(arg: String, block: suspend () -> Unit) {
	if (arg !in this) block()
}