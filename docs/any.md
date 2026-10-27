[← Home](../../README.md) · [Schemas](intro.md)

# `AnySchema`

Created with `FieldSchema().any()`. Accepts any **non-null** value and then runs the cases you add. It only has
the [common methods](../common-methods.md), which makes it useful together with `validateHandler` or `oneOf`.

| Case                              | Default error                         |
|-----------------------------------|---------------------------------------|
| `FieldCases.AnyCase` (type check) | `Value must be any` (value is `null`) |

```kotlin
val schema = FieldSchema()
    .any()
    .validateHandler({ it.toString().length < 5 }, "Too long when printed")
    .build()

schema.validate(12345)  // "Too long when printed"
schema.validate(null)   // "Value must be any"
```

**Next:** [`IsSchema`](is.md)
