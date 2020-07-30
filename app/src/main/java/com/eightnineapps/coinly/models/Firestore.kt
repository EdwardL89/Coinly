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
        return database.collection("users").document(user.email!!).update(field, value)
    }

    override fun read(user: User): DocumentReference {
        return database.collection("users").document(user.email!!)
    }

    fun updateNotifications(user: User): Task<Void> {
        return database.collection("users").document(user.email!!).update("notifications", user.notifications)
    }

    fun updateList(user: User, field: String, list: MutableList<String>): Task<Void> {
        return database.collection("users").document(user.email!!).update(field, list)
    }

    fun insertBig(littleEmail: String, big: User): Task<Void> {
        return database.collection("users").document(littleEmail).collection("Bigs").document(big.email!!).set(big)
    }

    fun insertLittle(bigEmail: String, little: User): Task<Void> {
        return database.collection("users").document(bigEmail).collection("Littles").document(little.email!!).set(little)
    }

    fun getBigs(littleEmail: String): CollectionReference {
        return database.collection("users").document(littleEmail).collection("Bigs")
    }

    fun getLittles(bigsEmail: String): CollectionReference {
        return database.collection("users").document(bigsEmail).collection("Littles")
    }

    fun removeBig(littleEmail: String, bigEmail: String) {
        database.collection("users").document(littleEmail).collection("Bigs").document(bigEmail).delete()
    }

    fun removeLittle(bigEmail: String, littleEmail: String) {
        database.collection("users").document(bigEmail).collection("Littles").document(littleEmail).delete()
    }

    fun getInstance() = database

}