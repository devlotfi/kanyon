package com.devlotfi.kanyon.schema

import com.devlotfi.kanyon.BuilderCase
import java.net.URI
import java.net.URISyntaxException
import java.util.*

// ---------------------------------------------------------------------------
// Enums used as parameters
// ---------------------------------------------------------------------------

enum class UuidVersion(val digit: Char) {
    V1('1'), V2('2'), V3('3'), V4('4'), V5('5'), V6('6'), V7('7'), V8('8')
}

enum class JwtAlgorithm {
    HS256, HS384, HS512,
    RS256, RS384, RS512,
    ES256, ES384, ES512,
    PS256, PS384, PS512,
    EdDSA
}

enum class HashAlgorithm(val byteLength: Int) {
    MD5(16), SHA1(20), SHA256(32), SHA384(48), SHA512(64)
}

enum class HashEncoding { HEX, BASE64, BASE64URL }

// ---------------------------------------------------------------------------
// Precompiled patterns + helpers (compiled once, never inside validate())
// ---------------------------------------------------------------------------

internal object StringPatterns {
    val EMAIL = Regex(
        """^(?!\.)(?!.*\.\.)([a-z0-9_'+\-\.]*)[a-z0-9_+-]@([a-z0-9][a-z0-9\-]*\.)+[a-z]{2,}$""",
        RegexOption.IGNORE_CASE
    )

    val BASE64 = Regex("(?:[0-9a-zA-Z+/]{4})*(?:[0-9a-zA-Z+/]{2}==|[0-9a-zA-Z+/]{3}=)?")
    val BASE64URL = Regex("[A-Za-z0-9_-]*")

    private const val HEX = "[0-9a-fA-F]"
    private const val VARIANT = "[89abAB]"

    private val UUID_ANY = Regex(
        "$HEX{8}-$HEX{4}-[1-8]$HEX{3}-$VARIANT$HEX{3}-$HEX{12}" +
                "|00000000-0000-0000-0000-000000000000" +
                "|ffffffff-ffff-ffff-ffff-ffffffffffff",
        RegexOption.IGNORE_CASE
    )

    private val UUID_BY_VERSION: Map<UuidVersion, Regex> = UuidVersion.values().associateWith {
        Regex("$HEX{8}-$HEX{4}-${it.digit}$HEX{3}-$VARIANT$HEX{3}-$HEX{12}")
    }

    fun uuid(version: UuidVersion?): Regex =
        if (version == null) UUID_ANY else UUID_BY_VERSION.getValue(version)

    val GUID = Regex("$HEX{8}-$HEX{4}-$HEX{4}-$HEX{4}-$HEX{12}")
    val CUID = Regex("[cC][^\\s-]{8,}")
    val CUID2 = Regex("[0-9a-z]+")
    val ULID = Regex("[0-9A-HJKMNP-TV-Za-hjkmnp-tv-z]{26}")
    val E164 = Regex("\\+[1-9]\\d{6,14}")
    val IBAN_FORMAT = Regex("[A-Z]{2}\\d{2}[A-Z0-9]{11,30}")
    val CREDIT_CARD_FORMAT = Regex("\\d+(?:[ -]\\d+)*")
    val JWT_ALG = Regex("\"alg\"\\s*:\\s*\"([^\"]*)\"")
    val JWT_TYP = Regex("\"typ\"\\s*:\\s*\"([^\"]*)\"")

    fun nanoid(length: Int): Regex {
        require(length > 0) { "nanoid length must be > 0" }
        return Regex("[a-zA-Z0-9_-]{$length}")
    }

    fun hexColor(lengths: Set<Int>, hash: Boolean): Regex {
        require(lengths.isNotEmpty() && lengths.all { it in setOf(3, 4, 6, 8) }) {
            "hexColor lengths must be a non-empty subset of {3, 4, 6, 8}"
        }
        val body = lengths.sorted().joinToString("|") { "$HEX{$it}" }
        return Regex((if (hash) "#" else "") + "(?:$body)")
    }

