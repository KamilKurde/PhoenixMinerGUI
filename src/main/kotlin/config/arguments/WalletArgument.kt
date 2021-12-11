package config.arguments

import config.CommandlineArgument

enum class WalletArgument(override val description: String, override val parameter: String, override val required: Boolean) : CommandlineArgument {
	Wallet("Ethash wallet", "wal", true)
}