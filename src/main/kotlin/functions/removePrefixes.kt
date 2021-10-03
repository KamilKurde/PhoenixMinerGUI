package functions

fun String.removeStrings(vararg strings: String): String
{
	var text = this
	strings.forEach {
		text = text.replace(it, "", true).trim()
	}
	return text
}