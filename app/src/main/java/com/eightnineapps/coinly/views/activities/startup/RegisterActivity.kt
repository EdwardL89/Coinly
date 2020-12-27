package com.eightnineapps.coinly.views.activities.startup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.enums.RegistrationErrorType
import com.eightnineapps.coinly.viewmodels.activityviewmodels.startup.LoginViewModel
import com.eightnineapps.coinly.views.activities.profiles.CreateProfileActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_register.*
import javax.inject.Inject

/**
 * Allows the user to login either through google or email & password
 */
@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    @Inject
    lateinit var loginViewModelFactory: LoginViewModel.Factory
    private lateinit var loginViewModel: LoginViewModel

    /**
     * Initializes sign-in flow
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProvider(this, loginViewModelFactory).get(LoginViewModel::class.java)
        setContentView(R.layout.activity_register)
        setupRegisterButton()
        setupSignUpButton()
        setupEditTexts()
    }

    /**
     * Makes for a clean transition back to the previous activity with no animation or flashes
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    /**
     * Called when sign in attempt is complete
     * Determines whether Google account sign-in was successful
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            try {
                checkForExistingEmail(data)
            } catch (e: ApiException) {
                Log.w("INFO", "Sign in attempt failed or was canceled with back press")
            }
        }
    }

    /**
     * Determines whether or not the selected email has been used for an account already
     */
    private fun checkForExistingEmail(data: Intent?) {
        val selectedEmail = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)!!.email!!
        loginViewModel.checkIfEmailIsUsed(selectedEmail).addOnCompleteListener {
            if (it.result!!.signInMethods!!.isEmpty()) {
                signIntoFirebase(data)
            } else {
                loginViewModel.signOutFromAuth(this)
                handleExistingEmail()
            }
        }
    }

    /**
     * Signs into firebase authentication and redirects the user
     */
    private fun signIntoFirebase(data: Intent?) {
        loginViewModel.attemptToSignIntoFirebase(GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)!!)
            .addOnCompleteListener(this as Activity) {
                if (!it.isSuccessful) Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show()
                else checkForReturningUser()
            }
    }

    /**
     * Query the firestore to check if the current user is an existing one to determine
     * which activity to launch
     */
    private fun checkForReturningUser() {
        loginViewModel.attemptToGetCurrentUSer().addOnCompleteListener {
            if (it.isSuccessful) {
                handleUserQuery(it)
            } else {
                Log.e("INFO", it.exception.toString())
            }
        }
    }

    /**
     * Redirects the user to appropriate activity depending on whether they've created a profile
     */
    private fun handleUserQuery(task: Task<DocumentSnapshot>) {
        if (!task.result?.exists()!!) {
            startActivity(Intent(this, CreateProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        } else {
            startActivity(Intent(this, HomeActivity::class.java)
                .putExtra("current_user", task.result?.toObject(User::class.java)!!)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
    }

    /**
     * Sets up the Google sign-in button and initiates the Firebase authentication process
     */
    private fun setupSignUpButton() {
        sign_up_button.setOnClickListener {
            email_edit_text.setText("")
            password_edit_text.setText("")
            confirm_password_edit_text.setText("")
            email_error_text_view.visibility = View.INVISIBLE
            startActivityForResult(GoogleSignIn.getClient(this, GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build())
                .signInIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), 1)
        }
    }

    /**
     * Sets up the register button to create an account with an email and password
     */
    private fun setupRegisterButton() {
        register_button.setOnClickListener {
            if (validInput()) {
                loginViewModel.registerNewUser(
                    email_edit_text.text.toString(),
                    confirm_password_edit_text.text.toString()
                ).addOnCompleteListener { task -> handleRegistrationAttempt(task) }
            }
        }
    }

    /**
     * Setup edit texts for password matching and error message display
     */
    private fun setupEditTexts() {
        email_edit_text.setOnFocusChangeListener { _, _ -> email_error_text_view.visibility = View.INVISIBLE}
        password_edit_text.setOnFocusChangeListener { _, _ -> password_error_text_view.visibility = View.INVISIBLE  }
        confirm_password_edit_text.addTextChangedListener {
            if (it.toString() == password_edit_text.text.toString()) {
                passwords_do_not_match_text_view.visibility = View.INVISIBLE
            } else {
                passwords_do_not_match_text_view.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Makes sure no edit texts are empty
     */
    private fun validInput(): Boolean {
        return if (email_edit_text.text.toString().isEmpty() ||
            confirm_password_edit_text.text.toString().isEmpty() ||
            password_edit_text.text.toString().isEmpty()) {
            Toast.makeText(this, "Missing info!", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    /**
     * Determines the next step after registration attempt
     */
    private fun handleRegistrationAttempt(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            checkForReturningUser()
        } else {
            when (loginViewModel.handleRegistrationException(task.exception)) {
                RegistrationErrorType.WEAK_PASSWORD -> handleWeakPassword()
                RegistrationErrorType.MALFORMED_EMAIL -> handleWrongEmail()
                RegistrationErrorType.EXISTING_EMAIL -> handleExistingEmail()
            }
        }
    }

    /**
     * Notify the user their password is too weak
     */
    private fun handleWeakPassword() {
        password_edit_text.clearFocus()
        password_error_text_view.text = getString(R.string.password_too_weak)
        password_error_text_view.visibility = View.VISIBLE
    }

    /**
     * Notify the user their email is malformed
     */
    private fun handleWrongEmail() {
        email_edit_text.clearFocus()
        email_error_text_view.text = getString(R.string.invalid_email)
        email_error_text_view.visibility = View.VISIBLE
    }

    /**
     * Notify the user an account already exists with the given email
     */
    private fun handleExistingEmail() {
        email_edit_text.clearFocus()
        email_error_text_view.text = getString(R.string.already_exists)
        email_error_text_view.visibility = View.VISIBLE
    }
}
