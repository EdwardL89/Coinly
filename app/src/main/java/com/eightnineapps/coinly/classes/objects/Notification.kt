package com.eightnineapps.coinly.classes.objects

import com.eightnineapps.coinly.views.activities.startup.HomeActivity.Companion.database
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
            else -> return
        }
    }

    /**
     * Queries the database to retrieve the bigs and littles list of both users to complete the request
     */
    private fun executeAddAsBig() {
        database.collection("users").document(toAddUserEmail).get().addOnCompleteListener {
            toAddUserTask ->
                database.collection("users").document(addingToUserEmail).get().addOnCompleteListener {
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
                database.collection("users").document(toAddUser.email!!).update("numOfLittles", toAddUser.numOfLittles)
                    database.collection("users").document(toAddUser.email!!).collection("Littles").document(addingToUser.email!!).set(mapOf("email" to addingToUser.email!!))

                addingToUser.numOfBigs += 1
                database.collection("users").document(addingToUser.email!!).update("numOfBigs", addingToUser.numOfBigs)
                database.collection("users").document(addingToUser.email!!).collection("Bigs").document(toAddUser.email!!).set(mapOf("email" to toAddUser.email!!))
            }
        }
    }

    /**
     * Queries the database to retrieve the bigs and littles list of both users to complete the request
     */
    private fun executeAddAsLittle() {
        database.collection("users").document(toAddUserEmail).get().addOnCompleteListener {
                toAddUserTask ->
            database.collection("users").document(addingToUserEmail).get().addOnCompleteListener {
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
                database.collection("users").document(toAddUser.email!!).update("numOfBigs", toAddUser.numOfBigs)
                database.collection("users").document(toAddUser.email!!).collection("Bigs").document(addingToUser.email!!).set(addingToUser)

                addingToUser.numOfLittles += 1
                database.collection("users").document(addingToUser.email!!).update("numOfLittles", addingToUser.numOfLittles)
                database.collection("users").document(addingToUser.email!!).collection("Littles").document(toAddUser.email!!).set(toAddUser)
            }
        }
    }
}