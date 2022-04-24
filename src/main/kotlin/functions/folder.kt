package functions

import java.io.File
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun folder(path: String): ReadOnlyProperty<Any?, String> = object : ReadOnlyProperty<Any?, String> {
	val folder by lazy {
		if (!File(path).exists()) {
			File(path).mkdirs()
		}
		path
	}
	
	override fun getValue(thisRef: Any?, property: KProperty<*>) = folder
}