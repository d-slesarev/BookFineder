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

class SignUpViewModel(application: Application) : AndroidViewModel(application){

    private val accHelper: FirebaseAccHelper = FirebaseAccHelper()

    fun uiState(): LiveData<UiState> = uiState
    val uiState: MutableLiveData<UiState> = MutableLiveData()

    private val TAG = "FirebaseAuth"

    @RequiresApi(Build.VERSION_CODES.P)
    fun signUpWithEmailPassword(email: String, password: String, username: String) {
        lateinit var result: Response

        var respSignUp:Response = Response.SUCCESS
        var respAddUser:Response = Response.SUCCESS
        var respSendEmail:Response = Response.SUCCESS

        viewModelScope.launch {
            try {

                respSignUp = withContext(Dispatchers.IO) {
                    // Вызываете первый сетевой метод
                    accHelper.signUpWithEmailPassword(email, password, username)
                }

                Log.d(TAG, "respSignUp: $respSignUp")

                val respAddUser = withContext(Dispatchers.IO) {
                    // Вызываете второй сетевой метод, используя результат первого
                    accHelper.addUserToDatabase(email, username)
                }

                Log.d(TAG, "respAddUser: $respAddUser")


                val respSendEmail = withContext(Dispatchers.IO) {
                    // Вызываете третий сетевой метод, используя результат второго
                    accHelper.sendEmailVerification()
                }

                ///

            } catch (e:Exception) {
                throw Exception("Registration failed: ${e.message}")
            }
        }

        if (respSignUp == Response.SUCCESS){


            if (respAddUser == Response.SUCCESS){


                if (respSendEmail == Response.SUCCESS){
                    uiState.value = UiState.Success(Response.SUCCESS.toString())
                }else {
                    uiState.value = UiState.Error(respAddUser.toString())
                }
            }else {
                uiState.value = UiState.Error(respAddUser.toString())
            }
        }else {
            uiState.value = UiState.Error(respSignUp.toString())
        }

        // Запуск корутины
/*        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result1 = accHelper.signUpWithEmailPassword(email, password, username)

                if (result1 == Response.SUCCESS) {

                    val result2 = accHelper.addUserToDatabase(email, username)

                    if (result2 == Response.SUCCESS) {

                        val result3 = accHelper.sendEmailVerification()

                        if (result3 == Response.SUCCESS) {
                            uiState.value = UiState.Success(Response.SUCCESS)
                        } else {
                            // Обработка ошибки в третьем запросе
                            uiState.value = UiState.Error(result3)
                        }
                    } else {
                        // Обработка ошибки во втором запросе
                        uiState.value = UiState.Error(result2)
                    }
                } else {
                    // Обработка ошибки в первом запросе
                    uiState.value = UiState.Error(result1)
                }
            } catch (e: Exception) {
                throw Exception("Registration failed: ${e.message}")
            }
        }*/

    }
}