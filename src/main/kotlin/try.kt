inline fun tryWithoutCatch(lambda: () -> Unit) = try{lambda()}catch (e: Exception){}
inline fun tryOrFalse(lambda: () -> Boolean) = try{lambda()}catch (e: Exception){false}
inline fun <T>tryOrNull(lambda: () -> T) = try { lambda()}catch (e: Exception){null}