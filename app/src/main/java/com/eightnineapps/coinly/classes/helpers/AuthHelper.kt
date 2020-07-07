package com.eightnineapps.coinly.classes.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.eightnineapps.coinly.views.activities.startup.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

/**
 * Model that provides access to the authenticated user
 */
class AuthHelper @Inject constructor() {

    private val authenticatedUser = FirebaseAuth.getInstance()

    fun getAuth() = authenticatedUser

    fun getAuthUser() = authenticatedUser.currentUser

    fun getAuthUserEmail() = authenticatedUser.currentUser?.email!!

    fun signOut(context: Context, appContext: Context) {
        val googleSignInOption: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, googleSignInOption)
        authenticatedUser.signOut()
        googleSignInClient.signOut().addOnCompleteListener{
            context.startActivity(Intent(appContext, LoginActivity::class.java))
            (context as Activity).finish()
        }
    }
}