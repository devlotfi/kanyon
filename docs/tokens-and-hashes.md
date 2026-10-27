[← Home](../../../README.md) · [Schemas](../README.md) · [StringSchema](intro.md)

# Tokens and hashes

## `jwt(algorithm: JwtAlgorithm? = null, error?)`

| Param       | Description                                                               |
|-------------|---------------------------------------------------------------------------|
| `algorithm` | Optional required `alg` header value. `null` accepts any non-empty `alg`. |
| `error`     | Custom error message.                                                     |

`JwtAlgorithm` values: `HS256, HS384, HS512, RS256, RS384, RS512, ES256, ES384, ES512, PS256, PS384, PS512, EdDSA`.

What is checked (structure only):

1. Exactly three dot-separated parts; header and payload are non-empty (the signature part may be empty).
2. All parts use the base64url alphabet.
3. The header decodes (strict, unpadded base64url) to something shaped like a JSON object (`{ ... }`).
4. If a `typ` field is present it must equal `"JWT"`.
5. An `alg` field must be present and non-empty, and equal to `algorithm` when one is given.

Default error: `Invalid JWT` or `Invalid JWT (expected algorithm HS256)`. Case: `JwtCase`.

> This is a **format check only**. The signature is **not** verified, the payload is not parsed, and expiration is not
> checked. Never use it as authentication. An `alg` of `none` passes unless you set `algorithm`.

```kotlin
StringSchema().jwt().validate(token)                         // null if well-formed
StringSchema().jwt(JwtAlgorithm.RS256).validate(hs256Token)  // "Invalid JWT (expected algorithm RS256)"
```

## `hash(algorithm: HashAlgorithm, encoding: HashEncoding = HashEncoding.HEX, error?)`

| Param       | Description                                                                                                                   |
|-------------|-------------------------------------------------------------------------------------------------------------------------------|
| `algorithm` | `HashAlgorithm.MD5`, `SHA1`, `SHA256`, `SHA384`, `SHA512`.                                                                    |
| `encoding`  | `HashEncoding.HEX` (default, either case), `BASE64` (standard alphabet, **padded**), or `BASE64URL` (URL-safe, **unpadded**). |
| `error`     | Custom error message.                                                                                                         |

Expected string lengths:

| Algorithm | Digest bytes | `HEX` | `BASE64` (padded)   | `BASE64URL` |
|-----------|--------------|-------|---------------------|-------------|
| `MD5`     | 16           | 32    | 24 (ends with `==`) | 22          |
| `SHA1`    | 20           | 40    | 28 (ends with `=`)  | 27          |
| `SHA256`  | 32           | 64    | 44 (ends with `=`)  | 43          |
| `SHA384`  | 48           | 96    | 64 (no padding)     | 64          |
| `SHA512`  | 64           | 128   | 88 (ends with `==`) | 86          |

Default error: `Invalid sha256 hash (hex)` (lower-cased algorithm and encoding names). Case: `HashCase`.

```kotlin
StringSchema().hash(HashAlgorithm.SHA256)
    .validate("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855") // null
StringSchema().hash(HashAlgorithm.MD5, HashEncoding.BASE64)
    .validate("1B2M2Y8AsgTpgAmY7PhCfg==")                      // null
```

**Next:** [Date and time](date-and-time.md)
