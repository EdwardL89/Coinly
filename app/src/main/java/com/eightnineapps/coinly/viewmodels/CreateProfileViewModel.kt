package com.eightnineapps.coinly.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.classes.User
import com.eightnineapps.coinly.models.Firestore
import com.eightnineapps.coinly.models.ImgStorage
import com.eightnineapps.coinly.views.activities.HomeActivity
import com.eightnineapps.coinly.views.activities.LoginActivity.Companion.auth
import java.io.ByteArrayOutputStream
import kotlin.random.Random

class CreateProfileViewModel : ViewModel() {

    private val firestore: Firestore = Firestore()
    private val imgStorage: ImgStorage = ImgStorage()
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private var userProfilePictureByteData = ByteArrayOutputStream().toByteArray()

    /**
     * Makes sure all fields are filled in before continuing
     */
    fun verifyProfileCreationIsComplete(context: Context, realName: EditText, displayName: EditText, bio: EditText) {
        if (noFieldsEmpty(realName, displayName, bio)) uploadUserAndGoToHome(User(realName.text.toString(), displayName.text.toString(), generateId(), auth.currentUser?.email!!, bio.text.toString()), context)
        else Toast.makeText(context, "Info missing!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Loads selected image to an image view and prepares the image to be uploaded to Storage
     */
    fun handleGallerySelectionCompletion(requestCode: Int, resultCode: Int, data: Intent?, appContext: Context, context: Context, profilePictureImgView: ImageView) {
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            Glide.with(appContext).load(data!!.data).into(profilePictureImgView)
            userProfilePictureByteData = prepareForFirebaseStorageUpload(data, context)
        }
    }

    /**
     * Opens the gallery app and returns the selected image
     */
    fun chooseImageFromGallery(context: Context) {
        val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        (context as Activity).startActivityForResult(openGallery, 1)
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
        imgStorage.insert(userProfilePictureByteData, newUser.id).addOnSuccessListener {
                imgStorage.read(newUser).addOnSuccessListener { uri -> newUser.profilePictureUri = uri.toString()
                        firestore.insert(newUser).addOnSuccessListener { goToHomePage(context)
                            }.addOnFailureListener { Log.w("INFO", "Could not upload user to Firestore") }
                    }.addOnFailureListener { Log.w("INFO", "Could not download from Storage") }
            }.addOnFailureListener { Log.w("INFO", "Could not upload to Firebase storage") }
    }

    /**
     * Launches an intent to go to the home page activity
     */
    private fun goToHomePage(context: Context) {
        val intent = Intent(context, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        context.startActivity(intent)
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