    fun mac(delimiter: String): Regex {
        require(delimiter.isNotEmpty()) { "mac delimiter must not be empty" }
        val d = Regex.escape(delimiter)
        // Zod: consistent case (all upper or all lower)
        return Regex("(?:[0-9A-F]{2}$d){5}[0-9A-F]{2}|(?:[0-9a-f]{2}$d){5}[0-9a-f]{2}")
    }

    fun hash(algorithm: HashAlgorithm, encoding: HashEncoding): Regex {
        val n = algorithm.byteLength
        val chars = (n * 8 + 5) / 6            // unpadded base64 length
        val padded = (n + 2) / 3 * 4           // padded base64 length
        return when (encoding) {
            HashEncoding.HEX -> Regex("$HEX{${n * 2}}")
            HashEncoding.BASE64 -> Regex("[A-Za-z0-9+/]{$chars}" + "=".repeat(padded - chars))
            HashEncoding.BASE64URL -> Regex("[A-Za-z0-9_-]{$chars}")
        }
    }
}

private fun isDigits(s: String) = s.isNotEmpty() && s.all { it in '0'..'9' }

internal fun isIpv4(value: String): Boolean {
    val parts = value.split('.')
    if (parts.size != 4) return false
    return parts.all { p ->
        isDigits(p) && p.length <= 3 && (p.length == 1 || p[0] != '0') && p.toInt() <= 255
    }
}

private fun isHexGroup(g: String) =
    g.length in 1..4 && g.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }

private fun splitGroups(part: String): List<String> =
    if (part.isEmpty()) emptyList() else part.split(':')

internal fun isIpv6(value: String): Boolean {
    if (value.isEmpty() || ":::" in value) return false
    val dbl = value.indexOf("::")
    if (dbl != value.lastIndexOf("::")) return false
    val head = if (dbl >= 0) value.substring(0, dbl) else value
    val tail = if (dbl >= 0) value.substring(dbl + 2) else ""
    val groups = splitGroups(head) + splitGroups(tail)
    var count = 0
    for ((i, g) in groups.withIndex()) {
        val last = i == groups.lastIndex
        if (last && '.' in g && !(dbl >= 0 && tail.isEmpty())) {
            // embedded IPv4 (e.g. ::ffff:192.168.0.1) counts as 2 groups
            if (!isIpv4(g)) return false
            count += 2
        } else {
            if (!isHexGroup(g)) return false
            count += 1
        }
    }
    return if (dbl >= 0) count <= 7 else count == 8
}

private fun isPrefix(s: String, max: Int) =
    isDigits(s) && s.length <= 3 && (s.length == 1 || s[0] != '0') && s.toInt() <= max

internal fun isCidr(value: String, v6: Boolean): Boolean {
    val idx = value.indexOf('/')
    if (idx < 0 || value.indexOf('/', idx + 1) >= 0) return false
    val addr = value.substring(0, idx)
    val prefix = value.substring(idx + 1)
    return if (v6) isIpv6(addr) && isPrefix(prefix, 128) else isIpv4(addr) && isPrefix(prefix, 32)
}

internal fun luhn(digits: String): Boolean {
    var sum = 0
    var alt = false
    for (i in digits.indices.reversed()) {
        var n = digits[i] - '0'
        if (alt) {
            n *= 2
            if (n > 9) n -= 9
        }
        sum += n
        alt = !alt
    }
    return sum % 10 == 0
}

internal fun ibanChecksumValid(iban: String): Boolean {
    val rearranged = iban.substring(4) + iban.substring(0, 4)
    var rem = 0
    for (c in rearranged) {
        rem = if (c in '0'..'9') (rem * 10 + (c - '0')) % 97
        else (rem * 100 + (c - 'A' + 10)) % 97
    }
    return rem == 1
}

