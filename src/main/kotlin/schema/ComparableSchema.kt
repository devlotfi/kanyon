package com.devlotfi.kanyon.schema

import com.devlotfi.kanyon.BuilderCase

sealed class ComparableCases {
    class GtCase<T : Comparable<T>>(
        val gt: T,
        override val userError: String? = null
    ) : BuilderCase<T>() {
        override fun validate(value: T): String? {
            return if (value > gt) null else getError()
        }

        override fun errorFactory(): String {
            return "Value must be > $gt"
        }
    }

    class GteCase<T : Comparable<T>>(
        val gte: T,
        override val userError: String? = null
    ) : BuilderCase<T>() {
        override fun validate(value: T): String? {
            return if (value >= gte) null else getError()
        }

        override fun errorFactory(): String {
            return "Value must be >= $gte"
        }
    }

    class LtCase<T : Comparable<T>>(
        val lt: T,
        override val userError: String? = null
    ) : BuilderCase<T>() {
        override fun validate(value: T): String? {
            return if (value < lt) null else getError()
        }

        override fun errorFactory(): String {
            return "Value must be < $lt"
        }
    }

    class LteCase<T : Comparable<T>>(
        val lte: T,
        override val userError: String? = null
    ) : BuilderCase<T>() {
        override fun validate(value: T): String? {
            return if (value <= lte) null else getError()
        }

        override fun errorFactory(): String {
            return "Value must be <= $lte"
        }
    }

    class EqCase<T : Comparable<T>>(
        val eq: T,
        override val userError: String? = null
    ) : BuilderCase<T>() {
        override fun validate(value: T): String? {
            return if (value == eq) null else getError()
        }

        override fun errorFactory(): String {
            return "Value must be == $eq"
        }
    }
}

abstract class ComparableSchema<T : Comparable<T>, S : ComparableSchema<T, S>>(
    override val root: FieldSchema? = null
) : BaseSchema<T, S>() {
    fun gt(gt: T, error: String? = null) = case(ComparableCases.GtCase(gt = gt, userError = error))
    fun gte(gte: T, error: String? = null) = case(ComparableCases.GteCase(gte = gte, userError = error))
    fun lt(lt: T, error: String? = null) = case(ComparableCases.LtCase(lt = lt, userError = error))
    fun lte(lte: T, error: String? = null) = case(ComparableCases.LteCase(lte = lte, userError = error))
    fun eq(eq: T, error: String? = null) = case(ComparableCases.EqCase(eq = eq, userError = error))
}