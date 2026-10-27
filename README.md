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

- [Docs](https://github.com/devlotfi/kanyon/blob/master/docs/intro.md)
- [llms.txt](https://devlotfi.github.io/kanyon/llms.txt)
