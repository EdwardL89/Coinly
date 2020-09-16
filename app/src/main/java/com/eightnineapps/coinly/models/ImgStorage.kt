package com.eightnineapps.coinly.models

import android.net.Uri
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.interfaces.Repository
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata

/**
 * Model that provides access to the Firebase Storage
 */
object ImgStorage : Repository<ByteArray, Uri, Task<Uri>, UploadTask> {

    private val imageStorage = Firebase.storage

    override fun insert(data: ByteArray, path: String): UploadTask {
        return imageStorage.reference.child(path).putBytes(data, storageMetadata { contentType = "image/jpeg" })
    }

    override fun update(user: User, field: String, value: String): Task<Uri> {
        TODO("Not yet implemented")
    }

    override fun read(user: User): Task<Uri> {
        return imageStorage.reference.child("profile_pictures").child(user.id).downloadUrl
    }

    fun delete(path: String): Task<Void> {
        return imageStorage.reference.child(path).delete()
    }

    fun read(path: String): Task<Uri> {
        return imageStorage.reference.child(path).downloadUrl
    }

    fun getReference(path: String): StorageReference {
        return imageStorage.reference.child(path)
    }
}