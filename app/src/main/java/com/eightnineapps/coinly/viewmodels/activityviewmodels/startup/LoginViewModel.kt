package com.eightnineapps.coinly.viewmodels.activityviewmodels.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eightnineapps.coinly.classes.helpers.AuthHelper
import com.eightnineapps.coinly.models.Firestore
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
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
}