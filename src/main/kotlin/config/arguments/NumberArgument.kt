package config.arguments

import config.CommandlineArgument

enum class NumberArgument(override val description: String, override val parameter: String, override val required: Boolean, val range: IntRange) : CommandlineArgument {
	Ttli("Lower GPU usage when GPU temperature is above given deg C. The value when disabled is 0, which means do not lower the usage regardless of the GPU temperature", "ttli", false, 0..100)
}