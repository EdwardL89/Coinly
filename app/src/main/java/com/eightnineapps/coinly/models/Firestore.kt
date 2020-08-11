package com.eightnineapps.coinly.models

import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.interfaces.Repository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Model that provides access to the Firebase Firestore
 */
object Firestore : Repository<User, Void, DocumentReference, Task<Void>> {

    private val database = FirebaseFirestore.getInstance()

    override fun insert(data: User, path: String): Task<Void> {
        return database.collection("users").document(data.email.toString()).set(data)
    }

    override fun update(user: User, field: String, value: String): Task<Void> {
        if (field == "numOfLittles" || field == "numOfLittles" || field == "coins")
            return database.collection("users").document(user.email!!).update(field, Integer.parseInt(value))
        return database.collection("users").document(user.email!!).update(field, value)
    }

    override fun read(user: User): DocumentReference {
        return database.collection("users").document(user.email!!)
    }

    fun read(email: String): DocumentReference {
        return database.collection("users").document(email)
    }

    fun updateNotifications(user: User): Task<Void> {
        return database.collection("users").document(user.email!!).update("notifications", user.notifications)
    }

    fun getBigs(littleEmail: String): CollectionReference {
        return database.collection("users").document(littleEmail).collection("Bigs")
    }

    fun getLittles(bigsEmail: String): CollectionReference {
        return database.collection("users").document(bigsEmail).collection("Littles")
    }

    fun addBig(littleEmail: String, bigEmail: String) {
        database.collection("users").document(littleEmail).collection("Bigs").document(bigEmail).set(mapOf("email" to bigEmail))
    }

    fun addLittle(bigEmail: String, littleEmail: String) {
        database.collection("users").document(bigEmail).collection("Littles").document(littleEmail).set(mapOf("email" to littleEmail))
    }

    fun removeBig(littleEmail: String, bigEmail: String) {
        database.collection("users").document(littleEmail).collection("Bigs").document(bigEmail).delete()
    }

    fun removeLittle(bigEmail: String, littleEmail: String) {
        database.collection("users").document(bigEmail).collection("Littles").document(littleEmail).delete()
    }

    fun getInstance() = database

}