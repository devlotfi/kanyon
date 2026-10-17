package com.devlotfi.kanyon

import com.devlotfi.kanyon.schema.FieldSchema
import kotlin.reflect.KProperty

class ValidatedProperty<T>(var value: T, val schema: FieldSchema?) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
        value = newValue
    }

    fun validate(): String? {
        if (schema != null) return schema.validate(value)
        return null
    }
}