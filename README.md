<p align="center">
  <img src="https://raw.githubusercontent.com/devlotfi/kanyon/master/github-assets/github-banner.png" alt="Kanyon">
</p>

# Kanyon

[![JitPack](https://jitpack.io/v/devlotfi/kanyon.svg)](https://jitpack.io/#devlotfi/kanyon)

Kanyon is a declarative, pure Kotlin validation library for individual values and object properties. Build reusable
schemas by composing validation rules; validation returns `null` when a value is valid, or a human-readable error
message when it is not.

## Features

- Fluent schemas for strings, numbers, booleans, enums, and custom types.
- Built-in rules for lengths, identifiers, network values, dates, encodings, financial data, tokens, and hashes.
- Nullable values, custom validation handlers, and custom error messages.
- Object-property validation with a map of field names to validation errors.
- Kotlin implementation without dependencies on `java.*`, `javax.*`, or Android APIs.

## Installation

Kanyon is published on [JitPack](https://jitpack.io/#devlotfi/kanyon). Add JitPack to the repositories used to resolve
dependencies in your root `settings.gradle.kts`:

## Quick start

Validate a single value with `FieldSchema`:

```kotlin
import com.devlotfi.kanyon.schema.FieldSchema

val emailSchema = FieldSchema()
    .string()
    .email()
    .build()

emailSchema.validate("alex@example.com") // null: valid
emailSchema.validate("not-an-email")     // "Invalid email address"
emailSchema.validate(42)                 // "Value must be a string"
```

For values that already have a known type, use a standalone typed schema:

```kotlin
import com.devlotfi.kanyon.schema.StringSchema

val error = StringSchema()
    .notBlank()
    .min(3)
    .validate("ab")

println(error) // Length must be at least 3
```

Validate object properties by extending `ValidatedObject`:

```kotlin
import com.devlotfi.kanyon.ValidatedObject
import com.devlotfi.kanyon.schema.FieldSchema

class SignUpForm : ValidatedObject() {
    var username by validatedProperty(
        "",
        FieldSchema().string().notBlank().min(3).build()
    )
    var email by validatedProperty(
        "",
        FieldSchema().string().email().build()
    )
}

val form = SignUpForm().apply {
    username = "ab"
    email = "not-an-email"
}

form.validate()
// {username=Length must be at least 3, email=Invalid email address}
form.valid() // false
```

## Documentation

- [Docs](https://github.com/devlotfi/kanyon/blob/main/docs/intro.md)
- [llms.txt](https://devlotfi.github.io/kanyon/llms.txt)

| Guide                                             | Topics                                                      |
|---------------------------------------------------|-------------------------------------------------------------|
| [Introduction](docs/intro.md)                     | Overview and quick-start examples                           |
| [Core concepts](docs/core-concepts.md)            | Schemas, validation cases, and error behavior               |
| [Building schemas](docs/building-schemas.md)      | Typed entry points, standalone schemas, and nullable values |
| [Common methods](docs/common-methods.md)          | Shared schema methods and rule composition                  |
| [Validating a single value](docs/single-value.md) | Single-value validation examples                            |
| [Object validation](docs/validated-object.md)     | Property delegates and object-level validation              |
| [Nested structures](docs/nested-structures.md)    | Nested objects, lists, and maps                             |
| [Custom validation](docs/custom-validation.md)    | Custom handlers and reusable validation cases               |
| [Errors and gotchas](docs/errors-and-gotchas.md)  | Exceptions, limitations, and behavior notes                 |

### Schema references

| Schema or rules          | Documentation                                                                                                                                                                      |
|--------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Any values               | [`AnySchema`](docs/any.md)                                                                                                                                                         |
| Custom types             | [`IsSchema`](docs/is.md)                                                                                                                                                           |
| Enums and allowed values | [Enums](docs/enum.md) · [Enums and `oneOf`](docs/enums-and-one-of.md)                                                                                                              |
| Numbers                  | [Numeric rules](docs/numeric.md)                                                                                                                                                   |
| Booleans                 | [`BooleanSchema`](docs/boolean.md)                                                                                                                                                 |
| Strings                  | [Length and content](docs/length-and-content.md) · [Identifiers](docs/identifiers.md)                                                                                              |
| String formats           | [Encodings](docs/encodings.md) · [Network](docs/network.md) · [Finance](docs/finance.md) · [Tokens and hashes](docs/tokens-and-hashes.md) · [Date and time](docs/date-and-time.md) |
| Combined examples        | [Dates, identifiers, and network values](docs/dates-and-identifiers.md)                                                                                                            |

For an index of the documentation in plain text, see [`docs/llms.txt`](docs/llms.txt).
