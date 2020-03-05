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
    var bigs = mutableListOf<String>()
    var littles = mutableListOf<String>()
    var prizes = mutableListOf<String>()

}