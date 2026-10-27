# Nested objects, lists and maps

Nested objects, lists and maps are validated **manually by you** with `isOf<...>()` and a custom `validateHandler` on
that field.

```kotlin
class Address : ValidatedObject() {
    var city by validatedProperty("", FieldSchema().string().notBlank().build())
    var zip by validatedProperty("", FieldSchema().string().regex(Regex("[0-9]{5}")).build())
}

class Customer : ValidatedObject() {
    var name by validatedProperty("", FieldSchema().string().notBlank().build())

    // Nested object: reuse the nested object's own validation
    var address by validatedProperty(
        Address(),
        FieldSchema()
            .isOf<Address>()
            .validateHandler({ it.valid() }, "Address is invalid")
            .build()
    )

    // List: check type, size, then every element
    var emails by validatedProperty(
        emptyList<String>(),
        FieldSchema()
            .isOf<List<*>>("Emails must be a list")
            .validateHandler({ it.isNotEmpty() }, "At least one email is required")
            .validateHandler(
                { list -> list.all { it is String && StringSchema().email().validate(it) == null } },
                "One or more emails are invalid"
            )
            .build()
    )

    // Map: check keys and values
    var scores by validatedProperty(
        emptyMap<String, Int>(),
        FieldSchema()
            .isOf<Map<*, *>>("Scores must be a map")
            .validateHandler({ m ->
                m.all { (k, v) -> k is String && k.isNotBlank() && v is Int && v in 0..100 }
            }, "Scores must map non-blank names to values between 0 and 100")
            .build()
    )

    // List of nested objects
    var previousAddresses by validatedProperty(
        emptyList<Address>(),
        FieldSchema()
            .isOf<List<*>>()
            .validateHandler({ list -> list.all { it is Address && it.valid() } }, "A previous address is invalid")
            .build()
    )
}

val c = Customer()
c.name = "Lotfi"
c.address.city = "Algiers"
c.address.zip = "abc"
c.emails = listOf("a@b.dev", "oops")
c.scores = mapOf("math" to 120)

c.validate()
// {address=Address is invalid,
//  emails=One or more emails are invalid,
//  scores=Scores must map non-blank names to values between 0 and 100}
```

## Surfacing the nested errors

A handler can only say "valid / invalid", so the parent reports a single message per field. If you want the detailed
nested errors, override `validate()` and merge them yourself (leave the nested property **without** a schema so you
don't get two messages):

```kotlin
class Customer2 : ValidatedObject() {
    var name by validatedProperty("", FieldSchema().string().notBlank().build())
    var address by validatedProperty(Address())                       // no schema: merged manually
    var previousAddresses by validatedProperty(emptyList<Address>())  // no schema: merged manually

    override fun validate(): Map<String, String> {
        val errors = super.validate().toMutableMap()
        address.validate().forEach { (field, msg) -> errors["address.$field"] = msg }
        previousAddresses.forEachIndexed { i, a ->
            a.validate().forEach { (field, msg) -> errors["previousAddresses[$i].$field"] = msg }
        }
        return errors
    }
}

// {address.zip=Value does not match pattern [0-9]{5}, previousAddresses[1].city=Value must not be blank}
```

**Next:** [Dates, identifiers and network values](dates-and-identifiers.md)
