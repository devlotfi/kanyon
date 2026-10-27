[← Home](../../README.md) · [Examples](intro.md)

# Dates, identifiers and network values

```kotlin
class Event : ValidatedObject() {
    var date by validatedProperty("", FieldSchema().string().isoDate().build())
    var startsAt by validatedProperty("", FieldSchema().string().isoDateTime(offset = true, precision = 0).build())
    var organizerId by validatedProperty("", FieldSchema().string().uuid().build())
    var color by validatedProperty("#ffffff", FieldSchema().string().hexColor(lengths = setOf(3, 6)).build())
    var serverIp by validatedProperty("", FieldSchema().string().ipv4().build())
    var allowedRange by validatedProperty("", FieldSchema().string().cidrv4().build())
    var checksum by validatedProperty("", FieldSchema().string().hash(HashAlgorithm.SHA256).build())
}
```

**Next:** [Errors, exceptions and gotchas](errors-and-gotchas.md)
