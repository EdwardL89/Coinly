package com.eightnineapps.coinly

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class FirebaseAuthActivity : AppCompatActivity() {

    /**
     * Initializes sign-in flow
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val googleSignInOption: GoogleSignInOptions = buildGoogleSignInOption()
        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)
        val googleSignInButton = findViewById<SignInButton>(R.id.sign_in_button)

        googleSignInButton.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        }
    }

    /**
     * Builds the Google sign-in option for the user
     */
    private fun buildGoogleSignInOption(): GoogleSignInOptions {
        return GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    /**
     * Determine whether or not a user has already been signed in
     * Update the UI accordingly
     */
    override fun onStart() {
        super.onStart()
        updateUI(auth.currentUser)
    }

    /**
     * Goes to the main activity page if sign in was successful
     */
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) startActivity(Intent(this, MainActivity::class.java))
        else Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show()
    }

    /**
     * Called when sign in attempt is complete
     * Determines whether Google account sign-in was successful
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try { // If Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) { // If Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                updateUI(null)
            }
        }
    }

    /**
     * Attempt sign-in to Firebase Authentication
     */
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else { // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    /**
     * Provides access to this class's fields without an actual instance of this class
     */
    companion object {
        var auth = FirebaseAuth.getInstance()
        private const val RC_SIGN_IN = 1
        private const val TAG = "INFO:"
    }
}
