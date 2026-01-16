package com.example.medhomeapp.utils

import com.example.medhomeapp.model.AppointmentModel
import com.example.medhomeapp.model.DoctorLeaveModel
import com.example.medhomeapp.model.TimeSlot

object ValidationUtils {

    // Email validation
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    // Phone validation
    fun isValidPhone(phone: String): Boolean {
        if (phone.isBlank()) return false
        // Accepts formats: +977-9841234567, 9841234567, +9779841234567
        val phonePattern = "^[+]?[0-9]{10,14}$"
        return phone.replace("-", "").replace(" ", "").matches(phonePattern.toRegex())
    }

    // Password validation
    fun isValidPassword(password: String): Pair<Boolean, String> {
        return when {
            password.length < 6 -> false to "Password must be at least 6 characters"
            password.length > 20 -> false to "Password must not exceed 20 characters"
            !password.any { it.isDigit() } -> false to "Password must contain at least one number"
            !password.any { it.isLetter() } -> false to "Password must contain at least one letter"
            else -> true to "Valid password"
        }
    }

    // Name validation
    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.length >= 2 && name.all { it.isLetter() || it.isWhitespace() }
    }

    // Appointment booking validation
    fun validateAppointmentBooking(
        slot: TimeSlot,
        patientNotes: String
    ): Pair<Boolean, String> {
        // Check if slot is available
        if (!slot.isAvailable) {
            return false to "This time slot is not available"
        }

        // Check if slot is already booked
        if (slot.isBooked) {
            return false to "This time slot is already booked"
        }

        // Check if date is in future
        if (DateTimeUtils.isDateInPast(slot.date)) {
            return false to "Cannot book appointment for a past date"
        }

        // Check if time is valid (at least 1 hour from now for same day)
        if (DateTimeUtils.isDateToday(slot.date)) {
            val hoursUntil = DateTimeUtils.getTimeDifferenceInHours(slot.date, slot.startTime)
            if (hoursUntil < 1) {
                return false to "Please book at least 1 hour in advance"
            }
        }

        // Validate notes length
        if (patientNotes.isNotBlank() && patientNotes.length > 500) {
            return false to "Notes cannot exceed 500 characters"
        }

        return true to "Booking is valid"
    }

    // Cancellation validation
    fun validateCancellation(
        appointment: AppointmentModel,
        reason: String
    ): Pair<Boolean, String> {
        // Check if appointment is already cancelled
        if (appointment.status == AppConstants.STATUS_CANCELLED) {
            return false to "This appointment is already cancelled"
        }

        // Check if appointment is already completed
        if (appointment.status == AppConstants.STATUS_COMPLETED) {
            return false to "Cannot cancel a completed appointment"
        }

        // Check if cancellation is within allowed time
        if (!DateTimeUtils.canCancelAppointment(appointment.date, appointment.time)) {
            return false to "Cannot cancel appointment less than ${AppConstants.MIN_HOURS_BEFORE_CANCELLATION} hours before scheduled time"
        }

        // Validate reason
        if (reason.isBlank()) {
            return false to "Please provide a cancellation reason"
        }

        if (reason.length > 200) {
            return false to "Cancellation reason cannot exceed 200 characters"
        }

        return true to "Cancellation is valid"
    }

    // Reschedule validation
    fun validateReschedule(
        currentAppointment: AppointmentModel,
        newDate: String,
        newTime: String
    ): Pair<Boolean, String> {
        // Check if current appointment can be rescheduled
        if (currentAppointment.status == AppConstants.STATUS_CANCELLED) {
            return false to "Cannot reschedule a cancelled appointment"
        }

        if (currentAppointment.status == AppConstants.STATUS_COMPLETED) {
            return false to "Cannot reschedule a completed appointment"
        }

        // Check if reschedule is within allowed time
        if (!DateTimeUtils.canRescheduleAppointment(currentAppointment.date, currentAppointment.time)) {
            return false to "Cannot reschedule appointment less than ${AppConstants.MIN_HOURS_BEFORE_RESCHEDULE} hours before scheduled time"
        }

        // Check if new date is in future
        if (DateTimeUtils.isDateInPast(newDate)) {
            return false to "Cannot reschedule to a past date"
        }

        // Check if new time is valid
        if (!DateTimeUtils.isAppointmentTimeValid(newDate, newTime)) {
            return false to "Please select a future date and time"
        }

        return true to "Reschedule is valid"
    }

    // Leave validation
    fun validateLeave(
        startDate: String,
        endDate: String,
        reason: String
    ): Pair<Boolean, String> {
        // Check if dates are valid
        if (startDate.isBlank() || endDate.isBlank()) {
            return false to "Please select both start and end dates"
        }

        // Check if start date is not in past
        if (DateTimeUtils.isDateInPast(startDate)) {
            return false to "Start date cannot be in the past"
        }

        // Check if end date is after start date
        if (DateTimeUtils.compareDates(endDate, startDate) < 0) {
            return false to "End date must be after start date"
        }

        // Check leave duration (max 30 days)
        val duration = DateTimeUtils.getTimeDifferenceInDays(endDate) -
                DateTimeUtils.getTimeDifferenceInDays(startDate)
        if (duration > 30) {
            return false to "Leave duration cannot exceed 30 days"
        }

        // Validate reason
        if (reason.isBlank()) {
            return false to "Please provide a reason for leave"
        }

        if (reason.length > 200) {
            return false to "Reason cannot exceed 200 characters"
        }

        return true to "Leave is valid"
    }

    // Rating validation
    fun validateRating(
        rating: Float,
        review: String
    ): Pair<Boolean, String> {
        // Check rating value
        if (rating < AppConstants.MIN_RATING || rating > AppConstants.MAX_RATING) {
            return false to "Please provide a rating between ${AppConstants.MIN_RATING} and ${AppConstants.MAX_RATING} stars"
        }

        // Validate review length (optional but if provided)
        if (review.isNotBlank() && review.length > 500) {
            return false to "Review cannot exceed 500 characters"
        }

        return true to "Rating is valid"
    }

    // Time slot validation
    fun validateTimeSlot(
        date: String,
        startTime: String,
        endTime: String,
        duration: Int
    ): Pair<Boolean, String> {
        // Check if date is in future
        if (DateTimeUtils.isDateInPast(date)) {
            return false to "Cannot create slots for past dates"
        }

        // Check if times are valid
        if (startTime.isBlank() || endTime.isBlank()) {
            return false to "Please select both start and end times"
        }

        // Check duration
        if (duration !in AppConstants.SLOT_DURATIONS) {
            return false to "Please select a valid duration: ${AppConstants.SLOT_DURATIONS.joinToString(", ")} minutes"
        }

        // Validate time range
        try {
            val start = DateTimeUtils.parseDateTime(date, startTime)
            val end = DateTimeUtils.parseDateTime(date, endTime)

            if (end <= start) {
                return false to "End time must be after start time"
            }

            val durationInMillis = end - start
            val minDuration = duration * 60 * 1000L

            if (durationInMillis < minDuration) {
                return false to "Time range is too short for the selected duration"
            }
        } catch (e: Exception) {
            return false to "Invalid time format"
        }

        return true to "Time slot is valid"
    }

    // Doctor profile validation
    fun validateDoctorProfile(
        specialization: String,
        experience: Int,
        qualifications: String,
        consultationFee: Double
    ): Pair<Boolean, String> {
        if (specialization.isBlank()) {
            return false to "Please select a specialization"
        }

        if (experience < 0 || experience > 50) {
            return false to "Please enter valid years of experience (0-50)"
        }

        if (qualifications.isBlank()) {
            return false to "Please enter your qualifications"
        }

        if (consultationFee < 0 || consultationFee > 100000) {
            return false to "Please enter a valid consultation fee"
        }

        return true to "Profile is valid"
    }

    // Notes validation
    fun validateNotes(notes: String, maxLength: Int = 500): Pair<Boolean, String> {
        if (notes.isBlank()) {
            return false to "Notes cannot be empty"
        }

        if (notes.length > maxLength) {
            return false to "Notes cannot exceed $maxLength characters"
        }

        return true to "Notes are valid"
    }
}