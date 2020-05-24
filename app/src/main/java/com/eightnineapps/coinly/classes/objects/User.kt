package com.eightnineapps.coinly.classes.objects

import com.eightnineapps.coinly.classes.objects.Notification
import java.io.Serializable

/**
 * Represents a user of the app
 */
class User(_realName: String = "", _displayName: String = "", _id: String = "", _email: String? = "", _bio: String? = ""): Serializable {

    var coins = 0
    var id = _id
    var bio = _bio
    var email = _email
    var realName = _realName
    var displayName = _displayName
    var bigs: MutableList<String> = mutableListOf()
    var littles: MutableList<String> = mutableListOf()
    var prizesClaimed: MutableList<String> = mutableListOf()
    var prizesGiven: MutableList<String> = mutableListOf()
    var notifications: MutableList<Notification> = mutableListOf()
    var profilePictureUri: String = ""

}