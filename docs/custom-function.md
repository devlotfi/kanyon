# Custom validation function

```kotlin
// Typed handler
val evenSquare = FieldSchema()
    .int()
    .gt(0)
    .validateHandler({ n ->
        val r = kotlin.math.sqrt(n.toDouble()).toInt()
        r * r == n
    }, "Must be a perfect square")
    .build()

evenSquare.validate(49) // null
evenSquare.validate(50) // "Must be a perfect square"

// Handler on the raw FieldSchema (receives Any?)
val notReservedName = FieldSchema()
    .string()
    .validateHandler({ it.lowercase() !in setOf("admin", "root", "system") }, "That name is reserved")
    .build()

notReservedName.validate("Admin") // "That name is reserved"
```

**Next:** [Object validation](object-validation.md)
