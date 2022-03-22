package data.miner

import Incrementable

class Shares(valid: Int, stale: Int, rejected: Int) {
	
	var valid by Incrementable(valid)
	var stale by Incrementable(stale)
	var rejected by Incrementable(rejected)
	override fun toString() = "$valid/$stale/$rejected"
	
	constructor(shares: Array<Int>) : this(shares[0], shares[1], shares[2])
}