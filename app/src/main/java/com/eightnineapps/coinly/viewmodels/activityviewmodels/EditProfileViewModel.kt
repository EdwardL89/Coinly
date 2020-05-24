package com.eightnineapps.coinly.viewmodels.activityviewmodels

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
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.eightnineapps.coinly.models.ImgStorage
import java.io.ByteArrayOutputStream

class EditProfileViewModel: ViewModel() {

    val imgUploadHelper = ImageUploadHelper()
    private var userProfilePictureByteData = ByteArrayOutputStream().toByteArray()
    var currentUser = CurrentUser
        private set

    /**
     * Update the given user with the given fields
     */
    fun updateUserFields(realName: EditText, displayName: EditText, bio: EditText) {
        currentUser.bio.value = bio.text.toString()
        currentUser.realName.value = realName.text.toString()
        currentUser.displayName.value = displayName.text.toString()
        updateProfilePicture()
    }

    /**
     * Updates the user object in the firestore with the changes made
     */
    fun commitProfileChanges(context: Context) {
        val writeBatch = Firestore.getInstance().batch()
        val userReference = Firestore.read(currentUser.instance!!)
        writeBatch.update(userReference, "bio", currentUser.bio.value)
        writeBatch.update(userReference, "realName", currentUser.realName.value)
        writeBatch.update(userReference, "displayName", currentUser.displayName.value)
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
     * Uploads image to storage nad updates the user's Uri
     */
    private fun updateProfilePicture() {
        ImgStorage.insert(userProfilePictureByteData, currentUser.instance!!.id).addOnSuccessListener {
            ImgStorage.read(currentUser.instance!!).addOnSuccessListener {
                            uri -> currentUser.profilePictureUri.value = uri.toString()
                            Firestore.update(currentUser.instance!!, "profilePictureUri", currentUser.profilePictureUri.value!!)
                    }
            }
    }

}