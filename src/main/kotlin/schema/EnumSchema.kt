package com.devlotfi.kanyon.schema

class EnumSchema<E : Enum<E>>(
    override val root: FieldSchema? = null
) : ComparableSchema<E, EnumSchema<E>>() {
    override val self get() = this
}