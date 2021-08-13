package miner

class Shares(valid: Int, stale: Int, rejected: Int)
{
	var valid: Int = valid
		get() = field + oldValid
		set(value)
		{
			if (value < field)
			{
				oldValid += field
			}
			field = value
		}
	var stale: Int = stale
		get() = field + oldStale
		set(value)
		{
			if (value < field)
			{
				oldStale += field
			}
			field = value
		}
	var rejected: Int = rejected
		get() = field + oldRejected
		set(value)
		{
			if (value < field)
			{
				oldRejected += field
			}
			field = value
		}
	private var oldValid: Int = 0
	private var oldStale: Int = 0
	private var oldRejected: Int = 0
	override fun toString() = "$valid/$stale/$rejected"
	constructor(shares: Array<Int>): this(shares[0], shares[1], shares[2])
	//constructor(fromString: String): this(fromString.split("/").map { it.toInt() }.toTypedArray())
}