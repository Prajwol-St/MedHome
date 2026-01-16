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
    const val MAX_CANCELLATIONS_PER_MONTH = 3

    // Slot Durations
    val SLOT_DURATIONS = listOf(15, 30, 45, 60)

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
}


