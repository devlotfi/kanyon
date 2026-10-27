package com.devlotfi.kanyon.schema

import com.devlotfi.kanyon.BuilderCase

sealed class FieldCases {
    class AnyCase(
        val schema: AnySchema,
        override val userError: String? = null
    ) : BuilderCase<Any?>() {
        override fun validate(value: Any?): String? {
            if (value is Any) {
                return schema.validate(value)
            }
            return getError()
        }

        override fun errorFactory(): String {
            return "Value must be any"
        }
    }

    class IsCase<E>(
        val typeName: String,
        val isInstance: (Any?) -> Boolean,
        val schema: IsSchema<E>,
        override val userError: String? = null
    ) : BuilderCase<Any?>() {
        override fun validate(value: Any?): String? {
            if (isInstance(value)) {
                @Suppress("UNCHECKED_CAST")
                return schema.validate(value as E)
            }
            return getError()
        }

        override fun errorFactory(): String {
            return "Value must be a $typeName"
        }
    }

    class EnumCase<E : Enum<E>>(
        val typeName: String,
        val isInstance: (Any?) -> Boolean,
        val schema: EnumSchema<E>,
        override val userError: String? = null
    ) : BuilderCase<Any?>() {
        override fun validate(value: Any?): String? {
            if (isInstance(value)) {
                @Suppress("UNCHECKED_CAST")
                return schema.validate(value as E)
            }
            return getError()
        }

        override fun errorFactory(): String {
            return "Value must be a $typeName"
        }
    }

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

    class ByteCase(
        val schema: ByteSchema,
        override val userError: String? = null
    ) : BuilderCase<Any?>() {
        override fun validate(value: Any?): String? {
            if (value is Byte) {
                return schema.validate(value)
            }
            return getError()
        }

        override fun errorFactory(): String {
            return "Value must be a byte"
        }
    }

    class ShortCase(
        val schema: ShortSchema,
        override val userError: String? = null
    ) : BuilderCase<Any?>() {
        override fun validate(value: Any?): String? {
            if (value is Short) {
                return schema.validate(value)
            }
            return getError()
        }

        override fun errorFactory(): String {
            return "Value must be a short"
        }
    }

    class IntCase(
        val schema: IntSchema,
        override val userError: String? = null
    ) : BuilderCase<Any?>() {
        override fun validate(value: Any?): String? {
            if (value is Int) {
                return schema.validate(value)
            }
            return getError()
        }

        override fun errorFactory(): String {
            return "Value must be an int"
        }
    }

    class LongCase(
        val schema: LongSchema,
        override val userError: String? = null
    ) : BuilderCase<Any?>() {
        override fun validate(value: Any?): String? {
            if (value is Long) {
                return schema.validate(value)
            }
            return getError()
        }

        override fun errorFactory(): String {
            return "Value must be a long"
        }
    }

    class FloatCase(
        val schema: FloatSchema,
        override val userError: String? = null
    ) : BuilderCase<Any?>() {
        override fun validate(value: Any?): String? {
            if (value is Float) {
                return schema.validate(value)
            }
            return getError()
        }

        override fun errorFactory(): String {
            return "Value must be a float"
        }
    }

    class DoubleCase(
        val schema: DoubleSchema,
        override val userError: String? = null
    ) : BuilderCase<Any?>() {
        override fun validate(value: Any?): String? {
            if (value is Double) {
                return schema.validate(value)
            }
            return getError()
        }

        override fun errorFactory(): String {
            return "Value must be a double"
        }
    }

    class BooleanCase(
        val schema: BooleanSchema,
        override val userError: String? = null
    ) : BuilderCase<Any?>() {
        override fun validate(value: Any?): String? {
            if (value is Boolean) {
                return schema.validate(value)
            }
            return getError()
        }

        override fun errorFactory(): String {
            return "Value must be a boolean"
        }
    }
}

class FieldSchema(
    override val root: FieldSchema? = null
) : BaseSchema<Any?, FieldSchema>() {
    override val self get() = this
    var isNullable = false

    fun any(error: String? = null): AnySchema =
        AnySchema(this).also {
            case(FieldCases.AnyCase(it, userError = error))
        }

    inline fun <reified E : Any> isOf(error: String? = null): IsSchema<E> =
        IsSchema<E>(this).also {
            case(FieldCases.IsCase(E::class.simpleName ?: "any", { v -> v is E }, it, userError = error))
        }

    inline fun <reified E : Enum<E>> enumOf(error: String? = null): EnumSchema<E> =
        EnumSchema<E>(this).also {
            case(FieldCases.EnumCase(E::class.simpleName ?: "enum", { v -> v is E }, it, userError = error))
        }

    fun string(error: String? = null): StringSchema =
        StringSchema(this).also {
            case(FieldCases.StringCase(it, userError = error))
        }

    fun byte(error: String? = null): ByteSchema =
        ByteSchema(this).also {
            case(FieldCases.ByteCase(it, userError = error))
        }

    fun short(error: String? = null): ShortSchema =
        ShortSchema(this).also {
            case(FieldCases.ShortCase(it, userError = error))
        }

    fun int(error: String? = null): IntSchema =
        IntSchema(this).also {
            case(FieldCases.IntCase(it, userError = error))
        }

    fun long(error: String? = null): LongSchema =
        LongSchema(this).also {
            case(FieldCases.LongCase(it, userError = error))
        }

    fun float(error: String? = null): FloatSchema =
        FloatSchema(this).also {
            case(FieldCases.FloatCase(it, userError = error))
        }

    fun double(error: String? = null): DoubleSchema =
        DoubleSchema(this).also {
            case(FieldCases.DoubleCase(it, userError = error))
        }

    fun boolean(error: String? = null): BooleanSchema =
        BooleanSchema(this).also {
            case(FieldCases.BooleanCase(it, userError = error))
        }

    override fun validate(value: Any?): String? {
        if (value == null && isNullable) return null
        return super.validate(value)
    }
}