package com.example.datossinmvvm

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    suspend fun getAll(): List<User>

    @Insert
    suspend fun insert(user: User)

    // --- Ejercicio 1 ---

    /** Último registro insertado: el de mayor uid, que es AUTOINCREMENT. */
    @Query("SELECT * FROM User ORDER BY uid DESC LIMIT 1")
    suspend fun getLast(): User?

    @Delete
    suspend fun delete(user: User)
}
