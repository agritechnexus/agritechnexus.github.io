package com.fixmybill.app.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Date
import java.util.Locale

/**
 * Formats a Double as Indian Rupee currency string.
 * Example: 2847.0 -> "₹2,847.00"
 */
fun Double.toCurrencyString(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    format.currency = Currency.getInstance("INR")
    format.minimumFractionDigits = 2
    format.maximumFractionDigits = 2
    return format.format(this)
}

/**
 * Formats a Long (epoch millis) to a human-readable date string.
 * Example: 1700000000000 -> "14 Nov 2023"
 */
fun Long.toDateString(pattern: String = Constants.DATE_FORMAT_DISPLAY): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(Date(this))
}

/**
 * Formats a Long (epoch millis) to a full date-time string.
 * Example: 1700000000000 -> "14 November 2023, 06:13 PM"
 */
fun Long.toFullDateTimeString(): String {
    return this.toDateString(Constants.DATE_FORMAT_FULL)
}

/**
 * Parses a date string to LocalDate using the given pattern.
 * Returns null if parsing fails.
 */
fun String.toLocalDate(pattern: String = Constants.DATE_FORMAT_API): LocalDate? {
    return try {
        LocalDate.parse(this, DateTimeFormatter.ofPattern(pattern))
    } catch (e: Exception) {
        null
    }
}

/**
 * Formats a Float as a percentage string.
 * Example: 0.856f -> "85.6%"
 */
fun Float.toPercentageString(decimalPlaces: Int = 1): String {
    val percentage = this * 100
    return String.format(Locale.getDefault(), "%.${decimalPlaces}f%%", percentage)
}

/**
 * Formats units consumed with appropriate suffix.
 * Example: 245.0 -> "245 kWh"
 */
fun Double.toUnitsString(unit: String = "kWh"): String {
    return if (this == this.toLong().toDouble()) {
        "${this.toLong()} $unit"
    } else {
        String.format(Locale.getDefault(), "%.1f %s", this, unit)
    }
}

/**
 * Truncates a string to the specified length with ellipsis.
 */
fun String.truncate(maxLength: Int = 50): String {
    return if (this.length <= maxLength) this
    else "${this.take(maxLength - 3)}..."
}
