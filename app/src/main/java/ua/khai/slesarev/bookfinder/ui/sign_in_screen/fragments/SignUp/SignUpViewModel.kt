package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SignUp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ua.khai.slesarev.bookfinder.ui.util.AccountHelper.FirebaseAccHelper
import ua.khai.slesarev.bookfinder.ui.util.UiState

class SignUpViewModel : ViewModel() {

    private val accHelper: FirebaseAccHelper = FirebaseAccHelper()

    val uiState: MutableLiveData<UiState> = MutableLiveData()

    private val TAG = "FirebaseAuth"

    suspend fun signUpWithEmailPassword(email: String, password: String, username: String) {

    }

}