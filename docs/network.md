# Phone and network

| Method                   | Params                                                                                                   | Passes when                                                                                                               | Default error                         |
|--------------------------|----------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------|---------------------------------------|
| `e164(error?)`           | –                                                                                                        | `+` followed by 7 to 15 digits, first digit not `0` (`+213550123456`). No spaces or dashes.                               | `Invalid E.164 phone number`          |
| `ipv4(error?)`           | –                                                                                                        | Four dot-separated decimal parts, each `0–255`, **no leading zeros** (`192.168.0.1` valid, `192.168.00.1` invalid).       | `Invalid IPv4 address`                |
| `ipv6(error?)`           | –                                                                                                        | Full or `::`-compressed IPv6, with optional embedded IPv4 tail (`::ffff:192.168.0.1`). No brackets, no zone id (`%eth0`). | `Invalid IPv6 address`                |
| `cidrv4(error?)`         | –                                                                                                        | Valid IPv4 + `/` + prefix `0–32` (no leading zeros).                                                                      | `Invalid IPv4 CIDR`                   |
| `cidrv6(error?)`         | –                                                                                                        | Valid IPv6 + `/` + prefix `0–128` (no leading zeros).                                                                     | `Invalid IPv6 CIDR`                   |
| `mac(delimiter, error?)` | `delimiter: String? = null` (defaults to `":"`, must not be empty, otherwise `IllegalArgumentException`) | Six pairs of hex digits joined by `delimiter`, with **consistent case** (all upper or all lower).                         | `Invalid MAC address (delimiter ':')` |

Cases: `E164Case`, `Ipv4Case`, `Ipv6Case`, `CidrV4Case`, `CidrV6Case`, `MacCase`.

```kotlin
StringSchema().e164().validate("+213550123456")            // null
StringSchema().ipv4().validate("256.1.1.1")                // "Invalid IPv4 address"
StringSchema().ipv6().validate("2001:db8::1")              // null
StringSchema().cidrv4().validate("10.0.0.0/8")             // null
StringSchema().cidrv6().validate("2001:db8::/129")         // "Invalid IPv6 CIDR"
StringSchema().mac().validate("aa:bb:cc:dd:ee:ff")         // null
StringSchema().mac().validate("aa:BB:cc:dd:ee:ff")         // error (mixed case)
StringSchema().mac(delimiter = "-").validate("AA-BB-CC-DD-EE-FF") // null
```

**Next:** [Finance](finance.md)