private fun isEmojiCodePoint(cp: Int): Boolean =
    cp in 0x1F000..0x1FAFF ||   // emoticons, symbols, pictographs, flags, skin tones
            cp in 0x2600..0x27BF ||     // misc symbols + dingbats
            cp in 0x2300..0x23FF ||     // misc technical (⌚ ⏰ ...)
            cp in 0x2B00..0x2BFF ||     // ⬆ ⭐ ...
            cp in 0x2190..0x21FF ||     // arrows
            cp in 0x2900..0x297F ||     // ⤴ ⤵
            cp == 0x00A9 || cp == 0x00AE || cp == 0x203C || cp == 0x2049 ||
            cp == 0x2122 || cp == 0x2139 || cp == 0x24C2 ||
            cp in 0x25AA..0x25FE ||
            cp == 0x3030 || cp == 0x303D || cp == 0x3297 || cp == 0x3299 ||
            cp == 0x200D || cp == 0xFE0F || cp == 0x20E3 ||   // ZWJ, VS16, keycap (components)
            cp in 0xE0020..0xE007F                            // tag characters (subdivision flags)

internal fun isEmoji(value: String): Boolean {
    if (value.isEmpty()) return false
    var i = 0
    while (i < value.length) {
        val cp = value.codePointAt(i)
        if (!isEmojiCodePoint(cp)) return false
        i += Character.charCount(cp)
    }
    return true
}

private const val HH_MM = "(?:[01]\\d|2[0-3]):[0-5]\\d"
private const val OFFSET = "[+-](?:[01]\\d|2[0-3]):[0-5]\\d"
private const val ISO_DATE = "(\\d{4})-(\\d{2})-(\\d{2})"

private fun requirePrecision(precision: Int?) {
    require(precision == null || precision >= -1) {
        "precision must be null (any), -1 (minutes), 0 (seconds) or the number of decimal digits"
    }
}

/** precision: null = HH:MM[:SS[.s+]], -1 = HH:MM, 0 = HH:MM:SS, n > 0 = HH:MM:SS.<n digits> */
internal fun isoTimeRegex(precision: Int?): Regex {
    requirePrecision(precision)
    val source = when {
        precision == null -> "$HH_MM(?::[0-5]\\d(?:\\.\\d+)?)?"
        precision == -1 -> HH_MM
        precision == 0 -> "$HH_MM:[0-5]\\d"
        else -> "$HH_MM:[0-5]\\d\\.\\d{$precision}"
    }
    return Regex(source)
}

/**
 * Exactly 3 capturing groups (year, month, day); everything else is non-capturing.
 * Seconds are required by default (RFC 3339), except for the unqualified `local` form.
 */
internal fun isoDateTimeRegex(offset: Boolean, local: Boolean, precision: Int?): Regex {
    requirePrecision(precision)
    val tz = if (offset) "Z|$OFFSET" else "Z"
    val tzSuffix = if (local) "(?:$tz)?" else "(?:$tz)"
    val time = when {
        precision == null && local ->
            "(?:$HH_MM(?::[0-5]\\d(?:\\.\\d+)?)?|$HH_MM:[0-5]\\d(?:\\.\\d+)?(?:$tz))"

        precision == null -> "$HH_MM:[0-5]\\d(?:\\.\\d+)?$tzSuffix"
        precision == -1 -> "$HH_MM$tzSuffix"
        precision == 0 -> "$HH_MM:[0-5]\\d$tzSuffix"
        else -> "$HH_MM:[0-5]\\d\\.\\d{$precision}$tzSuffix"
    }
    return Regex("${ISO_DATE}T$time")
}

internal val ISO_DATE_REGEX = Regex(ISO_DATE)

internal fun isValidCalendarDate(year: Int, month: Int, day: Int): Boolean {
    if (month !in 1..12 || day < 1) return false
    val leap = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    val daysInMonth = when (month) {
        2 -> if (leap) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }
    return day <= daysInMonth
}

