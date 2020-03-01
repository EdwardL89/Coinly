package com.eightnineapps.coinly

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val googleSignInOption: GoogleSignInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)
        val googleSignOutButton = findViewById<Button>(R.id.sign_out_button)
        googleSignOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            googleSignInClient.signOut().addOnCompleteListener{
                startActivity(Intent(applicationContext, FirebaseAuthActivity::class.java))
                this.finish()
            }
        }
    }
}
