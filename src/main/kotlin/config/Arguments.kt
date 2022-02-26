package config

import config.arguments.CommandlineArgument
import functions.tryOrNull

class Arguments(private var arguments: MutableList<Option> = mutableListOf()) : MutableCollection<Option> {
	constructor(vararg parameters: Option) : this(parameters.toMutableList())
	constructor(vararg parameters: String) : this(parameters.mapNotNull { tryOrNull { Option(it) } }.toMutableList())
	
	fun copy() = Arguments(arguments.map { it.copy() }.toMutableList())
	
	fun toStringArray(): Array<String> = arguments.map { "${it.parameter} ${it.valueAsString}" }.toTypedArray()
	
	override val size: Int
		get() = arguments.size
	
	fun contains(parameter: String) = arguments.any { it.parameter == parameter }
	
	override fun contains(element: Option) = contains(element.parameter)
	
	override fun containsAll(elements: Collection<Option>) = elements.all { contains(it) }
	
	override fun isEmpty() = arguments.isEmpty()
	
	override fun add(element: Option): Boolean {
		if (contains(element)) {
			return false
		}
		return arguments.add(element)
	}
	
	override fun addAll(elements: Collection<Option>) = arguments.addAll(elements)
	
	override fun clear() = arguments.clear()
	
	//fun remove(index: Int) = parameters.removeAt(index)
	
	override fun remove(element: Option) = arguments.removeAll { it.parameter == element.parameter }
	
	override fun removeAll(elements: Collection<Option>) = arguments.removeAll(elements)
	
	override fun retainAll(elements: Collection<Option>) = arguments.retainAll(elements)
	
	override fun iterator(): MutableIterator<Option> = arguments.iterator()
	
	operator fun get(index: Int): Option = arguments[index]
	
	operator fun get(parameter: String): Option? = arguments.firstOrNull { it.parameter == parameter }
	
	operator fun get(parameter: CommandlineArgument) = get(parameter.parameter)
	
	operator fun set(index: Int, option: Option) {
		if (!contains(option.parameter)) {
			arguments[index] = option
		}
	}
	
	operator fun set(parameter: String, option: Option) {
		val index = arguments.indexOfFirst { it.parameter == parameter }
		if (index >= 0) {
			arguments[index] = option
		} else {
			arguments.add(option)
		}
	}
	
	fun allConfigs(): List<OptionWrapper> = Option.possibleConfigs.map { commandLineArgument ->
		val config = arguments.firstOrNull { it.parameter == commandLineArgument }
		if (config != null) OptionWrapper(config, true) else OptionWrapper(Option(commandLineArgument))
	}.sortedWith(compareBy({ !it.config.required }, { it.config.name }))
}