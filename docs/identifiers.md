# Identifiers

## `email(pattern: Regex? = null, error?)`

| Param     | Description                                                                                              |
|-----------|----------------------------------------------------------------------------------------------------------|
| `pattern` | Optional custom regex. When provided it **replaces** the built-in one (and must match the whole string). |
| `error`   | Custom error message.                                                                                    |

Default error: `Invalid email address`. Case: `EmailCase`.

The built-in pattern is case-insensitive and rejects: a leading dot, consecutive dots, a dot right before `@`, a missing
domain, a domain without a dot, and TLDs shorter than 2 letters. Local parts may contain letters, digits and
`_ ' + - .`. Unicode (internationalized) addresses are not accepted by the default pattern; pass your own `pattern` if
you need them.

```kotlin
StringSchema().email().validate("john.doe+tag@example.co")  // null
StringSchema().email().validate("john..doe@example.com")    // "Invalid email address"
StringSchema().email().validate("john@localhost")           // "Invalid email address"
StringSchema().email(pattern = Regex(".+@company\\.com")).validate("a@company.com") // null
```

## `uuid(version: UuidVersion? = UuidVersion.V4, error?)`

| Param     | Description                                                                                                                                                                                                                                                                                                                                               |
|-----------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `version` | `UuidVersion.V1` … `V8` to require that version (the variant nibble must be `8`, `9`, `a` or `b`). **Default is `V4`.** Pass `null` to accept **any** version `V1`–`V8` **plus** the nil (`00000000-0000-0000-0000-000000000000`) and max (`ffffffff-ffff-ffff-ffff-ffffffffffff`) UUIDs. When a specific version is given, nil/max are **not** accepted. |
| `error`   | Custom error message.                                                                                                                                                                                                                                                                                                                                     |

Default error: `Invalid UUID` (when `version == null`) or `Invalid UUID v4` (lower-cased version name). Case:
`UuidCase`. Hex digits are case-insensitive.

```kotlin
StringSchema().uuid().validate("550e8400-e29b-41d4-a716-446655440000")               // null (v4)
StringSchema().uuid(UuidVersion.V7).validate("550e8400-e29b-41d4-a716-446655440000") // "Invalid UUID v7"
StringSchema().uuid(null).validate("00000000-0000-0000-0000-000000000000")           // null
```

## `hexColor(lengths: Set<Int> = setOf(3, 4, 6, 8), hash: Boolean = true, error?)`

| Param     | Description                                                                                                                                     |
|-----------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| `lengths` | Accepted numbers of hex digits. Non-empty subset of `{3, 4, 6, 8}` (`RGB`, `RGBA`, `RRGGBB`, `RRGGBBAA`), otherwise `IllegalArgumentException`. |
| `hash`    | `true`: a leading `#` is **required**. `false`: it must be **absent**.                                                                          |
| `error`   | Custom error message.                                                                                                                           |

Default error: `Value must be a hex color (#RGB, with 3/4/6/8 hex digits)` (the listed digit counts reflect `lengths`;
the `#` prefix is omitted when `hash = false`). Case: `HexColorCase`.

```kotlin
StringSchema().hexColor().validate("#fff")                                   // null
StringSchema().hexColor(lengths = setOf(6)).validate("#fff")                 // error
StringSchema().hexColor(lengths = setOf(6), hash = false).validate("ff00aa") // null
```

## `url(protocol: String? = null, hostname: String? = null, error?)`

| Param      | Description                                                                                                                                             |
|------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| `protocol` | Required scheme, compared **case-insensitively**. A trailing `:` is ignored (`"https"` and `"https:"` are equivalent).                                  |
| `hostname` | Required **exact** host (case-insensitive). No wildcard or subdomain matching. Opaque URLs (e.g. `mailto:`) have no host and therefore fail this check. |
| `error`    | Custom error message.                                                                                                                                   |

Default error: `Invalid URL` or `Invalid URL (expected protocol https and hostname example.com)` (only the constraints
you set are listed). Case: `UrlCase`.

A lenient, WHATWG-style check is used: a **scheme is required**, no whitespace or control characters are allowed, and,
when the URL has an authority (`scheme://...`), the host must be non-empty (except for `file:`), the port must be
numeric and `<= 65535`, and bracketed IPv6 hosts must be valid. Opaque forms such as `mailto:a@b.dev` and `urn:isbn:123`
are valid.

```kotlin
StringSchema().url().validate("https://example.com/path?q=1")      // null
StringSchema().url().validate("http://localhost:8080")             // null
StringSchema().url().validate("mailto:a@b.dev")                    // null
StringSchema().url().validate("example.com")                       // "Invalid URL" (no scheme)
StringSchema().url(protocol = "https").validate("http://a.com")    // "Invalid URL (expected protocol https)"
StringSchema().url(hostname = "example.com").validate("https://EXAMPLE.com/x") // null
StringSchema().url("https", "example.com").validate("https://api.example.com") // error (exact host only)
```

> Because opaque URLs are valid, strings like `localhost:3000` parse as scheme `localhost`. Combine `protocol` (and
> optionally `hostname`) with `url()` when you need real web URLs.

**Next:** [Phone and network](network.md)
