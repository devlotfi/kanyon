# Kanyon

A small, declarative, **pure Kotlin** validation library (Kotlin Multiplatform friendly: no `java.*`, no `javax.*`, no Android APIs).

You describe the rules for a value with a fluent *schema*, and the library gives you back either `null` (valid) or a human-readable error message. Schemas can be used on single values, or attached to the properties of a `ValidatedObject` to validate whole objects at once.

```kotlin
val schema = FieldSchema().string().notBlank().min(3).max(20).build()

schema.validate("kanyon") // null  -> valid
schema.validate("ab")     // "Length must be at least 3"
schema.validate(42)       // "Value must be a string"
```

## Quick start

### Validate a single value

```kotlin
val error: String? = StringSchema().email().validate("not-an-email")
// "Invalid email address"
```

### Validate an object

```kotlin
class SignUpForm : ValidatedObject() {
    var username by validatedProperty("", FieldSchema().string().notBlank().min(3).max(20).build())
    var email by validatedProperty("", FieldSchema().string().email("Please enter a valid email").build())
    var age by validatedProperty(0, FieldSchema().int().gte(18, "You must be at least 18").build())
}

val form = SignUpForm()
form.username = "ab"
form.email = "nope"
form.age = 20

form.validate() // {username=Length must be at least 3, email=Please enter a valid email}
form.valid()    // false
```

## Documentation

### Fundamentals

| Page | Contents |
|---|---|
| [Core concepts](docs/core-concepts.md) | Schemas, cases, `FieldSchema`, how validation runs, error model |
| [Building schemas](docs/building-schemas.md) | `FieldSchema` entry points, standalone schemas, `build()`, `nullable()` |
| [Methods available on every schema](docs/common-methods.md) | `validate`, `build`, `case`, `oneOf`, `notOneOf`, `nullable`, `validateHandler` |

### Schema reference

| Page | Contents |
|---|---|
| [Schema overview](docs/schemas/README.md) | Index of all schemas |
| [`AnySchema`](docs/schemas/any.md) | Any non-null value |
| [`IsSchema`](docs/schemas/is.md) | Type checks for custom classes, lists, maps, nested objects |
| [`EnumSchema`](docs/schemas/enum.md) | Enum instances and ordering |
| [Numeric schemas](docs/schemas/numeric.md) | `gt`, `gte`, `lt`, `lte`, `eq`, `multipleOf`, `even`, `odd` |
| [`BooleanSchema`](docs/schemas/boolean.md) | `eq` |
| [`StringSchema`](docs/schemas/string/README.md) | Index of all string rules |
| ↳ [Length and content](docs/schemas/string/length-and-content.md) | `notBlank`, `min`, `max`, `length`, `regex` |
| ↳ [Encodings](docs/schemas/string/encodings.md) | `base64`, `base64url` |
| ↳ [Identifiers](docs/schemas/string/identifiers.md) | `email`, `uuid`, `hexColor`, `url` |
| ↳ [Phone and network](docs/schemas/string/network.md) | `e164`, `ipv4`, `ipv6`, `cidrv4`, `cidrv6`, `mac` |
| ↳ [Finance](docs/schemas/string/finance.md) | `creditCard`, `currencyCode`, `iban` |
| ↳ [Tokens and hashes](docs/schemas/string/tokens-and-hashes.md) | `jwt`, `hash` |
| ↳ [Date and time](docs/schemas/string/date-and-time.md) | `isoDate`, `isoTime`, `isoDateTime` |

### Objects and customization

| Page | Contents |
|---|---|
| [`ValidatedObject`](docs/validated-object.md) | `validatedProperty`, `validate()`, `valid()`, cross-field validation |
| [Custom validation](docs/custom-validation.md) | `validateHandler` and custom `BuilderCase` rules |

### Guides

| Page | Contents |
|---|---|
| [Examples](docs/examples/README.md) | Single values, custom functions, objects, nested structures, and more |
| [Errors, exceptions and gotchas](docs/errors-and-gotchas.md) | Exceptions thrown when building schemas, behavior notes |

**Next:** [Core concepts](core-concepts.md)
