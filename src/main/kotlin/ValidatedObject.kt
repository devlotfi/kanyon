package com.devlotfi.kanyon

import com.devlotfi.kanyon.schema.FieldSchema
import kotlin.properties.PropertyDelegateProvider

abstract class ValidatedObject() {
    private val properties = mutableMapOf<String, ValidatedProperty<*>>()

    fun <T> validatedProperty(initial: T, schema: FieldSchema? = null) =
        PropertyDelegateProvider<ValidatedObject, ValidatedProperty<T>> { thisRef, property ->
            ValidatedProperty(initial, schema).also { properties[property.name] = it }
        }

    open fun validate(): Map<String, String> {
        val errorMap = mutableMapOf<String, String>()
        properties.mapNotNull { (name, property) ->
            val result = property.validate()
            if (result != null) {
                errorMap[name] = result
            }
        }
        return errorMap
    }

    fun valid(): Boolean {
        return validate().isEmpty()
    }
}