package com.devlotfi.kanyon.schema

class AnySchema(
    override val root: FieldSchema? = null
) : BaseSchema<Any, AnySchema>() {
    override val self get() = this
}