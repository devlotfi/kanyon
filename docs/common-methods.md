# Methods available on every schema

These come from `BaseSchema<T, S>` and are available on **all** schemas (`StringSchema`, `IntSchema`, `FieldSchema`, ...).

| Method | Description |
|---|---|
| `validate(value): String?` | Runs all cases in order. Returns `null` if valid, otherwise the first error message. |
| `build(): FieldSchema` | Returns the root `FieldSchema`. Only valid for schemas created through `FieldSchema`. |
| `case(case: BuilderCase<T>): S` | Adds a custom [`BuilderCase`](custom-validation.md#custom-case-buildercase). |
| `oneOf(values: Set<T>, error? = null): S` | Value must be one of `values`. |
| `notOneOf(values: Set<T>, error? = null): S` | Value must **not** be one of `values`. |
| `nullable(): S` | *(extension)* Allows `null`. See [Nullability](building-schemas.md#nullability-nullable). |
| `validateHandler(handler, error): S` | *(extension, import `com.devlotfi.kanyon.validateHandler`)* Custom predicate. See [Custom validation function](custom-validation.md#custom-validation-function-validatehandler). |

## `oneOf` / `notOneOf`

| Param | Type | Description |
|---|---|---|
| `values` | `Set<T>` | The allowed (`oneOf`) or forbidden (`notOneOf`) values. A defensive copy is made. `oneOf` requires a **non-empty** set (throws `IllegalArgumentException` otherwise). `notOneOf` accepts an empty set. |
| `error` | `String?` | Custom error message. |

| Case | Default error |
|---|---|
| `OneOfCase` | `Value must be one of: [a, b, c]` |
| `NotOneOfCase` | `Value must not be one of: [a, b, c]` |

```kotlin
StringSchema().oneOf(setOf("admin", "user", "guest")).validate("root")
// "Value must be one of: [admin, user, guest]"

IntSchema().notOneOf(setOf(0, 13), "Unlucky number").validate(13)
// "Unlucky number"
```

**Next:** [`AnySchema`](any.md)
