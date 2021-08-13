package config.arguments

import config.CommandlineArgument

enum class NumberArgument(override val  description: String, override val  parameter: String, override val required: Boolean, val range: IntRange): CommandlineArgument
{
	Cdm("Selects the level of support of the CDM remote networking: 0 - disabled (recommended if you don't know what CDM is), 1 - read-only (this is the default), 2 - full (only use on secure connections)", "cdm", false, 0..2),
	Ttli("Lower GPU usage when GPU temperature is above given deg C. The default value is 0, which means do not lower the usage regardless of the GPU temperature", "ttli", false, 0..100)
}