package com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.classes.helpers.AuthHelper
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.models.Firestore
import com.eightnineapps.coinly.models.ImgStorage
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import kotlin.random.Random

class CreateProfileViewModel : ViewModel() {

    val authHelper = AuthHelper()
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private var userProfilePictureByteData = ByteArrayOutputStream().toByteArray()
    private val STARTING_COIN_AMOUNT = 100

    /**
     * Saves the byte data of the selected image for the user's profile picture
     */
    fun saveSelectedImageByteData(byteData: ByteArray) {
        userProfilePictureByteData = byteData
    }

    /**
     * Determines whether or not all fields are empty
     */
    fun noFieldsEmpty(realName: String, displayName: String, bio: String): Boolean {
        return userProfilePictureByteData.isNotEmpty() && realName != "" &&
                displayName != "" && bio != ""
    }

    /**
     * Generates a random 30 character, alphanumerical id for each user
     */
    private fun generateId(): String {
        return (1..30).map { Random.nextInt(0, charPool.size) }.map(charPool::get).joinToString("")
    }

    /**
     * Saves a new user to the firestore
     */
    fun saveUser(newUser: User): Task<Void> {
        return Firestore.insert(newUser)
    }

    /**
     * Gets the uri of a profile picture of the given user id
     */
    fun getProfilePictureUri(newUserId: String) : Task<Uri> {
        return ImgStorage.read(newUserId)
    }

    /**
     * Saves a profile picture under the User's id in firebase storage
     */
    fun saveProfilePicture(newUserId: String): UploadTask {
        return ImgStorage.insert(userProfilePictureByteData, "profile_pictures/$newUserId")
    }

    /**
     * Instantiates and returns a new User object with the given attributes
     */
    fun createNewUser(realName: String, displayName: String, bio: String, cloudToken: String): User {
        val newUser =  User(realName, displayName, generateId(), authHelper.getAuthUserEmail(), bio)
        newUser.coins = STARTING_COIN_AMOUNT
        newUser.token = cloudToken
        return newUser
    }

    /**
     * Retrieves the token used for the current user's cloud function
     */
    fun retrieveCloudToken(): Task<String> {
        return FirebaseMessaging.getInstance().token
    }
}