package com.eightnineapps.coinly.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.eightnineapps.coinly.R

class LittleProfileActivity : AppCompatActivity() {

    private lateinit var giveCoinsButton: Button
    private lateinit var revokeCoinsButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_little_profile)
        giveCoinsButton = findViewById(R.id.give_coins_button)
        revokeCoinsButton = findViewById(R.id.revoke_coins_button)
        setUpButtons()
        addCoinlyActionBarTitle()
    }

    /**
     * Makes for a clean transition back to the previous activity with no animation or flashes
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private fun setUpButtons() {
        giveCoinsButton.setOnClickListener {
            val intent = Intent(this, GiveCoinsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        revokeCoinsButton.setOnClickListener {
            val intent = Intent(this, RevokeCoinsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
    }

    /**
     * Sets the title of the action bar to the app name in the custom font through an image view
     */
    @SuppressLint("InflateParams")
    private fun addCoinlyActionBarTitle() {
        val actionBar = this.supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false)
        val v: View = LayoutInflater.from(this).inflate(R.layout.app_bar_title, null)
        actionBar.customView = v
        actionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))
    }
}
