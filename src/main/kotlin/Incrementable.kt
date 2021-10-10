import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// Class which value cannot be lowered, if new value is lower than previous that means error occured and value isn't taking pre-error value into consideration
class Incrementable<T>(value: Int) : ReadWriteProperty<T, Int>
{
	private var current by mutableStateOf(value)
	private var old by mutableStateOf(0)
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