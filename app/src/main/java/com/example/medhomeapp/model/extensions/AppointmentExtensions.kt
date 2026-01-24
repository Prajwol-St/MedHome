package com.example.medhomeapp.model.extensions

import com.example.medhomeapp.model.AppointmentModel

fun AppointmentModel.getDateTimeInMillis(): Long {
    return com.example.medhomeapp.utils.DateTimeUtils.parseDateTime(date, time)
}

fun AppointmentModel.get24HourReminderTime(): Long {
    val appointmentTime = getDateTimeInMillis()
    return appointmentTime - (24 * 60 * 60 * 1000) // 24 hours before
}

fun AppointmentModel.get1HourReminderTime(): Long {
    val appointmentTime = getDateTimeInMillis()
    return appointmentTime - (60 * 60 * 1000) // 1 hour before
}

fun AppointmentModel.isUpcoming(): Boolean {
    return com.example.medhomeapp.utils.DateTimeUtils.isAppointmentTimeValid(date, time)
}

fun AppointmentModel.shouldScheduleReminders(): Boolean {
    // Only schedule reminders for upcoming appointments with status "scheduled" or "pending"
    return isUpcoming() && (status == "scheduled" || status == "pending" || status == "confirmed")
}

fun AppointmentModel.getHoursUntilAppointment(): Long {
    return com.example.medhomeapp.utils.DateTimeUtils.getTimeDifferenceInHours(date, time)
}