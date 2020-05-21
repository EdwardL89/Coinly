package com.eightnineapps.coinly.models

import androidx.lifecycle.MutableLiveData
import com.eightnineapps.coinly.classes.Notification
import com.eightnineapps.coinly.classes.User

/**
 * Model that provides access to the current user of the app. Can only have 1 current user
 * at a time, thus an object is declared
 */
object CurrentUser {

    var instance: User? = null

    val coins get() = MutableLiveData<Int>(instance?.coins)

    val bio get() = MutableLiveData<String>(instance?.bio)

    val numberOfBigs get() = MutableLiveData<Int>(instance?.bigs?.size)

    val displayName get() = MutableLiveData<String>(instance?.displayName)

    val numberOfLittles get() = MutableLiveData<Int>(instance?.littles?.size)

    val profilePictureUri get() = MutableLiveData<String>(instance?.profilePictureUri)

    val prizesClaimed get() = MutableLiveData<MutableList<String>>(instance?.prizesClaimed)

    val notifications get() = MutableLiveData<MutableList<Notification>>(instance?.notifications)
}