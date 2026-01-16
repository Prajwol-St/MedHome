package com.example.medhomeapp.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateTimeUtils {

    private val dateFormat = SimpleDateFormat(AppConstants.DATE_FORMAT, Locale.getDefault())
    private val timeFormat = SimpleDateFormat(AppConstants.TIME_FORMAT, Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat(AppConstants.DATETIME_FORMAT, Locale.getDefault())

    private val displayDateFormat = SimpleDateFormat(AppConstants.DISPLAY_DATE_FORMAT, Locale.getDefault())
    private val displayTimeFormat = SimpleDateFormat(AppConstants.DISPLAY_TIME_FORMAT, Locale.getDefault())
    private val displayDateTimeFormat = SimpleDateFormat(AppConstants.DISPLAY_DATETIME_FORMAT, Locale.getDefault())

    // Get current date and time
    fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }

    fun getCurrentTime(): String {
        return timeFormat.format(Date())
    }

    fun getCurrentDateTime(): String {
        return dateTimeFormat.format(Date())
    }

    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }

    // Format date and time for display
    fun formatDateForDisplay(date: String): String {
        return try {
            val parsedDate = dateFormat.parse(date)
            parsedDate?.let { displayDateFormat.format(it) } ?: date
        } catch (e: Exception) {
            date
        }
    }

    fun formatTimeForDisplay(time: String): String {
        return try {
            val parsedTime = timeFormat.parse(time)
            parsedTime?.let { displayTimeFormat.format(it) } ?: time
        } catch (e: Exception) {
            time
        }
    }

    fun formatDateTimeForDisplay(date: String, time: String): String {
        return try {
            val dateTime = "$date $time"
            val parsedDateTime = dateTimeFormat.parse(dateTime)
            parsedDateTime?.let { displayDateTimeFormat.format(it) } ?: dateTime
        } catch (e: Exception) {
            "$date $time"
        }
    }

    fun formatTimestampToDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatTimestampToTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun formatTimestampToDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }

    fun formatTimestampForDisplay(timestamp: Long): String {
        return displayDateTimeFormat.format(Date(timestamp))
    }

    // Date validation and comparison
    fun isDateInPast(date: String): Boolean {
        return try {
            val parsedDate = dateFormat.parse(date)
            val today = dateFormat.parse(getCurrentDate())
            parsedDate != null && today != null && parsedDate.before(today)
        } catch (e: Exception) {
            false
        }
    }

    fun isDateInFuture(date: String): Boolean {
        return try {
            val parsedDate = dateFormat.parse(date)
            val today = dateFormat.parse(getCurrentDate())
            parsedDate != null && today != null && parsedDate.after(today)
        } catch (e: Exception) {
            false
        }
    }

    fun isDateToday(date: String): Boolean {
        return date == getCurrentDate()
    }

    fun isDateTomorrow(date: String): Boolean {
        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
        }
        return date == dateFormat.format(tomorrow.time)
    }

    fun compareDates(date1: String, date2: String): Int {
        return try {
            val d1 = dateFormat.parse(date1)
            val d2 = dateFormat.parse(date2)
            when {
                d1 == null || d2 == null -> 0
                d1.before(d2) -> -1
                d1.after(d2) -> 1
                else -> 0
            }
        } catch (e: Exception) {
            0
        }
    }

    // Date/Time calculations
    fun parseDateTime(date: String, time: String): Long {
        return try {
            val dateTime = "$date $time"
            dateTimeFormat.parse(dateTime)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    fun getTimeDifferenceInHours(date: String, time: String): Long {
        val appointmentTime = parseDateTime(date, time)
        val now = System.currentTimeMillis()
        val diffInMillis = appointmentTime - now
        return TimeUnit.MILLISECONDS.toHours(diffInMillis)
    }

    fun getTimeDifferenceInMinutes(date: String, time: String): Long {
        val appointmentTime = parseDateTime(date, time)
        val now = System.currentTimeMillis()
        val diffInMillis = appointmentTime - now
        return TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
    }

    fun getTimeDifferenceInDays(date: String): Long {
        return try {
            val targetDate = dateFormat.parse(date)
            val today = dateFormat.parse(getCurrentDate())
            if (targetDate != null && today != null) {
                val diffInMillis = targetDate.time - today.time
                TimeUnit.MILLISECONDS.toDays(diffInMillis)
            } else {
                0L
            }
        } catch (e: Exception) {
            0L
        }
    }

    // Appointment-specific validations
    fun canCancelAppointment(date: String, time: String): Boolean {
        val hoursUntilAppointment = getTimeDifferenceInHours(date, time)
        return hoursUntilAppointment >= AppConstants.MIN_HOURS_BEFORE_CANCELLATION
    }

    fun canRescheduleAppointment(date: String, time: String): Boolean {
        val hoursUntilAppointment = getTimeDifferenceInHours(date, time)
        return hoursUntilAppointment >= AppConstants.MIN_HOURS_BEFORE_RESCHEDULE
    }

    fun isAppointmentTimeValid(date: String, time: String): Boolean {
        val appointmentTime = parseDateTime(date, time)
        val now = System.currentTimeMillis()
        return appointmentTime > now
    }

    // Date range operations
    fun isDateInRange(checkDate: String, startDate: String, endDate: String): Boolean {
        return try {
            val check = dateFormat.parse(checkDate)
            val start = dateFormat.parse(startDate)
            val end = dateFormat.parse(endDate)

            check != null && start != null && end != null &&
                    !check.before(start) && !check.after(end)
        } catch (e: Exception) {
            false
        }
    }

    fun addDaysToDate(date: String, days: Int): String {
        return try {
            val parsedDate = dateFormat.parse(date)
            val calendar = Calendar.getInstance()
            calendar.time = parsedDate ?: Date()
            calendar.add(Calendar.DAY_OF_MONTH, days)
            dateFormat.format(calendar.time)
        } catch (e: Exception) {
            date
        }
    }

    fun getDayOfWeek(date: String): String {
        return try {
            val parsedDate = dateFormat.parse(date)
            val calendar = Calendar.getInstance()
            calendar.time = parsedDate ?: Date()
            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            dayFormat.format(calendar.time)
        } catch (e: Exception) {
            ""
        }
    }

    // Relative time descriptions
    fun getRelativeTimeDescription(date: String): String {
        return when {
            isDateToday(date) -> "Today"
            isDateTomorrow(date) -> "Tomorrow"
            isDateInPast(date) -> {
                val daysAgo = -getTimeDifferenceInDays(date)
                when {
                    daysAgo == 1L -> "Yesterday"
                    daysAgo < 7 -> "$daysAgo days ago"
                    daysAgo < 30 -> "${daysAgo / 7} weeks ago"
                    else -> formatDateForDisplay(date)
                }
            }
            else -> {
                val daysFromNow = getTimeDifferenceInDays(date)
                when {
                    daysFromNow < 7 -> "In $daysFromNow days"
                    daysFromNow < 30 -> "In ${daysFromNow / 7} weeks"
                    else -> formatDateForDisplay(date)
                }
            }
        }
    }

    // Time slot helpers
    fun calculateEndTime(startTime: String, durationMinutes: Int): String {
        return try {
            val parsedTime = timeFormat.parse(startTime)
            val calendar = Calendar.getInstance()
            calendar.time = parsedTime ?: Date()
            calendar.add(Calendar.MINUTE, durationMinutes)
            timeFormat.format(calendar.time)
        } catch (e: Exception) {
            startTime
        }
    }

    fun generateTimeSlots(
        startTime: String,
        endTime: String,
        slotDuration: Int
    ): List<Pair<String, String>> {
        val slots = mutableListOf<Pair<String, String>>()
        try {
            val start = timeFormat.parse(startTime) ?: return slots
            val end = timeFormat.parse(endTime) ?: return slots

            val calendar = Calendar.getInstance()
            calendar.time = start

            while (calendar.time.before(end)) {
                val slotStart = timeFormat.format(calendar.time)
                calendar.add(Calendar.MINUTE, slotDuration)

                if (calendar.time.after(end)) break

                val slotEnd = timeFormat.format(calendar.time)
                slots.add(slotStart to slotEnd)
            }
        } catch (e: Exception) {
            // Return empty list on error
        }
        return slots
    }
}