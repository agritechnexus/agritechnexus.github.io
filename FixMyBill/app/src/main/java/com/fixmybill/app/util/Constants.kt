package com.fixmybill.app.util

object Constants {

    // AI Configuration
    const val ANTHROPIC_MODEL = "claude-sonnet-4-20250514"
    const val MAX_TOKENS = 4096

    // Supported Indian States
    val SUPPORTED_STATES = listOf(
        "Andhra Pradesh",
        "Assam",
        "Bihar",
        "Chhattisgarh",
        "Delhi",
        "Goa",
        "Gujarat",
        "Haryana",
        "Himachal Pradesh",
        "Jharkhand",
        "Karnataka",
        "Kerala",
        "Madhya Pradesh",
        "Maharashtra",
        "Manipur",
        "Meghalaya",
        "Mizoram",
        "Nagaland",
        "Odisha",
        "Punjab",
        "Rajasthan",
        "Tamil Nadu",
        "Telangana",
        "Tripura",
        "Uttar Pradesh",
        "Uttarakhand",
        "West Bengal"
    )

    // Bill Types
    val BILL_TYPES = listOf(
        "Electricity",
        "Water",
        "Gas"
    )

    // Date Format Patterns
    const val DATE_FORMAT_DISPLAY = "dd MMM yyyy"
    const val DATE_FORMAT_API = "yyyy-MM-dd"
    const val DATE_FORMAT_FULL = "dd MMMM yyyy, hh:mm a"
    const val DATE_FORMAT_MONTH_YEAR = "MMM yyyy"
    const val DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss"

    // DataStore Keys
    const val PREF_ONBOARDING_COMPLETED = "onboarding_completed"
    const val PREF_USER_STATE = "user_state"
    const val PREF_DEFAULT_BILL_TYPE = "default_bill_type"
    const val PREF_IS_PREMIUM = "is_premium"

    // Scan Limits
    const val FREE_SCAN_LIMIT = 3
    const val PREMIUM_SCAN_LIMIT = Int.MAX_VALUE

    // Database
    const val DATABASE_NAME = "fixmybill_database"
    const val DATABASE_VERSION = 1
}
