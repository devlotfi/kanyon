[← Home](../../README.md) · [Examples](intro.md)

# Object validation

```kotlin
class Product : ValidatedObject() {
    var name by validatedProperty("", FieldSchema().string().notBlank().max(80).build())
    var sku by validatedProperty(
        "",
        FieldSchema().string().regex(Regex("[A-Z]{3}-[0-9]{4}"), error = "Format: ABC-1234").build()
    )
    var price by validatedProperty(0.0, FieldSchema().double().gt(0.0, "Price must be positive").build())
    var currency by validatedProperty("USD", FieldSchema().string().currencyCode().build())
    var stock by validatedProperty(0, FieldSchema().int().gte(0).build())
    var website by validatedProperty<String?>(
        null,
        FieldSchema().string().url(protocol = "https").nullable().build()
    )
}

val p = Product().apply {
    name = ""
    sku = "abc-1"
    price = -5.0
    currency = "usd"
    website = "http://shop.example.com"
}

p.validate().forEach { (field, message) -> println("$field: $message") }
// name: Value must not be blank
// sku: Format: ABC-1234
// price: Price must be positive
// currency: Invalid ISO 4217 currency code
// website: Invalid URL (expected protocol https)

println(p.valid()) // false
```

**Next:** [Nested objects, lists and maps](nested-structures.md)
