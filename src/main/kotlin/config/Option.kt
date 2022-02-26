package config

import androidx.compose.runtime.*
import config.arguments.*
import data.Id

sealed class Option(val commandlineArgument: CommandlineArgument) : CommandlineArgument {
	
	override val name get() = commandlineArgument.name
	override val description get() = commandlineArgument.description
	override val parameter get() = commandlineArgument.parameter
	override val required get() = commandlineArgument.required
	abstract val valueAsString: kotlin.String
	open val fullArgument get() = "-$parameter $valueAsString"
	
	abstract fun copy(): Option
	
	companion object {
		
		operator fun invoke(inString: kotlin.String): Option {
			val parameter = inString.split(" ")[0]
			val withoutPrefix = if (inString.startsWith("$parameter ") && inString.length > parameter.length + 1) inString.removePrefix("$parameter ") else null
			return when {
				BooleanArgument.values().any { it.parameter == parameter } -> Boolean(BooleanArgument.values().first { it.parameter == parameter }, withoutPrefix == "1")
				GpusArgument.values().any { it.parameter == parameter } -> Gpus(GpusArgument.values().first { it.parameter == parameter }, withoutPrefix?.split(",")?.map { Id(it.toInt()) }?.toTypedArray() ?: emptyArray())
				NumberArgument.values().any { it.parameter == parameter } -> Number(NumberArgument.values().first { it.parameter == parameter }, withoutPrefix?.toInt() ?: 0)
				StringArgument.values().any { it.parameter == parameter } -> String(StringArgument.values().first { it.parameter == parameter }, withoutPrefix ?: "")
				WalletArgument.values().any { it.parameter == parameter } -> Wallet(WalletArgument.values().first { it.parameter == parameter }, Wallet(withoutPrefix ?: "0x65cbddb4e7dd27009278d3160c8a5a4990d580d9"))
				else -> throw IllegalArgumentException("Given argument wasn't found")
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
	
	class Boolean(val element: BooleanArgument, value: kotlin.Boolean) : Option(element) {
		
		var value by mutableStateOf(value)
		
		override val valueAsString get() = if (value) "1" else "0"
		
		override fun copy() = Boolean(element, value)
	}
	
	class String(val element: StringArgument, value: kotlin.String) : Option(element) {
		
		var value by mutableStateOf(value)
		
		override val valueAsString get() = value
		override fun copy() = String(element, value)
	}
	
	class Gpus(val element: GpusArgument, value: Array<Id>) : Option(element) {
		
		var value by mutableStateOf(value)
		
		override val valueAsString get() = value.joinToString(",")
		override fun copy() = Gpus(element, value)
	}
	
	class Wallet(val element: WalletArgument, value: config.Wallet) : Option(element) {
		
		var value by mutableStateOf(value)
		
		override val valueAsString get() = value.toString()
		override fun copy() = Wallet(element, value)
	}
	
	class Number(val element: NumberArgument, value: Int) : Option(element) {
		
		var value by mutableStateOf(value)
		
		override val valueAsString get() = value.toString()
		
		override fun copy() = Number(element, value)
		
		init {
			require(value in element.range) { "value is outside the allowed range" }
		}
	}
}

open class OptionWrapper(option: Option, enabled: Boolean = option.required) {
	
	val config by mutableStateOf(option.copy())
	var enabled by mutableStateOf(enabled)
}