/** Active ISO 4217 codes (incl. funds / precious metals / test codes). Re-check against ISO periodically. */
internal object CurrencyCodes {
    val ALL: Set<String> = (
            "AED AFN ALL AMD AOA ARS AUD AWG AZN BAM BBD BDT BHD BIF BMD BND BOB BOV BRL BSD BTN BWP BYN BZD " +
                    "CAD CDF CHE CHF CHW CLF CLP CNY COP COU CRC CUP CVE CZK DJF DKK DOP DZD EGP ERN ETB EUR FJD FKP " +
                    "GBP GEL GHS GIP GMD GNF GTQ GYD HKD HNL HTG HUF IDR ILS INR IQD IRR ISK JMD JOD JPY KES KGS KHR " +
                    "KMF KPW KRW KWD KYD KZT LAK LBP LKR LRD LSL LYD MAD MDL MGA MKD MMK MNT MOP MRU MUR MVR MWK MXN " +
                    "MXV MYR MZN NAD NGN NIO NOK NPR NZD OMR PAB PEN PGK PHP PKR PLN PYG QAR RON RSD RUB RWF SAR SBD " +
                    "SCR SDG SEK SGD SHP SLE SOS SRD SSP STN SVC SYP SZL THB TJS TMT TND TOP TRY TTD TWD TZS UAH UGX " +
                    "USD USN UYI UYU UYW UZS VED VES VND VUV WST XAF XAG XAU XBA XBB XBC XBD XCD XCG XDR XOF XPD XPF " +
                    "XPT XSU XTS XUA XXX YER ZAR ZMW ZWG"
            ).split(' ').toSet()
}

// ---------------------------------------------------------------------------
// Cases
// ---------------------------------------------------------------------------

