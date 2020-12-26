package com.eightnineapps.coinly.viewmodels.activityviewmodels.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eightnineapps.coinly.classes.helpers.AuthHelper
import com.eightnineapps.coinly.enums.RegistrationErrorType
import com.eightnineapps.coinly.models.Firestore
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentSnapshot
import java.lang.Exception
import javax.inject.Inject

class LoginViewModel constructor(private val authHelper: AuthHelper) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(private val authHelper: AuthHelper): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return  LoginViewModel(authHelper) as T
        }
    }

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
     * Determines the next step when a registration exception is thrown
     */
    fun handleRegistrationException(exception: Exception?): RegistrationErrorType {
        return when (exception) {
            is FirebaseAuthWeakPasswordException -> RegistrationErrorType.WEAK_PASSWORD
            is FirebaseAuthInvalidCredentialsException -> RegistrationErrorType.MALFORMED_EMAIL
            else -> RegistrationErrorType.EXISTING_EMAIL
        }
    }
}