package com.devlotfi.kanyon.schema

import com.devlotfi.kanyon.BuilderCase

fun <T, S : BaseSchema<T, S>> S.nullable(): S {
    if (root != null) {
        root.isNullable = true
    } else if (this is FieldSchema) {
        this.isNullable = true
    }
    return self
}

abstract class BaseSchema<T, S : BaseSchema<T, S>>(
    val root: FieldSchema? = null
) {
    protected val cases = mutableListOf<BuilderCase<T>>()
    abstract val self: S

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
}