package com.eightnineapps.coinly.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.eightnineapps.coinly.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

/**
 * Allows the user to login either through google or email & password
 */
class LoginActivity : AppCompatActivity() {

    /**
     * Provides access to this class's fields without an actual instance of this class
     */
    companion object {
        private const val RC_SIGN_IN = 1
        const val TAG = "INFO:"
        val auth = FirebaseAuth.getInstance()
    }

    /**
     * Initializes sign-in flow
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        updateUI(auth.currentUser)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupSignInButton()
    }

    /**
     * Close the app when the user hits "back"
     */
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
        exitProcess(0)
    }

    /**
     * Called when sign in attempt is complete
     * Determines whether Google account sign-in was successful
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleAuth(task)
        }
    }

    /**
     * Initiates Firebase authentication upon successful Google account authentication
     */
    private fun handleGoogleAuth(task: Task<GoogleSignInAccount>) {
        try { // If Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)
            initiateFirebaseAuthWithGoogle(account!!)
        } catch (e: ApiException) { // If Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
            updateUI(null)
        }
    }

    /**
     * Sends the user to the profile creating page upon successful Firebase authentication
     */
    private fun handleFirebaseAuth(task: Task<AuthResult>) {
        if (task.isSuccessful) { // Sign in success, update UI with the signed-in user's information
            Log.d(TAG, "signInWithCredential:success")
            updateUI(auth.currentUser)
        } else { // If sign in fails, display a message to the user.
            Log.w(TAG, "signInWithCredential:failure", task.exception)
            Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show()
            updateUI(null)
        }
    }

    /**
     * Attempt sign-in to Firebase Authentication
     */
    private fun initiateFirebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task -> handleFirebaseAuth(task) }
    }

    /**
     * Creates the Google sign-in button and initiates the Firebase authentication process
     */
    private fun setupSignInButton() {
        val googleSignInOption: GoogleSignInOptions = buildGoogleSignInOption()
        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)
        val googleSignInButton = findViewById<SignInButton>(R.id.sign_in_button)
        googleSignInButton.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION),
                RC_SIGN_IN
            )
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
     * Goes to the main activity page if sign in was successful
     */
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        } else {
            Log.d(TAG, "Failed to sign in, or new user")
        }
    }
}
