# `EnumSchema<E>`

Created with `FieldSchema().enumOf<E>()`. Accepts instances of the enum `E`. It is a **comparable schema**, so it
supports `gt`, `gte`, `lt`, `lte`, `eq` (comparison follows the enum declaration order, i.e. `compareTo` / ordinal) in
addition to the common methods.

| Case                               | Default error                           |
|------------------------------------|-----------------------------------------|
| `FieldCases.EnumCase` (type check) | `Value must be a <E::class.simpleName>` |

```kotlin
enum class Level { LOW, MEDIUM, HIGH }

val schema = FieldSchema()
    .enumOf<Level>()
    .gte(Level.MEDIUM, "Level must be at least MEDIUM")
    .build()

schema.validate(Level.HIGH)  // null
schema.validate(Level.LOW)   // "Level must be at least MEDIUM"
schema.validate("HIGH")      // "Value must be a Level"  (strings are not enum instances)
```

**Next:** [oneOf, enums and comparisons](enums-and-one-of.md)
