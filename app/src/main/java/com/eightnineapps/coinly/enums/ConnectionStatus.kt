package com.eightnineapps.coinly.enums

/**
 * Identifies the connection status between the current user and the big/little
 */
enum class ConnectionStatus {
    NOT_ADDED_AS_BIG, NOT_ADDED_AS_LITTLE, REQUESTED_BIG, REQUESTED_LITTLE, RECEIVED_REQUEST_FROM_BIG,
    RECEIVED_REQUEST_FROM_LITTLE, ADDED_AS_BIG, ADDED_AS_LITTLE
}