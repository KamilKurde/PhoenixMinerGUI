package data

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Id(val value: Int)
{
	init
	{
		require(value > 0){"data.Id in phoenix miner cannot be lower than 1"}
	}

	fun toInt() = value

	override fun toString() = value.toString()
}