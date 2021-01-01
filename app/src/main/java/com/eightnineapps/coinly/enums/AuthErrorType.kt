package com.eightnineapps.coinly.enums

/**
 * Specifies the type of exception that occurred with Firebase Auth during new user registration
 */
enum class AuthErrorType {
    WEAK_PASSWORD, EXISTING_EMAIL, MALFORMED_EMAIL
}