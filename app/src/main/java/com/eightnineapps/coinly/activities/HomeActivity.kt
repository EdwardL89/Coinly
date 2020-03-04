package com.eightnineapps.coinly.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.eightnineapps.coinly.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class HomeActivity : AppCompatActivity() {

    /**
     * Initializes required elements of the home page
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupSignOutButton()
    }
    
    /**
     * Creates the sign-out button and initiates the Firebase and Google sign-out process
     */
    private fun setupSignOutButton() {
        val googleSignInOption: GoogleSignInOptions = buildGoogleSignOutOption()
        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)
        val googleSignOutButton = findViewById<Button>(R.id.sign_out_button)
        googleSignOutButton.setOnClickListener {
            auth.signOut() // Sign out of Firebase
            googleSignInClient.signOut().addOnCompleteListener{ // Sign out of the Google account
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                this.finish()
            }
        }
    }

    /**
     * Builds the Google sign-out option for the user
     */
    private fun buildGoogleSignOutOption(): GoogleSignInOptions {
        return GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    }
}
