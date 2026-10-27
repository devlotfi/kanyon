package com.devlotfi.kanyon.schema

class IsSchema<T>(
    override val root: FieldSchema? = null
) : BaseSchema<T, IsSchema<T>>() {
    override val self get() = this
}