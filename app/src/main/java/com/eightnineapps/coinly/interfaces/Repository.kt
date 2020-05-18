package com.eightnineapps.coinly.interfaces

import com.eightnineapps.coinly.classes.User
import com.google.android.gms.tasks.Task

/**
 * A Model interface to perform CRUD operations using the Repository pattern
 */
interface Repository<T, S, out U> {

    fun insert(data: T, path: String = ""): U

    fun update(user: User): Task<S>

    fun read(user: User): Task<S>

}