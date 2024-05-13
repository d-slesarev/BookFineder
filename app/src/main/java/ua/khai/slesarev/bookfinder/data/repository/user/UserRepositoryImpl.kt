package ua.khai.slesarev.bookfinder.data.repository.user

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.khai.slesarev.bookfinder.data.local.database.BookFinderDatabase
import ua.khai.slesarev.bookfinder.data.local.database.dao.UserDao
import ua.khai.slesarev.bookfinder.data.model.Person
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.data.remote.api.user_profile.GooglePeopleAPI
import ua.khai.slesarev.bookfinder.data.remote.api.user_profile.RetrofitClientGP
import ua.khai.slesarev.bookfinder.data.repository.authentication.appoauth.TokenStorage
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl(private val context: Context): UserRepository {

    private val userProfileAPI: GooglePeopleAPI = RetrofitClientGP.instance
    private val localDatabase: BookFinderDatabase = BookFinderDatabase.getInstance(context)
    private val localDao: UserDao = localDatabase.userDao()
    override suspend fun addUser(user: User): Result<Unit> {
        return try {
            localDao.insertUser(user)
            Log.d(MY_TAG, "UserRepositoryImpl.addUsers: SUCCESS!")
            Result.success(Unit)
        } catch (e:Exception){
            Log.d(MY_TAG, "UserRepositoryImpl.addUsers: FAILURE!\nMassage: ${e.message}")
            Result.failure(e)
        }
    }
    override suspend fun deleteAllUsers(): Result<Unit> {
        return try {
            localDao.deleteAllUsers()
            Log.d(MY_TAG, "UserRepositoryImpl.deleteAllUsers: SUCCESS!")
            Result.success(Unit)
        } catch (e:Exception){
            Log.d(MY_TAG, "UserRepositoryImpl.deleteAllUsers: FAILURE!\nMassage: ${e.message}")
            Result.failure(e)
        }
    }
    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val users = localDao.getAllUsers()
            Log.d(MY_TAG, "UserRepositoryImpl.getAllUsers: SUCCESS!")
            Result.success(users)
        } catch (e:Exception){
            Log.e(MY_TAG, "UserRepositoryImpl.getAllUsers: FAILURE!\nMassage: ${e.message}")
            Result.failure(e)
        }
    }
    private suspend fun loadUser (accessToken: String): Result<Person> {
        return suspendCoroutine { continuation ->
            userProfileAPI.getUserInfo("Bearer $accessToken").enqueue(object : Callback<Person> {

                override fun onResponse(call: Call<Person>, response: Response<Person>) {
                    if (response.isSuccessful) {
                        Log.d(MY_TAG, "UserRepositoryImpl.loadUserFromAPI: SUCCESS!\nMessage: ${response.message()}")
                        response.body()?.let { user ->
                            continuation.resume(Result.success(user))
                        } ?: continuation.resumeWith(Result.failure(Exception("Person is null")))

                    } else {
                        Log.d(MY_TAG, "UserRepositoryImpl.loadUserFromAPI(response.isSuccessful): FAILURE!")
                        Log.d(MY_TAG, "UserRepositoryImpl.loadUserFromAPI-Exception: ${response.errorBody()?.string()}}")
                        continuation.resumeWith(Result.failure(Exception(response.errorBody()?.string())))
                    }
                }

                override fun onFailure(call: Call<Person>, t: Throwable) {
                    Log.d(MY_TAG, "UserRepositoryImpl.loadUserFromAPI(fun onFailure): FAILURE!")
                    continuation.resumeWith(Result.failure(t))
                }
            })
        }
    }
    override suspend fun loadUserFromAPI(accessToken: String): Result<Unit> {
        try {
            val person: Person? = loadUser(accessToken).getOrNull()
            return withContext(Dispatchers.IO) {
                try {
                    if (person != null) {
                        Log.d(MY_TAG, "Person: $person")
                        val user = User(
                            username = person.names.first().displayName,
                            email = person.emailAddresses.first().value,
                            imageUri = person.photos.first().url
                        )
                        Log.d(MY_TAG, "User: $user")
                        localDao.insertUser(user)
                        Log.d(MY_TAG, "UserRepositoryImpl.loadUserFromAPI.insertUsers: SUCCESS!")
                        Result.success(Unit)
                    } else {
                        Log.d(MY_TAG, "UserRepositoryImpl.loadUserFromAPI.insertUsers: Person is null")
                        Result.failure(Exception("Person is null"))
                    }
                } catch (e:Exception){
                    Log.d(MY_TAG, "UserRepositoryImpl.loadUserFromAPI.insertUsers-Exception: ${e.message}")
                    Result.failure(e)
                }
            }
        } catch (e:Exception){
            Log.d(MY_TAG, "UserRepositoryImpl.loadUserFromAPI.loadUser-Exception: ${e.message}")
            return Result.failure(e)
        }
    }
}