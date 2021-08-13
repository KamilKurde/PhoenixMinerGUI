package config.arguments

import config.CommandlineArgument

enum class GpusArgument(override val  description: String, override val  parameter: String, override val required: Boolean): CommandlineArgument
{
	Gpus("Use only the specified GPUs", "gpus", false)
}