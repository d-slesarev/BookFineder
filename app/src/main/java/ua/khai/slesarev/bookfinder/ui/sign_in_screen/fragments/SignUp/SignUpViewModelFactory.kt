package ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SignUp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.SingInActivity

class SignUpViewModelFactory() :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignUpViewModel() as T
    }

}