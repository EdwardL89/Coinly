package com.eightnineapps.coinly.classes.objects

import java.io.Serializable

class Prize(_id: String, _name: String, _price: Int, _uri: String): Serializable {

    var id = _id
    var uri = _uri
    var price = _price
    var name = _name
    var winner: User? = null
    var setter: User? = null
    var hasBeenAwarded = false

}