package com.eightnineapps.coinly.views.activities.profiles

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.helpers.ImageUploadHelper
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.CreateProfileViewModel
import com.eightnineapps.coinly.views.activities.startup.HomeActivity
import kotlinx.android.synthetic.main.activity_create_profile.*

/**
 * Creates a new user
 */
class CreateProfileActivity : AppCompatActivity() {

    private val imgUploadHelper = ImageUploadHelper()
    private lateinit var createProfileViewModel: CreateProfileViewModel

    /**
     * Initializes what's required for the user to create their profile
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        createProfileViewModel = ViewModelProvider(this).get(CreateProfileViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        setupAddProfilePictureButton()
        addBackArrowToActionBar()
        addCoinlyActionBarTitle()
        setupDoneButton()
    }

    /**
     * Close the app when the user hits "back"
     */
    override fun onBackPressed() {
        super.onBackPressed()
        createProfileViewModel.authHelper.signOut(this)
    }

    /**
     * Catches the result of the intent that opens the gallery to select a profile picture image
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Glide.with(applicationContext).load(data!!.data).into(user_profile_picture)
            createProfileViewModel.saveSelectedImageByteData(imgUploadHelper.prepareForFirebaseStorageUpload(data, this))
        }
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
            imgUploadHelper.chooseImageFromGallery(this)
        }
    }

    /**
     * Creates a new user, writes it to the database, then navigates to the home page
     */
    private fun setupDoneButton() {
        done_button.setOnClickListener {
            val realName = real_name_editText.text.toString()
            val displayName = display_name_editText.text.toString()
            val bio = bio_edit_text.text.toString()
            if (createProfileViewModel.noFieldsEmpty(realName, displayName, bio)) {
                Toast.makeText(this, "Creating profile...", Toast.LENGTH_LONG).show()
                createProfileViewModel.retrieveCloudToken().addOnCompleteListener {
                    uploadUser(createProfileViewModel.createNewUser(realName, displayName, bio, it.result.toString()))
                }
            } else {
                Toast.makeText(this, "Info missing!", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Executes the steps to upload a new user to firestore
     */
    private fun uploadUser(newUser: User) {
        createProfileViewModel.saveProfilePicture(newUser.id).addOnSuccessListener {
            createProfileViewModel.getProfilePictureUri(newUser.id).addOnSuccessListener { uri -> newUser.profilePictureUri = uri.toString()
                createProfileViewModel.saveUser(newUser).addOnSuccessListener { goToHomePage(newUser)
                }.addOnFailureListener { Log.w("INFO", "Could not upload user to Firestore") }
            }.addOnFailureListener { Log.w("INFO", "Could not download from Storage") }
        }.addOnFailureListener { Log.w("INFO", "Could not upload to Firebase storage") }
    }

    /**
     * Launches an intent to go to the home page activity
     */
    private fun goToHomePage(user: User) {
        startActivity(Intent(this, HomeActivity::class.java).putExtra("current_user", user)
            .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
    }

    /**
     * Adds a back arrow to navigate back to the previous activity
     */
    private fun addBackArrowToActionBar() {
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.arrow_back)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
