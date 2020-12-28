package com.eightnineapps.coinly.views.activities.startup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.viewmodels.activityviewmodels.startup.LoginViewModel
import com.eightnineapps.coinly.views.activities.profiles.CreateProfileActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var loginViewModelFactory: LoginViewModel.Factory
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProvider(this, loginViewModelFactory).get(LoginViewModel::class.java)
        setContentView(R.layout.activity_login)
        setupSignInButton()
        setupLoginButton()
        setupEditTexts()
    }

    /**
     * Clear all edit texts when page is loaded
     */
    override fun onResume() {
        super.onResume()
        clearFields()
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
                signIntoFirebase(data)
            } catch (e: ApiException) {
                Log.w("INFO", "Sign in attempt failed or was canceled with back press")
            }
        }
    }

    /**
     * Clears all edit texts
     */
    private fun clearFields() {
        email_edit_text.setText("")
        password_edit_text.setText("")
    }

    /**
     * Sets up the Google sign-in button and initiates the Firebase authentication process
     */
    private fun setupSignInButton() {
        sign_in_button.setOnClickListener {
            clearFields()
            startActivityForResult(
                GoogleSignIn.getClient(this, GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build())
                .signInIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), 1)
        }
    }

    /**
     * Sets up the login button with email and password
     */
    private fun setupLoginButton() {
        login_button.setOnClickListener {
            hideSoftKeyboard()
            if (validInput()) {
                loginViewModel.loginUser(email_edit_text.text.toString(),
                    password_edit_text.text.toString()
                ).addOnCompleteListener { task -> handleLoginAttempt(task) }
            }
        }
    }

    /**
     * Hides the keyboard from the screen
     */
    private fun hideSoftKeyboard() {
        val view = currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    /**
     * Makes sure there's no missing information
     */
    private fun validInput(): Boolean {
        return if (email_edit_text.text.toString().isEmpty() ||
            password_edit_text.text.toString().isEmpty()) {
            Toast.makeText(this, "Missing info!", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    /**
     * Determine next steps depending on whether the login was successful
     */
    private fun handleLoginAttempt(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            checkForReturningUser()
        } else {
            val exceptionMsg = task.exception.toString()
            when {
                exceptionMsg.contains("password") -> handleWrongPassword()
                exceptionMsg.contains("email") -> handleBadEmail()
                else -> handleNoUser()
            }
        }
    }

    /**
     * Hides all error messages when edit texts are focused on
     */
    private fun setupEditTexts() {
        email_edit_text.setOnFocusChangeListener { _, _ -> email_error_text_view.visibility = View.INVISIBLE}
        password_edit_text.setOnFocusChangeListener { _, _ -> password_error_text_view.visibility = View.INVISIBLE}
    }

    /**
     * Displays the error message for non-existent account
     */
    private fun handleNoUser() {
        email_edit_text.clearFocus()
        email_error_text_view.text = getString(R.string.no_account)
        email_error_text_view.visibility = View.VISIBLE
    }

    /**
     * Displays the error message for an invalid email
     */
    private fun handleBadEmail() {
        email_edit_text.clearFocus()
        email_error_text_view.text = getString(R.string.invalid_email)
        email_error_text_view.visibility = View.VISIBLE
    }

    /**
     * Displays the error message for a wrong password
     */
    private fun handleWrongPassword() {
        password_edit_text.clearFocus()
        password_error_text_view.text = getString(R.string.wrong_password)
        password_error_text_view.visibility = View.VISIBLE
    }

    /**
     * Signs into firebase authentication and redirects the user
     */
    private fun signIntoFirebase(data: Intent?) {
        loginViewModel.attemptToSignIntoFirebase(GoogleSignIn.getSignedInAccountFromIntent(data).getResult(
            ApiException::class.java)!!)
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
}