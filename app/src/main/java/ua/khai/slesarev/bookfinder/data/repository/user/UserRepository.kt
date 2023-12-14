package ua.khai.slesarev.bookfinder.data.repository.user

import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.data.model.UserRemote
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response

interface UserRepository {

    suspend fun addUser(user: User): Response<Event>
    suspend fun updateUser(user: User): Response<Event>
    suspend fun deleteUser(user: User): Response<Event>
    suspend fun getUserByUID(): Response<User>

}