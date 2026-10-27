# Finance

| Method                 | Passes when                                                                                                                                                                        | Default error                    |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------|
| `creditCard(error?)`   | Digits with an optional **single** space or hyphen between groups (no leading/trailing separators), **13 to 19 digits**, and a valid **Luhn** checksum. Card brand is not checked. | `Invalid credit card number`     |
| `currencyCode(error?)` | A code from the built-in **ISO 4217** list (including funds, precious metals and test codes like `XAU`, `XXX`). **Case-sensitive, upper case only.**                               | `Invalid ISO 4217 currency code` |
| `iban(error?)`         | Upper-case, no spaces: 2 letters + 2 check digits (`02–98`) + 11 to 30 alphanumerics, and a valid **mod-97** checksum. Country-specific length and BBAN layout are not verified.   | `Invalid IBAN`                   |

Cases: `CreditCardCase`, `CurrencyCodeCase`, `IbanCase`.

```kotlin
StringSchema().creditCard().validate("4111 1111 1111 1111")  // null
StringSchema().creditCard().validate("4111 1111 1111 1112")  // "Invalid credit card number"
StringSchema().currencyCode().validate("DZD")                // null
StringSchema().currencyCode().validate("usd")                // "Invalid ISO 4217 currency code"
StringSchema().iban().validate("GB82WEST12345698765432")     // null
```

**Next:** [Tokens and hashes](tokens-and-hashes.md)
