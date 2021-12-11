package config.arguments

import config.CommandlineArgument

enum class StringArgument(override val description: String, override val parameter: String, override val required: Boolean) : CommandlineArgument {
	Pool("Ethash pool address", "pool", true),
	Password("Ethash password", "pass", false),
	Worker("Ethash worker name", "worker", true),
}