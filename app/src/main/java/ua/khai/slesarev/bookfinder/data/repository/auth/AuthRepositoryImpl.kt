package ua.khai.slesarev.bookfinder.data.repository.auth

import android.content.Context
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.AuthService
import ua.khai.slesarev.bookfinder.data.remote.api.authentication.impl.FirebaseAuthService
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepository
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepositoryImpl
import ua.khai.slesarev.bookfinder.data.util.Event
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.ui.util.UiState

class AuthRepositoryImpl(context: Context) : AuthRepository {

    private val auth: AuthService = FirebaseAuthService()
    private val userRepo: UserRepository = UserRepositoryImpl(context)

    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String,
        username: String
    ): Response<Event> {
        lateinit var respSignUp: Event
        lateinit var respAddUser: Event
        lateinit var respSendEmail: Event
        lateinit var rollBackReg: Event
        lateinit var rollBackAdd: Event

/*        val chackValue = accHelper.signUpEmptyCheck(email, password, username)

        if (chackValue == Event.SUCCESS){
            try {
                respSignUp = auth.signUpWithEmailPassword(email, password, username)

                if (respSignUp == Event.SUCCESS) {

                    respAddUser = auth.addUserToDatabase(email, username)

                    if (respAddUser == Event.SUCCESS) {

                        respSendEmail = accHelper.sendEmailVerification()

                        if (respSendEmail == Event.SUCCESS) {
                            uiState.value = UiState.Success(Event.SUCCESS.toString())
                        } else {

                            rollBackAdd = auth.rollBackAddUser()

                            if (rollBackAdd == Event.SUCCESS){

                                rollBackReg = auth.rollBackRegister()

                                if (rollBackReg == Event.SUCCESS){
                                    uiState.value = UiState.Error(respSendEmail.toString())
                                } else {
                                    uiState.value = UiState.Error(rollBackReg.toString())
                                }

                            } else {
                                uiState.value = UiState.Error(rollBackAdd.toString())
                            }
                        }
                    } else {

                        rollBackReg = auth.rollBackRegister()

                        if (rollBackReg == Event.SUCCESS){
                            uiState.value = UiState.Error(respAddUser.toString())
                        } else {
                            uiState.value = UiState.Error(rollBackReg.toString())
                        }
                    }
                } else {
                    uiState.value = UiState.Error(respSignUp.toString())
                }

            }
            catch (e: Exception) {
                throw Exception("Registration failed: ${e.message}")
            }
        }
        else {
            uiState.value = UiState.Error(chackValue.toString())
        }*/
        return Response.Success(Event.SUCCESS)
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): Response<Event> {
        TODO("Not yet implemented")
    }

    override fun signOut(): UiState {
        TODO("Not yet implemented")
    }

    override suspend fun resetPassword(email: String): Response<Event> {
        TODO("Not yet implemented")
    }

    override suspend fun signInWithGoogle(): Response<Event> {
        TODO("Not yet implemented")
    }
}