package com.eightnineapps.coinly.models

import com.eightnineapps.coinly.classes.User
import com.eightnineapps.coinly.interfaces.Repository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Model that provides access to the Firebase Firestore
 */
class Firestore : Repository<User, Void, Task<Void>> {

    private val database = FirebaseFirestore.getInstance()

    override fun insert(data: User, path: String): Task<Void> {
        return database.collection("users").document(data.email.toString()).set(data)
    }

    override fun update(user: User): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun read(user: User): Task<Void> {
        TODO("Not yet implemented")
    }
}