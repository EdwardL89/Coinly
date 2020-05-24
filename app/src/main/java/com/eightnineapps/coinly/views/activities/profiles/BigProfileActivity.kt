package com.eightnineapps.coinly.views.activities.profiles

import android.annotation.SuppressLint
import android.content.Intent
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
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.databinding.ActivityBigProfileBinding
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.BigProfileViewModel
import com.eightnineapps.coinly.views.activities.actions.AppealActivity
import com.eightnineapps.coinly.views.activities.actions.RequestCoinsActivity
import kotlinx.android.synthetic.main.activity_big_profile.*

/**
 * Displays the prizes a little can claim from this big as well as the prizes already claimed
 */
class BigProfileActivity : AppCompatActivity() {

    private lateinit var bigProfileViewModel: BigProfileViewModel
    private lateinit var observedUserInstance: User
    private lateinit var binding: ActivityBigProfileBinding
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bigProfileViewModel = ViewModelProvider(this).get(BigProfileViewModel::class.java)
        bigProfileViewModel.observedUserInstance = intent.getSerializableExtra("observed_user") as User
        observedUserInstance = bigProfileViewModel.observedUserInstance
        binding = DataBindingUtil.setContentView(this, R.layout.activity_big_profile)
        binding.observedUserInstance = observedUserInstance
        view = binding.root
        loadProfilePicture()
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
     * Loads the observe user's profile picture
     */
    private fun loadProfilePicture() {
        Glide.with(view).load(observedUserInstance.profilePictureUri).into(view.findViewById(R.id.user_profile_picture))
    }

    /**
     * Sets up the on click listeners for all buttons
     */
    private fun setUpButtons() {
        appeal_button.setOnClickListener {
            startActivity(Intent(this, AppealActivity::class.java)
                .putExtra("profile_picture_uri", observedUserInstance.profilePictureUri)
                .putExtra("display_name", observedUserInstance.displayName)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
        request_coins_button.setOnClickListener {
            startActivity(Intent(this, RequestCoinsActivity::class.java)
                .putExtra("profile_picture_uri", observedUserInstance.profilePictureUri)
                .putExtra("display_name", observedUserInstance.displayName)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
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
