package data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object IdSerializer: KSerializer<Id>
{
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Id", PrimitiveKind.INT)

	override fun deserialize(decoder: Decoder) = Id(decoder.decodeInt())

	override fun serialize(encoder: Encoder, value: Id)
	{
		encoder.encodeInt(value.toInt())
	}
}


@Suppress("EqualsOrHashCode")
@Serializable(with = IdSerializer::class)
class Id(val value: Int)
{
	init
	{
		require(value > 0) { "Id in phoenix miner cannot be lower than 1" }
	}

	fun toInt() = value

	override fun toString() = value.toString()

	override fun equals(other: Any?) = when (other)
	{
		is Id  -> value == other.value
		is Int -> value == other
		else   -> false
	}
}