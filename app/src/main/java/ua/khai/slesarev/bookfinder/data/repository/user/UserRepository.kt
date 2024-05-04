package ua.khai.slesarev.bookfinder.data.repository.user

import ua.khai.slesarev.bookfinder.data.local.database.dao.UserDao
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.data.model.UserRemote
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response

interface UserRepository {
    suspend fun addUser(user: User): Result<Unit>
    suspend fun deleteAllUsers(): Result<Unit>
    suspend fun getAllUsers(): Result<List<User>>

    suspend fun loadUserFromAPI(accessToken: String): Result<Unit>

}