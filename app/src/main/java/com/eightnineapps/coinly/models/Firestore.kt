package com.eightnineapps.coinly.models

import com.eightnineapps.coinly.classes.objects.Notification
import com.eightnineapps.coinly.classes.objects.Prize
import com.eightnineapps.coinly.classes.objects.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

/**
 * Model that provides access to the Firebase Firestore
 */
object Firestore {

    private val database = FirebaseFirestore.getInstance()
    private val numericFields = hashSetOf("numOfBigs", "numOfLittles", "coins", "numOfPrizesGiven",
        "numOfPrizesClaimed", "avgPriceOfPrizesGiven", "avgPriceOfPrizesClaimed")

    //region Users

    fun insert(data: User): Task<Void> {
        return database
            .collection("users")
            .document(data.email.toString())
            .set(data)
    }

    fun update(userEmail: String, field: String, value: String): Task<Void> {
        return database
            .collection("users")
            .document(userEmail)
            .update(field, if (numericFields.contains(field)) Integer.parseInt(value) else value)
    }

    fun read(userEmail: String): DocumentReference {
        return database
            .collection("users")
            .document(userEmail)
    }

    fun getAllUsers(): Task<QuerySnapshot> {
        return database
            .collection("users")
            .get()
    }

    //endregion Users

    //region Notifications

    fun addNotification(userEmail: String, notification: Notification): Task<Void> {
        return database
            .collection("users")
            .document(userEmail)
            .collection("notifications")
            .document(notification.id)
            .set(notification)
    }

    fun removeNotification(userEmail: String, notificationId: String) {
        database
            .collection("users")
            .document(userEmail)
            .collection("notifications")
            .document(notificationId)
            .delete()
    }

    fun getNotifications(userEmail: String): CollectionReference {
        return database
            .collection("users")
            .document(userEmail)
            .collection("notifications")
    }

    //endregion Users

    //region Bigs

    fun getBigs(littleEmail: String): CollectionReference {
        return database
            .collection("users")
            .document(littleEmail)
            .collection("Bigs")
    }

    fun addBig(littleEmail: String, bigEmail: String, profilePictureUri: String, displayName: String) {
        database
            .collection("users")
            .document(littleEmail)
            .collection("Bigs")
            .document(bigEmail)
            .set(mapOf("email" to bigEmail, "profilePictureUri" to profilePictureUri, "displayName" to displayName))
    }

    fun removeBig(littleEmail: String, bigEmail: String) {
        val prizesToDeleteFromStorage = mutableListOf<String>()
        database
            .collection("users")
            .document(littleEmail)
            .collection("Bigs")
            .document(bigEmail)
            .collection("Prizes")
            .get().addOnSuccessListener {
            for (document in it) { //Max of 10 prizes can be set
                prizesToDeleteFromStorage.add(document["id"].toString())
                document.reference.delete()
            }
            database
                .collection("users")
                .document(littleEmail)
                .collection("Bigs")
                .document(bigEmail)
                .delete()
            ImgStorage
                .deleteSetPrizes(prizesToDeleteFromStorage, "set_prizes/$bigEmail/$littleEmail/")
        }
    }

    //endregion Bigs

    //region Littles

    fun getLittles(bigsEmail: String): CollectionReference {
        return database
            .collection("users")
            .document(bigsEmail)
            .collection("Littles")
    }

    fun addLittle(bigEmail: String, littleEmail: String, profilePictureUri: String, displayName: String) {
        database
            .collection("users")
            .document(bigEmail)
            .collection("Littles")
            .document(littleEmail)
            .set(mapOf("email" to littleEmail, "profilePictureUri" to profilePictureUri, "displayName" to displayName))
    }

    fun removeLittle(bigEmail: String, littleEmail: String) {
        database
            .collection("users")
            .document(bigEmail)
            .collection("Littles")
            .document(littleEmail)
            .delete()
    }

    //endregion Littles

    //region Prizes

    fun getPrizesSet(littleEmail: String, bigEmail: String): CollectionReference {
        return database
            .collection("users")
            .document(littleEmail)
            .collection("Bigs")
            .document(bigEmail)
            .collection("Prizes")
    }

    fun getPrizesClaimed(littleEmail: String, bigEmail: String): CollectionReference {
        return database
            .collection("users")
            .document(littleEmail)
            .collection("BigsYouClaimedFrom")
            .document(bigEmail)
            .collection("Prizes")
    }

    fun claimNewPrize(littleEmail: String, bigEmail: String, prize: Prize): Task<Void> {
        return getPrizesClaimed(littleEmail, bigEmail)
            .document(prize.id)
            .set(prize)
    }

    fun setNewPrize(littleEmail: String, bigEmail: String, prize: Prize): Task<Void> {
        return getPrizesSet(littleEmail, bigEmail)
            .document(prize.id)
            .set(prize)
    }

    fun deletePrize(littleEmail: String, bigEmail: String, prizeId: String): Task<Void> {
        return getPrizesSet(littleEmail, bigEmail)
            .document(prizeId).delete()
    }

    //endregion Prizes

    fun getBatch() = database.batch()
}
