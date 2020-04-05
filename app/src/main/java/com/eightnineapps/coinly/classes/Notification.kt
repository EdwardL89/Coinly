package com.eightnineapps.coinly.classes

import com.eightnineapps.coinly.activities.HomeActivity.Companion.database
import com.eightnineapps.coinly.enums.NotificationType
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable

class Notification(): Serializable {
    var type = NotificationType.DEFAULT
    var addingToUserEmail = ""
    var toAddUserEmail = ""
    var message = ""

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
        if (!toAddUser.littles.contains(addingToUser.email!!)) {
            toAddUser.littles.add(addingToUser.email!!)
            database.collection("users").document(toAddUser.email!!).update("littles", toAddUser.littles)
            addingToUser.bigs.add(toAddUser.email!!)
            database.collection("users").document(addingToUser.email!!).update("bigs", addingToUser.bigs)
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
        if (!toAddUser.bigs.contains(addingToUser.email!!)) { // For when both users request and accept eachother
            toAddUser.bigs.add(addingToUser.email!!)
            database.collection("users").document(toAddUser.email!!).update("bigs", toAddUser.bigs)
            addingToUser.littles.add(toAddUser.email!!)
            database.collection("users").document(addingToUser.email!!).update("littles", addingToUser.littles)
        }
    }
}