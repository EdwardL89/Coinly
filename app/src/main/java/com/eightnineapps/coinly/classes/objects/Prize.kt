package com.eightnineapps.coinly.classes.objects

class Prize(_name: String, _price: Int) {

    //Properties
    var price = _price
    var name = _name
    var winner: User? = null
    var setter: User? = null
    var hasBeenAwarded = false

}