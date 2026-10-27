# Core concepts

| Concept | Description |
|---|---|
| **Schema** | A fluent builder holding an ordered list of rules (`StringSchema`, `IntSchema`, `BooleanSchema`, ...). |
| **Case** | One single rule (`MinCase`, `EmailCase`, `GtCase`, ...). A case returns `null` when the value passes, or an error `String` when it fails. Every case extends `BuilderCase<T>`. |
| **`FieldSchema`** | The *entry point* for typed validation of untyped (`Any?`) values. It first checks the runtime type of the value, then delegates to the inner typed schema (`string()`, `int()`, ...). It also owns the `nullable` flag. |
| **`ValidatedObject`** | A base class whose properties can be declared with `validatedProperty(...)` so the whole object can be validated with `validate()` / `valid()`. |

## How validation works

- Cases run **in the order they were added**.
- Validation **stops at the first failing case** and returns that single error message. A schema never returns multiple errors for the same value.
- Every case method takes an optional trailing `error: String?` parameter. If provided, it **replaces** the default error message of that case.
- A valid value yields `null`; an invalid one yields the error message (`String?` is the return type of every `validate` call).

```kotlin
val schema = StringSchema()
    .min(3, "Too short!")   // custom message
    .max(10)                // default message: "Length must be at most 10"

schema.validate("ab")           // "Too short!"
schema.validate("abcdefghijk")  // "Length must be at most 10"
schema.validate("hello")        // null
```

**Next:** [Building schemas](building-schemas.md)
