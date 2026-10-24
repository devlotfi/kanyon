package com.devlotfi.kanyon.schema

import com.devlotfi.kanyon.BuilderCase

class MultipleOfCase(
    val multipleOf: Int,
    override val userError: String? = null
) : BuilderCase<Int>() {
    override fun validate(value: Int): String? {
        return if (value % multipleOf == 0) null else getError()
    }

    override fun errorFactory(): String {
        return "Value must be a multiple of $multipleOf"
    }
}

class EvenCase(
    override val userError: String? = null
) : BuilderCase<Int>() {
    override fun validate(value: Int): String? {
        return if (value % 2 == 0) null else getError()
    }

    override fun errorFactory(): String {
        return "Value must be even"
    }
}

class OddCase(
    override val userError: String? = null
) : BuilderCase<Int>() {
    override fun validate(value: Int): String? {
        return if (value % 2 != 0) null else getError()
    }

    override fun errorFactory(): String {
        return "Value must be odd"
    }
}

class IntSchema(
    override val root: FieldSchema? = null
) : ComparableSchema<Int, IntSchema>() {
    override val self get() = this

    fun multipleOf(multipleOf: Int, error: String? = null) =
        case(MultipleOfCase(multipleOf = multipleOf, userError = error))

    fun even(error: String? = null) =
        case(EvenCase(userError = error))

    fun odd(error: String? = null) =
        case(OddCase(userError = error))
}

class ByteSchema(
    override val root: FieldSchema? = null
) : ComparableSchema<Byte, ByteSchema>() {
    override val self get() = this
}

class ShortSchema(
    override val root: FieldSchema? = null
) : ComparableSchema<Short, ShortSchema>() {
    override val self get() = this
}

class LongSchema(
    override val root: FieldSchema? = null
) : ComparableSchema<Long, LongSchema>() {
    override val self get() = this
}

class FloatSchema(
    override val root: FieldSchema? = null
) : ComparableSchema<Float, FloatSchema>() {
    override val self get() = this
}

class DoubleSchema(
    override val root: FieldSchema? = null
) : ComparableSchema<Double, DoubleSchema>() {
    override val self get() = this
}