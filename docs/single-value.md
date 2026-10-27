# Validating a single value

```kotlin
// Typed, standalone (value already has a known type)
val error: String? = StringSchema().email().validate("not-an-email")
if (error != null) println(error) // Invalid email address

// Through FieldSchema (unknown type, e.g. form input, JSON, map values)
val portSchema = FieldSchema().int().gte(1).lte(65535, "Port out of range").build()

portSchema.validate(8080)    // null
portSchema.validate(70000)   // "Port out of range"
portSchema.validate("8080")  // "Value must be an int"
portSchema.validate(null)    // "Value must be an int"

// Optional value
val phone = FieldSchema().string().e164("Use international format, e.g. +213...").nullable().build()
phone.validate(null)             // null
phone.validate("0550123456")     // "Use international format, e.g. +213..."
```

**Next:** [Custom validation function](custom-function.md)
