package functions

fun String.containsOnlyGivenChars(vararg chars: Char) = this.all { it in chars }