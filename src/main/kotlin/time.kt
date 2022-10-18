import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.format.SignStyle
import java.time.temporal.ChronoField

fun main() {

    val now = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC)
    val serialNow = dateTimeToString(now)
    val deserialNow = fromString(serialNow)

    println("$now -> toString: $serialNow")
    println("deserial -> $deserialNow")
    println("equals: ${now.equals(deserialNow)}")

    val other = "1970-01-01T03:12:10.500Z"
    val other2 = "1970-01-01T03:12:10.500000000Z"

    println("other: ${fromString(other)}")
    println("other2: ${fromString(other2)}")
    println("equals2: ${fromString(other)!! == fromString(other2)}")


    val other3 = "1970-01-01T03:12:10Z"
    println("other3: ${fromString(other3)}")
    println("other3 conv: ${ZonedDateTime.parse(other3)}")
    println("other3 conv2: ${dateTimeToString(ZonedDateTime.parse(other3))}")
}

private val DATE_TIME_FORMATTER = DateTimeFormatterBuilder()
    .appendValue(ChronoField.YEAR, 4, 4, SignStyle.NOT_NEGATIVE)
    .appendLiteral('-')
    .appendValue(ChronoField.MONTH_OF_YEAR, 2, 2, SignStyle.NOT_NEGATIVE)
    .appendLiteral('-')
    .appendValue(ChronoField.DAY_OF_MONTH, 2, 2, SignStyle.NOT_NEGATIVE)
    .appendLiteral('T')
    .appendValue(ChronoField.HOUR_OF_DAY, 2, 2, SignStyle.NOT_NEGATIVE)
    .appendLiteral(':')
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2, 2, SignStyle.NOT_NEGATIVE)
    .appendLiteral(':')
    .appendValue(ChronoField.SECOND_OF_MINUTE, 2, 2, SignStyle.NOT_NEGATIVE)
    .appendLiteral('.')
    .appendValue(ChronoField.NANO_OF_SECOND, 9, 9, SignStyle.NOT_NEGATIVE)
    .appendPattern("X")
    .toFormatter()

/**
 * Returns a string representation of supplied zonedDateTime in the UTC timezone, formatted with
 * nanosecond resolution.
 */
fun dateTimeToString(dateTime: ZonedDateTime?): String? {
    return dateTime?.withZoneSameInstant(ZoneOffset.UTC)?.format(DATE_TIME_FORMATTER)
}

fun fromString(dateTimeString: String?): ZonedDateTime? {
    return try {
        dateTimeString?.let {
            println("parsing ${migrateMillisToNanos(it)}")
            ZonedDateTime.parse(migrateMillisToNanos(it), DATE_TIME_FORMATTER)
        }
    } catch (e: DateTimeParseException) {
        e.printStackTrace()
        null
    }
}

private fun migrateMillisToNanos(original: String): String {
    if (original.contains(".")) {
        val splitMs = original.split(".")
        val splitZone = splitMs.last().split("Z", "-", "+")
        val ms = splitZone.first()
        val nano = rightPadZeros(ms, 9)
        val zone = when {
            original.endsWith("Z") -> "Z"
            original.contains("+") -> "+" + splitZone.drop(1).joinToString("-")
            else -> "-" + splitZone.drop(1).joinToString("-")
        }
        return splitMs.dropLast(1).joinToString() + "." + nano + zone
    } else {
        return original
    }
}

private fun rightPadZeros(str: String, num: Int): String {
    return String.format("%1$-" + num + "s", str).replace(' ', '0')
}
