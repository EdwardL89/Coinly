package com.eightnineapps.coinly.models

import androidx.lifecycle.MutableLiveData
import com.eightnineapps.coinly.classes.objects.Notification
import com.eightnineapps.coinly.classes.objects.User

/**
 * Model that provides access to the current user of the app. Can only have 1 current user
 * at a time, thus an object is declared
 */
object CurrentUser {

    var instance: User? = null

    val coins = MutableLiveData<Int>()

    val bio = MutableLiveData<String>()

    val realName = MutableLiveData<String>()

    val numberOfBigs = MutableLiveData<Int>()

    val displayName = MutableLiveData<String>()

    val numberOfLittles = MutableLiveData<Int>()

    val profilePictureUri = MutableLiveData<String>()

    val prizesClaimed = MutableLiveData<MutableList<String>>()

    val notifications = MutableLiveData<MutableList<Notification>>()
}