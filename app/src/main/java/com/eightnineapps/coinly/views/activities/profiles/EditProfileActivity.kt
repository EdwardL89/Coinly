package com.eightnineapps.coinly.views.activities.profiles

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.databinding.ActivityEditProfileBinding
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.EditProfileViewModel
import com.firebase.ui.auth.data.model.User
import kotlinx.android.synthetic.main.activity_edit_profile.*

/**
 * Allows the user to edit their profile information
 */
class EditProfileActivity : AppCompatActivity() {

    private lateinit var editProfileViewModel: EditProfileViewModel
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editProfileViewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        binding.editProfileViewModel = editProfileViewModel
        view = binding.root
        addCoinlyActionBarTitle()
        addBackArrowToActionBar()
        loadProfilePicture()
        setUpButtons()
    }

    /**
     * Disable any and all animations when the going back from this activity
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
     * Catches the result of the intent that opens the gallery to select a profile picture image
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        editProfileViewModel.handleGallerySelectionCompletion(requestCode, resultCode, data, applicationContext, this, user_profile_picture)
    }

    /**
     * Sets the title of the action bar to the app name in the custom font through an image view
     */
    @SuppressLint("InflateParams")
    private fun addCoinlyActionBarTitle() {
        val actionBar = this.supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.customView = LayoutInflater.from(this).inflate(R.layout.app_bar_title, null)
        actionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))
    }

    /**
     * Populates all the editable fields of the user's profile with the existing information
     */
    private fun loadProfilePicture() {
        Glide.with(this).load(editProfileViewModel.currentUser.instance!!.profilePictureUri).into(user_profile_picture)
    }

    /**
     * Sets the on click listeners of all buttons of this activity
     */
    private fun setUpButtons() {
        cancel_edit_profile_button.setOnClickListener {
            onBackPressed()
        }
        done_edit_profile_button.setOnClickListener {
            editProfileViewModel.updateUserFields(real_name_editText, display_name_editText, bio_edit_text)
            editProfileViewModel.commitProfileChanges(this)
        }
        add_profile_picture_button.setOnClickListener {
            editProfileViewModel.imgUploadHelper.chooseImageFromGallery(this)
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
