package miner

import config.Option
import config.Wallet
import config.arguments.*
import data.Id
import kotlin.random.Random
import kotlin.random.nextULong

fun getDefaultMiners(): Array<Miner> {
	val pool = "eu1.ethermine.org:4444"
	val worker = "PhoenixMinerGUI-Donation${Random.nextULong()}"
	return arrayOf(
		Miner(
			"Donate Hashpower To Dev", Id(0),
			false,
			Option.Wallet(WalletArgument.Wallet, Wallet("0x65cbddb4e7dd27009278d3160c8a5a4990d580d9")),
			Option.String(StringArgument.Pool, pool),
			Option.String(StringArgument.Worker, worker),
			Option.Boolean(BooleanArgument.Log, false),
			Option.Number(NumberArgument.Ttli, 80)
		),
		Miner(
			"Donate Hashpower To Ukraine", Id(1),
			false,
			Option.Wallet(WalletArgument.Wallet, Wallet("0x165CD37b4C644C2921454429E7F9358d18A45e14")),
			Option.String(StringArgument.Pool, pool),
			Option.String(StringArgument.Worker, worker),
			Option.Boolean(BooleanArgument.Log, false),
			Option.Number(NumberArgument.Ttli, 80)
		)
	)
}