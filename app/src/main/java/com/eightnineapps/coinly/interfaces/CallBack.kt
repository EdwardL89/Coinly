package com.eightnineapps.coinly.interfaces

/**
 * An interface that lets you access Firestore data outside its onCompleteListener method
 */
interface CallBack {

    /**
     * Call back helper method to allow a double/two-layered Firestore query for the Big and Little tabs.
     * Queries the Firestore for the display name of a user, given their email.
     */
    fun secondQueryCallBack(userEmails: MutableList<*>)
}