sealed class StringCases {
    /** Shared base for cases that are just "the whole string must match this regex". */
    abstract class PatternCase(private val pattern: Regex) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            return if (pattern.matches(value)) null else getError()
        }
    }

    class NotBlankCase(
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            return if (value.isNotBlank()) null else getError()
        }

        override fun errorFactory(): String {
            return "Value must not be blank"
        }
    }

    class MinCase(
        val min: Int,
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            return if (value.length >= min) null else getError()
        }

        override fun errorFactory(): String {
            return "Length must be at least $min"
        }
    }

    class MaxCase(
        val max: Int,
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            return if (value.length <= max) null else getError()
        }

        override fun errorFactory(): String {
            return "Length must be at most $max"
        }
    }

    class RegexCase(
        val regex: Regex,
        val fullMatch: Boolean = true,
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            val ok = if (fullMatch) regex.matches(value) else regex.containsMatchIn(value)
            return if (ok) null else getError()
        }

        override fun errorFactory(): String {
            return "Value does not match pattern ${regex.pattern}"
        }
    }

    class EmojiCase(
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            return if (isEmoji(value)) null else getError()
        }

        override fun errorFactory(): String {
            return "Value must be an emoji"
        }
    }

    class Base64Case(
        override val userError: String? = null
    ) : PatternCase(StringPatterns.BASE64) {
        override fun errorFactory(): String {
            return "Value must be valid base64"
        }
    }

    class Base64UrlCase(
        override val userError: String? = null
    ) : PatternCase(StringPatterns.BASE64URL) {
        override fun errorFactory(): String {
            return "Value must be valid base64url"
        }
    }

    class EmailCase(
        pattern: Regex? = null,
        override val userError: String? = null
    ) : PatternCase(pattern ?: StringPatterns.EMAIL) {
        override fun errorFactory(): String {
            return "Invalid email address"
        }
    }

    class UuidCase(
        val version: UuidVersion? = UuidVersion.V4,
        override val userError: String? = null
    ) : PatternCase(StringPatterns.uuid(version)) {
        override fun errorFactory(): String {
            return if (version == null) "Invalid UUID" else "Invalid UUID ${version.name.lowercase()}"
        }
    }

    class GuidCase(
        override val userError: String? = null
    ) : PatternCase(StringPatterns.GUID) {
        override fun errorFactory(): String {
            return "Invalid GUID"
        }
    }

    class NanoidCase(
        val length: Int = 21,
        override val userError: String? = null
    ) : PatternCase(StringPatterns.nanoid(length)) {
        override fun errorFactory(): String {
            return "Invalid nanoid (expected $length characters of A-Z a-z 0-9 _ -)"
        }
    }

    class CuidCase(
        override val userError: String? = null
    ) : PatternCase(StringPatterns.CUID) {
        override fun errorFactory(): String {
            return "Invalid cuid"
        }
    }

    class Cuid2Case(
        override val userError: String? = null
    ) : PatternCase(StringPatterns.CUID2) {
        override fun errorFactory(): String {
            return "Invalid cuid2"
        }
    }

    class UlidCase(
        override val userError: String? = null
    ) : PatternCase(StringPatterns.ULID) {
        override fun errorFactory(): String {
            return "Invalid ULID"
        }
    }

    class HexColorCase(
        val lengths: Set<Int> = setOf(3, 4, 6, 8),
        val hash: Boolean = true,
        override val userError: String? = null
    ) : PatternCase(StringPatterns.hexColor(lengths, hash)) {
        override fun errorFactory(): String {
            val prefix = if (hash) "#" else ""
            return "Value must be a hex color (${prefix}RGB, with ${lengths.sorted().joinToString("/")} hex digits)"
        }
    }

    class UrlCase(
        val protocol: String? = null,
        val hostname: String? = null,
        override val userError: String? = null
    ) : BuilderCase<String>() {
        private val expectedProtocol = protocol?.removeSuffix(":")

        override fun validate(value: String): String? {
            val uri = try {
                URI(value)
            } catch (e: URISyntaxException) {
                return getError()
            }
            val scheme = uri.scheme ?: return getError()
            if (expectedProtocol != null && !scheme.equals(expectedProtocol, ignoreCase = true)) {
                return getError()
            }
            if (hostname != null && uri.host?.equals(hostname, ignoreCase = true) != true) {
                return getError()
            }
            return null
        }

        override fun errorFactory(): String {
            val parts = mutableListOf<String>()
            if (protocol != null) parts += "protocol $protocol"
            if (hostname != null) parts += "hostname $hostname"
            return if (parts.isEmpty()) "Invalid URL" else "Invalid URL (expected ${parts.joinToString(" and ")})"
        }
    }

    class E164Case(
        override val userError: String? = null
    ) : PatternCase(StringPatterns.E164) {
        override fun errorFactory(): String {
            return "Invalid E.164 phone number"
        }
    }

    class Ipv4Case(
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            return if (isIpv4(value)) null else getError()
        }

        override fun errorFactory(): String {
            return "Invalid IPv4 address"
        }
    }

    class Ipv6Case(
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            return if (isIpv6(value)) null else getError()
        }

        override fun errorFactory(): String {
            return "Invalid IPv6 address"
        }
    }

    class CidrV4Case(
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            return if (isCidr(value, v6 = false)) null else getError()
        }

        override fun errorFactory(): String {
            return "Invalid IPv4 CIDR"
        }
    }

    class CidrV6Case(
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            return if (isCidr(value, v6 = true)) null else getError()
        }

        override fun errorFactory(): String {
            return "Invalid IPv6 CIDR"
        }
    }

    class MacCase(
        val delimiter: String? = null,
        override val userError: String? = null
    ) : PatternCase(StringPatterns.mac(delimiter ?: ":")) {
        override fun errorFactory(): String {
            return "Invalid MAC address (delimiter '${delimiter ?: ":"}')"
        }
    }

    class CreditCardCase(
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            // digits with optional SINGLE space/hyphen between groups, no surrounding whitespace
            if (!StringPatterns.CREDIT_CARD_FORMAT.matches(value)) return getError()
            val digits = value.filter { it in '0'..'9' }
            if (digits.length !in 13..19) return getError()
            return if (luhn(digits)) null else getError()
        }

        override fun errorFactory(): String {
            return "Invalid credit card number"
        }
    }

    class CurrencyCodeCase(
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            return if (value in CurrencyCodes.ALL) null else getError()
        }

        override fun errorFactory(): String {
            return "Invalid ISO 4217 currency code"
        }
    }

    class IbanCase(
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            if (!StringPatterns.IBAN_FORMAT.matches(value)) return getError()
            if (value.substring(2, 4).toInt() !in 2..98) return getError()
            return if (ibanChecksumValid(value)) null else getError()
        }

        override fun errorFactory(): String {
            return "Invalid IBAN"
        }
    }

    class JwtCase(
        val algorithm: JwtAlgorithm? = null,
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            val parts = value.split('.')
            if (parts.size != 3) return getError()
            val (header, payload, _) = parts
            if (header.isEmpty() || payload.isEmpty()) return getError()
            if (!parts.all { StringPatterns.BASE64URL.matches(it) }) return getError()

            val json = try {
                String(Base64.getUrlDecoder().decode(header), Charsets.UTF_8).trim()
            } catch (e: IllegalArgumentException) {
                return getError()
            }
            if (!json.startsWith("{") || !json.endsWith("}")) return getError()

            val typ = StringPatterns.JWT_TYP.find(json)?.groupValues?.get(1)
            if (typ != null && typ != "JWT") return getError()

            val alg = StringPatterns.JWT_ALG.find(json)?.groupValues?.get(1)
            if (alg.isNullOrEmpty()) return getError()
            if (algorithm != null && alg != algorithm.name) return getError()
            return null
        }

        override fun errorFactory(): String {
            return if (algorithm == null) "Invalid JWT" else "Invalid JWT (expected algorithm ${algorithm.name})"
        }
    }

    class HashCase(
        val algorithm: HashAlgorithm,
        val encoding: HashEncoding = HashEncoding.HEX,
        override val userError: String? = null
    ) : PatternCase(StringPatterns.hash(algorithm, encoding)) {
        override fun errorFactory(): String {
            return "Invalid ${algorithm.name.lowercase()} hash (${encoding.name.lowercase()})"
        }
    }

    class LengthCase(
        val length: Int,
        override val userError: String? = null
    ) : BuilderCase<String>() {
        init {
            require(length >= 0) { "length must be >= 0" }
        }

        override fun validate(value: String): String? {
            return if (value.length == length) null else getError()
        }

        override fun errorFactory(): String {
            return "Length must be exactly $length"
        }
    }

    class IsoDateCase(
        override val userError: String? = null
    ) : BuilderCase<String>() {
        override fun validate(value: String): String? {
            val match = ISO_DATE_REGEX.matchEntire(value) ?: return getError()
            val (y, m, d) = match.destructured
            return if (isValidCalendarDate(y.toInt(), m.toInt(), d.toInt())) null else getError()
        }

        override fun errorFactory(): String {
            return "Invalid ISO date (expected YYYY-MM-DD)"
        }
    }

    class IsoTimeCase(
        val precision: Int? = null,
        override val userError: String? = null
    ) : PatternCase(isoTimeRegex(precision)) {
        override fun errorFactory(): String {
            val format = when {
                precision == null -> "HH:MM[:SS[.s+]]"
                precision == -1 -> "HH:MM"
                precision == 0 -> "HH:MM:SS"
                else -> "HH:MM:SS.${"s".repeat(precision)}"
            }
            return "Invalid ISO time (expected $format)"
        }
    }

    class IsoDateTimeCase(
        val offset: Boolean = false,
        val local: Boolean = false,
        val precision: Int? = null,
        override val userError: String? = null
    ) : BuilderCase<String>() {
        private val regex = isoDateTimeRegex(offset, local, precision)

        override fun validate(value: String): String? {
            val match = regex.matchEntire(value) ?: return getError()
            val (y, m, d) = match.destructured
            return if (isValidCalendarDate(y.toInt(), m.toInt(), d.toInt())) null else getError()
        }

        override fun errorFactory(): String {
            val zone = when {
                local && offset -> "with an optional Z or ±HH:MM offset"
                local -> "with an optional Z"
                offset -> "with a Z or ±HH:MM offset"
                else -> "ending in Z"
            }
            return "Invalid ISO datetime ($zone)"
        }
    }
}

