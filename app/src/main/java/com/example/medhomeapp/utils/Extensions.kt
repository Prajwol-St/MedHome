package com.example.medhomeapp.utils

import android.content.Context
import android.widget.Toast
import java.util.*

fun String.toFormattedDate(): String {
    return DateTimeUtils.formatDateForDisplay(this)
}

fun String.toFormattedTime(): String {
    return DateTimeUtils.formatTimeForDisplay(this)
}

fun String.isValidEmail(): Boolean {
    return ValidationUtils.isValidEmail(this)
}

fun String.isValidPhone(): Boolean {
    return ValidationUtils.isValidPhone(this)
}

fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }
}

fun String.truncate(maxLength: Int, ellipsis: String = "..."): String {
    return if (this.length > maxLength) {
        "${this.substring(0, maxLength)}$ellipsis"
    } else {
        this
    }
}

// Long (timestamp) extensions
fun Long.toDateonlyString(): String {
    return DateTimeUtils.formatTimestampToDate(this)
}

fun Long.toTimeonlyString(): String {
    return DateTimeUtils.formatTimestampToTime(this)
}

fun Long.toDateTimeString(): String {
    return DateTimeUtils.formatTimestampToDateTime(this)
}

fun Long.toDisplayDateString(): String {
    return DateTimeUtils.formatDateForDisplay(this.toDateString())
}

fun Long.toDisplayTimeString(): String {
    return DateTimeUtils.formatTimeForDisplay(this.toTimeString())
}

fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
        hours < 24 -> "$hours hour${if (hours > 1) "s" else ""} ago"
        days < 7 -> "$days day${if (days > 1) "s" else ""} ago"
        days < 30 -> "${days / 7} week${if (days / 7 > 1) "s" else ""} ago"
        else -> DateTimeUtils.formatTimestampForDisplay(this)
    }
}

// Float (rating) extensions
fun Float.toRatingString(): String {
    return String.format("%.1f", this)
}

fun Float.toStars(): String {
    val fullStars = this.toInt()
    val hasHalfStar = (this - fullStars) >= 0.5
    val emptyStars = 5 - fullStars - if (hasHalfStar) 1 else 0

    return "★".repeat(fullStars) +
            (if (hasHalfStar) "½" else "") +
            "☆".repeat(emptyStars)
}

// Double (fee/price) extensions
fun Double.toCurrency(): String {
    return "NPR ${String.format("%.2f", this)}"
}

fun Double.toCompactCurrency(): String {
    return when {
        this >= 1000 -> "NPR ${String.format("%.1fk", this / 1000)}"
        else -> "NPR ${this.toInt()}"
    }
}

// Context extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

// List extensions
fun <T> List<T>.secondOrNull(): T? {
    return if (this.size >= 2) this[1] else null
}

fun <T> List<T>.thirdOrNull(): T? {
    return if (this.size >= 3) this[2] else null
}

// Int extensions for minutes
fun Int.toHoursMinutesString(): String {
    val hours = this / 60
    val minutes = this % 60
    return when {
        hours == 0 -> "$minutes min"
        minutes == 0 -> "$hours hr"
        else -> "$hours hr $minutes min"
    }
}