package com.devlotfi.kanyon.schema

import com.devlotfi.kanyon.BuilderCase

class StringCase(
    val schema: StringSchema,
    override val userError: String? = null
) : BuilderCase<Any?>() {
    override fun validate(value: Any?): String? {
        if (value is String) {
            return schema.validate(value)
        }
        return getError()
    }

    override fun errorFactory(): String {
        return "Value must be a string"
    }
}

class FieldSchema() : BaseSchema<Any?, FieldSchema>(root = null) {
    override val self get() = this
    var isNullable = false

    fun string(error: String? = null): StringSchema {
        val stringSchema = StringSchema(this)
        val stringCase = StringCase(stringSchema, userError = error)
        case(stringCase)
        return stringSchema
    }

    override fun validate(value: Any?): String? {
        if (value == null && isNullable) return null
        return super.validate(value)
    }
}