package com.eightnineapps.coinly.models

import android.net.Uri
import com.eightnineapps.coinly.classes.User
import com.eightnineapps.coinly.interfaces.Repository
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

/**
 * Model that provides access to the Firebase Storage
 */
class ImgStorage : Repository<ByteArray, Uri, UploadTask> {

    private val imageStorage = Firebase.storage

    override fun insert(data: ByteArray, path: String): UploadTask {
        return imageStorage.reference.child("profile_pictures").child(path).putBytes(data)
    }

    override fun update(user: User): Task<Uri> {
        TODO("Not yet implemented")
    }

    override fun read(user: User): Task<Uri> {
        return imageStorage.reference.child("profile_pictures").child(user.id).downloadUrl
    }

}