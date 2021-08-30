package config

import tryOrNull

class Parameters(private var parameters: MutableList<Config> = mutableListOf()): MutableCollection<Config>
{
	constructor(vararg parameters: Config): this(parameters.toMutableList())
	constructor(vararg parameters: String): this(parameters.mapNotNull { tryOrNull { Config(it) } }.toMutableList())

	fun copy() = Parameters(parameters.map { it.copy() }.toMutableList())

	fun toStringArray(): Array<String> = parameters.map { "${it.parameter} ${it.valueAsString}" }.toTypedArray()

	override val size: Int
		get() = parameters.size

	fun contains(parameter: String) = parameters.any { it.parameter == parameter }

	override fun contains(element: Config) = contains(element.parameter)

	override fun containsAll(elements: Collection<Config>) = elements.all { contains(it) }

	override fun isEmpty() = parameters.isEmpty()

	override fun add(element: Config): Boolean
	{
		if (contains(element))
		{
			return false
		}
		return parameters.add(element)
	}

	override fun addAll(elements: Collection<Config>) = parameters.addAll(elements)

	override fun clear() = parameters.clear()

	//fun remove(index: Int) = parameters.removeAt(index)

	override fun remove(element: Config) = parameters.removeAll { it.parameter == element.parameter }

	override fun removeAll(elements: Collection<Config>) = parameters.removeAll(elements)

	override fun retainAll(elements: Collection<Config>) = parameters.retainAll(elements)

	override fun iterator(): MutableIterator<Config> = parameters.iterator()

	operator fun get(index: Int): Config = parameters[index]

	operator fun get(parameter: String): Config? = parameters.firstOrNull { it.parameter == parameter }

	operator fun get(parameter: CommandlineArgument) = get(parameter.parameter)

	operator fun set(index: Int, config: Config)
	{
		if (!contains(config.parameter))
		{
			parameters[index] = config
		}
	}

	operator fun set(parameter: String, config: Config)
	{
		val index = parameters.indexOfFirst { it.parameter == parameter }
		if (index >= 0)
		{
			parameters[index] = config
		}
		else
		{
			parameters.add(config)
		}
	}

	fun allConfigs(): Array<SettingsConfig>
	{
		val allConfigs = Config.possibleConfigs.map { commandLineArgument ->
			val config = parameters.firstOrNull { it.parameter == commandLineArgument }
			if (config != null) SettingsConfig(config, true) else SettingsConfig(Config(commandLineArgument), false)
		}.sortedBy { it.config.name }
		val required = allConfigs.filter { it.config.required }
		val optional = allConfigs.filter { !it.config.required }
		return (required + optional).toTypedArray()
	}
}