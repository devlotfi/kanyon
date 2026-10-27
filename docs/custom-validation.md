[← Home](../README.md)

# Custom validation

## Custom validation function: `validateHandler`

```kotlin
import com.devlotfi.kanyon.validateHandler

fun <T, S : BaseSchema<T, S>> S.validateHandler(
    validationHandler: (value: T) -> Boolean,
    error: String
): S
```

| Param | Description |
|---|---|
| `validationHandler` | Receives the value (typed as the schema's `T`) and returns **`true` if valid**, `false` otherwise. |
| `error` | **Required** error message used when the handler returns `false`. |

It works on **every** schema (`StringSchema`, `IntSchema`, `IsSchema<E>`, `AnySchema`, `FieldSchema`, ...). On a `FieldSchema` directly the value is `Any?`, on typed schemas it is already narrowed (e.g. `String`).

> `error` is the **last** parameter, so you cannot use trailing-lambda syntax. Write `validateHandler({ ... }, "message")` or use named arguments: `validateHandler(validationHandler = { ... }, error = "message")`.

```kotlin
val username = FieldSchema()
    .string()
    .min(3)
    .validateHandler({ !it.contains(' ') }, "Username must not contain spaces")
    .validateHandler({ it.lowercase() == it }, "Username must be lowercase")
    .build()

username.validate("john doe") // "Username must not contain spaces"
username.validate("John")     // "Username must be lowercase"
username.validate("john")     // null
```

If the handler may throw (e.g. parsing), guard it yourself; exceptions are not caught by the library.

## Custom case: `BuilderCase`

For reusable rules, subclass `BuilderCase<T>`:

```kotlin
abstract class BuilderCase<in T> {
    abstract fun validate(value: T): String?   // null = valid, otherwise error
    abstract fun errorFactory(): String        // default error message
    abstract val userError: String?            // user-supplied override (nullable)
    fun getError(): String = userError ?: errorFactory()
}
```

Attach it with `schema.case(...)`. Wrapping it in an extension function gives you a first-class rule:

```kotlin
import com.devlotfi.kanyon.BuilderCase
import com.devlotfi.kanyon.schema.StringSchema

class StartsWithCase(
    val prefix: String,
    override val userError: String? = null
) : BuilderCase<String>() {
    override fun validate(value: String): String? =
        if (value.startsWith(prefix)) null else getError()

    override fun errorFactory(): String = "Value must start with \"$prefix\""
}

fun StringSchema.startsWith(prefix: String, error: String? = null) =
    case(StartsWithCase(prefix, error))

StringSchema().startsWith("kn_").validate("abc")    // Value must start with "kn_"
StringSchema().startsWith("kn_").validate("kn_abc") // null
```

**Next:** [Validating a single value](single-value.md)
