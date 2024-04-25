package ua.khai.slesarev.bookfinder.data.repository.user

import android.content.Context
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.khai.slesarev.bookfinder.data.local.database.BookFinderDatabase
import ua.khai.slesarev.bookfinder.data.local.database.dao.UserDao
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.data.remote.api.user_profile.GooglePeopleAPI
import ua.khai.slesarev.bookfinder.data.remote.api.user_profile.RetrofitClientGP
import ua.khai.slesarev.bookfinder.data.repository.authentication.appoauth.TokenStorage
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl(private val context: Context): UserRepository {

    private var userProfileAPI: GooglePeopleAPI = RetrofitClientGP.instance
    private var localDatabase: BookFinderDatabase = BookFinderDatabase.getInstance(context)
    private var localDao: UserDao = localDatabase.userDao()
    override suspend fun addUser(user: User): Result<Unit> {
        return try {
            localDao.insertUser(user)
            Log.d(MY_TAG, "UserRepositoryImpl.addUsers: SUCCESS!")
            Result.success(Unit)
        } catch (e:Exception){
            Log.d(MY_TAG, "UserRepositoryImpl.addUsers: FAILURE!")
            Result.failure(e)
        }
    }
    override suspend fun deleteAllUsers(user: User): Result<Unit> {
        return try {
            localDao.deleteAllUsers()
            Log.d(MY_TAG, "UserRepositoryImpl.deleteAllUsers: SUCCESS!")
            Result.success(Unit)
        } catch (e:Exception){
            Log.d(MY_TAG, "UserRepositoryImpl.deleteAllUsers: FAILURE!")
            Result.failure(e)
        }
    }
    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val users = localDao.getAllUsers()
            Log.d(MY_TAG, "UserRepositoryImpl.getAllUsers: SUCCESS!")
            Result.success(users)
        } catch (e:Exception){
            Log.d(MY_TAG, "UserRepositoryImpl.getAllUsers: FAILURE!")
            Result.failure(e)
        }
    }
    override suspend fun loadUserFromAPI(accessToken: String): Result<Unit> {
        return suspendCoroutine{ continuation ->
            userProfileAPI.getUserInfo("Bearer $accessToken").enqueue(object : Callback<User> {

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        Log.d(MY_TAG, "UserRepositoryImpl.loadUserFromAPI: SUCCESS!")
                        response.body()?.let { user ->
                            try {
                                localDao.insertUser(user)
                                Log.d(MY_TAG, "UserRepositoryImpl.loadUserFromAPI.insertUsers: SUCCESS!")
                                continuation.resume(Result.success(Unit))
                            } catch (e:Exception){
                                Log.d(MY_TAG, "UserRepositoryImpl.loadUserFromAPI.insertUsers(insertUser): FAILURE!")
                                continuation.resume( Result.failure(e))
                            }
                        } ?: continuation.resumeWith(Result.failure(Exception("User is null")))

                    } else {
                        Log.d(MY_TAG, "UserRepositoryImpl.loadUserFromAPI(response.isSuccessful): FAILURE!")
                        Log.d(MY_TAG, "UserRepositoryImpl.loadUserFromAPI-Exception: ${response.errorBody()?.string()}}")
                        continuation.resumeWith(Result.failure(Exception(response.errorBody()?.string())))
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d(MY_TAG, "UserRepositoryImpl.loadUserFromAPI(fun onFailure): FAILURE!")
                    continuation.resumeWith(Result.failure(t))
                }
            })

        }

    }
}