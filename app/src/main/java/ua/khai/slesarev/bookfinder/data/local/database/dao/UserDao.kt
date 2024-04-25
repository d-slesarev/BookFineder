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
    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>
    @Query("DELETE FROM users")
    fun deleteAllUsers(): Int


}