package ua.khai.slesarev.bookfinder.data.local.database.dao

import android.provider.ContactsContract.CommonDataKinds.Email
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
    @Query("UPDATE users SET email =:email, user_name = :name  WHERE uid = :id")
    fun updateUser(id: String, email: String, name: String): Int
    @Delete
    fun deleteUser(user: User): Int
    @Query("SELECT * FROM users WHERE uid = :id LIMIT 1")
    fun getUserByID(id:String): User
    @Query("DELETE FROM users")
    fun deleteAllUsers(): Int


}