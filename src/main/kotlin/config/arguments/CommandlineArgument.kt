package config.arguments

interface CommandlineArgument {
	
	val name: String
	val description: String
	val parameter: String
	val required: Boolean
}