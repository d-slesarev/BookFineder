package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.PassRecovery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ua.khai.slesarev.bookfinder.data.repository.auth.AuthRepository
import ua.khai.slesarev.bookfinder.data.repository.auth.AuthRepositoryImpl
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.ui.util.UiState

class PassRecoveryViewModel(application: Application) : AndroidViewModel(application) {

    private val authHelper: AuthRepository = AuthRepositoryImpl(application)
    val uiState: MutableLiveData<UiState> = MutableLiveData()

    suspend fun resetPassword(email: String) {
        uiState.value = UiState.Loading

        val result = authHelper.resetPassword(email)

        if (result is Response.Success) {
            uiState.value = UiState.Success(result.data.toString())
        } else if(result is Response.Error) {
            uiState.value = UiState.Error(result.errorMessage)
        }

    }
}