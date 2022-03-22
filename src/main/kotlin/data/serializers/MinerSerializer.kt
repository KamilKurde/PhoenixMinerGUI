package data.serializers

import config.Arguments
import data.Id
import data.miner.Miner
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class MinerSerializer : KSerializer<Miner> {
	
	@Serializable
	private class MinerData(val name: String = "", val id: Id = Id(1), val mineOnStartup: Boolean = false, val settings: Array<String> = emptyArray()) {
		
		constructor(miner: Miner) : this(miner.name, miner.id, miner.mineOnStartup, miner.arguments.toStringArray())
		
		fun toMiner() = Miner(name, id, mineOnStartup, Arguments(*settings))
	}
	
	override fun deserialize(decoder: Decoder): Miner = MinerData.serializer().deserialize(decoder).toMiner()
	
	override val descriptor: SerialDescriptor
		get() = MinerData.serializer().descriptor
	
	override fun serialize(encoder: Encoder, value: Miner) = MinerData.serializer().serialize(encoder, MinerData(value))
}