// ---------------------------------------------------------------------------
// Schema
// ---------------------------------------------------------------------------

class StringSchema(
    override val root: FieldSchema? = null
) : BaseSchema<String, StringSchema>() {
    override val self get() = this

    fun notBlank(error: String? = null) = case(StringCases.NotBlankCase(userError = error))
    fun min(min: Int, error: String? = null) = case(StringCases.MinCase(min = min, userError = error))
    fun max(max: Int, error: String? = null) = case(StringCases.MaxCase(max = max, userError = error))

    fun regex(regex: Regex, fullMatch: Boolean = true, error: String? = null) =
        case(StringCases.RegexCase(regex = regex, fullMatch = fullMatch, userError = error))

    fun emoji(error: String? = null) = case(StringCases.EmojiCase(userError = error))
    fun base64(error: String? = null) = case(StringCases.Base64Case(userError = error))
    fun base64url(error: String? = null) = case(StringCases.Base64UrlCase(userError = error))

    fun email(pattern: Regex? = null, error: String? = null) =
        case(StringCases.EmailCase(pattern = pattern, userError = error))

    /** [version] = null accepts any version (plus the nil and max UUIDs). */
    fun uuid(version: UuidVersion? = UuidVersion.V4, error: String? = null) =
        case(StringCases.UuidCase(version = version, userError = error))

    fun guid(error: String? = null) = case(StringCases.GuidCase(userError = error))
    fun nanoid(length: Int = 21, error: String? = null) =
        case(StringCases.NanoidCase(length = length, userError = error))

    fun cuid(error: String? = null) = case(StringCases.CuidCase(userError = error))
    fun cuid2(error: String? = null) = case(StringCases.Cuid2Case(userError = error))
    fun ulid(error: String? = null) = case(StringCases.UlidCase(userError = error))

    fun hexColor(
        lengths: Set<Int> = setOf(3, 4, 6, 8),
        hash: Boolean = true,
        error: String? = null
    ) = case(StringCases.HexColorCase(lengths = lengths, hash = hash, userError = error))

    fun url(protocol: String? = null, hostname: String? = null, error: String? = null) =
        case(StringCases.UrlCase(protocol = protocol, hostname = hostname, userError = error))

    fun e164(error: String? = null) = case(StringCases.E164Case(userError = error))
    fun ipv4(error: String? = null) = case(StringCases.Ipv4Case(userError = error))
    fun ipv6(error: String? = null) = case(StringCases.Ipv6Case(userError = error))
    fun cidrv4(error: String? = null) = case(StringCases.CidrV4Case(userError = error))
    fun cidrv6(error: String? = null) = case(StringCases.CidrV6Case(userError = error))

    fun mac(delimiter: String? = null, error: String? = null) =
        case(StringCases.MacCase(delimiter = delimiter, userError = error))

    fun creditCard(error: String? = null) = case(StringCases.CreditCardCase(userError = error))
    fun currencyCode(error: String? = null) = case(StringCases.CurrencyCodeCase(userError = error))
    fun iban(error: String? = null) = case(StringCases.IbanCase(userError = error))

    fun jwt(algorithm: JwtAlgorithm? = null, error: String? = null) =
        case(StringCases.JwtCase(algorithm = algorithm, userError = error))

    fun hash(
        algorithm: HashAlgorithm,
        encoding: HashEncoding = HashEncoding.HEX,
        error: String? = null
    ) = case(StringCases.HashCase(algorithm = algorithm, encoding = encoding, userError = error))

    fun length(length: Int, error: String? = null) =
        case(StringCases.LengthCase(length = length, userError = error))

    fun isoDate(error: String? = null) = case(StringCases.IsoDateCase(userError = error))

    /** precision: null = HH:MM[:SS[.s+]], -1 = HH:MM, 0 = HH:MM:SS, n = n decimal digits. */
    fun isoTime(precision: Int? = null, error: String? = null) =
        case(StringCases.IsoTimeCase(precision = precision, userError = error))

    /**
     * offset: also allow ±HH:MM offsets (Z is always allowed unless [local] makes the zone optional).
     * local: allow datetimes without a timezone (seconds become optional only in that unqualified form).
     * precision: null = seconds required + arbitrary decimals, -1 = minutes only, 0 = seconds only, n = n decimals.
     */
    fun isoDateTime(
        offset: Boolean = false,
        local: Boolean = false,
        precision: Int? = null,
        error: String? = null
    ) = case(StringCases.IsoDateTimeCase(offset = offset, local = local, precision = precision, userError = error))
}