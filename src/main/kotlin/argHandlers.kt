fun Array<String>.ifArg(arg: String, block: () -> Unit) = if (arg in this) block() else Unit

fun Array<String>.ifNoArg(arg: String, block: () -> Unit) = if (arg !in this) block() else Unit