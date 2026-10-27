# `ValidatedObject`

`ValidatedObject` lets you attach a schema to each property of a class and validate the whole object in one call.

```kotlin
import com.devlotfi.kanyon.ValidatedObject
import com.devlotfi.kanyon.schema.FieldSchema

class SignUpForm : ValidatedObject() {
    var username by validatedProperty(
        "",
        FieldSchema().string().notBlank().min(3).max(20).build()
    )
    var email by validatedProperty(
        "",
        FieldSchema().string().email("Please enter a valid email").build()
    )
    var age by validatedProperty(
        0,
        FieldSchema().int().gte(18, "You must be at least 18").build()
    )
}
```

## API

| Member | Description |
|---|---|
| `validatedProperty(initial: T, schema: FieldSchema? = null)` | Property delegate. Declare with `var x by validatedProperty(initialValue, schema)`. The property is registered under its **Kotlin property name**. With `schema = null` the property is stored but never produces an error. |
| `open fun validate(): Map<String, String>` | Validates every registered property (in declaration order) and returns a map of **property name → error message**. Valid properties are absent. An empty map means the object is valid. Only the first failing rule of each property is reported. |
| `fun valid(): Boolean` | `validate().isEmpty()`. Because it calls `validate()`, it respects overrides. |

## Reading and writing

The delegate behaves like a normal property, so the object is a plain mutable state holder:

```kotlin
val form = SignUpForm()
form.username = "ab"
form.email = "not-an-email"
form.age = 20

form.validate()
// {username=Length must be at least 3, email=Please enter a valid email}

form.valid() // false

form.username = "alice"
form.email = "alice@example.com"
form.validate() // {}
form.valid()    // true
```

## Nullable properties

The property type must be nullable and the schema must be `nullable()`:

```kotlin
class Profile : ValidatedObject() {
    var bio by validatedProperty<String?>(
        null,
        FieldSchema().string().max(200).nullable().build()
    )
}
```

## Cross-field validation (override `validate()`)

`validate()` is `open`. Call `super.validate()` and add your own entries:

```kotlin
class PasswordForm : ValidatedObject() {
    var password by validatedProperty("", FieldSchema().string().min(8).build())
    var confirm by validatedProperty("", FieldSchema().string().notBlank().build())

    override fun validate(): Map<String, String> {
        val errors = super.validate().toMutableMap()
        if ("confirm" !in errors && password != confirm) {
            errors["confirm"] = "Passwords do not match"
        }
        return errors
    }
}
```

`valid()` automatically takes the extra rule into account.

**Next:** [Custom validation](custom-validation.md)
