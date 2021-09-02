package functions

fun String.containsOnlyGivenChars(vararg chars: Char) = this.all { char -> chars.any { char == it } }