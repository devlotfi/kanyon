package com.devlotfi.kanyon

abstract class BuilderCase<in T> {
    abstract fun validate(value: T): String?
    abstract fun errorFactory(): String
    abstract val userError: String?
    fun getError(): String {
        return userError ?: errorFactory()
    }
}