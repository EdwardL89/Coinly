package com.eightnineapps.coinly.models

import androidx.lifecycle.MutableLiveData
import com.eightnineapps.coinly.classes.objects.User
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Model that provides access to the current user of the app. Can only have 1 current user
 * at a time, thus an object is declared
 */
object CurrentUser {

    var instance: User? = null
    var bigToBeRemoved: User? = null
    var littleToBeRemoved: User? = null
    var bigToBeAdded: DocumentSnapshot? = null
    var littleToBeAdded: DocumentSnapshot? = null

    val numberOfBigs = MutableLiveData<Int>()
    val numberOfLittles = MutableLiveData<Int>()
    val numOfPrizesGiven = MutableLiveData<Int>()
    val numOfPrizesClaimed = MutableLiveData<Int>()
    val avgPriceOfPrizesGiven = MutableLiveData<Int>()
    val avgPriceOfPrizesClaimed = MutableLiveData<Int>()
    val coins = MutableLiveData<Int>()
    val bio = MutableLiveData<String>()
    val realName = MutableLiveData<String>()
    val displayName = MutableLiveData<String>()
    val profilePictureUri = MutableLiveData<String>()

    /**
     * Sets the user object to be used throughout the app
     */
    fun setUserInstance(user: User) {
        instance = user
    }

    /**
     * Instantiates the live data of this singleton to their initial values
     */
    fun instantiateLiveData() {
        bio.value = instance!!.bio
        coins.value = instance!!.coins
        realName.value = instance!!.realName
        numberOfBigs.value = instance!!.numOfBigs
        displayName.value = instance!!.displayName
        numberOfLittles.value = instance!!.numOfLittles
        profilePictureUri.value = instance!!.profilePictureUri
        numOfPrizesGiven.value = instance!!.numOfPrizesGiven
        numOfPrizesClaimed.value = instance!!.numOfPrizesClaimed
        avgPriceOfPrizesGiven.value = instance!!.avgPriceOfPrizesGiven
        avgPriceOfPrizesClaimed.value = instance!!.avgPriceOfPrizesClaimed
    }
}