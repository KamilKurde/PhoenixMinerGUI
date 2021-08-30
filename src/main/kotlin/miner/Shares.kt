package miner

import Incrementable

class Shares(valid: Int, stale: Int, rejected: Int)
{
	var valid by Incrementable()
	var stale by Incrementable()
	var rejected by Incrementable()
	override fun toString() = "$valid/$stale/$rejected"
	constructor(shares: Array<Int>): this(shares[0], shares[1], shares[2])
	//constructor(fromString: String): this(fromString.split("/").map { it.toInt() }.toTypedArray())
}