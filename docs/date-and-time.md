[← Home](../../../README.md) · [Schemas](../README.md) · [StringSchema](intro.md)

# Date and time (ISO 8601 / RFC 3339 style)

All of these validate the **string format**, and dates are also checked against the real calendar (leap years, month
lengths). The `T` separator and `Z` must be upper case.

## `isoDate(error?)`

Format `YYYY-MM-DD`, with a real calendar date. Default error: `Invalid ISO date (expected YYYY-MM-DD)`. Case:
`IsoDateCase`.

```kotlin
StringSchema().isoDate().validate("2024-02-29")  // null (leap year)
StringSchema().isoDate().validate("2023-02-29")  // error
StringSchema().isoDate().validate("2023-13-01")  // error
```

## `isoTime(precision: Int? = null, error?)`

Time of day **without** timezone. Hours `00–23`, minutes and seconds `00–59`.

| `precision`      | Accepted format                                               |
|------------------|---------------------------------------------------------------|
| `null` (default) | `HH:MM`, `HH:MM:SS` or `HH:MM:SS.s+` (any number of decimals) |
| `-1`             | `HH:MM` only                                                  |
| `0`              | `HH:MM:SS` only                                               |
| `n > 0`          | `HH:MM:SS.` followed by **exactly** `n` decimals              |
| `< -1`           | invalid: throws `IllegalArgumentException`                    |

Default error: `Invalid ISO time (expected HH:MM:SS.sss)` (the expected format reflects `precision`). Case:
`IsoTimeCase`.

```kotlin
StringSchema().isoTime().validate("14:30")                 // null
StringSchema().isoTime(precision = 0).validate("14:30")    // error
StringSchema().isoTime(precision = 3).validate("14:30:05.123") // null
StringSchema().isoTime().validate("24:00")                 // error
```

## `isoDateTime(offset: Boolean = false, local: Boolean = false, precision: Int? = null, error?)`

| Param       | Description                                                                                                                                                                                                       |
|-------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `offset`    | `false` (default): the only accepted zone designator is `Z`. `true`: `Z` **or** a `±HH:MM` offset is accepted.                                                                                                    |
| `local`     | `false` (default): a timezone is **required**. `true`: the timezone is **optional**.                                                                                                                              |
| `precision` | Same meaning as in `isoTime`: `null` = seconds required with arbitrary decimals, `-1` = minutes only, `0` = seconds only (no decimals), `n > 0` = exactly `n` decimals. `< -1` throws `IllegalArgumentException`. |
| `error`     | Custom error message.                                                                                                                                                                                             |

Special case: with `local = true` and `precision = null` (the unqualified local form), seconds become **optional** when
there is no timezone. If a timezone is present, seconds are still required.

Default error depends on the options:

| Options                       | Default error                                                |
|-------------------------------|--------------------------------------------------------------|
| default                       | `Invalid ISO datetime (ending in Z)`                         |
| `offset = true`               | `Invalid ISO datetime (with a Z or ±HH:MM offset)`           |
| `local = true`                | `Invalid ISO datetime (with an optional Z)`                  |
| `local = true, offset = true` | `Invalid ISO datetime (with an optional Z or ±HH:MM offset)` |

Case: `IsoDateTimeCase`.

```kotlin
val s = StringSchema()

s.isoDateTime().validate("2026-10-27T14:30:00Z")                       // null
s.isoDateTime().validate("2026-10-27T14:30:00.123456Z")                // null
s.isoDateTime().validate("2026-10-27T14:30:00")                        // error: zone required
s.isoDateTime().validate("2026-10-27T14:30:00+01:00")                  // error: offsets not enabled

s.isoDateTime(offset = true).validate("2026-10-27T14:30:00+01:00")     // null
s.isoDateTime(local = true).validate("2026-10-27T14:30")               // null (seconds optional, no zone)
s.isoDateTime(local = true).validate("2026-10-27T14:30:00Z")           // null
s.isoDateTime(local = true).validate("2026-10-27T14:30Z")              // error: zone present => seconds required
s.isoDateTime(precision = 3).validate("2026-10-27T14:30:00.123Z")      // null
s.isoDateTime(precision = -1).validate("2026-10-27T14:30Z")            // null
s.isoDateTime(precision = 0).validate("2026-10-27T14:30:00.5Z")        // error
s.isoDateTime().validate("2025-02-30T10:00:00Z")                       // error: not a real date
```

**Next:** [`ValidatedObject`](validated-object.md)
