[← Home](../../../README.md) · [Schemas](../README.md) · [StringSchema](intro.md)

# Encodings

| Method              | Passes when                                                                                                            | Default error                   |
|---------------------|------------------------------------------------------------------------------------------------------------------------|---------------------------------|
| `base64(error?)`    | Standard base64 alphabet (`A-Za-z0-9+/`), length multiple of 4, **correct `=` padding**. The empty string is accepted. | `Value must be valid base64`    |
| `base64url(error?)` | Characters from `A-Za-z0-9_-` only (URL-safe alphabet, **no padding**). The empty string is accepted.                  | `Value must be valid base64url` |

Cases: `Base64Case`, `Base64UrlCase`.

```kotlin
StringSchema().base64().validate("SGVsbG8=")      // null
StringSchema().base64().validate("SGVsbG8")       // "Value must be valid base64" (missing padding)
StringSchema().base64url().validate("SGVsbG8")    // null
```

**Next:** [Identifiers](identifiers.md)
