package com.eightnineapps.coinly.enums

/**
 * Specifies the type of exception that occurred with Firebase Auth during new user registration
 */
enum class RegistrationErrorType {
    WEAK_PASSWORD, EXISTING_EMAIL, MALFORMED_EMAIL
}