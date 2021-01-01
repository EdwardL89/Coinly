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
            NotificationType.CLAIMING_PRIZE -> addressClaimedPrize()
            else -> acceptRequest()
        }
    }

    private fun addressClaimedPrize() {}

    /**
     * Addresses the coins given. Keep in mind the giver will be executing the notification,
     * not the receiver of the notification.
     */
    private fun addressCoinsGiven() {
        CurrentUser.subtractCoins(coins)
        Firestore.update(CurrentUser.getEmail()!!, "coins", CurrentUser.coins.value.toString())
        Firestore.read(toAddUserEmail).get().addOnCompleteListener {
            it.result!!.reference.update("coins", Integer.parseInt(it.result!!["coins"].toString()) + coins)
        }
    }

    /**
     * Start the coin transfer that fulfills the request by the Little  
     */
    private fun acceptRequest() {
        CurrentUser.subtractCoins(coins)
        Firestore.update(CurrentUser.getEmail()!!, "coins", CurrentUser.coins.value.toString())
        Firestore.read(addingToUserEmail).get().addOnCompleteListener {
            it.result!!.reference.update("coins", Integer.parseInt(it.result!!["coins"].toString()) + coins)
        }
    }

    /**
     * Queries the database to retrieve the bigs and littles list of both users to complete the request
     */
    private fun executeAddAsBig() {
        CurrentUser.notificationsToBeRemoved.add(id)
        Firestore.read(addingToUserEmail).get().addOnCompleteListener {
            CurrentUser.littleToBeAdded = it.result!!
            updateLittleUsingReference(CurrentUser.littleToBeAdded!!)
            addLittleToBig(CurrentUser.littleToBeAdded!!)
        }
    }

    /**
     * Updates the bigs count of the little through a document reference and adds the big to it
     */
    private fun updateLittleUsingReference(littleToBeAdded: DocumentSnapshot) {
        val previousNumOfBigsTheLittleHas = Integer.parseInt(littleToBeAdded["numOfBigs"].toString())
        littleToBeAdded.reference.update("numOfBigs", previousNumOfBigsTheLittleHas + 1)
        Firestore.addBig(littleToBeAdded["email"].toString(), CurrentUser.getEmail()!!,
            CurrentUser.profilePictureUri.value!!, CurrentUser.displayName.value!!)
    }

    /**
     * Updates the littles count of the current user and adds the little to it
     */
    private fun addLittleToBig(littleToBeAdded: DocumentSnapshot) {
        CurrentUser.incrementLittles()
        Firestore.update(CurrentUser.getEmail()!!, "numOfLittles", CurrentUser.numOfLittles.value.toString())
        Firestore.addLittle(CurrentUser.getEmail()!!, littleToBeAdded["email"].toString(),
            littleToBeAdded["profilePictureUri"].toString(), littleToBeAdded["displayName"].toString())
    }


    /**l
     * Queries the database to retrieve the bigs and littles list of both users to complete the request
     */
    private fun executeAddAsLittle() {
        CurrentUser.notificationsToBeRemoved.add(id)
        Firestore.read(addingToUserEmail).get().addOnCompleteListener {
            CurrentUser.bigToBeAdded = it.result!!
            updateBigUsingReference(CurrentUser.bigToBeAdded!!)
            addBigToLittle(CurrentUser.bigToBeAdded!!)
        }
    }

    /**
     * Updates the little count of the big through a document reference and adds the little to it
     */
    private fun updateBigUsingReference(bigToBeAdded: DocumentSnapshot) {
        val previousNumOfLittlesTheBigHas = Integer.parseInt(bigToBeAdded["numOfLittles"].toString())
        bigToBeAdded.reference.update("numOfLittles",  previousNumOfLittlesTheBigHas + 1)
        Firestore.addLittle(bigToBeAdded["email"].toString(), CurrentUser.getEmail()!!,
            CurrentUser.profilePictureUri.value!!, CurrentUser.displayName.value!!)
    }

    /**
     * Updates the bigs count of the current user and adds the big to it
     */
    private fun addBigToLittle(bigToBeAdded: DocumentSnapshot) {
        CurrentUser.incrementBigs()
        Firestore.update(CurrentUser.getEmail()!!, "numOfBigs", CurrentUser.numOfBigs.value.toString())
        Firestore.addBig(CurrentUser.getEmail()!!, bigToBeAdded["email"].toString(),
            bigToBeAdded["profilePictureUri"].toString(), bigToBeAdded["displayName"].toString())
    }
}