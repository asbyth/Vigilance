package club.sk1er.vigilance.data

import club.sk1er.vigilance.Vigilant
import java.lang.reflect.Field
import java.util.function.Consumer

data class PropertyData(val property: PropertyAttributes, val value: PropertyValue, val instance: Vigilant) {
    fun getDataType() = property.type
    var action: ((Any?) -> Unit)? = null

    inline fun <reified T> getValue(): T {
        return value.getValue(instance) as T
    }

    fun getAsAny(): Any? = value.getValue(instance)

    fun getAsBoolean(): Boolean = getValue()

    fun <T> getAs(clazz: Class<T>) = clazz.cast(getAsAny())

    fun setValue(value: Any?) {
        if (value != null) action?.invoke(value)

        this.value.setValue(value, instance)

        instance.markDirty()
    }

    fun setCallbackConsumer(callback: Consumer<Any?>) {
        this.action = callback::accept
    }

    companion object {
        fun fromField(property: Property, field: Field, instance: Vigilant): PropertyData {
            return PropertyData(
                PropertyAttributes.fromPropertyAnnotation(property),
                FieldBackedPropertyValue(field),
                instance
            )
        }

        fun fromField(propertyAttributes: PropertyAttributes, field: Field, instance: Vigilant): PropertyData {
            return PropertyData(
                propertyAttributes,
                FieldBackedPropertyValue(field),
                instance
            )
        }

        fun withValue(property: Property, obj: Any?, instance: Vigilant): PropertyData {
            return PropertyData(
                PropertyAttributes.fromPropertyAnnotation(property),
                ValueBackedPropertyValue(obj),
                instance
            )
        }
    }
}

abstract class PropertyValue {
    abstract fun getValue(instance: Vigilant): Any?
    abstract fun setValue(value: Any?, instance: Vigilant)
}

class FieldBackedPropertyValue(internal val field: Field) : PropertyValue() {
    override fun getValue(instance: Vigilant): Any? {
        return field.get(instance)
    }

    override fun setValue(value: Any?, instance: Vigilant) {
        if (value is Double && field.type == Float::class.java) {
            field.set(instance, value.toFloat())
        } else {
            field.set(instance, value)
        }
    }
}

class ValueBackedPropertyValue(private var obj: Any?) : PropertyValue() {
    override fun getValue(instance: Vigilant): Any? {
        return obj
    }

    override fun setValue(value: Any?, instance: Vigilant) {
        obj = value
    }
}