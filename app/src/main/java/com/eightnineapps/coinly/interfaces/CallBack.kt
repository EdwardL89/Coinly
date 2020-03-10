package com.eightnineapps.coinly.interfaces

/**
 * An interface that lets you access Firestore data outside its onCompleteListener method
 */
interface CallBack {

    /**
     * The method that gets call to handle the data queried from the Firestore at a different location
     */
    fun onCallBack(usernames: MutableList<String>)
}