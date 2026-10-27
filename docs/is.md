[← Home](../../README.md) · [Schemas](intro.md)

# `IsSchema<E>`

Created with `FieldSchema().isOf<E>()`. Passes if `value is E` (subclasses and implementations are accepted), then runs
your cases against the value typed as `E`. Use it for **custom classes, lists, maps and nested objects**.

| Case                             | Default error                           |
|----------------------------------|-----------------------------------------|
| `FieldCases.IsCase` (type check) | `Value must be a <E::class.simpleName>` |

Only the [common methods](../common-methods.md) are available (`validateHandler`, `case`, `oneOf`, `notOneOf`).

```kotlin
data class Point(val x: Int, val y: Int)

val schema = FieldSchema()
    .isOf<Point>("Expected a point")
    .validateHandler({ it.x >= 0 && it.y >= 0 }, "Point must be in the first quadrant")
    .build()

schema.validate(Point(1, 2))   // null
schema.validate(Point(-1, 2))  // "Point must be in the first quadrant"
schema.validate("hello")       // "Expected a point"
```

> Generic type arguments are erased at runtime. Use `isOf<List<*>>()` / `isOf<Map<*, *>>()` and check elements in a
> handler (see [examples](../examples/nested-structures.md)).

**Next:** [`EnumSchema`](enum.md)
