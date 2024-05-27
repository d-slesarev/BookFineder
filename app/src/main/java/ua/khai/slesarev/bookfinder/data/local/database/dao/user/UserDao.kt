package ua.khai.slesarev.bookfinder.data.local.database.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ua.khai.slesarev.bookfinder.data.models.local.User

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User): Long
    @Query("SELECT * FROM USERS")
    fun getAllUsers(): List<User>
    @Query("DELETE FROM USERS")
    fun deleteAllUsers(): Int
}