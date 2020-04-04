package com.eightnineapps.coinly.classes

import java.io.Serializable

/**
 * Represents a user of the app
 */
data class User(var _realName: String = "", var _displayName: String = "", var _id: String = "", var _email: String? = ""): Serializable {

    //Properties
    var coins = 0
    var isBig = false
    var id = _id
    var email = _email
    var realName = _realName
    var displayName = _displayName
    var bigs: MutableList<String> = mutableListOf()
    var prizes: MutableList<String> = mutableListOf()
    var littles: MutableList<String> = mutableListOf()
    var notifications: MutableList<String> = mutableListOf()
    var profilePictureUri: String = ""

}