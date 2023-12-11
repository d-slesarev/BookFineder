package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SingIn

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SignUp.UiState
import ua.khai.slesarev.bookfinder.util.AccountHelper.FirebaseAccHelper
import ua.khai.slesarev.bookfinder.util.AccountHelper.Response

class SignInViewModel : ViewModel() {

    private val accHelper: FirebaseAccHelper = FirebaseAccHelper()
    val uiState: MutableLiveData<UiState> = MutableLiveData()
    private val TAG = "FirebaseAuth"
    lateinit var userName:String
    lateinit var userEmail:String

    suspend fun signInWithEmailPassword(email: String,password: String) {
        lateinit var respSignIn: Response

        val chackValue = accHelper.signInEmptyCheck(email, password)

        if (chackValue == Response.SUCCESS){

            try {
                uiState.value = UiState.Loading

                respSignIn = accHelper.signInWithEmailPassword(email, password)

                if (respSignIn == Response.SUCCESS){
                    uiState.value = UiState.Success(respSignIn.toString())
                } else {
                    uiState.value = UiState.Success(respSignIn.toString())
                }


            } catch (e: Exception){
                throw Exception("Registration failed: ${e.message}")
            }

        } else {
            uiState.value = UiState.Error(chackValue.toString())
        }

    }

}