package config.arguments

import config.CommandlineArgument

enum class BooleanArgument(override val description: String, override val parameter: String, override val required: Boolean) : CommandlineArgument {
	Log("Selects the log file mode: disabled, no log file will be written; enabled, write log file", "log", true)
}