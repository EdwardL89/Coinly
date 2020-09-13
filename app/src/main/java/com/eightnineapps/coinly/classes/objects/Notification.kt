package com.eightnineapps.coinly.classes.objects

import com.eightnineapps.coinly.enums.NotificationType
import com.eightnineapps.coinly.models.Firestore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable

class Notification: Serializable {
    var type = NotificationType.DEFAULT
    var addingToUserEmail = ""
    var toAddUserEmail = ""
    var message = ""
    var profilePictureUri = ""

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
     * Clears the notification as an addressing of the coins given
     */
    private fun addressCoinsGiven() {

    }

    /**
     * Start the coin transfer that fulfills the request by the Little
     */
    private fun acceptRequest() {

    }

    /**
     * Queries the database to retrieve the bigs and littles list of both users to complete the request
     */
    private fun executeAddAsBig() {
        Firestore.read(toAddUserEmail).get().addOnCompleteListener {
            toAddUserTask ->
                Firestore.read(addingToUserEmail).get().addOnCompleteListener {
                    addingToUserTask -> addAsBig(toAddUserTask, addingToUserTask)
                }
        }
    }

    /**
     * Adds the current user as a big to the requester and updates the firestore
     */
    private fun addAsBig(toAddUserTask: Task<DocumentSnapshot>, addingToUserTask: Task<DocumentSnapshot>) {
        val toAddUser = toAddUserTask.result!!.toObject(User::class.javaObjectType)!!
        val addingToUser = addingToUserTask.result!!.toObject(User::class.javaObjectType)!!

        Firestore.getLittles(toAddUserEmail).document(addingToUser.email!!).get().addOnCompleteListener {
            if (!it.result!!.exists()) {
                toAddUser.numOfLittles += 1
                Firestore.update(toAddUser, "numOfLittles", toAddUser.numOfLittles.toString())
                Firestore.addLittle(toAddUser.email!!, addingToUser.email!!)

                addingToUser.numOfBigs += 1
                Firestore.update(addingToUser, "numOfBigs", addingToUser.numOfBigs.toString())
                Firestore.addBig(addingToUser.email!!, toAddUser.email!!)
            }
        }
    }

    /**
     * Queries the database to retrieve the bigs and littles list of both users to complete the request
     */
    private fun executeAddAsLittle() {
        Firestore.read(toAddUserEmail).get().addOnCompleteListener {
                toAddUserTask ->
            Firestore.read(addingToUserEmail).get().addOnCompleteListener {
                    addingToUserTask -> addAsLittle(toAddUserTask, addingToUserTask)
            }
        }
    }

    /**
     * Adds the current user as a little to the requester and updates the firestore
     */
    private fun addAsLittle(toAddUserTask: Task<DocumentSnapshot>, addingToUserTask: Task<DocumentSnapshot>) {
        val toAddUser = toAddUserTask.result!!.toObject(User::class.javaObjectType)!!
        val addingToUser = addingToUserTask.result!!.toObject(User::class.javaObjectType)!!

        Firestore.getBigs(toAddUserEmail).document(addingToUser.email!!).get().addOnCompleteListener {
            if (!it.result!!.exists()) {
                toAddUser.numOfBigs += 1
                Firestore.update(toAddUser, "numOfBigs", toAddUser.numOfBigs.toString())
                Firestore.addBig(toAddUser.email!!, addingToUser.email!!)

                addingToUser.numOfLittles += 1
                Firestore.update(addingToUser, "numOfLittles", addingToUser.numOfLittles.toString())
                Firestore.addLittle(addingToUser.email!!, toAddUser.email!!)
            }
        }
    }
}