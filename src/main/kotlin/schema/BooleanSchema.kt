package com.devlotfi.kanyon.schema

import com.devlotfi.kanyon.BuilderCase

sealed class BooleanCases {
    class EqCase(
        val eq: Boolean,
        override val userError: String? = null
    ) : BuilderCase<Boolean>() {
        override fun validate(value: Boolean): String? {
            return if (value == eq) null else getError()
        }

        override fun errorFactory(): String {
            return "Value must be $eq"
        }
    }
}

class BooleanSchema(
    override val root: FieldSchema? = null
) : BaseSchema<Boolean, BooleanSchema>() {
    override val self get() = this

    fun eq(eq: Boolean, error: String? = null) = case(BooleanCases.EqCase(eq = eq, userError = error))
}