package com.eightnineapps.coinly.viewmodels.activityviewmodels.startup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eightnineapps.coinly.classes.helpers.AuthHelper
import com.eightnineapps.coinly.enums.AuthErrorType
import com.eightnineapps.coinly.models.Firestore
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentSnapshot
import javax.inject.Inject

class LoginViewModel constructor(private val authHelper: AuthHelper) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(private val authHelper: AuthHelper): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return  LoginViewModel(authHelper) as T
        }
    }

    /**
     * Queries the Firebase for an account with the given email
     */
    fun checkIfEmailIsUsed(email: String): Task<SignInMethodQueryResult> {
        return authHelper.getSignInMethodsForEmail(email)
    }

    /**
     * Signs out from the temporary login used for registration purposes
     */
    fun signOutFromAuth(context: Context) = authHelper.signOut(context)

    /**
     * Queries the firestore to see if the current user has created a profile
     */
    fun attemptToGetCurrentUSer(): Task<DocumentSnapshot> {
        return if (authHelper.getAuthUser() != null) {
            Firestore.read(authHelper.getAuthUserEmail()).get()
        } else {
            Firestore.read("Failure").get()
        }
    }

    /**
     * Signs into firebase authentication through Google account
     */
    fun attemptToSignIntoFirebase(acct: GoogleSignInAccount): Task<AuthResult> {
        return authHelper.getAuth().signInWithCredential(GoogleAuthProvider.getCredential(acct.idToken, null))
    }

    /**
     * Registers a new user with Firebase Authentication
     */
    fun registerNewUser(email: String, password: String): Task<AuthResult> {
        return authHelper.getAuth().createUserWithEmailAndPassword(email, password)
    }

    /**
     * Signs in a user with a password and email
     */
    fun loginUser(email: String, password: String): Task<AuthResult> {
        return authHelper.getAuth().signInWithEmailAndPassword(email, password)
    }

    /**
     * Determines the next step when a registration exception is thrown
     */
    fun handleRegistrationException(exception: Exception?): AuthErrorType {
        return when (exception) {
            is FirebaseAuthWeakPasswordException -> AuthErrorType.WEAK_PASSWORD
            is FirebaseAuthInvalidCredentialsException -> AuthErrorType.MALFORMED_EMAIL
            else -> AuthErrorType.EXISTING_EMAIL
        }
    }

    /**
     * Sends an email to reset password
     */
    fun sendResetEmail(email: String): Task<Void> {
        return authHelper.getAuth().sendPasswordResetEmail(email)
    }
}