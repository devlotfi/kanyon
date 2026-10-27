[← Home](../README.md)

# Building schemas

There are two ways to create a schema.

## Through `FieldSchema` (recommended)

`FieldSchema` validates values of unknown type (`Any?`). Calling one of its type methods adds a **type check** and returns the matching typed schema, on which you chain further rules. Finish with `.build()` to get the `FieldSchema` back.

```kotlin
val ageSchema: FieldSchema = FieldSchema()
    .int("Age must be a whole number")
    .gte(18, "You must be an adult")
    .lte(120)
    .build()

ageSchema.validate(25)    // null
ageSchema.validate(15)    // "You must be an adult"
ageSchema.validate("25")  // "Age must be a whole number"
ageSchema.validate(null)  // "Age must be a whole number"  (null is not allowed by default)
```

### `FieldSchema` entry points

| Method | Returns | Accepts | Default type-mismatch error |
|---|---|---|---|
| `any(error?)` | `AnySchema` | any **non-null** value | `Value must be any` |
| `isOf<E>(error?)` | `IsSchema<E>` | any instance of `E` (`value is E`), subclasses included | `Value must be a <E simple name>` |
| `enumOf<E>(error?)` | `EnumSchema<E>` | an instance of enum `E` | `Value must be a <E simple name>` |
| `string(error?)` | `StringSchema` | `String` | `Value must be a string` |
| `byte(error?)` | `ByteSchema` | `Byte` | `Value must be a byte` |
| `short(error?)` | `ShortSchema` | `Short` | `Value must be a short` |
| `int(error?)` | `IntSchema` | `Int` | `Value must be an int` |
| `long(error?)` | `LongSchema` | `Long` | `Value must be a long` |
| `float(error?)` | `FloatSchema` | `Float` | `Value must be a float` |
| `double(error?)` | `DoubleSchema` | `Double` | `Value must be a double` |
| `boolean(error?)` | `BooleanSchema` | `Boolean` | `Value must be a boolean` |

> The type check is **strict**: `int()` does not accept a `Long` or a `Double`, `double()` does not accept an `Int`, and so on.

> One `FieldSchema` is meant to carry **one** type method. Calling several (e.g. `.string()` then `.int()` on the same `FieldSchema`) adds several type checks that must *all* pass, which can never succeed for a single value. Kanyon has no union types; use `any()` / `isOf<...>()` plus a [custom handler](custom-validation.md#custom-validation-function-validatehandler) if you need that.

## Standalone typed schemas

Typed schemas can also be instantiated directly. They validate **already-typed** values (no runtime type check, no nullability) and return `String?`.

```kotlin
StringSchema().email().validate("user@example.com")   // null
IntSchema().gt(0).even().validate(3)                  // "Value must be even"
BooleanSchema().eq(true).validate(false)              // "Value must be true"
```

> `build()` only works on schemas created through `FieldSchema` (they carry a `root`). Calling `build()` on a standalone schema throws, because it has no root. Use `validate()` directly on standalone schemas.

## Nullability: `nullable()`

By default `null` is **rejected** by `FieldSchema` (the type case fails). Call `nullable()` to accept `null`. When the value is `null` and the field is nullable, **all rules are skipped** and the value is considered valid.

```kotlin
val nickname = FieldSchema().string().min(2).max(16).nullable().build()

nickname.validate(null)    // null  -> valid
nickname.validate("a")     // "Length must be at least 2"
nickname.validate("alice") // null
```

- `nullable()` can be called at any point of the chain (position does not matter), because it flips the flag on the shared `FieldSchema`.
- It can also be called first: `FieldSchema().nullable().string().min(2).build()`.
- On a standalone schema it has no effect (there is no `FieldSchema` to flag).
- The flag is also readable/writable as `fieldSchema.isNullable`.

**Next:** [Methods available on every schema](common-methods.md)
