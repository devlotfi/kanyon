package com.devlotfi.kanyon.schema

import com.devlotfi.kanyon.BuilderCase

sealed class StringCases {
    class NotBlankCase(
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            return if (value.isNotBlank()) null else getError()
        }

        override fun errorFactory(): String {
            return "Value must not be blank"
        }
    }

    class MinCase(
        val min: Int,
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            return if (value.length >= min) null else getError()
        }

        override fun errorFactory(): String {
            return "Length must be at least $min"
        }
    }

    class MaxCase(
        val max: Int,
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            return if (value.length <= max) null else getError()
        }

        override fun errorFactory(): String {
            return "Length must be at most $max"
        }
    }
}

class StringSchema(
    override val root: FieldSchema? = null
) : BaseSchema<String, StringSchema>() {
    override val self get() = this

    fun notBlank(error: String? = null) = case(StringCases.NotBlankCase(userError = error))
    fun min(min: Int, error: String? = null) = case(StringCases.MinCase(min = min, userError = error))
    fun max(max: Int, error: String? = null) = case(StringCases.MaxCase(max = max, userError = error))
}