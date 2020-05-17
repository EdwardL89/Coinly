package com.eightnineapps.coinly.classes

import com.eightnineapps.coinly.views.activities.HomeActivity.Companion.database
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

class FirestoreHelper()
{
    fun getUser(user: String): Task<DocumentSnapshot> = database.collection("users").document(user).get()
}