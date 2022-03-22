package data.serializers

import androidx.compose.ui.window.WindowPlacement
import data.Settings
import data.miner.Miner
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class SettingsSerializer : KSerializer<Settings> {
	
	@Serializable
	private class SettingsData constructor(
		val phoenixPath: String = "",
		val miners: Array<Miner> = emptyArray(),
		val width: Int = 720,
		val height: Int = 1280,
		val placement: WindowPlacement = WindowPlacement.Maximized,
		val positionX: Int = 0,
		val positionY: Int = 0,
		val darkMode: Boolean = true,
	) {
		
		fun toSettings() = Settings(
			phoenixPath,
			miners,
			width,
			height,
			placement,
			positionX,
			positionY,
			darkMode
		)
		
		constructor(settings: Settings) : this(
			settings.phoenixPath,
			settings.miners.toTypedArray(),
			settings.width,
			settings.height,
			settings.placement,
			settings.positionX,
			settings.positionY,
			settings.darkMode
		)
	}
	
	override fun deserialize(decoder: Decoder): Settings = SettingsData.serializer().deserialize(decoder).toSettings()
	
	override val descriptor: SerialDescriptor
		get() = SettingsData.serializer().descriptor
	
	override fun serialize(encoder: Encoder, value: Settings) = SettingsData.serializer().serialize(encoder, SettingsData(value))
}