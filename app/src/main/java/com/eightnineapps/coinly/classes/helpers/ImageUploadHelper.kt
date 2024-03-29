package com.eightnineapps.coinly.classes.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import kotlin.random.Random

/**
 * Class with helper methods to upload an image to Firebase storage
 */
class ImageUploadHelper {

    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    /**
     * Opens the gallery app and returns the selected image
     */
    fun chooseImageFromGallery(context: Context) {
        val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        (context as Activity).startActivityForResult(openGallery, 1)
    }

    /**
     * Begins the process to upload the selected image to the Firebase storage reference.
     */
    fun prepareForFirebaseStorageUpload(data: Intent?, context: Context) : ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        convertUriToBitmap(data!!.data, context).compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    /**
     * Generates a random 30 character, alphanumerical id for each user
     */
    fun generateId(): String {
        return (1..30).map { Random.nextInt(0, charPool.size) }.map(charPool::get).joinToString("")
    }

    /**
     * Converts a Uri to a Bitmap
     */
    @Suppress("DEPRECATION")
    private fun convertUriToBitmap(selectedImageUri: Uri?, context: Context): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, selectedImageUri!!))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, selectedImageUri)
        }
    }

}