package data.miner

enum class MinerStatus {
	Offline,
	Waiting,
	Launching,
	Connecting,
	DagBuilding {
		
		override fun toString() = "Building DAG"
	},
	Running,
	Closing,
	ConnectionError {
		
		override fun toString() = "Connection Error"
	},
	ProgramError {
		
		override fun toString() = "Miner Error"
	},
}