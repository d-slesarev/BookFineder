package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SignUp

import android.app.Application
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.ui.base.BaseViewModel
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.SingInActivity
import ua.khai.slesarev.bookfinder.util.AccountHelper.AccountHelper
import ua.khai.slesarev.bookfinder.util.AccountHelper.AccountHelperProvider
import ua.khai.slesarev.bookfinder.util.AccountHelper.FirebaseAccHelper
import ua.khai.slesarev.bookfinder.util.AccountHelper.Response

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    private val accHelper: FirebaseAccHelper = FirebaseAccHelper()

    val uiState: MutableLiveData<UiState> = MutableLiveData()

    private val TAG = "FirebaseAuth"

    suspend fun signUpWithEmailPassword(email: String, password: String, username: String) {
        lateinit var respSignUp: Response
        lateinit var respAddUser: Response
        lateinit var respSendEmail: Response

        val chackValue = accHelper.emptyCheck(email, password, username)

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
                            uiState.value = UiState.Error(respAddUser.toString())
                        }
                    } else {
                        uiState.value = UiState.Error(respAddUser.toString())
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