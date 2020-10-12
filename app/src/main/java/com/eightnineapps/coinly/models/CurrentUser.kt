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
    var notificationsToBeRemoved = mutableListOf<String>()

    val numOfBigs = MutableLiveData<Int>()
    val numOfLittles = MutableLiveData<Int>()
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
     * Returns the ID of the current user
     */
    fun getId() = instance?.id

    /**
     * Sets the user object to be used throughout the app
     */
    fun setUserInstance(user: User) {
        instance = user
    }

    /**
     * Subtracts coins from the user instance and the live data
     */
    fun subtractCoins(coinsSubtracting: Int) {
        instance!!.coins -= coinsSubtracting
        coins.value = coins.value!!.minus(coinsSubtracting)

    }

    /**
     * Increments the big count
     */
    fun incrementBigs() {
        instance!!.numOfBigs += 1
        numOfBigs.value = numOfBigs.value!!.plus(1)
    }

    /**
     * Decrements the big count
     */
    fun decrementBigs() {
        instance!!.numOfBigs -= 1
        numOfBigs.value = numOfBigs.value!!.minus(1)
    }

    /**
     * Increments the little count
     */
    fun incrementLittles() {
        instance!!.numOfLittles += 1
        numOfLittles.value = numOfLittles.value!!.plus(1)
    }

    /**
     * Decrements the little count
     */
    fun decrementLittles() {
        instance!!.numOfLittles -= 1
        numOfLittles.value = numOfLittles.value!!.minus(1)
    }

    /**
     * Increments the number of prizes given by the current user
     */
    fun incrementPrizesClaimed() {
        instance!!.numOfPrizesClaimed += 1
        numOfPrizesClaimed.value = numOfPrizesClaimed.value!!.plus(1)
    }

    /**
     * Updates the average price of the prizes claimed of the current user
     */
    fun updateAveragePriceOfPrizesClaimed(newAverage: Int) {
        instance!!.avgPriceOfPrizesClaimed = newAverage
        avgPriceOfPrizesClaimed.value = newAverage
    }

    /**
     * Returns the email of the current user
     */
    fun getEmail() = instance!!.email

    /**
     * Instantiates the live data of this singleton to their initial values
     */
    fun instantiateLiveData() {
        bio.value = instance!!.bio
        coins.value = instance!!.coins
        realName.value = instance!!.realName
        numOfBigs.value = instance!!.numOfBigs
        displayName.value = instance!!.displayName
        numOfLittles.value = instance!!.numOfLittles
        numOfPrizesGiven.value = instance!!.numOfPrizesGiven
        profilePictureUri.value = instance!!.profilePictureUri
        numOfPrizesClaimed.value = instance!!.numOfPrizesClaimed
        avgPriceOfPrizesGiven.value = instance!!.avgPriceOfPrizesGiven
        avgPriceOfPrizesClaimed.value = instance!!.avgPriceOfPrizesClaimed
    }
}