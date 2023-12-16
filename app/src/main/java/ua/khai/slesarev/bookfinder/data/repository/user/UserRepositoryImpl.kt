package ua.khai.slesarev.bookfinder.data.repository.user

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import ua.khai.slesarev.bookfinder.data.local.database.BookFinderDatabase
import ua.khai.slesarev.bookfinder.data.local.database.dao.UserDao
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.data.model.UserRemote
import ua.khai.slesarev.bookfinder.data.remote.database.dao.UserDaoServiceImpl
import ua.khai.slesarev.bookfinder.data.remote.database.service.UserDaoService
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import ua.khai.slesarev.bookfinder.data.util.getDefaultProfileImage

class UserRepositoryImpl(private val context: Context): UserRepository {

    private var remoteDao: UserDaoService = UserDaoServiceImpl()
    private var localDatabase: BookFinderDatabase = BookFinderDatabase.getInstance(context)
    private var localDao:UserDao = localDatabase.userDao()
    private var auth: FirebaseAuth = Firebase.auth

    override suspend fun addUser(user: User): Response<Event> {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            user.uid = uid
        }
        val result = localDao.insertUser(user)
        if (result > 0){
            Log.d(MY_TAG, "UserRepository.addUser(Room): SUCCESS!")
            try {
                val result = remoteDao.saveUser(UserRemote(user.username, user.email))

                if (result == Event.SUCCESS){
                    Log.d(MY_TAG, "UserRepository.addUser(Firebase): SUCCESS!")
                    return Response.Success(result)
                }else {// Убираем пользователя из локальной БД, так как в Firebase удалить не вышло
                    localDao.deleteUser(user)
                    Log.d(MY_TAG, "UserRepository.addUser(Firebase): FAILURE!")
                    return Response.Error(result.toString())
                }
            } catch (e: Exception) {
                Log.d(MY_TAG, "UserRepository.addUser-Exception(Firebase): ${e.message.toString()}")
                return Response.Error(e.message.toString())
            }

        } else {
            return Response.Error(Event.FAILURE.toString())
        }
    }

    override suspend fun updateUser(user: User): Response<Event> {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val unchangedUser:User = localDao.getUserByID(uid)
            user.uid = uid
            val result = localDao.updateUser(id = user.uid, email = user.email, name = user.username, imageUri = user.imageUri)
            if (result > 0){
                try {
                    val result = remoteDao.updateUser(UserRemote(user.username, user.email))

                    if (result == Event.SUCCESS){
                        Log.d(MY_TAG, "UserRepository.updateUser: SUCCESS!")
                        return Response.Success(result)
                    }else {// Возвращаем пользователя в локальную БД, так как в Firebase обновить не вышло
                        localDao.updateUser(id = user.uid, email = unchangedUser.email, name = unchangedUser.username, imageUri = user.imageUri)
                        Log.d(MY_TAG, "UserRepository.updateUser: FAILURE!")
                        return Response.Error(result.toString())
                    }
                } catch (e: Exception) {
                    Log.d(MY_TAG, "UserRepository.updateUser-Exception: ${e.message.toString()}")
                    return Response.Error(e.message.toString())
                }

            } else {
                return Response.Error(Event.FAILURE.toString())
            }
        }
        else {
            return Response.Error(Event.FAILURE.toString())
        }
    }

    override suspend fun deleteUser(user: User): Response<Event> {
        val result = localDao.deleteUser(user)
        if (result > 0){
            try {
                val uid = auth.currentUser?.uid

                val result = remoteDao.deleteUserByID(uid!!)

                if (result == Event.SUCCESS){
                    Log.d(MY_TAG, "UserRepository.deleteUser: SUCCESS!")
                    return Response.Success(result)
                }else {// Возвращаем пользователя в локальную БД, так как в Firebase удалить не вышло
                    localDao.insertUser(user)
                    Log.d(MY_TAG, "UserRepository.deleteUser: FAILURE!")
                    return Response.Error(result.toString())
                }
            } catch (e: Exception) {
                Log.d(MY_TAG, "UserRepository.deleteUser-Exception: ${e.message.toString()}")
                return Response.Error(e.message.toString())
            }

        } else {
            Log.d(MY_TAG, "UserRepository.deleteUser: FAILURE!")
            return Response.Error(Event.FAILURE.toString())
        }
    }

    override suspend fun getUserByUID(): Response<User> {
        val uid = auth.currentUser?.uid
        var uri = getDefaultProfileImage(context)
        if (uid != null) {
            val user = localDao.getUserByID(uid)
            if (user != null){
                Log.d(MY_TAG, "UserRepository.getUserByID(Room): SUCCESS!")
                return Response.Success(user)
            } else {
                try {
                    val response:Response<UserRemote> = remoteDao.loadUserByID(uid)
                    if (response is Response.Success){

                        val remoteUser = response.data
                        val user = User(uid = uid, username = remoteUser.username, email = remoteUser.email, imageUri = uri.toString())
                        Log.d(MY_TAG, "UserRepository.getUserByID(Firebase): SUCCESS!")
                        val insert = localDao.insertUser(user)

                        if (insert > 0) {
                            Log.d(MY_TAG, "UserRepository.getUserByID(Firebase)[insertUser]: SUCCESS!")
                        }

                        return Response.Success(user)

                    } else {
                        response as Response.Error
                        Log.d(MY_TAG, "UserRepository.getUserByID(Firebase): FAILURE!")
                        return Response.Error(response.errorMessage)
                    }

                } catch (e: Exception) {
                    Log.d(MY_TAG, "UserRepository.getUserByID-Exception: ${e.message.toString()}")
                    return Response.Error(e.message.toString())
                }
            }
        } else {
            throw Exception("auth.currentUser?.uid is NULL!!!")
        }
    }

}