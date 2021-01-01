package com.eightnineapps.coinly.views.activities.startup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eightnineapps.coinly.R
import kotlinx.android.synthetic.main.activity_login_or_register.*
import kotlin.system.exitProcess

class LoginOrRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_or_register)
        setupButtons()
    }

    /**
     * Makes for a clean transition back to the previous activity with no animation or flashes
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    /**
     * Close the app when the user hits "back"
     */
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
        exitProcess(0)
    }

    private fun setupButtons() {
        go_to_login_button.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
        go_to_register_button.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
    }
}