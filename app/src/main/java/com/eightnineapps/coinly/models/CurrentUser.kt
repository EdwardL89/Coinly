package com.eightnineapps.coinly.models

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.eightnineapps.coinly.classes.User

/**
 * Model that provides access to the current user of the app. Can only have 1 current user
 * at a time, thus an object is declared
 */
object CurrentUser {

    var instance: User? = null

    val realName = MutableLiveData<String>()

}