package ua.khai.slesarev.bookfinder.data.repository.user

import ua.khai.slesarev.bookfinder.data.model.User

interface UserRepository {

    suspend fun addUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
    suspend fun getUserByID(id:Int): User
}