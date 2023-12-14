package ua.khai.slesarev.bookfinder.ui.home_screen.fragments

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.khai.slesarev.bookfinder.data.model.User
import ua.khai.slesarev.bookfinder.data.repository.auth.AuthRepository
import ua.khai.slesarev.bookfinder.data.repository.auth.AuthRepositoryImpl
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepository
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepositoryImpl
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.data.util.TAG

class HomeActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val authHelper: AuthRepository = AuthRepositoryImpl(application)
    private val userRepo: UserRepository = UserRepositoryImpl(application)

    fun singOut(){

        CoroutineScope(Dispatchers.IO).launch {
            val response = userRepo.getUserByUID()
            if (response is Response.Success) {
                val user = response.data

                user.remember = false

                val response = userRepo.updateUser(user)

                if (response is Response.Success) {
                    Log.d(TAG, "HomeActivityViewModel.rememberState: Success!")
                    authHelper.signOut()
                }
            }
        }

    }

}