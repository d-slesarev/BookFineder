package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SignUp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ua.khai.slesarev.bookfinder.util.AccountHelper.FirebaseAccHelper
import ua.khai.slesarev.bookfinder.util.AccountHelper.Response

class SignUpViewModel : ViewModel() {

    private val accHelper: FirebaseAccHelper = FirebaseAccHelper()

    val uiState: MutableLiveData<UiState> = MutableLiveData()

    private val TAG = "FirebaseAuth"

    suspend fun signUpWithEmailPassword(email: String, password: String, username: String) {
        lateinit var respSignUp: Response
        lateinit var respAddUser: Response
        lateinit var respSendEmail: Response
        lateinit var rollBackReg: Response
        lateinit var rollBackAdd: Response

        val chackValue = accHelper.signUpEmptyCheck(email, password, username)

        if (chackValue == Response.SUCCESS){
            try {
                uiState.value = UiState.Loading
                respSignUp = accHelper.signUpWithEmailPassword(email, password, username)

                if (respSignUp == Response.SUCCESS) {

                    respAddUser = accHelper.addUserToDatabase(email, username)

                    if (respAddUser == Response.SUCCESS) {

                        respSendEmail = accHelper.sendEmailVerification()

                        if (respSendEmail == Response.SUCCESS) {
                            uiState.value = UiState.Success(Response.SUCCESS.toString())
                        } else {

                            rollBackAdd = accHelper.rollBackAddUser()

                            if (rollBackAdd == Response.SUCCESS){

                                rollBackReg = accHelper.rollBackRegister()

                                if (rollBackReg == Response.SUCCESS){
                                    uiState.value = UiState.Error(respSendEmail.toString())
                                } else {
                                    uiState.value = UiState.Error(rollBackReg.toString())
                                }

                            } else {
                                uiState.value = UiState.Error(rollBackAdd.toString())
                            }
                        }
                    } else {

                        rollBackReg = accHelper.rollBackRegister()

                        if (rollBackReg == Response.SUCCESS){
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
        }
    }

}