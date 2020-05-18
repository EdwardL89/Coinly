package com.eightnineapps.coinly.classes

import com.google.firebase.auth.FirebaseAuth

/**
 * Model that provides access to the authenticated user
 */
class AuthHelper {

    private val authenticatedUser = FirebaseAuth.getInstance()

    fun getAuth() = authenticatedUser

    fun getAuthUser() = authenticatedUser.currentUser

    fun getAuthUserEmail() = authenticatedUser.currentUser?.email!!
}