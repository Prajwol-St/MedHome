package com.example.medhomeapp.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateTimeUtils {

    private const val DATE_FORMAT = "yyyy-MM-dd"
    private const val TIME_FORMAT = "HH:mm"
    private const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm"
    private const val DISPLAY_DATE_FORMAT = "dd MMM yyyy"
    private const val DISPLAY_TIME_FORMAT = "hh:mm a"
    private const val DISPLAY_DATE_TIME_FORMAT = "dd MMM yyyy, hh:mm a"
    private const val DAY_NAME_FORMAT = "EEEE"

    // Get current date in yyyy-MM-dd format
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return sdf.format(Date())
    }

    // Get current time in HH:mm format
    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        return sdf.format(Date())
    }

    // Get current timestamp
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }

    // Format date for display (e.g., "16 Jan 2026")
    fun formatDateForDisplay(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            parsedDate?.let { outputFormat.format(it) } ?: date
        } catch (e: Exception) {
            date
        }
    }

    // Format time for display (e.g., "02:30 PM")
    fun formatTimeForDisplay(time: String): String {
        return try {
            val inputFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat(DISPLAY_TIME_FORMAT, Locale.getDefault())
            val parsedTime = inputFormat.parse(time)
            parsedTime?.let { outputFormat.format(it) } ?: time
        } catch (e: Exception) {
            time
        }
    }

    // Format timestamp to readable date
    fun formatTimestampToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // Format timestamp to readable date and time
    fun formatTimestampToDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat(DISPLAY_DATE_TIME_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // Get day name from date (e.g., "Monday")
    fun getDayName(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat(DAY_NAME_FORMAT, Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            parsedDate?.let { outputFormat.format(it) } ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    // Check if date is today
    fun isToday(date: String): Boolean {
        return date == getCurrentDate()
    }

    // Check if date is in the past
    fun isPastDate(date: String): Boolean {
        return try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val inputDate = sdf.parse(date)
            val today = sdf.parse(getCurrentDate())
            inputDate?.before(today) ?: false
        } catch (e: Exception) {
            false
        }
    }

    // Check if date is in the future
    fun isFutureDate(date: String): Boolean {
        return try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val inputDate = sdf.parse(date)
            val today = sdf.parse(getCurrentDate())
            inputDate?.after(today) ?: false
        } catch (e: Exception) {
            false
        }
    }

    // Get days between two dates
    fun getDaysBetween(startDate: String, endDate: String): Int {
        return try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val start = sdf.parse(startDate)
            val end = sdf.parse(endDate)
            if (start != null && end != null) {
                val diff = end.time - start.time
                TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
            } else 0
        } catch (e: Exception) {
            0
        }
    }

    // Get hours between two times
    fun getHoursBetween(startTime: String, endTime: String): Int {
        return try {
            val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            val start = sdf.parse(startTime)
            val end = sdf.parse(endTime)
            if (start != null && end != null) {
                val diff = end.time - start.time
                TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS).toInt()
            } else 0
        } catch (e: Exception) {
            0
        }
    }

    // Check if a date is within a range
    fun isDateInRange(date: String, startDate: String, endDate: String): Boolean {
        return try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val checkDate = sdf.parse(date)
            val start = sdf.parse(startDate)
            val end = sdf.parse(endDate)

            if (checkDate != null && start != null && end != null) {
                !checkDate.before(start) && !checkDate.after(end)
            } else false
        } catch (e: Exception) {
            false
        }
    }

    // Add days to a date
    fun addDaysToDate(date: String, days: Int): String {
        return try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.time = sdf.parse(date) ?: Date()
            calendar.add(Calendar.DAY_OF_MONTH, days)
            sdf.format(calendar.time)
        } catch (e: Exception) {
            date
        }
    }

    // Get time remaining until appointment (e.g., "2 hours 30 minutes")
    fun getTimeRemaining(date: String, time: String): String {
        return try {
            val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
            val appointmentDateTime = sdf.parse("$date $time")
            val now = Date()

            if (appointmentDateTime != null && appointmentDateTime.after(now)) {
                val diff = appointmentDateTime.time - now.time
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60

                when {
                    hours > 24 -> {
                        val days = TimeUnit.MILLISECONDS.toDays(diff)
                        "$days day${if (days > 1) "s" else ""}"
                    }
                    hours > 0 -> "$hours hour${if (hours > 1) "s" else ""} $minutes min"
                    else -> "$minutes minute${if (minutes > 1) "s" else ""}"
                }
            } else "Past"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    // Check if time slot conflicts with another
    fun isTimeSlotConflict(
        slot1Start: String,
        slot1End: String,
        slot2Start: String,
        slot2End: String
    ): Boolean {
        return try {
            val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            val s1Start = sdf.parse(slot1Start)
            val s1End = sdf.parse(slot1End)
            val s2Start = sdf.parse(slot2Start)
            val s2End = sdf.parse(slot2End)

            if (s1Start != null && s1End != null && s2Start != null && s2End != null) {
                s1Start.before(s2End) && s1End.after(s2Start)
            } else false
        } catch (e: Exception) {
            false
        }
    }

    // Format relative time (e.g., "2 hours ago", "Just now")
    fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "$minutes minute${if (minutes > 1) "s" else ""} ago"
            }
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "$hours hour${if (hours > 1) "s" else ""} ago"
            }
            diff < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "$days day${if (days > 1) "s" else ""} ago"
            }
            else -> formatTimestampToDate(timestamp)
        }
    }

    // Get list of dates between two dates
    fun getDatesBetween(startDate: String, endDate: String): List<String> {
        val dates = mutableListOf<String>()
        try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val start = sdf.parse(startDate)
            val end = sdf.parse(endDate)

            if (start != null && end != null) {
                val calendar = Calendar.getInstance()
                calendar.time = start

                while (!calendar.time.after(end)) {
                    dates.add(sdf.format(calendar.time))
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dates
    }

    // Check if appointment can be cancelled (e.g., at least 24 hours before)
    fun canCancelAppointment(appointmentDate: String, appointmentTime: String, hoursBeforeLimit: Int = 24): Boolean {
        return try {
            val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
            val appointmentDateTime = sdf.parse("$appointmentDate $appointmentTime")
            val now = Date()

            if (appointmentDateTime != null) {
                val diff = appointmentDateTime.time - now.time
                val hoursRemaining = TimeUnit.MILLISECONDS.toHours(diff)
                hoursRemaining >= hoursBeforeLimit
            } else false
        } catch (e: Exception) {
            false
        }
    }
}