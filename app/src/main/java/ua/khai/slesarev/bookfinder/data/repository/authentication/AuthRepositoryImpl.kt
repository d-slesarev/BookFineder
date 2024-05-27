package ua.khai.slesarev.bookfinder.data.repository.authentication

import android.content.Context
import android.content.Intent
import android.util.Log
import ua.khai.slesarev.bookfinder.data.local.database.BookFinderDatabase
import ua.khai.slesarev.bookfinder.data.local.database.dao.user.UserDao
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.googlesingin.FirebaseAuthService
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.googlesingin.impl.FirebaseAuthServiceImpl
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepository
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepositoryImpl
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.data.util.MY_TAG

class AuthRepositoryImpl(private val context: Context) : AuthRepository {

    private val auth: FirebaseAuthService = FirebaseAuthServiceImpl(context)
    private val userRepo: UserRepository = UserRepositoryImpl(context)
    private var localDatabase: BookFinderDatabase = BookFinderDatabase.getInstance()
    private var localDao: UserDao = localDatabase.userDao()

    override fun signOut(): Response<Event> {
        val result = auth.signOut()

        if (result == Event.SUCCESS) {
            return Response.Success(result)
        } else {
            return Response.Error(result.toString())
        }
    }

    override fun signOutGoogle(): Response<Event> {
        val result = auth.signOutGoogle()

        if (result == Event.SUCCESS) {
            return Response.Success(result)
        } else {
            return Response.Error(result.toString())
        }
    }

    override suspend fun resetPassword(email: String): Response<Event> {
        val result: Event

        if (email.isNotEmpty()) {
            result = auth.resetPassword(email)

            if (result == Event.SUCCESS) {
                return Response.Success(result)
            } else {
                return Response.Error(result.toString())
            }
        } else {
            return Response.Error(Event.ERROR_MISSING_EMAIL.toString())
        }
    }

    override suspend fun signInWithGoogle(
        token: String
    ) {
        try {
            Log.i(MY_TAG, "Before: AuthRepository.signInWithGoogle(token)")
            auth.signInWithGoogle(token)
        } catch (e: Exception) {
            Log.i(MY_TAG, "AuthRepo.signInWithGoogle-Exception: ${e.message}")
            throw Exception("AuthRepo.signInWithGoogle-Exception: ${e.message}")
        }
    }

    override fun getGoogleSignInIntent(context:Context): Intent {
        return auth.getGoogleSignInIntent(context)
    }

    private fun signUpEmptyCheck(email: String, password: String, username: String): Event {

        if (email.isEmpty() && password.isEmpty() && username.isEmpty()) {
            return Event.ERROR_MISSING_EMAIL_AND_PASSWORD_AND_NAME
        } else {
            if (email.isEmpty() && password.isEmpty()) {
                return Event.ERROR_MISSING_EMAIL_AND_PASSWORD
            } else {
                if (email.isEmpty() && username.isEmpty()) {
                    return Event.ERROR_MISSING_EMAIL_AND_NAME
                } else {
                    if (username.isEmpty() && password.isEmpty()) {
                        return Event.ERROR_MISSING_NAME_AND_PASSWORD
                    } else {
                        return if (email.isNotEmpty()) {
                            if (password.isNotEmpty()) {
                                if (username.isNotEmpty()) {
                                    Event.SUCCESS
                                } else {
                                    Event.ERROR_MISSING_NAME
                                }
                            } else {
                                Event.ERROR_MISSING_PASSWORD
                            }
                        } else {
                            Event.ERROR_MISSING_EMAIL
                        }
                    }
                }
            }
        }
    }

    private fun signInEmptyCheck(email: String, password: String): Event {
        return if (email.isEmpty() && password.isEmpty()) {
            Event.ERROR_MISSING_EMAIL_AND_PASSWORD
        } else {
            if (email.isNotEmpty()) {
                return if (password.isNotEmpty()) {
                    Event.SUCCESS
                } else {
                    Event.ERROR_MISSING_PASSWORD
                }
            } else {
                return Event.ERROR_MISSING_EMAIL
            }
        }
    }
}