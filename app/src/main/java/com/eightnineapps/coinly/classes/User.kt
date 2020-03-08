package com.eightnineapps.coinly.classes

/**
 * Represents a user of the app
 */
class User(_realName: String, _displayName: String, _id: String, _email: String?) {

    //Properties
    var coins = 0
    var isBig = false
    var id = _id
    var email = _email
    var realName = _realName
    var displayName = _displayName
    var bigs: MutableList<String> = mutableListOf()
    var littles: MutableList<String> = mutableListOf()
    var prizes: MutableList<String> = mutableListOf()

}