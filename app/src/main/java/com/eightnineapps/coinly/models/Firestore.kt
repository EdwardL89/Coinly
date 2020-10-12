package com.eightnineapps.coinly.models

import com.eightnineapps.coinly.classes.objects.Notification
import com.eightnineapps.coinly.classes.objects.Prize
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
        if (field == "numOfBigs" || field == "numOfLittles" || field == "coins" || field == "numOfPrizesGiven" || field == "numOfPrizesClaimed" || field == "avgPriceOfPrizesGiven" || field == "avgPriceOfPrizesClaimed")
            return database.collection("users").document(user.email!!).update(field, Integer.parseInt(value))
        return database.collection("users").document(user.email!!).update(field, value)
    }

    override fun read(userEmail: String): DocumentReference {
        return database.collection("users").document(userEmail)
    }

    fun addNotification(userEmail: String, notification: Notification): Task<Void> {
        return database.collection("users").document(userEmail).collection("notifications").document(notification.id).set(notification)
    }

    fun removeNotification(userEmail: String, notification: Notification) {
        database.collection("users").document(userEmail).collection("notifications").document(notification.id).delete()
    }

    fun getNotifications(userEmail: String): CollectionReference {
        return database.collection("users").document(userEmail).collection("notifications")
    }

    fun getBigs(littleEmail: String): CollectionReference {
        return database.collection("users").document(littleEmail).collection("Bigs")
    }

    fun getLittles(bigsEmail: String): CollectionReference {
        return database.collection("users").document(bigsEmail).collection("Littles")
    }

    fun addBig(littleEmail: String, bigEmail: String, profilePictureUri: String, displayName: String) {
        database.collection("users").document(littleEmail).collection("Bigs").document(bigEmail).set(mapOf("email" to bigEmail, "profilePictureUri" to profilePictureUri, "displayName" to displayName))
    }

    fun addLittle(bigEmail: String, littleEmail: String, profilePictureUri: String, displayName: String) {
        database.collection("users").document(bigEmail).collection("Littles").document(littleEmail).set(mapOf("email" to littleEmail, "profilePictureUri" to profilePictureUri, "displayName" to displayName))
    }

    fun removeBig(littleEmail: String, bigEmail: String) {
        val prizesToDeleteFromStorage = mutableListOf<String>()
        database.collection("users").document(littleEmail).collection("Bigs").document(bigEmail).collection("Prizes").get().addOnSuccessListener {
            for (document in it) { //Max of 10 prizes can be set
                prizesToDeleteFromStorage.add(document["id"].toString())
                document.reference.delete()
            }
            database.collection("users").document(littleEmail).collection("Bigs").document(bigEmail).delete()
            ImgStorage.deleteSetPrizes(prizesToDeleteFromStorage, "set_prizes/$bigEmail/$littleEmail/")
        }
    }

    fun removeLittle(bigEmail: String, littleEmail: String) {
        database.collection("users").document(bigEmail).collection("Littles").document(littleEmail).delete()
    }

    fun getPrizesSet(littleEmail: String, bigEmail: String): CollectionReference {
        return database.collection("users").document(littleEmail).collection("Bigs").document(bigEmail).collection("Prizes")
    }

    fun getPrizesClaimed(littleEmail: String, bigEmail: String): CollectionReference {
        return database.collection("users").document(littleEmail).collection("BigsYouClaimedFrom").document(bigEmail).collection("Prizes")
    }

    fun claimNewPrize(littleEmail: String, bigEmail: String, prize: Prize): Task<Void> {
        return getPrizesClaimed(littleEmail, bigEmail).document(prize.id).set(prize)
    }

    fun setNewPrize(littleEmail: String, bigEmail: String, prize: Prize): Task<Void> {
        return getPrizesSet(littleEmail, bigEmail).document(prize.id).set(prize)
    }

    fun deletePrize(littleEmail: String, bigEmail: String, prizeId: String): Task<Void> {
        return getPrizesSet(littleEmail, bigEmail).document(prizeId).delete()
    }

    fun getInstance() = database

}