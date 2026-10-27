# `oneOf`, enums and comparisons

```kotlin
enum class Role { GUEST, USER, ADMIN }

class Invite : ValidatedObject() {
    var role by validatedProperty(
        Role.USER,
        FieldSchema().enumOf<Role>().notOneOf(setOf(Role.ADMIN), "Cannot invite admins").build()
    )
    var plan by validatedProperty(
        "free",
        FieldSchema().string().oneOf(setOf("free", "pro", "team")).build()
    )
    var seats by validatedProperty(
        1,
        FieldSchema().int().gte(1).lte(500).multipleOf(5, "Seats are sold in packs of 5").build()
    )
}
```

**Next:** [Numeric schemas](numeric.md)
