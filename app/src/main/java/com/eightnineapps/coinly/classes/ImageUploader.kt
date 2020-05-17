package com.eightnineapps.coinly.classes

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.eightnineapps.coinly.views.activities.CreateProfileActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

abstract class ImageUploader() : AppCompatActivity() {

    private var IMAGE_SELECTION_SUCCESS = 1

    /**
     * Begins the process to upload the selected image to the Firebase storage reference.
     * Does not upload yet because we need to make sure a new user has been created (hitting the
     * "done" button) so we can name the image file the user's unique ID.
     */
    fun prepareForFirebaseStorageUpload(data: Intent?) : ByteArray {
        val selectedImageUri = data!!.data
        val selectedImageBitmap = convertUriToBitmap(selectedImageUri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    /**
     * Converts a Uri to a Bitmap
     */
    private fun convertUriToBitmap(selectedImageUri: Uri?): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val imageSource = ImageDecoder.createSource(this.contentResolver, selectedImageUri!!)
            ImageDecoder.decodeBitmap(imageSource)
        } else {
            MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
        }
    }

    /**
     * Uploads a byte array to firebase storage
     */
    fun uploadToImageStorage(imageByteData: ByteArray, currentUser: User): UploadTask {
        return CreateProfileActivity.imageStorage.reference.child("profile_pictures").child(currentUser.id).putBytes(imageByteData)
    }

    /**
     * Queries the Firebase Storage reference for the user's profile picture
     */
    fun downloadProfilePicture(newUser: User): Task<Uri> {
        return CreateProfileActivity.imageStorage.reference
            .child("profile_pictures")
            .child(newUser.id).downloadUrl
    }

    fun chooseImageFromGallery() {
        val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(openGallery, IMAGE_SELECTION_SUCCESS)
    }
}