# Length and content

| Method                            | Params                                                          | Passes when                                                                                                     | Default error                                  |
|-----------------------------------|-----------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|------------------------------------------------|
| `notBlank(error?)`                | –                                                               | `value.isNotBlank()` (empty and whitespace-only strings fail)                                                   | `Value must not be blank`                      |
| `min(min, error?)`                | `min: Int`                                                      | `value.length >= min`                                                                                           | `Length must be at least <min>`                |
| `max(max, error?)`                | `max: Int`                                                      | `value.length <= max`                                                                                           | `Length must be at most <max>`                 |
| `length(length, error?)`          | `length: Int` (must be `>= 0`, else `IllegalArgumentException`) | `value.length == length`                                                                                        | `Length must be exactly <length>`              |
| `regex(regex, fullMatch, error?)` | `regex: Regex`, `fullMatch: Boolean = true`                     | `fullMatch = true`: whole string matches. `fullMatch = false`: the regex is found anywhere (`containsMatchIn`). | `Value does not match pattern <regex.pattern>` |

Cases: `NotBlankCase`, `MinCase`, `MaxCase`, `LengthCase`, `RegexCase`.

> Lengths are measured in UTF-16 code units (Kotlin `String.length`), so some emoji count as 2.

```kotlin
StringSchema().regex(Regex("[A-Z]{3}-[0-9]{4}")).validate("ABC-1234")                 // null
StringSchema().regex(Regex("[0-9]+"), fullMatch = false).validate("room 42")          // null
StringSchema().regex(Regex("[0-9]+"), error = "Digits only").validate("12a")          // "Digits only"
```

**Next:** [Encodings](encodings.md)
