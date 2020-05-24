package com.eightnineapps.coinly.views.activities.actions

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.databinding.ActivityAppealBinding
import com.eightnineapps.coinly.viewmodels.activityviewmodels.actions.AppealViewModel
import kotlinx.android.synthetic.main.activity_appeal.*

/**
 * Allows the little to send an appeal to an action done by the little's big
 */
class AppealActivity : AppCompatActivity() {

    private lateinit var appealViewModel: AppealViewModel
    private lateinit var binding: ActivityAppealBinding
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appealViewModel = ViewModelProvider(this).get(AppealViewModel::class.java)
        appealViewModel.observedUserDisplayName = intent.getSerializableExtra("display_name") as String
        binding = DataBindingUtil.setContentView(this, R.layout.activity_appeal)
        binding.appealViewModel = appealViewModel
        view = binding.root
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
        cancel_appeal_coins_button.setOnClickListener {
            finish()
        }
    }
}
