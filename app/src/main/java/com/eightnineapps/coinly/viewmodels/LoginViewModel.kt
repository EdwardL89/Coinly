package com.eightnineapps.coinly.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.classes.AuthHelper
import com.eightnineapps.coinly.views.activities.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider

class LoginViewModel : ViewModel() {

    private val authHelper = AuthHelper()

    /**
     * Goes to the main activity page if sign in was successful
     */
    fun updateUI(context: Context) {
        if (authHelper.getAuthUser() != null) {
            context.startActivity(Intent(context, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
    }

    /**
     * Handles the Google sign in event once it's completed
     */
    fun checkAndHandleActivityResultCode(requestCode: Int, data: Intent?, context: Context) {
        if (requestCode == 1) handleGoogleAuth(GoogleSignIn.getSignedInAccountFromIntent(data), context)
    }

    /**
     * Initiates Firebase authentication upon successful Google account authentication
     */
    private fun handleGoogleAuth(task: Task<GoogleSignInAccount>, context: Context) {
        try {
            initiateFirebaseAuthWithGoogle(task.getResult(ApiException::class.java)!!, context)
        } catch (e: ApiException) {
            updateUI(context)
        }
    }

    /**
     * Attempt sign-in to Firebase Authentication
     */
    private fun initiateFirebaseAuthWithGoogle(acct: GoogleSignInAccount, context: Context) {
        authHelper.getAuth().signInWithCredential(GoogleAuthProvider.getCredential(acct.idToken, null))
            .addOnCompleteListener(context as Activity) { task -> handleFirebaseAuth(task, context) }
    }

    /**
     * Sends the user to the profile creating page upon successful Firebase authentication
     */
    private fun handleFirebaseAuth(task: Task<AuthResult>, context: Context) {
        if (!task.isSuccessful) Toast.makeText(context, "Sign-in failed", Toast.LENGTH_SHORT).show()
        updateUI(context)
    }
}