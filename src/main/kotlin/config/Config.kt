package config

import config.arguments.*
import data.Id
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import kotlin.Boolean
import kotlin.String

interface CommandlineArgument
{
	val name: String
	val description: String
	val parameter: String
	val required: Boolean
}

sealed class Config(val configElement: CommandlineArgument): CommandlineArgument
{
	override val name get() = configElement.name
	override val description get() = configElement.description
	override val parameter get() = configElement.parameter
	override val required get() = configElement.required
	open val valueAsString get() = ""
	open val fullParameter get() = "-$parameter $valueAsString"

	abstract fun copy(): Config

	companion object
	{
		operator fun invoke(inString: String): Config
		{
			val parameter = inString.split(" ")[0]
			val withoutPrefix = if (inString.startsWith("$parameter ") && inString.length > parameter.length + 1) inString.removePrefix("$parameter ") else null
			return when
			{
				BooleanArgument.values().any { it.parameter == parameter } -> BooleanParameter(BooleanArgument.values().first { it.parameter == parameter }, withoutPrefix == "1")
				GpusArgument.values().any { it.parameter == parameter }    -> GpusParameter(GpusArgument.values().first { it.parameter == parameter }, withoutPrefix?.split(",")?.map { Id(it.toInt()) }?.toTypedArray() ?: emptyArray())
				NumberArgument.values().any { it.parameter == parameter }  -> NumberParameter(NumberArgument.values().first { it.parameter == parameter }, withoutPrefix?.toInt() ?: 0)
				StringArgument.values().any { it.parameter == parameter }  -> StringParameter(StringArgument.values().first { it.parameter == parameter }, withoutPrefix ?: "")
				WalletArgument.values().any { it.parameter == parameter }  -> WalletParameter(WalletArgument.values().first { it.parameter == parameter }, Wallet(withoutPrefix ?: "0x65cbddb4e7dd27009278d3160c8a5a4990d580d9"))
				else                                                       -> throw IllegalArgumentException("Given argument wasn't found")
			}
		}

		val possibleConfigs = (
				BooleanArgument.values().map { it.parameter } +
				GpusArgument.values().map { it.parameter } +
				NumberArgument.values().map { it.parameter } +
				StringArgument.values().map { it.parameter } +
				WalletArgument.values().map { it.parameter }
							  )
	}

	class BooleanParameter(val element: BooleanArgument, value: Boolean): Config(element)
	{
		var value by mutableStateOf(value)

		override val valueAsString get() = if (value) "1" else "0"

		override fun copy() = BooleanParameter(element, value)
	}

	class StringParameter(val element: StringArgument, value: String): Config(element)
	{
		var value by mutableStateOf(value)

		override val valueAsString get() = value
		override fun copy() = StringParameter(element, value)
	}

	class GpusParameter(val element: GpusArgument, value: Array<Id>): Config(element)
	{
		var value by mutableStateOf(value)

		override val valueAsString get() = value.joinToString(",")
		override fun copy() = GpusParameter(element, value)
	}

	class WalletParameter(val element: WalletArgument, value: Wallet): Config(element)
	{
		var value by mutableStateOf(value)

		override val valueAsString get() = value.toString()
		override fun copy() = WalletParameter(element, value)
	}

	class NumberParameter(val element: NumberArgument, value: Int): Config(element)
	{
		var value by mutableStateOf(value)

		override val valueAsString get() = value.toString()

		override fun copy() = NumberParameter(element, value)

		init
		{
			require(value in element.range) { "value is outside the allowed range" }
		}
	}
}

class SettingsConfig(config: Config, enabled: Boolean)
{
	val config by mutableStateOf(config.copy())
	var enabled by mutableStateOf(enabled)
}