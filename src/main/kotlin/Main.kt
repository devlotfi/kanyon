package com.devlotfi.kanyon

import com.devlotfi.kanyon.schema.FieldSchema
import com.devlotfi.kanyon.schema.nullable

class User(
    firstName: String,
    lastName: String,
    address: String?,
    testa: String,
) : ValidatedObject() {
    val firstName by validatedProperty(firstName, FieldSchema().string().min(10, "error works").build())
    val lastName by validatedProperty(lastName)
    val address by validatedProperty(address, FieldSchema().string().nullable().build())
    val testa by validatedProperty(testa, FieldSchema().string().build())
}

enum class Options(lol1: String) {
    Option1("lol1")
}

fun main() {
    val validation = FieldSchema().string().min(2).max(10).notBlank().validateHandler(
        validationHandler = { value ->
            return@validateHandler value != "testa"
        },
        error = "Function works"
    ).nullable().build()
    println("Validation: ${validation.validate(null)}")

    val validation2 = FieldSchema().int().gt(10).multipleOf(5).build()
    FieldSchema().boolean().eq(true).build()
    println("Validation: ${validation2.validate(15)}")
    val lol = User(
        firstName = "Lol",
        lastName = "Lol",
        address = null,
        testa = "test"
    )
    println(lol.valid())
    println(lol.validate())

    val testa = null
    requireNotNull(testa)
}