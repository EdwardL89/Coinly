package com.eightnineapps.coinly.interfaces

import com.eightnineapps.coinly.classes.User
import com.google.android.gms.tasks.Task

/**
 * A Model interface to perform CRUD operations using the Repository pattern
 */
interface Repository<T, S, U, out V> {

    fun insert(data: T, path: String = ""): V

    fun update(user: User, field: String, value: String): Task<S>

    fun read(user: User): U

}