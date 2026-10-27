# Numeric schemas and comparable cases

`IntSchema`, `LongSchema`, `ShortSchema`, `ByteSchema`, `FloatSchema`, `DoubleSchema` (and `EnumSchema`) all extend
`ComparableSchema` and share these five rules. Each takes the value to compare against and an optional `error`.

| Method                | Param | Passes when    | Default error            |
|-----------------------|-------|----------------|--------------------------|
| `gt(gt: T, error?)`   | `gt`  | `value > gt`   | `Value must be > <gt>`   |
| `gte(gte: T, error?)` | `gte` | `value >= gte` | `Value must be >= <gte>` |
| `lt(lt: T, error?)`   | `lt`  | `value < lt`   | `Value must be < <lt>`   |
| `lte(lte: T, error?)` | `lte` | `value <= lte` | `Value must be <= <lte>` |
| `eq(eq: T, error?)`   | `eq`  | `value == eq`  | `Value must be == <eq>`  |

The underlying cases are `ComparableCases.GtCase`, `GteCase`, `LtCase`, `LteCase`, `EqCase`.

```kotlin
IntSchema().gte(1).lte(10).validate(11)         // "Value must be <= 10"
DoubleSchema().gt(0.0).validate(0.0)            // "Value must be > 0.0"
LongSchema().eq(42L, "Must be 42").validate(7L) // "Must be 42"
ByteSchema().gte(1.toByte()).validate(0)        // literal conversion: use .toByte() / .toShort() for Byte/Short
```

> For `ByteSchema` / `ShortSchema`, pass explicitly typed values (`1.toByte()`, `1.toShort()`).

## Extra `IntSchema` rules

| Method                                | Param                                                                               | Passes when                                      | Default error                     |
|---------------------------------------|-------------------------------------------------------------------------------------|--------------------------------------------------|-----------------------------------|
| `multipleOf(multipleOf: Int, error?)` | `multipleOf` (**must not be 0**, otherwise validation throws `ArithmeticException`) | `value % multipleOf == 0`                        | `Value must be a multiple of <n>` |
| `even(error?)`                        | –                                                                                   | `value % 2 == 0`                                 | `Value must be even`              |
| `odd(error?)`                         | –                                                                                   | `value % 2 != 0` (negative odd numbers work too) | `Value must be odd`               |

Underlying cases: `MultipleOfCase`, `EvenCase`, `OddCase`.

```kotlin
IntSchema().multipleOf(5).validate(12)  // "Value must be a multiple of 5"
IntSchema().even().validate(7)          // "Value must be even"
IntSchema().odd().validate(-3)          // null
```

**Next:** [`BooleanSchema`](boolean.md)
