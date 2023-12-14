package ua.khai.slesarev.bookfinder.ui.sign_in_screen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepository
import ua.khai.slesarev.bookfinder.data.repository.user.UserRepositoryImpl
import ua.khai.slesarev.bookfinder.data.util.Response
import ua.khai.slesarev.bookfinder.data.util.TAG

class SingInActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepo: UserRepository = UserRepositoryImpl(application)

    var userEmail: String = ""
    var userName: String = ""

    suspend fun updateUI(){
        withContext(Dispatchers.IO) {
            val response = userRepo.getUserByUID()

            if (response is Response.Success) {
                val user = response.data

                userName = user.username
                userEmail = user.email
                Log.d(TAG, "SingInActivityViewModel.updateUI: Success!")
            }
        }
    }
}