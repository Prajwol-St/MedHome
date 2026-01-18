package com.example.medhomeapp.utils

object AppConstants {

    // Appointment Status
    const val STATUS_PENDING = "pending"
    const val STATUS_CONFIRMED = "confirmed"
    const val STATUS_COMPLETED = "completed"
    const val STATUS_CANCELLED = "cancelled"
    const val STATUS_NO_SHOW = "no_show"

    // Appointment Type
    const val TYPE_IN_PERSON = "in_person"
    const val TYPE_VIDEO_CALL = "video_call"

    // Cancelled By
    const val CANCELLED_BY_PATIENT = "patient"
    const val CANCELLED_BY_DOCTOR = "doctor"
    const val CANCELLED_BY_SYSTEM = "system"

    // Leave Types
    const val LEAVE_TYPE_PERSONAL = "personal"
    const val LEAVE_TYPE_MEDICAL = "medical"
    const val LEAVE_TYPE_CONFERENCE = "conference"
    const val LEAVE_TYPE_VACATION = "vacation"

    // User Roles
    const val ROLE_PATIENT = "patient"
    const val ROLE_DOCTOR = "doctor"

    // Cancellation Policy
    const val MIN_HOURS_BEFORE_CANCELLATION = 2
    const val MIN_HOURS_BEFORE_RESCHEDULE = 2
    const val MAX_CANCELLATIONS_PER_MONTH = 3
    const val NO_SHOW_PENALTY_DAYS = 7

    // Slot Durations (in minutes)
    val SLOT_DURATIONS = listOf(15, 30, 45, 60)
    const val DEFAULT_SLOT_DURATION = 30

    // Sort Options
    const val SORT_RATING_DESC = "rating_desc"
    const val SORT_RATING_ASC = "rating_asc"
    const val SORT_FEE_ASC = "fee_asc"
    const val SORT_FEE_DESC = "fee_desc"
    const val SORT_DISTANCE_ASC = "distance_asc"
    const val SORT_EXPERIENCE_DESC = "experience_desc"

    // Rating Range
    const val MIN_RATING = 1f
    const val MAX_RATING = 5f

    // Days of Week
    val DAYS_OF_WEEK = listOf(
        "Monday", "Tuesday", "Wednesday", "Thursday",
        "Friday", "Saturday", "Sunday"
    )

    // Time Slots (24-hour format)
    val MORNING_SLOTS = listOf(
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30"
    )

    val AFTERNOON_SLOTS = listOf(
        "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"
    )

    val EVENING_SLOTS = listOf(
        "17:00", "17:30", "18:00", "18:30", "19:00"
    )

    // Firebase Database Paths
    const val PATH_USER = "User"
    const val PATH_APPOINTMENTS = "appointments"
    const val PATH_DOCTOR_AVAILABILITY = "doctor_availability"
    const val PATH_DOCTOR_LEAVES = "doctorLeaves"
    const val PATH_RATINGS = "ratings"
    const val PATH_APPOINTMENTS_BY_PATIENT = "appointmentsByPatient"
    const val PATH_APPOINTMENTS_BY_DOCTOR = "appointmentsByDoctor"
    const val PATH_SPECIALIZATIONS = "specializations"

    // Date/Time Formats
    const val DATE_FORMAT = "yyyy-MM-dd"
    const val TIME_FORMAT = "HH:mm"
    const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm"
    const val DISPLAY_DATE_FORMAT = "dd MMM yyyy"
    const val DISPLAY_TIME_FORMAT = "hh:mm a"
    const val DISPLAY_DATETIME_FORMAT = "dd MMM yyyy, hh:mm a"
}