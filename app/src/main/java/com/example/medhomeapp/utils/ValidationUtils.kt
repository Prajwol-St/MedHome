package com.example.medhomeapp.utils

import android.util.Patterns
import java.util.regex.Pattern

object ValidationUtils {

    private const val MIN_PASSWORD_LENGTH = 6
    private const val MIN_NAME_LENGTH = 2
    private const val MAX_NAME_LENGTH = 50
    private const val MIN_AGE = 1
    private const val MAX_AGE = 150
    private const val MIN_EXPERIENCE = 0
    private const val MAX_EXPERIENCE = 70
    private const val MIN_FEE = 0.0
    private const val MAX_FEE = 100000.0

    // Email validation
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Password validation (at least 6 characters)
    fun isValidPassword(password: String): Boolean {
        return password.length >= MIN_PASSWORD_LENGTH
    }

    // Strong password validation (at least 8 chars, 1 uppercase, 1 lowercase, 1 digit)
    fun isStrongPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"
        return Pattern.compile(passwordPattern).matcher(password).matches()
    }

    // Phone number validation (10 digits)
    fun isValidPhoneNumber(phone: String): Boolean {
        val cleanedPhone = phone.replace(Regex("[\\s()-]"), "")
        return cleanedPhone.matches(Regex("^[0-9]{10}$"))
    }

    // Name validation
    fun isValidName(name: String): Boolean {
        val trimmedName = name.trim()
        return trimmedName.length in MIN_NAME_LENGTH..MAX_NAME_LENGTH &&
                trimmedName.matches(Regex("^[a-zA-Z\\s.'-]+$"))
    }

    // Age validation
    fun isValidAge(age: Int): Boolean {
        return age in MIN_AGE..MAX_AGE
    }

    // Experience validation (for doctors)
    fun isValidExperience(experience: Int): Boolean {
        return experience in MIN_EXPERIENCE..MAX_EXPERIENCE
    }

    // Consultation fee validation
    fun isValidConsultationFee(fee: Double): Boolean {
        return fee in MIN_FEE..MAX_FEE
    }

    // Rating validation (0.0 to 5.0)
    fun isValidRating(rating: Float): Boolean {
        return rating in 0.0f..5.0f
    }

    // Address validation
    fun isValidAddress(address: String): Boolean {
        return address.trim().length >= 5
    }

    // Specialization validation
    fun isValidSpecialization(specialization: String): Boolean {
        return specialization.trim().length >= 2
    }

    // Reason/Notes validation
    fun isValidReason(reason: String, minLength: Int = 5): Boolean {
        return reason.trim().length >= minLength
    }

    // Time slot validation (HH:mm format)
    fun isValidTimeFormat(time: String): Boolean {
        return time.matches(Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$"))
    }

    // Date validation (yyyy-MM-dd format)
    fun isValidDateFormat(date: String): Boolean {
        return date.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))
    }

    // Check if end time is after start time
    fun isEndTimeAfterStartTime(startTime: String, endTime: String): Boolean {
        return try {
            val start = startTime.split(":").map { it.toInt() }
            val end = endTime.split(":").map { it.toInt() }

            when {
                end[0] > start[0] -> true
                end[0] == start[0] && end[1] > start[1] -> true
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }

    // Check if end date is after or equal to start date
    fun isEndDateValid(startDate: String, endDate: String): Boolean {
        return DateTimeUtils.getDaysBetween(startDate, endDate) >= 0
    }

    // Validate slot duration (minimum 15 minutes)
    fun isValidSlotDuration(startTime: String, endTime: String, minMinutes: Int = 15): Boolean {
        return try {
            val start = startTime.split(":").map { it.toInt() }
            val end = endTime.split(":").map { it.toInt() }

            val startMinutes = start[0] * 60 + start[1]
            val endMinutes = end[0] * 60 + end[1]

            (endMinutes - startMinutes) >= minMinutes
        } catch (e: Exception) {
            false
        }
    }

    // License number validation
    fun isValidLicenseNumber(license: String): Boolean {
        return license.trim().matches(Regex("^[A-Z0-9-]{5,20}$"))
    }

    // Qualification validation
    fun isValidQualification(qualification: String): Boolean {
        return qualification.trim().length >= 2
    }

    // Blood group validation
    fun isValidBloodGroup(bloodGroup: String): Boolean {
        val validGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
        return bloodGroup.trim().uppercase() in validGroups
    }

    // Gender validation
    fun isValidGender(gender: String): Boolean {
        val validGenders = listOf("Male", "Female", "Other")
        return gender.trim() in validGenders
    }

    // Review text validation
    fun isValidReview(review: String): Boolean {
        val trimmed = review.trim()
        return trimmed.isEmpty() || trimmed.length in 5..500
    }

    // Cancel reason validation
    fun isValidCancelReason(reason: String): Boolean {
        return reason.trim().length >= 10
    }

    // Doctor notes validation
    fun isValidDoctorNotes(notes: String): Boolean {
        return notes.trim().length >= 5
    }

    // Leave type validation
    fun isValidLeaveType(leaveType: String): Boolean {
        val validTypes = listOf("Sick Leave", "Vacation", "Emergency", "Personal", "Conference", "Other")
        return leaveType.trim() in validTypes
    }

    // Search query validation
    fun isValidSearchQuery(query: String): Boolean {
        return query.trim().length >= 2
    }

    // Fee range validation
    fun isValidFeeRange(minFee: Double, maxFee: Double): Boolean {
        return minFee >= 0 && maxFee >= minFee && maxFee <= MAX_FEE
    }

    // Get password strength (Weak, Medium, Strong)
    fun getPasswordStrength(password: String): String {
        return when {
            password.length < 6 -> "Weak"
            password.length < 8 -> "Medium"
            isStrongPassword(password) -> "Strong"
            else -> "Medium"
        }
    }

    // Get validation error message
    fun getEmailError(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !isValidEmail(email) -> "Invalid email format"
            else -> null
        }
    }

    fun getPasswordError(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < MIN_PASSWORD_LENGTH -> "Password must be at least $MIN_PASSWORD_LENGTH characters"
            else -> null
        }
    }

    fun getNameError(name: String): String? {
        return when {
            name.isBlank() -> "Name is required"
            name.trim().length < MIN_NAME_LENGTH -> "Name is too short"
            !isValidName(name) -> "Name contains invalid characters"
            else -> null
        }
    }

    fun getPhoneError(phone: String): String? {
        return when {
            phone.isBlank() -> "Phone number is required"
            !isValidPhoneNumber(phone) -> "Invalid phone number (10 digits required)"
            else -> null
        }
    }

    fun getTimeSlotError(startTime: String, endTime: String): String? {
        return when {
            !isValidTimeFormat(startTime) -> "Invalid start time format"
            !isValidTimeFormat(endTime) -> "Invalid end time format"
            !isEndTimeAfterStartTime(startTime, endTime) -> "End time must be after start time"
            !isValidSlotDuration(startTime, endTime) -> "Slot duration must be at least 15 minutes"
            else -> null
        }
    }

    fun getLeaveError(startDate: String, endDate: String, reason: String): String? {
        return when {
            !isValidDateFormat(startDate) -> "Invalid start date"
            !isValidDateFormat(endDate) -> "Invalid end date"
            !isEndDateValid(startDate, endDate) -> "End date must be after or equal to start date"
            !isValidReason(reason, 10) -> "Reason must be at least 10 characters"
            else -> null
        }
    }
}