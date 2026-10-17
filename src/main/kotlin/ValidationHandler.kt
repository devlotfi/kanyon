package com.devlotfi.kanyon

import com.devlotfi.kanyon.schema.BaseSchema

class ValidationHandler<T>(
    val validationHandler: (value: T) -> Boolean,
    override val userError: String? = null
) : BuilderCase<T>() {
    override fun validate(value: T): String? {
        return if (validationHandler(value)) null else getError()
    }

    override fun errorFactory(): String {
        return "Validation handler error"
    }
}

fun <T, S : BaseSchema<T, S>> S.validateHandler(
    validationHandler: (value: T) -> Boolean,
    error: String
): S {
    return case(ValidationHandler(validationHandler, error))
}