package com.eightnineapps.coinly.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.viewmodels.activityviewmodels.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.system.exitProcess

/**
 * Allows the user to login either through google or email & password
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    companion object {
        var auth = FirebaseAuth.getInstance()
    }

    /**
     * Initializes sign-in flow
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.updateUI(this)
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
        loginViewModel.checkAndHandleActivityResultCode(requestCode, data, this)
    }

    /**
     * Creates the Google sign-in button and initiates the Firebase authentication process
     */
    private fun setupSignInButton() {
        sign_in_button.setOnClickListener {
            startActivityForResult(GoogleSignIn.getClient(this, GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build())
                .signInIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), 1)
        }
    }
}
