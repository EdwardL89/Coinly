package com.eightnineapps.coinly.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.User
import com.eightnineapps.coinly.viewmodels.activityviewmodels.LittleProfileViewModel
import kotlinx.android.synthetic.main.activity_little_profile.*

/**
 * Displays the prizes the little has claim from you as well as the prizes you have set for the little
 */
class LittleProfileActivity : AppCompatActivity() {

    private lateinit var littleProfileViewModel: LittleProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        littleProfileViewModel = ViewModelProvider(this).get(LittleProfileViewModel::class.java)
        littleProfileViewModel.observedUserInstance = intent.getSerializableExtra("observed_user") as User
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_little_profile)
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

    /**
     * Attaches the on click listeners to all buttons
     */
    private fun setUpButtons() {
        give_coins_button.setOnClickListener {
            startActivity(Intent(this, GiveCoinsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
        revoke_coins_button.setOnClickListener {
            startActivity(Intent(this, RevokeCoinsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
        remove_little_button.setOnClickListener {
            littleProfileViewModel.removeLittleAndSendBack(this)
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
