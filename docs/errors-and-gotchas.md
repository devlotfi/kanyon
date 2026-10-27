[← Home](../README.md)

# Errors, exceptions and gotchas

## Exceptions thrown while building schemas

These are programming errors, raised **when the rule is created** (not when a value is validated):

| Where | Condition | Exception |
|---|---|---|
| `oneOf(values)` | `values` is empty | `IllegalArgumentException` ("oneOf needs at least one value") |
| `length(n)` | `n < 0` | `IllegalArgumentException` |
| `hexColor(lengths)` | `lengths` empty or not a subset of `{3, 4, 6, 8}` | `IllegalArgumentException` |
| `mac(delimiter)` | `delimiter` is empty | `IllegalArgumentException` |
| `isoTime(precision)` / `isoDateTime(precision)` | `precision < -1` | `IllegalArgumentException` |
| `build()` | Called on a schema that was not created through `FieldSchema` | Exception (no root) |

And at validation time: `multipleOf(0)` throws `ArithmeticException` (division by zero) when a value is validated.

## Behavior notes and gotchas

- **One error per value.** The first failing rule wins. Order your rules from the most to the least fundamental (for strings: `notBlank` before `min`, `min` before `regex`, ...).
- **Type check comes first.** With `FieldSchema`, a value of the wrong type (or `null` when not nullable) yields the *type* error and none of the inner rules run.
- **`nullable()` skips everything for `null`.** For non-null values all rules apply normally.
- **Type checks are exact.** `int()` ≠ `long()` ≠ `double()`. Convert before validating if your source gives you a different numeric type.
- **No trimming or normalisation.** Strings are validated exactly as provided. Trim before validating if your input may contain stray whitespace.
- **Case sensitivity.** `currencyCode()` and `iban()` require upper case; `mac()` requires consistent case; `uuid()`, `hash(HEX)`, email and URL scheme/host matching are case-insensitive.
- **Format checks are not existence checks.** `email`, `url`, `iban`, `creditCard`, `jwt`, ... verify the *shape* (and checksum where one exists), not that the thing actually exists or is trustworthy.
- **Generics are erased.** Use `isOf<List<*>>()` / `isOf<Map<*, *>>()` and validate elements inside a `validateHandler`.
- **`validateHandler` needs an import** (`com.devlotfi.kanyon.validateHandler`), as do `nullable` and `oneOf` helpers living in `com.devlotfi.kanyon.schema` (covered by `import com.devlotfi.kanyon.schema.*`).
- **Pure Kotlin.** Regexes avoid `\d` in favour of `[0-9]` so behavior is identical on JVM, Android, JS and Native.

**Next:** [Kanyon (start)](intro.md)
