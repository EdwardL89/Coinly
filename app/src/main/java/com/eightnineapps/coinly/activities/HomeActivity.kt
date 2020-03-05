package com.eightnineapps.coinly.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.eightnineapps.coinly.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlin.system.exitProcess

class HomeActivity : AppCompatActivity() {

    /**
     * Initializes required elements of the home page
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    /**
     * Create the triple dots on the action bar to give options to the user, like signing out
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.home_action_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Assign actions to when the menu items in the action bar are tapped
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sign_out -> signOut()
            else -> print("Action unknown")
        }
        return super.onOptionsItemSelected(item)
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
     * Creates the sign-out button and initiates the Firebase and Google sign-out process
     */
    private fun signOut() {
        val googleSignInOption: GoogleSignInOptions = buildGoogleSignOutOption()
        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)
        auth.signOut() // Sign out of Firebase
        googleSignInClient.signOut().addOnCompleteListener{ // Sign out of the Google account
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            this.finish()
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
