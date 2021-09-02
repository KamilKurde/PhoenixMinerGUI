package config

import functions.containsOnlyGivenChars
import kotlinx.serialization.Serializable
import kotlin.String

@Serializable
@JvmInline
value class Wallet(val value: String)
{
	companion object{
		val possibleChars = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F')
	}
	init
	{
		require(value.startsWith("0x") && value.length == 42 && value.drop(2).containsOnlyGivenChars(*possibleChars)){"Given String doesn't conform Ethereum address pattern"}
	}

	override fun toString() = value
}