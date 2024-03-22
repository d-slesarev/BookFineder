package ua.khai.slesarev.bookfinder.data.repository.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.khai.slesarev.bookfinder.data.local.database.BookFinderDatabase
import ua.khai.slesarev.bookfinder.data.local.database.dao.UserDao
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.AuthService
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.impl.FirebaseAuthService
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepository
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepositoryImpl
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import ua.khai.slesarev.bookfinder.data.util.getDefaultProfileImage

class AuthRepositoryImpl(private val context: Context) : AuthRepository {

    private val auth: AuthService = FirebaseAuthService(context)
    private val userRepo: UserRepository = UserRepositoryImpl(context)
    private var localDatabase: BookFinderDatabase = BookFinderDatabase.getInstance(context)
    private var localDao: UserDao = localDatabase.userDao()

    override suspend fun signUpWithEmailPassword(email: String,password: String,username: String): Response<Event> {
        lateinit var respSignUp: Event
        lateinit var respAddUser: Response<Event>
        lateinit var respSendEmail: Event
        lateinit var rollBackReg: Event
        lateinit var rollBackAdd: Response<Event>

        var uri = getDefaultProfileImage(context)

        val chackValue = signUpEmptyCheck(email, password, username)

        if (chackValue == Event.SUCCESS) {
            try {
                Log.i(MY_TAG, "Before: FirebaseAuth.signUp()")
                respSignUp = auth.signUpWithEmailPassword(email, password, username)

                if (respSignUp == Event.SUCCESS) {
                    withContext(Dispatchers.IO) {
                        Log.i(MY_TAG, "Before: UserRepo.addUser()")
                        respAddUser = userRepo.addUser(User(username = username, email = email, imageUri = uri.toString()))
                    }

                    if (respAddUser is Response.Success) {

                        Log.i(MY_TAG, "Before: FirebaseAuth.sendEmailVer()")
                        respSendEmail = auth.sendEmailVerification()

                        if (respSendEmail == Event.SUCCESS) {
                            return Response.Success(Event.SUCCESS)
                        } else {

                            withContext(Dispatchers.IO) {
                                Log.i(MY_TAG, "Before: UserRepo.deleteUser()")
                                rollBackAdd = userRepo.deleteUser(User(username = username, email = email, imageUri = uri.toString()))
                            }

                            if (respAddUser is Response.Success) {

                                Log.d(MY_TAG, "Before: FirebaseAuth.rollBackReg()")
                                rollBackReg = auth.rollBackRegister()

                                if (rollBackReg == Event.SUCCESS) {
                                    return Response.Error(respSendEmail.toString())
                                } else {
                                    return Response.Error(rollBackReg.toString())
                                }

                            } else {
                                return Response.Error(rollBackAdd.toString())
                            }
                        }
                    } else {

                        rollBackReg = auth.rollBackRegister()

                        if (rollBackReg == Event.SUCCESS) {
                            return Response.Error(respAddUser.toString())
                        } else {
                            return Response.Error(rollBackReg.toString())
                        }
                    }
                } else {
                    return Response.Error(respSignUp.toString())
                }

            } catch (e: Exception) {
                Log.i(MY_TAG, "AuthRepo.signUp-Exception: ${e.message}")
                throw Exception("AuthRepo.signUp-Exception: ${e.message}")
            }
        } else {
            return Response.Error(chackValue.toString())
        }
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): Response<Event> {
        lateinit var respSignIn: Event
        lateinit var response:Response<User>

        val chackValue = signInEmptyCheck(email, password)

        if (chackValue == Event.SUCCESS) {
            try {
                Log.i(MY_TAG, "Before: FirebaseAuth.signIn()")
                respSignIn = auth.signInWithEmailPassword(email, password)

                if (respSignIn == Event.SUCCESS) {
                    try {
                        withContext(Dispatchers.IO) {
                            response = userRepo.getUserByUID()
                        }

                        if (response is Response.Success) {
                            return Response.Success(respSignIn)
                        } else {
                            return Response.Error((response as Response.Error).errorMessage)
                        }
                    } catch (e: Exception) {
                        throw Exception("\"AuthRepo.signInWith-Exception: ${e.message}\"")
                    }
                } else {
                    return Response.Error(respSignIn.toString())
                }
            } catch (e: Exception) {
                throw Exception("\"AuthRepo.signInWith-Exception: ${e.message}\"")
            }
        } else {
            return Response.Error(chackValue.toString())
        }
    }

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
        account: GoogleSignInAccount,
        token: String
    ): Response<Event> {

        lateinit var respSignUp: Response<Event>
        lateinit var userState: Response<Event>
        lateinit var rollBackReg: Event

        if (account != null){
            var username = account.displayName
            var email = account.email
            var uri = account.photoUrl

            try {
                Log.i(MY_TAG, "Before: AuthRepository.signInWithGoogle(token)")
                respSignUp = auth.signInWithGoogle(token)

                if (respSignUp is Response.Success) {
                    if (respSignUp.data == Event.NEW_USER){
                        withContext(Dispatchers.IO) {
                            Log.i(MY_TAG, "Before: UserRepo.addUser()")
                            Log.i(MY_TAG, "URI: ${uri.toString()}")
                            userState = userRepo.addUser(User(username = username!!, email = email!!, imageUri = uri.toString()))
                        }
                    } else if(respSignUp.data == Event.OLD_USER){
                        withContext(Dispatchers.IO) {
                            Log.i(MY_TAG, "Before: UserRepo.getUserByUID()")
                            val response = userRepo.getUserByUID()
                            if (response is Response.Success) {
                                Log.i(MY_TAG, "URI: ${uri.toString()}")
                                val user = response.data
                                localDao.updateUser(id = user.uid, email = user.email, name = user.username, imageUri = uri.toString())
                                userState = Response.Success(Event.SUCCESS)
                            } else if(response is Response.Error) {
                                userState = Response.Error(response.errorMessage)
                            }
                        }
                    }

                    if (userState is Response.Success) {
                        return Response.Success(Event.GOOGLE_SUCCESS)
                    } else {

                        rollBackReg = auth.rollBackRegister()

                        if (rollBackReg == Event.SUCCESS) {
                            return Response.Error(userState.toString())
                        } else {
                            return Response.Error(rollBackReg.toString())
                        }
                    }
                } else {
                    return Response.Error(respSignUp.toString())
                }

            } catch (e: Exception) {
                Log.i(MY_TAG, "AuthRepo.signInWithGoogle-Exception: ${e.message}")
                throw Exception("AuthRepo.signInWithGoogle-Exception: ${e.message}")
            }
        } else {
            return Response.Error(Event.ERROR_UNKNOWN.toString())
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