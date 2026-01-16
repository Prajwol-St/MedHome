package com.example.medhomeapp.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

// Context Extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

// Fragment Extensions
fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().showToast(message, duration)
}

fun Fragment.showLongToast(message: String) {
    requireContext().showLongToast(message)
}

// View Extensions
fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE

fun View.isGone(): Boolean = visibility == View.GONE

fun View.isInvisible(): Boolean = visibility == View.INVISIBLE

fun View.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}

fun View.showLongSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun View.showSnackbarWithAction(
    message: String,
    actionText: String,
    action: (View) -> Unit
) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setAction(actionText, action)
        .show()
}

// String Extensions
fun String.isValidEmail(): Boolean = ValidationUtils.isValidEmail(this)

fun String.isValidPhone(): Boolean = ValidationUtils.isValidPhoneNumber(this)

fun String.isValidPassword(): Boolean = ValidationUtils.isValidPassword(this)

fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

fun String.toInitials(): String {
    return split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()
}

fun String.truncate(maxLength: Int, ellipsis: String = "..."): String {
    return if (length > maxLength) {
        "${take(maxLength - ellipsis.length)}$ellipsis"
    } else this
}

// Double Extensions
fun Double.formatAsCurrency(): String {
    return "₹%.2f".format(this)
}

fun Double.formatAsWholeNumber(): String {
    return "₹${toInt()}"
}

// Float Extensions
fun Float.formatRating(): String {
    return "%.1f".format(this)
}

// List Extensions
fun <T> List<T>.isNotNullOrEmpty(): Boolean {
    return isNotEmpty()
}

// StateFlow Result Extensions
fun Pair<Boolean?, String?>.isSuccess(): Boolean = first == true

fun Pair<Boolean?, String?>.isFailure(): Boolean = first == false

fun Pair<Boolean?, String?>.isPending(): Boolean = first == null

fun Pair<Boolean, String>.isSuccess(): Boolean = first

fun Pair<Boolean, String>.getMessage(): String = second

// Date/Time Extensions
fun String.formatAsDisplayDate(): String = DateTimeUtils.formatDateForDisplay(this)

fun String.formatAsDisplayTime(): String = DateTimeUtils.formatTimeForDisplay(this)

fun String.isToday(): Boolean = DateTimeUtils.isToday(this)

fun String.isPast(): Boolean = DateTimeUtils.isPastDate(this)

fun String.isFuture(): Boolean = DateTimeUtils.isFutureDate(this)

fun String.getDayName(): String = DateTimeUtils.getDayName(this)

fun Long.toRelativeTime(): String = DateTimeUtils.getRelativeTime(this)

fun Long.toDisplayDate(): String = DateTimeUtils.formatTimestampToDate(this)

fun Long.toDisplayDateTime(): String = DateTimeUtils.formatTimestampToDateTime(this)

// Collection Extensions for ViewModels
fun <T> List<T>.filterByQuery(query: String, predicate: (T, String) -> Boolean): List<T> {
    return if (query.isBlank()) this else filter { predicate(it, query) }
}

// Safe navigation for nullable lists
fun <T> List<T>?.orEmpty(): List<T> = this ?: emptyList()

// Appointment Status Extensions
fun String.getStatusColor(): Int {
    return when (this.lowercase()) {
        "scheduled", "upcoming" -> android.R.color.holo_green_dark
        "completed" -> android.R.color.holo_blue_dark
        "cancelled" -> android.R.color.holo_red_dark
        "no-show" -> android.R.color.holo_orange_dark
        else -> android.R.color.darker_gray
    }
}

fun String.getStatusText(): String {
    return when (this.lowercase()) {
        "scheduled" -> "Scheduled"
        "completed" -> "Completed"
        "cancelled" -> "Cancelled"
        "no-show" -> "No Show"
        else -> this.capitalizeWords()
    }
}