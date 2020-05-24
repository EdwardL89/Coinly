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
import com.eightnineapps.coinly.viewmodels.activityviewmodels.BigProfileViewModel
import kotlinx.android.synthetic.main.activity_big_profile.*

/**
 * Displays the prizes a little can claim from this big as well as the prizes already claimed
 */
class BigProfileActivity : AppCompatActivity() {

    private lateinit var bigProfileViewModel: BigProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        bigProfileViewModel = ViewModelProvider(this).get(BigProfileViewModel::class.java)
        bigProfileViewModel.observedUserInstance = intent.getSerializableExtra("observed_user") as User
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_big_profile)
        addCoinlyActionBarTitle()
        setUpButtons()
    }

    /**
     * Makes for a clean transition back to the previous activity with no animation or flashes
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    /**
     * Sets up the on click listeners for all buttons
     */
    private fun setUpButtons() {
        appeal_button.setOnClickListener {
            startActivity(Intent(this, AppealActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
        request_coins_button.setOnClickListener {
            startActivity(Intent(this, RequestCoinsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
        remove_big_button.setOnClickListener {
            bigProfileViewModel.removeBigAndSendBack(this)
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
