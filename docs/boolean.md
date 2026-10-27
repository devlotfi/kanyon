[← Home](../../README.md) · [Schemas](intro.md)

# `BooleanSchema`

Created with `FieldSchema().boolean()` or `BooleanSchema()`.

| Method                    | Param | Passes when   | Default error        |
|---------------------------|-------|---------------|----------------------|
| `eq(eq: Boolean, error?)` | `eq`  | `value == eq` | `Value must be <eq>` |

Underlying case: `BooleanCases.EqCase`.

```kotlin
val acceptTerms = FieldSchema().boolean().eq(true, "You must accept the terms").build()

acceptTerms.validate(true)   // null
acceptTerms.validate(false)  // "You must accept the terms"
```

**Next:** [Length and content](length-and-content.md)
