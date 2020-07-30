package com.eightnineapps.coinly.classes.objects

import java.io.Serializable

/**
 * Represents a user of the app
 */
class User(_realName: String = "", _displayName: String = "", _id: String = "", _email: String? = "", _bio: String? = ""): Serializable {

    var coins = 0
    var id = _id
    var bio = _bio
    var numOfBigs: Int = 0
    var numOfLittles: Int = 0
    var email = _email
    var realName = _realName
    var displayName = _displayName
    var notifications: MutableList<Notification> = mutableListOf()
    var profilePictureUri: String = ""

}