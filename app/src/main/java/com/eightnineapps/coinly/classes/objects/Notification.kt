package com.eightnineapps.coinly.classes.objects

import com.eightnineapps.coinly.enums.NotificationType
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable

class Notification: Serializable {
    var id = ""
    var coins = 0
    var message = ""
    var toAddUserEmail = ""
    var moreInformation = ""
    var addingToUserEmail = ""
    var profilePictureUri = ""
    var type = NotificationType.DEFAULT

    /**
     * Determines the type of notification and calls the appropriate method to handle the notification
     */
    fun execute() {
        when (type) {
            NotificationType.ADDING_AS_BIG -> executeAddAsBig()
            NotificationType.ADDING_AS_LITTLE -> executeAddAsLittle()
            NotificationType.GIVING_COINS -> addressCoinsGiven()
            else -> acceptRequest()
        }
    }

    /**
     * Addresses the coins given. Keep in mind the giver will be executing the notification,
     * not the receiver of the notification.
     */
    private fun addressCoinsGiven() {
        CurrentUser.subtractCoins(coins)
        Firestore.update(CurrentUser.instance!!, "coins", CurrentUser.coins.value.toString())
        Firestore.read(toAddUserEmail).get().addOnCompleteListener {
            it.result!!.reference.update("coins", Integer.parseInt(it.result!!["coins"].toString()) + coins)
        }
    }

    /**
     * Start the coin transfer that fulfills the request by the Little  
     */
    private fun acceptRequest() {
        CurrentUser.subtractCoins(coins)
        Firestore.update(CurrentUser.instance!!, "coins", CurrentUser.coins.value.toString())
        Firestore.read(addingToUserEmail).get().addOnCompleteListener {
            it.result!!.reference.update("coins", Integer.parseInt(it.result!!["coins"].toString()) + coins)
        }
    }

    /**
     * Queries the database to retrieve the bigs and littles list of both users to complete the request
     */
    private fun executeAddAsBig() {
        Firestore.read(addingToUserEmail).get().addOnCompleteListener {
            CurrentUser.littleToBeAdded = it.result!!
            addAsBig(CurrentUser.littleToBeAdded!!)
        }
    }

    private fun addAsBig(littleToBeAdded: DocumentSnapshot) {
        val previousNumOfBigsTheLittleHas = Integer.parseInt(littleToBeAdded["numOfBigs"].toString())
        littleToBeAdded.reference.update("numOfBigs", previousNumOfBigsTheLittleHas + 1)
        Firestore.addBig(littleToBeAdded["email"].toString(), CurrentUser.getEmail()!!, CurrentUser.profilePictureUri.value!!)

        CurrentUser.incrementLittles()
        Firestore.update(CurrentUser.instance!!, "numOfLittles", CurrentUser.numOfLittles.value.toString())
        Firestore.addLittle(CurrentUser.getEmail()!!, littleToBeAdded["email"].toString(), littleToBeAdded["profilePictureUri"].toString())
    }

    /**
     * Queries the database to retrieve the bigs and littles list of both users to complete the request
     */
    private fun executeAddAsLittle() {
        Firestore.read(toAddUserEmail).get().addOnCompleteListener {
            CurrentUser.bigToBeAdded = it.result!!
            addAsLittle(CurrentUser.bigToBeAdded!!)
        }
    }

    /**
     * Adds the current user as a little to the requester and updates the firestore
     */
    private fun addAsLittle(bigToBeAdded: DocumentSnapshot) {
        val previousNumOfLittlesTheBigHas = Integer.parseInt(bigToBeAdded["numOfLittles"].toString())
        bigToBeAdded.reference.update("numOfLittles",  previousNumOfLittlesTheBigHas + 1)
        Firestore.addLittle(bigToBeAdded["email"].toString(), CurrentUser.getEmail()!!, CurrentUser.profilePictureUri.value!!)

        CurrentUser.incrementBigs()
        Firestore.update(CurrentUser.instance!!, "numOfBigs", CurrentUser.numOfBigs.value.toString())
        Firestore.addBig(CurrentUser.getEmail()!!, bigToBeAdded["email"].toString(), bigToBeAdded["profilePictureUri"].toString())
    }
}