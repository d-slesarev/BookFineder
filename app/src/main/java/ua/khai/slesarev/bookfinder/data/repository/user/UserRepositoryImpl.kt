package ua.khai.slesarev.bookfinder.data.repository.user

import android.content.Context
import android.util.Log
import com.google.android.gms.common.internal.ResourceUtils
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.khai.slesarev.bookfinder.data.local.database.AppDatabase
import ua.khai.slesarev.bookfinder.data.local.database.dao.UserDao
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.data.model.UserRemote
import ua.khai.slesarev.bookfinder.data.remote.database.dao.UserDaoServiceImpl
import ua.khai.slesarev.bookfinder.data.remote.database.service.UserDaoService
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.data.util.TAG
import ua.khai.slesarev.bookfinder.data.util.USER_ID
import kotlin.properties.Delegates

class UserRepositoryImpl(context: Context): UserRepository {

    private var remoteDao: UserDaoService = UserDaoServiceImpl()
    private var localDatabase: AppDatabase = AppDatabase.getInstance(context)
    private var localDao:UserDao = localDatabase.userDao()
    private var auth: FirebaseAuth = Firebase.auth

    override suspend fun addUser(user: User): Response<Event> {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            user.uid = uid
        }
        val result = localDao.insertUser(user)
        if (result > 0){
            USER_ID = result.toInt()
            Log.d(TAG, "UserRepository.addUser(Room): SUCCESS!")
            try {
                val result = remoteDao.saveUser(UserRemote(user.username, user.email))

                if (result == Event.SUCCESS){
                    Log.d(TAG, "UserRepository.addUser(Firebase): SUCCESS!")
                    return Response.Success(result)
                }else {// Убираем пользователя из локальной БД, так как в Firebase удалить не вышло
                    localDao.deleteUser(user)
                    Log.d(TAG, "UserRepository.addUser(Firebase): FAILURE!")
                    return Response.Error(result.toString())
                }
            } catch (e: Exception) {
                Log.d(TAG, "UserRepository.addUser-Exception(Firebase): ${e.message.toString()}")
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
            val result = localDao.updateUser(id = user.uid, remember = user.remember, email = user.email, name = user.username)
            if (result > 0){
                try {
                    val result = remoteDao.updateUser(UserRemote(user.username, user.email))

                    if (result == Event.SUCCESS){
                        Log.d(TAG, "UserRepository.updateUser: SUCCESS!")
                        return Response.Success(result)
                    }else {// Возвращаем пользователя в локальную БД, так как в Firebase обновить не вышло
                        localDao.updateUser(id = user.uid, remember = unchangedUser.remember, email = unchangedUser.email, name = unchangedUser.username)
                        Log.d(TAG, "UserRepository.updateUser: FAILURE!")
                        return Response.Error(result.toString())
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "UserRepository.updateUser-Exception: ${e.message.toString()}")
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
                    Log.d(TAG, "UserRepository.deleteUser: SUCCESS!")
                    return Response.Success(result)
                }else {// Возвращаем пользователя в локальную БД, так как в Firebase удалить не вышло
                    localDao.insertUser(user)
                    Log.d(TAG, "UserRepository.deleteUser: FAILURE!")
                    return Response.Error(result.toString())
                }
            } catch (e: Exception) {
                Log.d(TAG, "UserRepository.deleteUser-Exception: ${e.message.toString()}")
                return Response.Error(e.message.toString())
            }

        } else {
            Log.d(TAG, "UserRepository.deleteUser: FAILURE!")
            return Response.Error(Event.FAILURE.toString())
        }
    }

    override suspend fun getUserByUID(): Response<User> {
        val uid = auth.currentUser?.uid
        val user = uid?.let { localDao.getUserByID(it) }
        if (user != null){
            Log.d(TAG, "UserRepository.getUserByID(Room): SUCCESS!")
            return Response.Success(user)
        } else {
            val uid = auth.currentUser?.uid

            try {
                val response:Response<UserRemote> = remoteDao.loadUserByID(uid!!)
                if (response is Response.Success){

                    val remoteUser = response.data
                    Log.d(TAG, "UserRepository.getUserByID(Firebase): SUCCESS!")
                    return Response.Success(User(username = remoteUser.username, remember = false, email = remoteUser.email))

                } else {
                    response as Response.Error
                    Log.d(TAG, "UserRepository.getUserByID(Firebase): FAILURE!")
                    return Response.Error(response.errorMessage)
                }

            } catch (e: Exception) {
                Log.d(TAG, "UserRepository.getUserByID-Exception: ${e.message.toString()}")
                return Response.Error(e.message.toString())
            }
        }
    }

}