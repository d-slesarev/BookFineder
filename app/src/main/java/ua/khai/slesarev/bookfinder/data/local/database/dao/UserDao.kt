package ua.khai.slesarev.bookfinder.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ua.khai.slesarev.bookfinder.data.model.User

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User): Long
    @Update
    fun updateUser(user: User): Int
    @Delete
    fun deleteUser(user: User): Int
    @Query("SELECT * FROM users WHERE uid = :id LIMIT 1")
    fun getUserByID(id:Int): User?

}