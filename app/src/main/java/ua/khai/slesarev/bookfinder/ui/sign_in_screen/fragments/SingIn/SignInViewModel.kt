package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SingIn

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.khai.slesarev.bookfinder.data.repository.auth.AuthRepository
import ua.khai.slesarev.bookfinder.data.repository.auth.AuthRepositoryImpl
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepository
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepositoryImpl
import ua.khai.slesarev.bookfinder.ui.util.UiState
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.data.util.MY_TAG

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val authHelper: AuthRepository = AuthRepositoryImpl(application)
    private val userRepo: UserRepository = UserRepositoryImpl(application)

    val uiState: MutableLiveData<UiState> = MutableLiveData()

    var userEmail: String = ""
    var userName: String = ""

    suspend fun signInWithEmailPassword(email: String, password: String) {
        uiState.value = UiState.Loading

        val result = authHelper.signInWithEmailPassword(email, password)

        if (result is Response.Success) {

            try {
                Log.i(MY_TAG, "Before: SignInView.getUserByID()")
                withContext(Dispatchers.IO) {
                    val response = userRepo.getUserByUID()

                    if (response is Response.Success) {
                        val user = response.data

                        userName = user.username
                        userEmail = user.email
                    }
                }
            } catch (e: Exception) {
                Log.d(MY_TAG, "SignInView.getUserByID-Exception: ${e.message.toString()}")
            }

            uiState.value = UiState.Success(result.data.toString())
        } else if (result is Response.Error) {
            authHelper.signOut()
            uiState.value = UiState.Error(result.errorMessage)
        }
    }
}