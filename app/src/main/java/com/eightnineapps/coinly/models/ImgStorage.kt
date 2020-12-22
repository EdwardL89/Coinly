package com.eightnineapps.coinly.models

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata

/**
 * Model that provides access to the Firebase Storage
 */
object ImgStorage {

    private val imageStorage = Firebase.storage

    fun insert(data: ByteArray, path: String): UploadTask {
        return imageStorage.reference.child(path).putBytes(data, storageMetadata { contentType = "image/jpeg" })
    }

    fun read(userEmail: String): Task<Uri> {
        return imageStorage.reference.child("profile_pictures").child(userEmail).downloadUrl
    }

    fun delete(path: String): Task<Void> {
        return imageStorage.reference.child(path).delete()
    }

    fun deleteSetPrizes(prizeIds: MutableList<String>, path: String) {
        for (ids in prizeIds) delete(path + ids)
    }

    fun readImage(path: String): Task<Uri> {
        return imageStorage.reference.child(path).downloadUrl
    }

    fun getReference(path: String): StorageReference {
        return imageStorage.reference.child(path)
    }
}