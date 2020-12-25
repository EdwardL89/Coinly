package com.eightnineapps.coinly.classes.objects

import java.io.Serializable

/**
 * A Prize object where the picture's URI string represents the prize's unique ID
 */
class Prize(_name: String = "", _price: Int = 0, _uri: String = "", _id: String = ""): Serializable {

    var uri = _uri
    var price = _price
    var name = _name
    var id = _id
}