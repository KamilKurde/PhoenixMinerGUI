package data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object IdSerializer : KSerializer<Id> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Id", PrimitiveKind.INT)

	override fun deserialize(decoder: Decoder) = Id(decoder.decodeInt())

	override fun serialize(encoder: Encoder, value: Id) {
		encoder.encodeInt(value.value)
	}
}


@Suppress("EqualsOrHashCode")
@JvmInline
@Serializable(IdSerializer::class)
value class Id(val value: Int) {
	init {
		require(value >= 0) { "Id in phoenix miner cannot be lower than 0" }
	}

	override fun toString() = value.toString()
}