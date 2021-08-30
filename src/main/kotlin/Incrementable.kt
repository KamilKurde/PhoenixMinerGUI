import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// Class which value cannot be lowered, if new value is lower than previous that means error occured and value isn't taking pre-error value into consideration
class Incrementable<T> : ReadWriteProperty<T, Int>
{
	private var current = 0
	private var old = 0
	override fun getValue(thisRef: T, property: KProperty<*>) = current + old

	override fun setValue(thisRef: T, property: KProperty<*>, value: Int)
	{
		if (value < current)
		{
			old += current
		}
		current = value
	}
}