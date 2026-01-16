package com.example.medhomeapp.utils

object AppConstants {

    // User Roles
    const val ROLE_PATIENT = "patient"
    const val ROLE_DOCTOR = "doctor"
    const val ROLE_ADMIN = "admin"

    // Appointment Status
    const val STATUS_SCHEDULED = "scheduled"
    const val STATUS_COMPLETED = "completed"
    const val STATUS_CANCELLED = "cancelled"
    const val STATUS_NO_SHOW = "no-show"
    const val STATUS_RESCHEDULED = "rescheduled"

    // Cancellation
    const val CANCELLED_BY_PATIENT = "patient"
    const val CANCELLED_BY_DOCTOR = "doctor"
    const val CANCELLED_BY_SYSTEM = "system"

    // Leave Types
    const val LEAVE_SICK = "Sick Leave"
    const val LEAVE_VACATION = "Vacation"
    const val LEAVE_EMERGENCY = "Emergency"
    const val LEAVE_PERSONAL = "Personal"
    const val LEAVE_CONFERENCE = "Conference"
    const val LEAVE_OTHER = "Other"

    // Sort Options
    const val SORT_RATING_DESC = "rating_desc"
    const val SORT_RATING_ASC = "rating_asc"
    const val SORT_FEE_ASC = "fee_asc"
    const val SORT_FEE_DESC = "fee_desc"
    const val SORT_EXPERIENCE_DESC = "experience_desc"
    const val SORT_NAME_ASC = "name_asc"

    // Time Constraints
    const val MIN_CANCELLATION_HOURS = 24
    const val SLOT_DURATION_MINUTES = 30
    const val MIN_SLOT_DURATION_MINUTES = 15

    // Limits
    const val MAX_RATING = 5.0f
    const val MIN_RATING = 0.0f
    const val MAX_REVIEW_LENGTH = 500
    const val MIN_REVIEW_LENGTH = 5
    const val MAX_NOTES_LENGTH = 1000
    const val MIN_NOTES_LENGTH = 5

    // Firebase Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_DOCTORS = "doctors"
    const val COLLECTION_APPOINTMENTS = "appointments"
    const val COLLECTION_TIME_SLOTS = "timeSlots"
    const val COLLECTION_RATINGS = "ratings"
    const val COLLECTION_LEAVES = "leaves"

    // Preferences Keys
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_ROLE = "user_role"
    const val PREF_USER_NAME = "user_name"
    const val PREF_USER_EMAIL = "user_email"
    const val PREF_IS_LOGGED_IN = "is_logged_in"

    // Request Codes
    const val REQUEST_CODE_LOCATION = 100
    const val REQUEST_CODE_CAMERA = 101
    const val REQUEST_CODE_GALLERY = 102

    // Blood Groups
    val BLOOD_GROUPS = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

    // Genders
    val GENDERS = listOf("Male", "Female", "Other")

    // Specializations
    val SPECIALIZATIONS = listOf(
        "General Physician",
        "Cardiologist",
        "Dermatologist",
        "Pediatrician",
        "Orthopedic",
        "Gynecologist",
        "Neurologist",
        "Psychiatrist",
        "Dentist",
        "ENT Specialist",
        "Ophthalmologist",
        "Urologist",
        "Gastroenterologist"
    )
}