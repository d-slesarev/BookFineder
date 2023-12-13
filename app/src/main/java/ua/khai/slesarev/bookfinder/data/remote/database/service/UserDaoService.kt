package ua.khai.slesarev.bookfinder.data.remote.database.service

import ua.khai.slesarev.bookfinder.data.model.UserRemote
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response

interface UserDaoService {

    suspend fun saveUser(user: UserRemote): Event
    suspend fun loadUserByID(uid: String): Response<UserRemote>
    suspend fun updateUser(user: UserRemote): Event
    suspend fun deleteUser(user: UserRemote): Event



}