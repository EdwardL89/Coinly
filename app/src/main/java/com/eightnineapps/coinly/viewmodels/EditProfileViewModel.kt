package com.eightnineapps.coinly.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.classes.ImageUploadHelper
import com.eightnineapps.coinly.classes.User
import com.eightnineapps.coinly.models.Firestore
import com.eightnineapps.coinly.models.ImgStorage
import java.io.ByteArrayOutputStream

class EditProfileViewModel: ViewModel() {

    val imgUploadHelper = ImageUploadHelper()
    private var userProfilePictureByteData = ByteArrayOutputStream().toByteArray()
    lateinit var currentUser: User
        private set

    /**
     * Updates the user object in the firestore with the changes made
     */
    fun commitProfileChanges(context: Context) {
        val writeBatch = Firestore.getInstance().batch()
        val userReference = Firestore.read(currentUser)
        writeBatch.update(userReference, "realName", currentUser.realName)
        writeBatch.update(userReference, "displayName", currentUser.displayName)
        writeBatch.update(userReference, "bio", currentUser.bio)
        writeBatch.commit().addOnSuccessListener {
            Toast.makeText(context, "Changes Saved!", Toast.LENGTH_SHORT).show()
            (context as Activity).finish()
        }
    }

    /**
     * Loads selected image to an image view and prepares the image to be uploaded to Storage
     */
    fun handleGallerySelectionCompletion(requestCode: Int, resultCode: Int, data: Intent?, appContext: Context, context: Context, profilePictureImgView: ImageView) {
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            Glide.with(appContext).load(data!!.data).into(profilePictureImgView)
            userProfilePictureByteData = imgUploadHelper.prepareForFirebaseStorageUpload(data, context)
        }
    }

    /**
     * Update the given user with the given fields
     */
    fun updateUserFields(realName: EditText, displayName: EditText, bio: EditText) {
        currentUser.realName = realName.text.toString()
        currentUser.displayName = displayName.text.toString()
        currentUser.bio = bio.text.toString()
        updateProfilePicture(currentUser)
    }

    /**
     * Instantiates the User object that's going to be used for editing
     */
    fun instantiateCurrentUser(context: Context) {
        currentUser = (context as Activity).intent.getSerializableExtra("current_user") as User
    }

    /**
     * Uploads image to storage nad updates the user's Uri
     */
    private fun updateProfilePicture(user: User) {
        ImgStorage.insert(userProfilePictureByteData, user.id).addOnSuccessListener {
            ImgStorage.read(user).addOnSuccessListener {
                            uri -> user.profilePictureUri = uri.toString()
                            Firestore.update(user, "profilePictureUri", user.profilePictureUri)
                    }
            }
    }

}