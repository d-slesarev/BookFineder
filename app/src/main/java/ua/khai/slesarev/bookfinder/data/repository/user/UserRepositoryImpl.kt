package ua.khai.slesarev.bookfinder.data.repository.user

import android.content.Context
import ua.khai.slesarev.bookfinder.data.local.database.AppDatabase
import ua.khai.slesarev.bookfinder.data.local.database.dao.UserDao
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.data.remote.database.dao.UserDaoServiceImpl
import ua.khai.slesarev.bookfinder.data.remote.database.service.UserDaoService

class UserRepositoryImpl(context: Context): UserRepository {

    private var remoteDao: UserDaoService = UserDaoServiceImpl()
    private var localDatabase: AppDatabase = AppDatabase.getInstance(context)
    private var localDao:UserDao = localDatabase.userDao()

    override suspend fun addUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserByID(id: Int): User {
        TODO("Not yet implemented")
    }
}