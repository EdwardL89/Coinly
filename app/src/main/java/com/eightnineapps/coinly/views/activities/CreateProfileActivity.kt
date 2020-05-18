package com.eightnineapps.coinly.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.viewmodels.CreateProfileViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_create_profile.*
import kotlin.system.exitProcess


/**
 * Creates a new user
 */
class CreateProfileActivity : AppCompatActivity() {

    private lateinit var createProfileViewModel: CreateProfileViewModel

    /**
     * Initializes what's required for the user to create their profile
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        createProfileViewModel = ViewModelProvider(this).get(CreateProfileViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        addCoinlyActionBarTitle()
        setupDoneButton()
        setupAddProfilePictureButton()
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
     * Catches the result of the intent that opens the gallery to select a profile picture image
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        createProfileViewModel.handleGallerySelectionCompletion(requestCode, resultCode, data, applicationContext, this, user_profile_picture)
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
     * Opens the gallery to select a profile picture image
     */
    private fun setupAddProfilePictureButton() {
        add_profile_picture_button.setOnClickListener {
            createProfileViewModel.imgUploadHelper.chooseImageFromGallery(this)
        }
    }

    /**
     * Creates a new user, writes it to the database, then navigates to the home page
     */
    private fun setupDoneButton() {
        done_button.setOnClickListener {
            createProfileViewModel.verifyProfileCreationIsComplete(this, real_name_editText, display_name_editText, bio_edit_text)
        }
    }
}
