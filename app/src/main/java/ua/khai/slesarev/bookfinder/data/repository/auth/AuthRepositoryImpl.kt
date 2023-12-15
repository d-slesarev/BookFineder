package ua.khai.slesarev.bookfinder.data.repository.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.AuthService
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.impl.FirebaseAuthService
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepository
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepositoryImpl
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.data.util.MY_TAG

class AuthRepositoryImpl(context: Context) : AuthRepository {

    private val auth: AuthService = FirebaseAuthService()
    private val userRepo: UserRepository = UserRepositoryImpl(context)

    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String,
        username: String
    ): Response<Event> {
        lateinit var respSignUp:  Event
        lateinit var respAddUser: Response<Event>
        lateinit var respSendEmail:  Event
        lateinit var rollBackReg:  Event
        lateinit var rollBackAdd:  Response<Event>

        val chackValue = signUpEmptyCheck(email, password, username)

        if (chackValue == Event.SUCCESS){
            try {
                Log.i(MY_TAG, "Before: FirebaseAuth.signUp()")
                respSignUp = auth.signUpWithEmailPassword(email, password, username)

                if (respSignUp == Event.SUCCESS) {
                    withContext(Dispatchers.IO){
                        Log.i(MY_TAG, "Before: UserRepo.addUser()")
                        respAddUser = userRepo.addUser(User(username = username, email = email))
                    }

                    if (respAddUser is Response.Success) {

                        Log.i(MY_TAG, "Before: FirebaseAuth.sendEmailVer()")
                        respSendEmail = auth.sendEmailVerification()

                        if (respSendEmail == Event.SUCCESS) {
                            return Response.Success(Event.SUCCESS)
                        } else {

                            withContext(Dispatchers.IO){
                                Log.i(MY_TAG, "Before: UserRepo.deleteUser()")
                                rollBackAdd = userRepo.deleteUser(User(username = username, email = email))
                            }

                            if (respAddUser is Response.Success){

                                Log.d(MY_TAG, "Before: FirebaseAuth.rollBackReg()")
                                rollBackReg = auth.rollBackRegister()

                                if (rollBackReg == Event.SUCCESS){
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

                        if (rollBackReg == Event.SUCCESS){
                            return Response.Error(respAddUser.toString())
                        } else {
                            return Response.Error(rollBackReg.toString())
                        }
                    }
                } else {
                    return Response.Error(respSignUp.toString())
                }

            }
            catch (e: Exception) {
                Log.i(MY_TAG, "AuthRepo.signUp-Exception: ${e.message}")
                throw Exception("AuthRepo.signUp-Exception: ${e.message}")
            }
        }
        else {
            return Response.Error(chackValue.toString())
        }
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): Response<Event> {
        lateinit var respSignIn: Event

        val chackValue = signInEmptyCheck(email, password)

        if (chackValue == Event.SUCCESS){

            try {
                Log.i(MY_TAG, "Before: FirebaseAuth.signIn()")
                respSignIn = auth.signInWithEmailPassword(email, password)

                if (respSignIn == Event.SUCCESS){
                    return Response.Success(respSignIn)
                } else {
                    return Response.Success(respSignIn)
                }


            } catch (e: Exception){
                throw Exception("\"AuthRepo.signIn-Exception: ${e.message}\"")
            }

        } else {
            return Response.Error(chackValue.toString())
        }
    }

    override fun signOut(): Response<Event> {
        val result = auth.signOut()

        if (result == Event.SUCCESS){
            return Response.Success(result)
        } else {
            return Response.Error(result.toString())
        }
    }

    override suspend fun resetPassword(email: String): Response<Event> {
        val result:Event

        if (email.isNotEmpty()){
            result = auth.resetPassword(email)

            if (result == Event.SUCCESS){
                return Response.Success(result)
            } else {
                return Response.Error(result.toString())
            }
        } else {
            return Response.Error(Event.ERROR_MISSING_EMAIL.toString())
        }
    }

    override suspend fun signInWithGoogle(): Response<Event> {
        TODO("Not yet implemented")
    }

    override fun getGoogleSignInIntent(activity: Activity): Intent {
 /*       val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.def))*/
        return Intent()
    }

    private fun signUpEmptyCheck(email: String, password: String, username: String): Event {

        if (email.isEmpty() && password.isEmpty() && username.isEmpty()) {
            return Event.ERROR_MISSING_EMAIL_AND_PASSWORD_AND_NAME
        } else {
            if (email.isEmpty() && password.isEmpty()) {
                return Event.ERROR_MISSING_EMAIL_AND_PASSWORD
            }
            else {
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
        }
        else {
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