package com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.classes.helpers.AuthHelper
import com.eightnineapps.coinly.classes.helpers.ImageUploadHelper
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.models.Firestore
import com.eightnineapps.coinly.models.ImgStorage
import com.eightnineapps.coinly.views.activities.startup.HomeActivity
import java.io.ByteArrayOutputStream
import kotlin.random.Random

class CreateProfileViewModel : ViewModel() {

    val authHelper = AuthHelper()
    val imgUploadHelper =
        ImageUploadHelper()
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private var userProfilePictureByteData = ByteArrayOutputStream().toByteArray()

    /**
     * Makes sure all fields are filled in before continuing
     */
    fun verifyProfileCreationIsComplete(context: Context, realName: EditText, displayName: EditText, bio: EditText) {
        if (noFieldsEmpty(realName, displayName, bio)) {
            Toast.makeText(context, "Creating profile...", Toast.LENGTH_LONG).show()
            uploadUserAndGoToHome(
                User(
                    realName.text.toString(),
                    displayName.text.toString(),
                    generateId(),
                    authHelper.getAuthUserEmail(),
                    bio.text.toString()
                ), context)
        } else {
            Toast.makeText(context, "Info missing!", Toast.LENGTH_SHORT).show()
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
     * Determines whether or not all fields are empty
     */
    private fun noFieldsEmpty(realName: EditText, displayName: EditText, bio: EditText): Boolean {
        return userProfilePictureByteData.isNotEmpty() && realName.text.toString() != "" &&
                displayName.text.toString() != "" && bio.text.toString() != ""
    }

    /**
     * Generates a random 30 character, alphanumerical id for each user
     */
    private fun generateId(): String {
        return (1..30).map { Random.nextInt(0, charPool.size) }.map(charPool::get).joinToString("")
    }

    /**
     * Uploads the profile picture to Storage, downloads and saves the image's Uri for reuse, then
     * uploads the user to the Firestore
     */
    private fun uploadUserAndGoToHome(newUser: User, context: Context) {
        ImgStorage.insert(userProfilePictureByteData, "profile_pictures/${newUser.id}").addOnSuccessListener {
            ImgStorage.read(newUser).addOnSuccessListener { uri -> newUser.profilePictureUri = uri.toString()
                        Firestore.insert(newUser).addOnSuccessListener { goToHomePage(context, newUser)
                            }.addOnFailureListener { Log.w("INFO", "Could not upload user to Firestore") }
                    }.addOnFailureListener { Log.w("INFO", "Could not download from Storage") }
            }.addOnFailureListener { Log.w("INFO", "Could not upload to Firebase storage") }
    }

    /**
     * Launches an intent to go to the home page activity
     */
    private fun goToHomePage(context: Context, user: User) {
        val intent = Intent(context, HomeActivity::class.java)
            .putExtra("current_user", user)
            .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        context.startActivity(intent)
    }
}