package data

import Incrementable

class Time(minutes: Int) {
	
	var minutes by Incrementable(minutes)
	
	var totalTime
		get() = "${(minutes / 60)}:${(minutes % 60).toString().padStart(2, '0')}"
		set(value) {
			minutes = value.split(":").let { (it[0].toInt() * 60) + it[1].toInt() }
		}
	
	override fun toString() = totalTime
	
	companion object {
		
		operator fun invoke(time: String) = Time(0).apply {
			totalTime = time
		}
	}
}