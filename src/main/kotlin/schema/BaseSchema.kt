package com.devlotfi.kanyon.schema

import com.devlotfi.kanyon.BuilderCase

class OneOfCase<T>(
    val values: Set<T>,
    override val userError: String? = null
) : BuilderCase<T>() {
    override fun validate(value: T): String? {
        return if (value in values) null else getError()
    }

    override fun errorFactory(): String {
        return "Value must be one of: $values"
    }
}

class NotOneOfCase<T>(
    val values: Set<T>,
    override val userError: String? = null
) : BuilderCase<T>() {
    override fun validate(value: T): String? {
        return if (value !in values) null else getError()
    }

    override fun errorFactory(): String {
        return "Value must not be one of: $values"
    }
}

fun <T, S : BaseSchema<T, S>> S.nullable(): S {
    val root = this.root
    if (root != null) {
        root.isNullable = true
    } else if (this is FieldSchema) {
        this.isNullable = true
    }
    return self
}

abstract class BaseSchema<T, S : BaseSchema<T, S>> {
    protected val cases = mutableListOf<BuilderCase<T>>()
    abstract val self: S
    abstract val root: FieldSchema?

    open fun validate(value: T): String? {
        cases.forEach {
            val result = it.validate(value)
            if (result != null) return result
        }
        return null
    }

    fun case(case: BuilderCase<T>): S {
        cases.add(case)
        return self
    }

    fun build(): FieldSchema {
        return root as FieldSchema
    }

    fun oneOf(values: Set<T>, error: String? = null): S {
        require(values.isNotEmpty()) { "oneOf needs at least one value" }
        return case(OneOfCase(values.toSet(), error))
    }

    fun notOneOf(values: Set<T>, error: String? = null): S =
        case(NotOneOfCase(values.toSet(), error))
}