package com.eightnineapps.coinly.views.activities.actions

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.databinding.ActivityRevokeCoinsBinding
import com.eightnineapps.coinly.viewmodels.activityviewmodels.actions.RevokeCoinsViewModel
import kotlinx.android.synthetic.main.activity_revoke_coins.*

/**
 * Allows a big to revoke a specified number of coins from a little
 */
class RevokeCoinsActivity : AppCompatActivity() {

    private lateinit var revokeCoinsViewModel: RevokeCoinsViewModel
    private lateinit var binding: ActivityRevokeCoinsBinding
    private lateinit var view: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        revokeCoinsViewModel = ViewModelProvider(this).get(RevokeCoinsViewModel::class.java)
        revokeCoinsViewModel.observedUserDisplayName = intent.getSerializableExtra("display_name") as String
        binding = DataBindingUtil.setContentView(this, R.layout.activity_revoke_coins)
        binding.revokeCoinsViewModel = revokeCoinsViewModel
        view = binding.root
        addBackArrowToActionBar()
        addCoinlyActionBarTitle()
        loadProfilePicture()
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
     * Determines actions based on what items in the action bar are selected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * Loads the observe user's profile picture
     */
    private fun loadProfilePicture() {
        Glide.with(view).load(intent.getSerializableExtra("profile_picture_uri") as String).into(view.findViewById(R.id.user_profile_picture))
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

    /**
     * Sets the on click listeners of all buttons of this activity
     */
    private fun setUpButtons() {
        cancel_revoke_coins_button.setOnClickListener {
            onBackPressed()
        }
    }

    /**
     * Adds a back arrow to navigate back to the previous activity
     */
    private fun addBackArrowToActionBar() {
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.arrow_back)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
