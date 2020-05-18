package com.eightnineapps.coinly.views.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.ImageUploader
import com.eightnineapps.coinly.classes.User
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.ByteArrayOutputStream

/**
 * Allows the user to edit their profile information
 */
class EditProfileActivity : AppCompatActivity() {

    private lateinit var currentUser: User
    private var IMAGE_SELECTION_SUCCESS = 1
    private lateinit var userProfilePictureByteData: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        addCoinlyActionBarTitle()
        userProfilePictureByteData = ByteArrayOutputStream().toByteArray()
        populateUserDetailFields()
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
     * Catches the result of the intent that opens the gallery to select a profile picture image
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_SELECTION_SUCCESS && resultCode == RESULT_OK) {
            Glide.with(applicationContext).load(data!!.data).into(user_profile_picture)
            userProfilePictureByteData = prepareForFirebaseStorageUpload(data, this)
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

    /**
     * Populates all the editable fields of the user's profile with the existing information
     */
    private fun populateUserDetailFields() {
        currentUser = intent.getSerializableExtra("current_user") as User
        Glide.with(this).load(currentUser.profilePictureUri).into(user_profile_picture)
        real_name_editText.setText(currentUser.realName)
        display_name_editText.setText(currentUser.displayName)
        bio_edit_text.setText(currentUser.bio)
    }

    /**
     * Sets the on click listeners of all buttons of this activity
     */
    private fun setUpButtons() {
        cancel_edit_profile_button.setOnClickListener {
            finish()
        }
        done_edit_profile_button.setOnClickListener {
            commitProfileChanges()
        }
        add_profile_picture_button.setOnClickListener {
            val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(openGallery, IMAGE_SELECTION_SUCCESS)
        }
    }

    /**
     * Updates the user object in the firestore with the changes made
     */
    private fun commitProfileChanges() {
        currentUser.realName = real_name_editText.text.toString()
        currentUser.displayName = display_name_editText.text.toString()
        currentUser.bio = bio_edit_text.text.toString()
        updateProfilePicture()

        val writeBatch = HomeActivity.database.batch()
        val userReference = HomeActivity.database.collection("users").document(currentUser.email!!)
        writeBatch.update(userReference, "realName", currentUser.realName)
        writeBatch.update(userReference, "displayName", currentUser.displayName)
        writeBatch.update(userReference, "bio", currentUser.bio)
        writeBatch.commit().addOnSuccessListener {
            Toast.makeText(this, "Changes Saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateProfilePicture() {
        //Temporary until view model is implemented:
        val helper = ImageUploader()
        helper.uploadToImageStorage(userProfilePictureByteData, currentUser)
            .addOnSuccessListener {
                helper.downloadProfilePicture(currentUser)
                    .addOnSuccessListener {
                        uri ->
                        currentUser.profilePictureUri = uri.toString()
                        HomeActivity.database.collection("users").document(currentUser.email!!).update("profilePictureUri", currentUser.profilePictureUri)
                    }
            }
    }

    /**
     * Begins the process to upload the selected image to the Firebase storage reference.
     * Does not upload yet because we need to make sure a new user has been created (hitting the
     * "done" button) so we can name the image file the user's unique ID.
     */
    private fun prepareForFirebaseStorageUpload(data: Intent?, context: Context) : ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        convertUriToBitmap(data!!.data, context).compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    /**
     * Converts a Uri to a Bitmap
     */
    private fun convertUriToBitmap(selectedImageUri: Uri?, context: Context): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, selectedImageUri!!))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, selectedImageUri)
        }
    }
}
