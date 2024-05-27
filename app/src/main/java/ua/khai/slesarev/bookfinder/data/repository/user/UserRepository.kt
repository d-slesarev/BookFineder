package ua.khai.slesarev.bookfinder.data.repository.user

import ua.khai.slesarev.bookfinder.data.models.local.User

interface UserRepository {
    suspend fun addUser(user: User): Result<Unit>
    suspend fun deleteAllUsers(): Result<Unit>
    suspend fun getAllUsers(): Result<List<User>>

    suspend fun loadUserFromAPI(accessToken: String): Result<Unit>

}