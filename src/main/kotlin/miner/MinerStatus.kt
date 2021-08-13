package miner

enum class MinerStatus
{
	Offline,
	Waiting,
	Connecting,
	DagBuilding
	{
		override fun toString() = "Building DAG"
	},
	Running,
	Error,
	Closing
}